package net.sacredlabyrinth.phaed.simpleclans.proxy.json;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanPlayerListAdapter extends TypeAdapter<List<ClanPlayer>> {
    private final SimpleClans plugin;

    public ClanPlayerListAdapter(SimpleClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void write(JsonWriter out, List<ClanPlayer> value) throws IOException {
        out.beginArray();
        for (ClanPlayer clanPlayer : value) {
            out.beginObject();
            out.name("uuid");
            out.value(clanPlayer.getUniqueId().toString());
            out.endObject();
        }
        out.endArray();
    }

    @Override
    public List<ClanPlayer> read(JsonReader in) throws IOException {
        List<ClanPlayer> list = new ArrayList<>();
        in.beginArray();
        while (in.peek() == JsonToken.BEGIN_OBJECT) {
            in.beginObject();
            in.nextName();
            UUID uuid = UUID.fromString(in.nextString());
            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(uuid);
            in.endObject();
            if (cp != null) {
                list.add(cp);
            }
        }
        in.endArray();
        return list;
    }

    public static Type getType() {
        return TypeToken.getParameterized(List.class, ClanPlayer.class).getType();
    }
}
