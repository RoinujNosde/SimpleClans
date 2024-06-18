package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.FrameOpenEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.WordWrapper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class InventoryDrawer {
    private static final SimpleClans plugin = SimpleClans.getInstance();
    private static final ConcurrentHashMap<UUID, SCFrame> OPENING = new ConcurrentHashMap<>();

    private InventoryDrawer() {
    }

    public static void open(@Nullable SCFrame frame) {
        if (frame == null) {
            return;
        }
        UUID uuid = frame.getViewer().getUniqueId();
        if (frame.equals(OPENING.get(uuid))) {
            return;
        }

	FrameOpenEvent event = new FrameOpenEvent(frame.getViewer(), frame);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
	}
	OPENING.put(uuid, frame);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
	    Inventory inventory = prepareInventory(frame);

            if (!frame.equals(OPENING.get(uuid))) {
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> {
                frame.getViewer().openInventory(inventory);
                InventoryController.register(frame);
                OPENING.remove(uuid);
            });
        });
    }

    @NotNull
    private static Inventory prepareInventory(@NotNull SCFrame frame) {
        Inventory inventory = Bukkit.createInventory(frame.getViewer(), frame.getSize(), frame.getTitle());
        long start = System.currentTimeMillis();
        setComponents(inventory, frame);

        if (plugin.getSettingsManager().is(DEBUG)) {
            plugin.getLogger().log(Level.INFO,
                    String.format("It took %s millisecond(s) to load the frame %s for %s",
                            System.currentTimeMillis() - start, frame.getTitle(), frame.getViewer().getName()));
        }
        return inventory;
    }

    /**
     *
     * @deprecated use {@link InventoryDrawer#open(SCFrame)}
     */
    @Deprecated
    public static void update(@NotNull SCFrame frame) {
        open(frame);
    }

    private static void setComponents(@NotNull Inventory inventory, @NotNull SCFrame frame) {
        frame.clear();
        try {
            frame.createComponents();
        } catch (NoSuchFieldError ex) {
            runHelpCommand(frame.getViewer());
            return;
        }

        Set<SCComponent> components = frame.getComponents();
        if (components.isEmpty()) {
            plugin.getLogger().warning(String.format("Frame %s has no components", frame.getTitle()));
            return;
        }
        for (SCComponent c : frame.getComponents()) {
            if (c.getSlot() >= frame.getSize()) {
                continue;
            }
            checkLorePermission(frame, c);
            processLineBreaks(c);
            inventory.setItem(c.getSlot(), c.getItem());
        }
    }

    private static void processLineBreaks(SCComponent c) {
        ItemMeta itemMeta = c.getItemMeta();
        if (itemMeta != null) {
            List<String> oldLore = itemMeta.getLore();
            if (oldLore != null) {
                ArrayList<String> newLore = new ArrayList<>();
                for (String line : oldLore) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    WordWrapper wrapper = new WordWrapper(line, plugin.getSettingsManager().getInt(LORE_LENGTH));
                    newLore.addAll(Arrays.asList(wrapper.wrap()));
                }
                itemMeta.setLore(newLore);
                c.setItemMeta(itemMeta);
            }
        }
    }

    private static void runHelpCommand(@NotNull Player player) {
        Bukkit.getScheduler().runTask(plugin, () -> plugin.getServer().getConsoleSender().sendMessage(lang("gui.not.supported")));
        SettingsManager settingsManager = plugin.getSettingsManager();
        settingsManager.set(ENABLE_GUI, false);
        String commandClan = settingsManager.getString(COMMANDS_CLAN);
        player.performCommand(commandClan);
    }

    private static void checkLorePermission(@NotNull SCFrame frame, @NotNull SCComponent component) {
        ItemMeta itemMeta = component.getItemMeta();
        if (itemMeta != null) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                Object permission = component.getLorePermission();
                if (permission != null) {
                    if (!hasPermission(frame.getViewer(), permission)) {
                        lore.clear();
                        lore.add(lang("gui.lore.no.permission", frame.getViewer()));
                        itemMeta.setLore(lore);
                        component.setItemMeta(itemMeta);
                    }
                }
            }
        }
    }

    private static boolean hasPermission(@NotNull Player viewer, @NotNull Object permission) {
    	if (permission instanceof String) {
    		return plugin.getPermissionsManager().has(viewer, (String) permission);
		}
    	return plugin.getPermissionsManager().has(viewer, (RankPermission) permission, false);
	}

}
