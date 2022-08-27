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
import net.sacredlabyrinth.phaed.simpleclans.proxy.adapters.ClanPlayerListAdapter;
import net.sacredlabyrinth.phaed.simpleclans.proxy.adapters.ClanPlayerTypeAdapterFactory;
import net.sacredlabyrinth.phaed.simpleclans.proxy.adapters.SCMessageAdapter;
import net.sacredlabyrinth.phaed.simpleclans.proxy.listeners.MessageListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public final class BungeeManager implements ProxyManager, PluginMessageListener {

    private static final String UPDATE_CLAN_CHANNEL = "UpdateClan";
    private static final String UPDATE_CLANPLAYER_CHANNEL = "UpdateClanPlayer";
    private static final String DELETE_CLAN_CHANNEL = "DeleteClan";
    private static final String DELETE_CLANPLAYER_CHANNEL = "DeleteClanPlayer";
    private static final String CHAT_CHANNEL = "Chat";

    private final SimpleClans plugin;
    private final Gson gson;
    private final List<String> onlinePlayers = new ArrayList<>();

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
        Bukkit.getScheduler().runTaskTimer(plugin, this::requestPlayerList, 0, 60 * 20);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        String subChannel = input.readUTF();

        try {
            Class<?> clazz = Class.forName("net.sacredlabyrinth.phaed.simpleclans.proxy.listeners." + subChannel);
            MessageListener listener = (MessageListener) clazz.getConstructor(BungeeManager.class).newInstance(this);
            listener.accept(input);
        } catch (ClassNotFoundException e) {
            SimpleClans.debug(String.format("Unknown channel: %s", subChannel));
        } catch (ReflectiveOperationException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error processing channel %s", subChannel), ex);
        }
    }

    @Override
    public boolean isOnline(String playerName) {
        if (Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getName().equals(playerName))) {
            return true;
        }
        return onlinePlayers.contains(playerName);
    }

    public void setOnlinePlayers(@NotNull List<String> onlinePlayers) {
        this.onlinePlayers.clear();
        this.onlinePlayers.addAll(onlinePlayers);
    }

    @Override
    public void sendMessage(SCMessage message) {
        forwardPluginMessage(CHAT_CHANNEL, gson.toJson(message), false);
    }

    @Override
    public void sendDelete(Clan clan) {
        forwardPluginMessage(DELETE_CLAN_CHANNEL, clan.getTag());
    }

    @Override
    public void sendDelete(ClanPlayer cp) {
        forwardPluginMessage(DELETE_CLANPLAYER_CHANNEL, cp.getUniqueId().toString());
    }

    @Override
    public void sendUpdate(Clan clan) {
        forwardPluginMessage(UPDATE_CLAN_CHANNEL, gson.toJson(clan));
    }

    @Override
    public void sendUpdate(ClanPlayer cp) {
        forwardPluginMessage(UPDATE_CLANPLAYER_CHANNEL, gson.toJson(cp));
    }

    public SimpleClans getPlugin() {
        return plugin;
    }

    public Gson getGson() {
        return gson;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void requestPlayerList() {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            return;
        }
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("PlayerList");
        output.writeUTF("ALL");

        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player -> {
            player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
            debug("Requested player list");
        });
    }

    private void forwardPluginMessage(final String subChannel, final String message) {
        forwardPluginMessage(subChannel, message, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void forwardPluginMessage(final String subChannel, final String message, final boolean all) {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            return;
        }
        String target = all ? "ALL" : "ONLINE";
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Forward");
        output.writeUTF(target);
        output.writeUTF(subChannel);
        output.writeUTF(message);

        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player -> {
            player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
            debug(String.format("Sent message on channel %s", subChannel));
        });
    }

}
