package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BungeeManager implements PluginMessageListener {

    private static final String UPDATE_CLAN_CHANNEL = "sc:update_clan";
    private static final String UPDATE_CLANPLAYER_CHANNEL = "sc:update_clanplayer";
    private static final String INSERT_CLAN_CHANNEL = "sc:insert_clan";
    private static final String INSERT_CLANPLAYER_CHANNEL = "sc:insert_clanplayer";

    private final Gson clanPlayerGson = new GsonBuilder().registerTypeAdapter(Clan.class, new ClanAdapter()).create();
    private final Gson clanGson = new GsonBuilder().registerTypeAdapter(ClanPlayer.class, new ClanPlayerAdapter()).create();

    // TODO Replace usage of JsonSimple in pom.xml

    private final SimpleClans plugin = SimpleClans.getInstance();

    public BungeeManager() {
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, UPDATE_CLAN_CHANNEL, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, UPDATE_CLANPLAYER_CHANNEL, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, UPDATE_CLAN_CHANNEL);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, UPDATE_CLANPLAYER_CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, INSERT_CLAN_CHANNEL, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, INSERT_CLANPLAYER_CHANNEL, this);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, INSERT_CLAN_CHANNEL);
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, INSERT_CLANPLAYER_CHANNEL);
    }

    public void sendUpdate(Clan clan) {
        byte[] message = clanGson.toJson(clan).getBytes(StandardCharsets.UTF_8); //todo extract
        plugin.getServer().sendPluginMessage(plugin, UPDATE_CLAN_CHANNEL, message);
    }

    public void sendUpdate(ClanPlayer cp) {
        byte[] message = clanPlayerGson.toJson(cp).getBytes(StandardCharsets.UTF_8);
        plugin.getServer().sendPluginMessage(plugin, UPDATE_CLANPLAYER_CHANNEL, message);
    }

    public void sendInsert(Clan clan) {
        byte[] message = clanGson.toJson(clan).getBytes(StandardCharsets.UTF_8);
        plugin.getServer().sendPluginMessage(plugin, INSERT_CLAN_CHANNEL, message);
    }

    public void sendInsert(ClanPlayer cp) {
        byte[] message = clanPlayerGson.toJson(cp).getBytes(StandardCharsets.UTF_8);
        plugin.getServer().sendPluginMessage(plugin, INSERT_CLANPLAYER_CHANNEL, message);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        switch (channel) {
            case INSERT_CLAN_CHANNEL:
                insertClan(message);
                break;
            case INSERT_CLANPLAYER_CHANNEL:
                insertClanPlayer(message);
                break;
            case UPDATE_CLAN_CHANNEL:
                updateClan(message);
                break;
            case UPDATE_CLANPLAYER_CHANNEL:
                updateClanPlayer(message);
                break;
        }
    }

    private void insertClan(byte[] message) {
        String json = new String(message, StandardCharsets.UTF_8); //todo extract
        Clan clan = clanGson.fromJson(json, Clan.class);
        plugin.getClanManager().importClan(clan);
    }

    private void insertClanPlayer(byte[] message) {
        String json = new String(message, StandardCharsets.UTF_8);
        ClanPlayer cp = clanPlayerGson.fromJson(json, ClanPlayer.class);
        plugin.getClanManager().importClanPlayer(cp);
    }

    private void updateClan(byte[] message) {
        // todo Find Clan object with the same tag, copy field **values** to it, so the reference remains the same
    }

    private void updateClanPlayer(byte[] message) {
        // todo implement in a similar way as updateClan()
    }

    class ClanAdapter extends TypeAdapter<Clan> {

        @Override
        public void write(JsonWriter out, Clan clan) throws IOException {
            out.beginObject();
            out.name("tag");
            out.value(clan.getTag());
            out.endObject();
        }

        @Override
        public Clan read(JsonReader in) throws IOException {
            in.beginObject();
            in.nextName();
            String tag = in.nextString();
            return plugin.getClanManager().getClan(tag);
        }
    }

    class ClanPlayerAdapter extends TypeAdapter<ClanPlayer> {

        @Override
        public void write(JsonWriter out, ClanPlayer value) throws IOException {
            out.beginObject();
            out.name("uuid");
            out.value(value.getUniqueId().toString());
            out.endObject();
        }

        @Override
        public @Nullable ClanPlayer read(JsonReader in) throws IOException {
            in.beginObject();
            in.nextName();
            UUID uuid = UUID.fromString(in.nextString());
            return plugin.getClanManager().getAnyClanPlayer(uuid);
        }
    }


}
