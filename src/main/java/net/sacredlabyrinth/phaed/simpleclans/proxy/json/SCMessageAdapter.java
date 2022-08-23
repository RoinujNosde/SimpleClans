package net.sacredlabyrinth.phaed.simpleclans.proxy.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.UUID;

public class SCMessageAdapter extends TypeAdapter<SCMessage> {

    private final SimpleClans plugin;

    public SCMessageAdapter(SimpleClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void write(JsonWriter out, SCMessage value) throws IOException {
        out.beginObject();
        out.name("channel");
        out.value(value.getChannel().toString());
        out.name("sender");
        out.value(value.getSender().getUniqueId().toString());
        out.name("content");
        out.value(value.getContent());
        out.endObject();
    }

    @Override
    public @Nullable SCMessage read(JsonReader in) throws IOException {
        in.beginObject();
        in.nextName();
        ClanPlayer.Channel channel = ClanPlayer.Channel.valueOf(in.nextString());
        in.nextName();
        UUID uuid = UUID.fromString(in.nextString());
        ClanPlayer sender = plugin.getClanManager().getAnyClanPlayer(uuid);
        in.nextName();
        String content = in.nextString();
        in.endObject();

        if (sender == null) {
            return null;
        }
        return new SCMessage(Source.PROXY, channel, sender, content);
    }
}
