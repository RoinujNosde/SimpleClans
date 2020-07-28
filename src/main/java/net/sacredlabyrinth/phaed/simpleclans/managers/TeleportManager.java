package net.sacredlabyrinth.phaed.simpleclans.managers;

import io.papermc.lib.PaperLib;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.TeleportState;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public final class TeleportManager {
    private SimpleClans plugin;
    private HashMap<String, TeleportState> waitingPlayers = new HashMap<>();

    /**
     *
     */
    public TeleportManager() {
        plugin = SimpleClans.getInstance();
        startCounter();
    }

    /**
     * Add player to teleport waiting queue
     *
     * @param player
     * @param dest
     * @param clanName
     */
    public void addPlayer(Player player, Location dest, String clanName) {
		int secs = SimpleClans.getInstance().getSettingsManager().getWaitSecs();
		waitingPlayers.put(player.getUniqueId().toString(), new TeleportState(player, dest, clanName));

		if (secs > 0) {
			ChatBlock.sendMessage(player, ChatColor.AQUA
					+ MessageFormat.format(lang("waiting.for.teleport.stand.still.for.0.seconds",player), secs));
		}
    }

    private void dropItems(Player player) {
        if (plugin.getSettingsManager().isDropOnHome()) {
            PlayerInventory inv = player.getInventory();
            ItemStack[] contents = inv.getContents();

            for (ItemStack item : contents) {
                if (item == null) {
                    continue;
                }

                List<Integer> itemsList = plugin.getSettingsManager().getItemsList();

                if (itemsList.contains(item.getType().getId())) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    inv.remove(item);
                }
            }
        } else if (plugin.getSettingsManager().isKeepOnHome()) {
            try {
                PlayerInventory inv = player.getInventory();
                ItemStack[] contents = inv.getContents().clone();

                for (ItemStack item : contents) {
                    if (item == null) {
                        continue;
                    }

                    List<Integer> itemsList = plugin.getSettingsManager().getItemsList();

                    if (!itemsList.contains(item.getType().getId())) {
                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                        inv.remove(item);
                    }
                }
            } catch (Exception ex) {
                Helper.dumpStackTrace();
            }
        }
    }

    private void startCounter() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Iterator iter = waitingPlayers.values().iterator(); iter.hasNext(); ) {
                    TeleportState state = (TeleportState) iter.next();

                    if (state.isProcessing()) {
                        continue;
                    }
                    state.setProcessing(true);

                    Player player = state.getPlayer();

                    if (player != null) {
                        if (state.isTeleportTime()) {
                            if (Helper.isSameBlock(player.getLocation(), state.getLocation())) {
                                Location loc = state.getDestination();

                                int x = loc.getBlockX();
                                int z = loc.getBlockZ();

                                if (plugin.getSettingsManager().isTeleportBlocks()) {
                                    player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);
                                    player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                    player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z - 1), Material.GLASS, (byte) 0);
                                    player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z + 1), Material.GLASS, (byte) 0);
                                }

                                if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.keep-items")) {
                                    dropItems(player);
                                }

                                SimpleClans.debug("teleporting");

                                Location location = new Location(loc.getWorld(), loc.getBlockX() + .5, loc.getBlockY(), loc.getBlockZ() + .5);
                                PaperLib.teleportAsync(player, location, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                                    if (result) {
                                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("now.at.homebase", player, state.getClanName()));
                                    } else {
                                        plugin.getLogger().log(Level.WARNING, "An error occurred while teleporting a player");
                                    }
                                });

                            } else {
                                ChatBlock.sendMessage(player, ChatColor.RED + lang("you.moved.teleport.cancelled",player));
                            }

                            iter.remove();
                        } else {
                            if (!Helper.isSameBlock(player.getLocation(), state.getLocation())) {
                                ChatBlock.sendMessage(player, ChatColor.RED + lang("you.moved.teleport.cancelled",player));
                                iter.remove();
                            } else {
                                ChatBlock.sendMessage(player, ChatColor.AQUA + "" + state.getCounter());
                            }
                        }
                    } else {
                        iter.remove();
                    }

                    state.setProcessing(false);
                }
            }
        }, 0, 20L);
    }
}