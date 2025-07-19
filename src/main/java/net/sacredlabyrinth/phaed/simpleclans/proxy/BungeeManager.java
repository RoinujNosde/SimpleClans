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
import net.sacredlabyrinth.phaed.simpleclans.proxy.adapters.ConfigurationSerializableAdapter;
import net.sacredlabyrinth.phaed.simpleclans.proxy.adapters.SCMessageAdapter;
import net.sacredlabyrinth.phaed.simpleclans.proxy.listeners.MessageListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BungeeManager implements ProxyManager, PluginMessageListener {

    private static final String UPDATE_CLAN_CHANNEL = "UpdateClan";
    private static final String UPDATE_CLANPLAYER_CHANNEL = "UpdateClanPlayer";
    private static final String DELETE_CLAN_CHANNEL = "DeleteClan";
    private static final String DELETE_CLANPLAYER_CHANNEL = "DeleteClanPlayer";
    private static final String CHAT_CHANNEL = "Chat";
    private static final String BROADCAST = "Broadcast";
    private static final String MESSAGE = "Message";
    private static final String VERSION = "v1";
    private static final Pattern SUBCHANNEL_PATTERN =
            Pattern.compile("SimpleClans\\|(?<subchannel>\\w+)\\|(?<version>v\\d+)\\|(?<server>.+)");

    private final SimpleClans plugin;
    private final Gson gson;
    private final List<String> onlinePlayers = new ArrayList<>();
    private String serverName = "";
    private final Set<String> unsupportedChannels = new HashSet<>();

    public BungeeManager(SimpleClans plugin) {
        this.plugin = plugin;
        gson = new GsonBuilder().registerTypeAdapterFactory(new ClanPlayerTypeAdapterFactory(plugin))
                .registerTypeAdapterFactory(new ConfigurationSerializableAdapter())
                .registerTypeAdapter(ClanPlayerListAdapter.getType(), new ClanPlayerListAdapter(plugin))
                .registerTypeAdapter(SCMessage.class, new SCMessageAdapter(plugin)).setExclusionStrategies()
                .create();
        if (!plugin.getSettingsManager().is(ConfigField.PERFORMANCE_USE_BUNGEECORD)) {
            return;
        }
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        plugin.getScheduler().runTimer(this::requestPlayerList, 0, 60 * 20);
        requestServerName();
    }

    @SuppressWarnings("UnstableApiUsage")
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        ByteArrayDataInput dataInput = ByteStreams.newDataInput(data);
        String subChannel = dataInput.readUTF();
        if (unsupportedChannels.contains(subChannel)) {
            return;
        }

        SimpleClans.debug("Message received, sub-channel: " + subChannel);
        try {
            String serverName = null;
            String className = subChannel;
            String version = null;
            Matcher matcher = SUBCHANNEL_PATTERN.matcher(subChannel);
            if (matcher.find()) {
                serverName = matcher.group("server");
                className = matcher.group("subchannel");
                version = matcher.group("version");
            }
            Class<?> clazz = Class.forName("net.sacredlabyrinth.phaed.simpleclans.proxy.listeners." + className);
            MessageListener listener = (MessageListener) clazz.getConstructor(BungeeManager.class).newInstance(this);
            if (listener.isBungeeSubchannel()) {
                listener.accept(dataInput); //uses the original data
                return;
            }
            if (!VERSION.equals(version)) {
                plugin.getLogger().severe(String.format("Unsupported channel (%s), expected version: %s", subChannel, VERSION));
                unsupportedChannels.add(subChannel);
                return;
            }
            if (serverName != null && !isServerAllowed(serverName)) {
                SimpleClans.debug(String.format("Server not allowed: %s", serverName));
                return;
            }
            byte[] messageBytes = new byte[dataInput.readShort()];
            dataInput.readFully(messageBytes);
            final ByteArrayDataInput message = ByteStreams.newDataInput(messageBytes);

            listener.accept(message); // uses the internal data
        } catch (ClassNotFoundException e) {
            SimpleClans.debug(String.format("Unknown channel: %s", subChannel));
            unsupportedChannels.add(subChannel);
        } catch (Exception ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error processing channel %s", subChannel), ex);
        }
    }

    private boolean isServerAllowed(@NotNull String serverName) {
        List<String> servers = plugin.getSettingsManager().getStringList(ConfigField.BUNGEE_SERVERS);
        if (servers.isEmpty()) {
            return true;
        }

        return servers.contains(serverName);
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
    public String getServerName() {
        return serverName;
    }

    public void setServerName(@NotNull String name) {
        this.serverName = name;
    }

    @Override
    public void sendMessage(SCMessage message) {
        forwardToOnlineServers(CHAT_CHANNEL, gson.toJson(message));
    }

    @Override
    public void sendMessage(@NotNull String target, @NotNull String message) {
        if (message.isEmpty()) {
            return;
        }

        if ("ALL".equals(target)) {
            Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
            sendBroadcast(message);
            return;
        }

        Player player = Bukkit.getPlayerExact(target);
        if (player != null) {
            player.sendMessage(message);
            return;
        }

        sendPrivateMessage(target, message);
    }

    private void sendBroadcast(@NotNull String message) {
        forwardToOnlineServers(BROADCAST, message);
    }

    @Override
    public void sendDelete(Clan clan) {
        forwardToAllServers(DELETE_CLAN_CHANNEL, clan.getTag());
    }

    @Override
    public void sendDelete(ClanPlayer cp) {
        forwardToAllServers(DELETE_CLANPLAYER_CHANNEL, cp.getUniqueId().toString());
    }

    @Override
    public void sendUpdate(Clan clan) {
        forwardToAllServers(UPDATE_CLAN_CHANNEL, gson.toJson(clan));
    }

    @Override
    public void sendUpdate(ClanPlayer cp) {
        forwardToAllServers(UPDATE_CLANPLAYER_CHANNEL, gson.toJson(cp));
    }

    public SimpleClans getPlugin() {
        return plugin;
    }

    public Gson getGson() {
        return gson;
    }

    @SuppressWarnings("UnstableApiUsage")
    private void requestPlayerList() {
        if (!isChannelRegistered()) {
            return;
        }
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("PlayerList");
        output.writeUTF("ALL");

        sendOnBungeeChannel(output);
    }

    private void requestServerName() {
        plugin.getScheduler().runTimer(task -> {
            if (!serverName.isEmpty()) {
                task.cancel();
                return;
            }
            Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(player -> {
                @SuppressWarnings("UnstableApiUsage")
                ByteArrayDataOutput output = ByteStreams.newDataOutput();
                output.writeUTF("GetServer");
                player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
            });
        }, 0L, 20L);
    }

    private String createSubchannelName(String subchannel) {
        return String.format("SimpleClans|%s|%s|%s", subchannel, VERSION, serverName);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendPrivateMessage(@NotNull String playerName, @NotNull String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(playerName);
        output.writeUTF(message);

        forwardPluginMessage(MESSAGE, output, false);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void forwardToAllServers(final String subChannel, final String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(message);
        forwardPluginMessage(subChannel, output, true);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void forwardToOnlineServers(final String subChannel, final String message) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF(message);
        forwardPluginMessage(subChannel, output, false);
    }

    @SuppressWarnings("UnstableApiUsage")
    private void forwardPluginMessage(final String subChannel, final ByteArrayDataOutput message, final boolean all) {
        SimpleClans.debug(String.format("Forwarding message, channel %s, message %s, all %s", subChannel, message, all));
        if (!isChannelRegistered()) {
            return;
        }
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Forward");
        output.writeUTF(all ? "ALL" : "ONLINE");
        output.writeUTF(createSubchannelName(subChannel));

        output.writeShort(message.toByteArray().length);
        output.write(message.toByteArray());

        sendOnBungeeChannel(output);
    }

    private void sendOnBungeeChannel(ByteArrayDataOutput output) {
        Bukkit.getOnlinePlayers().stream().findAny().ifPresent(player ->
                player.sendPluginMessage(plugin, "BungeeCord", output.toByteArray()));
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isChannelRegistered() {
        boolean registered = Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord");
        SimpleClans.debug(String.format("BungeeCord channel registered: %s", registered));
        return registered;
    }

}
