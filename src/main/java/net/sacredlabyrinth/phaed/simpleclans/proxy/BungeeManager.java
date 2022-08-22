package net.sacredlabyrinth.phaed.simpleclans.proxy;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public final class BungeeManager implements PluginMessageListener {

    private static final String UPDATE_CLAN_CHANNEL = "sc:update_clan";
    private static final String UPDATE_CLANPLAYER_CHANNEL = "sc:update_clanplayer";
    private static final String INSERT_CLAN_CHANNEL = "sc:insert_clan";
    private static final String INSERT_CLANPLAYER_CHANNEL = "sc:insert_clanplayer";
    private static final String DELETE_CLAN_CHANNEL = "sc:delete_clan";
    private static final String DELETE_CLANPLAYER_CHANNEL = "sc:delete_clanplayer";

    private final Gson clanPlayerGson = new GsonBuilder().registerTypeAdapter(Clan.class, new ClanAdapter()).create();
    private final Gson clanGson = new GsonBuilder().registerTypeAdapter(ClanPlayer.class, new ClanPlayerAdapter()).create();
    private final SimpleClans plugin;

    public BungeeManager(SimpleClans plugin) {
        this.plugin = plugin;
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
        String message = input.readUTF();
        switch (subChannel) {
            case DELETE_CLAN_CHANNEL:
                deleteClan(message);
                break;
            case DELETE_CLANPLAYER_CHANNEL:
                deleteClanPlayer(message);
                break;
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

    public void sendDelete(Clan clan) {
        sendMessage(DELETE_CLAN_CHANNEL, clan.getTag());
        debug(String.format("Sent delete clan %s", clan.getTag()));
    }

    @SuppressWarnings("UnstableApiUsage")
    private void sendMessage(String subChannel, String message) {
        if (!Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, "BungeeCord")) {
            return;
        }
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("Forward");
        output.writeUTF("ALL");
        output.writeUTF(subChannel);
        output.writeUTF(message);

        plugin.getServer().sendPluginMessage(plugin, "BungeeCord", output.toByteArray());
    }

    public void sendDelete(ClanPlayer cp) {
        sendMessage(DELETE_CLANPLAYER_CHANNEL, cp.getUniqueId().toString());
        debug(String.format("Sent delete cp %s", cp.getName()));
    }

    public void sendUpdate(Clan clan) {
        sendMessage(UPDATE_CLAN_CHANNEL,  toJson(clan));
        debug(String.format("Sent update clan %s", clan.getTag()));
    }

    public void sendUpdate(ClanPlayer cp) {
        sendMessage(UPDATE_CLANPLAYER_CHANNEL, toJson(cp));
        debug(String.format("Sent update cp %s", cp.getName()));
    }

    public void sendInsert(Clan clan) {
        sendMessage(INSERT_CLAN_CHANNEL, toJson(clan));
        debug(String.format("Sent insert clan %s", clan.getTag()));
    }

    public void sendInsert(ClanPlayer cp) {
        sendMessage(INSERT_CLANPLAYER_CHANNEL, toJson(cp));
        debug(String.format("Sent insert cp %s", cp.getName()));
    }

    private void insertClan(String message) {
        Clan clan = clanFromJson(message);
        plugin.getClanManager().importClan(clan);
        debug(String.format("Inserted clan %s", clan.getTag()));
    }

    private void insertClanPlayer(String message) {
        ClanPlayer cp = clanPlayerFromJson(message);
        plugin.getClanManager().importClanPlayer(cp);
        debug(String.format("Inserted cp %s", cp.getName()));
    }

    private void updateClan(String message) {
        Clan bungeeClan = clanFromJson(message);
        Clan clan = plugin.getClanManager().getClan(bungeeClan.getTag());
        if (clan == null) {
            insertClan(message);
            return;
        }
        try {
            updateFields(bungeeClan, clan);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("An error happened while update the clan %s",
                    clan.getTag()), e);
        }
        debug(String.format("Updated clan %s", clan.getTag()));
    }

    private void updateClanPlayer(String message) {
        ClanPlayer bungeeCp = clanPlayerFromJson(message);
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(bungeeCp.getUniqueId());
        if (cp == null) {
            insertClanPlayer(message);
            return;
        }
        try {
            updateFields(bungeeCp, cp);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while updating ClanPlayer %s", cp.getUniqueId()), e);
        }
        debug(String.format("Updated cp %s", cp.getName()));
    }

    private void deleteClan(String tag) {
        plugin.getClanManager().removeClan(tag);
        debug(String.format("Deleted clan %s", tag));
    }

    private void deleteClanPlayer(String message) {
        UUID uuid = UUID.fromString(message);
        plugin.getClanManager().deleteClanPlayerFromMemory(uuid);
        debug(String.format("Deleted cp %s", uuid));
    }

    static void updateFields(Object origin, Object destination) throws IllegalAccessException {
        if (origin.getClass() != destination.getClass()) {
            throw new IllegalArgumentException("origin and destination must be of the same type");
        }
        Field[] fields = origin.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (Modifier.isFinal(field.getModifiers())) {
                copyValues(field, field.get(origin), field.get(destination));
                continue;
            }
            field.set(destination, field.get(origin));
        }
    }

    private static boolean isPrimitive(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive() || Number.class.isAssignableFrom(type) || type == String.class;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyValues(Field field, Object originValue, Object destValue) {
        if (isPrimitive(field)) {
            return;
        }
        if (originValue instanceof Collection) {
            Collection<?> destColl = (Collection<?>) destValue;
            destColl.clear();
            destColl.addAll((Collection) originValue);
            return;
        }
        if (originValue instanceof Map) {
            Map<?, ?> destMap = (Map<?, ?>) destValue;
            destMap.clear();
            destMap.putAll((Map) originValue);
            return;
        }
        throw new UnsupportedOperationException(String.format("unknown field type: %s", originValue.getClass()));
    }

    private ClanPlayer clanPlayerFromJson(String json) {
        return clanPlayerGson.fromJson(json, ClanPlayer.class);
    }

    private Clan clanFromJson(String json) {
        return clanGson.fromJson(json, Clan.class);
    }

    private String toJson(ClanPlayer cp) {
        return clanPlayerGson.toJson(cp);
    }

    private String toJson(Clan clan) {
        return clanGson.toJson(clan);
    }

    class ClanAdapter extends TypeAdapter<Clan> {

        @Override
        public void write(JsonWriter out, Clan clan) throws IOException {
            out.beginObject();
            out.name("tag");
            String tag = clan == null ? "" : clan.getTag();
            out.value(tag);
            out.endObject();
        }

        @Override
        public @Nullable Clan read(JsonReader in) throws IOException {
            in.beginObject();
            in.nextName();
            String tag = in.nextString();
            in.endObject();
            if (tag.isEmpty()) {
                return null;
            }
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
            in.endObject();
            return plugin.getClanManager().getAnyClanPlayer(uuid);
        }
    }

}
