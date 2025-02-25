package net.sacredlabyrinth.phaed.simpleclans.chest;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanChest implements Serializable, InventoryHolder {

    @Serial
    private static final long serialVersionUID = 1L;

    private transient @Nullable Inventory chest;

    @Override
    public @NotNull Inventory getInventory() {
        if (chest == null) {
            chest = Bukkit.createInventory(this, 27, lang("clan.chest.title"));
        }

        return chest;
    }

    public byte[] serialize() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(this);
            return baos.toByteArray();
        } catch (IOException ex) {
            SimpleClans.getInstance().getLogger().log(Level.SEVERE, "Failed to serialize clan chest", ex);
        }

        return new byte[0];
    }

    public static @NotNull ClanChest deserialize(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (ClanChest) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            SimpleClans.getInstance().getLogger().log(Level.SEVERE, "Failed to deserialize clan chest", ex);
        }

        return new ClanChest();
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        ItemStack[] contents = chest != null ? chest.getContents() : new ItemStack[27];
        List<Map<String, Object>> serializedContents = Arrays.stream(contents)
                .map(item -> item != null ? item.serialize() : null)
                .toList();

        out.writeObject(serializedContents);
    }

    @Serial
    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        chest = Bukkit.createInventory(this, 27, lang("clan.chest.title"));

        List<Map<String, Object>> serializedContents = (List<Map<String, Object>>) in.readObject();
        ItemStack[] contents = serializedContents.stream()
                .map(map -> map != null ? ItemStack.deserialize(map) : null)
                .toArray(ItemStack[]::new);

        chest.setContents(contents);
    }
}
