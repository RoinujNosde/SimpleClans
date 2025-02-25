package net.sacredlabyrinth.phaed.simpleclans.proxy.adapters;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chest.ClanChest;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ClanChestAdapter extends TypeAdapter<ClanChest> {

    private final SimpleClans plugin;

    public ClanChestAdapter(SimpleClans plugin) {
        this.plugin = plugin;
    }

    @Override
    public void write(JsonWriter out, ClanChest clanChest) throws IOException {
        Gson gson = plugin.getProxyManager().getGson();
        out.beginObject();

        out.name("content");
        ItemStack[] contents = clanChest.getInventory().getContents();
        out.value(gson.toJson(contents, ItemStack[].class));

        out.endObject();
    }

    @Override
    public ClanChest read(JsonReader in) throws IOException {
        Gson gson = plugin.getProxyManager().getGson();
        in.beginObject();

        ItemStack[] items = null;
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("content")) {
                String json = in.nextString();
                items = gson.fromJson(json, ItemStack[].class);
            } else {
                in.skipValue();
            }
        }

        in.endObject();

        ClanChest clanChest = new ClanChest();
        if (items != null) {
            clanChest.getInventory().setContents(items);
        }

        return clanChest;
    }
}
