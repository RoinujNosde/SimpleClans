package net.sacredlabyrinth.phaed.simpleclans.managers;

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
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public final class BungeeManager {

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
        Messenger messenger = Bukkit.getMessenger();
        messenger.registerIncomingPluginChannel(plugin, UPDATE_CLAN_CHANNEL, this::updateClan);
        messenger.registerIncomingPluginChannel(plugin, UPDATE_CLANPLAYER_CHANNEL, this::updateClanPlayer);
        messenger.registerOutgoingPluginChannel(plugin, UPDATE_CLAN_CHANNEL);
        messenger.registerOutgoingPluginChannel(plugin, UPDATE_CLANPLAYER_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, INSERT_CLAN_CHANNEL, this::insertClan);
        messenger.registerIncomingPluginChannel(plugin, INSERT_CLANPLAYER_CHANNEL, this::insertClanPlayer);
        messenger.registerOutgoingPluginChannel(plugin, INSERT_CLAN_CHANNEL);
        messenger.registerOutgoingPluginChannel(plugin, INSERT_CLANPLAYER_CHANNEL);
        messenger.registerOutgoingPluginChannel(plugin, DELETE_CLAN_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, DELETE_CLAN_CHANNEL, this::deleteClan);
        messenger.registerOutgoingPluginChannel(plugin, DELETE_CLAN_CHANNEL);
        messenger.registerIncomingPluginChannel(plugin, DELETE_CLANPLAYER_CHANNEL, this::deleteClanPlayer);
        messenger.registerOutgoingPluginChannel(plugin, DELETE_CLANPLAYER_CHANNEL);
    }

    public void sendDelete(Clan clan) {
        if (isChannelUnregistered(DELETE_CLAN_CHANNEL)) {
            return;
        }
        plugin.getServer().sendPluginMessage(plugin, DELETE_CLAN_CHANNEL, clan.getTag().getBytes(StandardCharsets.UTF_8));
        debug(String.format("Sent delete clan %s", clan.getTag()));
    }

    public void sendDelete(ClanPlayer cp) {
        if (isChannelUnregistered(DELETE_CLANPLAYER_CHANNEL)) {
            return;
        }
        plugin.getServer().sendPluginMessage(plugin, DELETE_CLANPLAYER_CHANNEL,
                cp.getUniqueId().toString().getBytes(StandardCharsets.UTF_8));
        debug(String.format("Sent delete cp %s", cp.getName()));
    }

    public void sendUpdate(Clan clan) {
        if (isChannelUnregistered(UPDATE_CLAN_CHANNEL)) {
            return;
        }
        byte[] message = getBytes(clan);
        plugin.getServer().sendPluginMessage(plugin, UPDATE_CLAN_CHANNEL, message);
        debug(String.format("Sent update clan %s", clan.getTag()));
    }

    public void sendUpdate(ClanPlayer cp) {
        if (isChannelUnregistered(UPDATE_CLANPLAYER_CHANNEL)) {
            return;
        }
        byte[] message = getBytes(cp);
        plugin.getServer().sendPluginMessage(plugin, UPDATE_CLANPLAYER_CHANNEL, message);
        debug(String.format("Sent update cp %s", cp.getName()));
    }

    public void sendInsert(Clan clan) {
        if (isChannelUnregistered(INSERT_CLAN_CHANNEL)) {
            return;
        }
        byte[] message = getBytes(clan);
        plugin.getServer().sendPluginMessage(plugin, INSERT_CLAN_CHANNEL, message);
        debug(String.format("Sent insert clan %s", clan.getTag()));
    }

    public void sendInsert(ClanPlayer cp) {
        if (isChannelUnregistered(INSERT_CLANPLAYER_CHANNEL)) {
            return;
        }
        byte[] message = getBytes(cp);
        plugin.getServer().sendPluginMessage(plugin, INSERT_CLANPLAYER_CHANNEL, message);
        debug(String.format("Sent insert cp %s", cp.getName()));
    }

    private boolean isChannelUnregistered(String channel) {
        return !Bukkit.getMessenger().isOutgoingChannelRegistered(plugin, channel);
    }

    private void insertClan(String channel, Player player, byte[] message) {
        Clan clan = clanFromBytes(message);
        plugin.getClanManager().importClan(clan);
        debug(String.format("Inserted clan %s", clan.getTag()));
    }

    private void insertClanPlayer(String channel, Player player, byte[] message) {
        ClanPlayer cp = clanPlayerFromBytes(message);
        plugin.getClanManager().importClanPlayer(cp);
        debug(String.format("Inserted cp %s", cp.getName()));
    }

    private void updateClan(String channel, Player player, byte[] message) {
        Clan bungeeClan = clanFromBytes(message);
        Clan clan = plugin.getClanManager().getClan(bungeeClan.getTag());
        if (clan == null) {
            insertClan(channel, player, message);
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

    private void updateClanPlayer(String channel, Player player, byte[] message) {
        ClanPlayer bungeeCp = clanPlayerFromBytes(message);
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(bungeeCp.getUniqueId());
        if (cp == null) {
            insertClanPlayer(channel, player, message);
            return;
        }
        try {
            updateFields(bungeeCp, cp);
        } catch (IllegalAccessException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error while updating ClanPlayer %s", cp.getUniqueId()), e);
        }
        debug(String.format("Updated cp %s", cp.getName()));
    }

    private void deleteClan(String channel, Player player, byte[] message) {
        String tag = new String(message, StandardCharsets.UTF_8);
        plugin.getClanManager().removeClan(tag);
        debug(String.format("Deleted clan %s", tag));
    }

    private void deleteClanPlayer(String channel, Player player, byte[] message) {
        UUID uuid = UUID.fromString(new String(message, StandardCharsets.UTF_8));
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

    private ClanPlayer clanPlayerFromBytes(byte[] message) {
        String json = new String(message, StandardCharsets.UTF_8);
        return clanPlayerGson.fromJson(json, ClanPlayer.class);
    }

    private Clan clanFromBytes(byte[] message) {
        String json = new String(message, StandardCharsets.UTF_8);
        return clanGson.fromJson(json, Clan.class);
    }

    private byte[] getBytes(ClanPlayer cp) {
        return clanPlayerGson.toJson(cp).getBytes(StandardCharsets.UTF_8);
    }

    private byte[] getBytes(Clan clan) {
        return clanGson.toJson(clan).getBytes(StandardCharsets.UTF_8);
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
            return plugin.getClanManager().getAnyClanPlayer(uuid);
        }
    }

}
