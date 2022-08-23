package net.sacredlabyrinth.phaed.simpleclans.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import net.sacredlabyrinth.phaed.simpleclans.proxy.json.ClanPlayerListAdapter;
import net.sacredlabyrinth.phaed.simpleclans.proxy.json.ClanPlayerTypeAdapterFactory;
import net.sacredlabyrinth.phaed.simpleclans.proxy.json.SCMessageAdapter;
import net.sacredlabyrinth.phaed.simpleclans.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public final class BungeeManager implements ProxyManager, PluginMessageListener {

    private static final String UPDATE_CLAN_CHANNEL = "sc:updateClan";
    private static final String UPDATE_CLANPLAYER_CHANNEL = "sc:updateClanPlayer";
    private static final String INSERT_CLAN_CHANNEL = "sc:insertClan";
    private static final String INSERT_CLANPLAYER_CHANNEL = "sc:insertClanPlayer";
    private static final String DELETE_CLAN_CHANNEL = "sc:deleteClan";
    private static final String DELETE_CLANPLAYER_CHANNEL = "sc:deleteClanPlayer";
    private static final String CHAT_CHANNEL = "sc:processChat";

    private final SimpleClans plugin;
    private final Gson gson;

    public BungeeManager(SimpleClans plugin) {
        this.plugin = plugin;
        gson = new GsonBuilder().registerTypeAdapterFactory(new ClanPlayerTypeAdapterFactory(plugin))
                .registerTypeAdapter(ClanPlayerListAdapter.getType(), new ClanPlayerListAdapter(plugin))
                .registerTypeAdapter(SCMessage.class, new SCMessageAdapter(plugin)).setExclusionStrategies()
                .create();
        if (!plugin.getSettingsManager().is(ConfigField.PERFORMANCE_USE_BUNGEECORD)) {
            return;
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        String subChannel = input.readUTF();
        if (!subChannel.startsWith("sc:")) {
            return;
        }
        String message = input.readUTF();

        try {
            String methodName = subChannel.replace("sc:", "");
            Method method = this.getClass().getDeclaredMethod(methodName, String.class);
            method.invoke(this, message);
        } catch (NoSuchMethodException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Unknown channel: %s", subChannel));
        } catch (IllegalAccessException ignored) {
        } catch (InvocationTargetException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error processing channel %s", subChannel), ex);
        }
    }


    @Override
    public void sendMessage(SCMessage message) {
        sendPluginMessage(CHAT_CHANNEL, gson.toJson(message), false);
    }

    public void sendDelete(Clan clan) {
        sendPluginMessage(DELETE_CLAN_CHANNEL, clan.getTag());
        debug(String.format("Sent delete clan %s", clan.getTag()));
    }

    public void sendDelete(ClanPlayer cp) {
        sendPluginMessage(DELETE_CLANPLAYER_CHANNEL, cp.getUniqueId().toString());
        debug(String.format("Sent delete cp %s", cp.getName()));
    }

    public void sendUpdate(Clan clan) {
        sendPluginMessage(UPDATE_CLAN_CHANNEL, gson.toJson(clan));
        debug(String.format("Sent update clan %s", clan.getTag()));
    }

    public void sendUpdate(ClanPlayer cp) {
        sendPluginMessage(UPDATE_CLANPLAYER_CHANNEL, gson.toJson(cp));
        debug(String.format("Sent update cp %s", cp.getName()));
    }

    public void sendInsert(Clan clan) {
        sendPluginMessage(INSERT_CLAN_CHANNEL, gson.toJson(clan));
        debug(String.format("Sent insert clan %s", clan.getTag()));
    }

    public void sendInsert(ClanPlayer cp) {
        sendPluginMessage(INSERT_CLANPLAYER_CHANNEL, gson.toJson(cp));
        debug(String.format("Sent insert cp %s", cp.getName()));
    }

    private void sendPluginMessage(String subChannel, String message) {
        sendPluginMessage(subChannel, message, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendPluginMessage(String subChannel, String message, boolean all) {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            return;
        }
        String target = all ? "ALL" : "ONLINE";
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Forward");
        output.writeUTF(target);
        output.writeUTF(subChannel);
        output.writeUTF(message);

        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player ->
                player.getServer().sendPluginMessage(plugin, "BungeeCord", output.toByteArray()));
    }

    private void insertClan(String message) {
        Clan clan = gson.fromJson(message, Clan.class);
        plugin.getClanManager().importClan(clan);
        debug(String.format("Inserted clan %s", clan.getTag()));
    }

    private void insertClanPlayer(String message) {
        ClanPlayer cp = gson.fromJson(message, ClanPlayer.class);
        plugin.getClanManager().importClanPlayer(cp);
        debug(String.format("Inserted cp %s", cp.getName()));
    }

    @SuppressWarnings("unused")
    private void updateClan(String message) {
        Clan bungeeClan = gson.fromJson(message, Clan.class);
        Clan clan = plugin.getClanManager().getClan(bungeeClan.getTag());
        if (clan == null) {
            insertClan(message);
            return;
        }
        try {
            ObjectUtils.updateFields(bungeeClan, clan);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("An error happened while update the clan %s",
                    clan.getTag()), e);
        }
        debug(String.format("Updated clan %s", clan.getTag()));
    }

    @SuppressWarnings("unused")
    private void updateClanPlayer(String message) {
        ClanPlayer bungeeCp = gson.fromJson(message, ClanPlayer.class);
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(bungeeCp.getUniqueId());
        if (cp == null) {
            insertClanPlayer(message);
            return;
        }
        try {
            ObjectUtils.updateFields(bungeeCp, cp);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while updating ClanPlayer %s", cp.getUniqueId()), e);
        }
        debug(String.format("Updated cp %s", cp.getName()));
    }

    @SuppressWarnings("unused")
    private void deleteClan(String tag) {
        plugin.getClanManager().removeClan(tag);
        debug(String.format("Deleted clan %s", tag));
    }

    @SuppressWarnings("unused")
    private void deleteClanPlayer(String message) {
        UUID uuid = UUID.fromString(message);
        plugin.getClanManager().deleteClanPlayerFromMemory(uuid);
        debug(String.format("Deleted cp %s", uuid));
    }

    @SuppressWarnings("unused")
    private void processChat(String json) {
        SCMessage message = gson.fromJson(json, SCMessage.class);
        if (message.getSender().getClan() == null) {
            return;
        }
        plugin.getChatManager().processChat(message);
    }

}
