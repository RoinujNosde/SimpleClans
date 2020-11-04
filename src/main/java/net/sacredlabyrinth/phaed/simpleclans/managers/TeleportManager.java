package net.sacredlabyrinth.phaed.simpleclans.managers;

import io.papermc.lib.PaperLib;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public final class TeleportManager {
    private final SimpleClans plugin;
    private final HashMap<String, TeleportState> waitingPlayers = new HashMap<>();

    public TeleportManager() {
        plugin = SimpleClans.getInstance();
        startCounter();
    }

    /**
     * Add player to teleport waiting queue
     *
     * @param player      the Player
     * @param destination the destination
     * @param clanName    the Clan name
     */
    public void addPlayer(Player player, Location destination, String clanName) {
        int secs = SimpleClans.getInstance().getSettingsManager().getWaitSecs();
        waitingPlayers.put(player.getUniqueId().toString(), new TeleportState(player, destination, clanName));

        if (secs > 0) {
            ChatBlock.sendMessage(player, ChatColor.AQUA
                    + MessageFormat.format(lang("waiting.for.teleport.stand.still.for.0.seconds", player), secs));
        }
    }

    @SuppressWarnings("deprecation")
    private void sendTeleportBlocks(Player player, Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        if (plugin.getSettingsManager().isTeleportBlocks()) {
            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z + 1),
                    Material.GLASS, (byte) 0);
            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z - 1),
                    Material.GLASS, (byte) 0);
            player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY() - 1, z - 1),
                    Material.GLASS, (byte) 0);
            player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY() - 1, z + 1),
                    Material.GLASS, (byte) 0);
        }
    }

    public void teleport(Clan clan, Location location) {
        List<ClanPlayer> members = clan.getOnlineMembers();
        for (ClanPlayer cp : members) {
            Player player = cp.toPlayer();
            if (player == null) {
                continue;
            }
            int x = location.getBlockX();
            int z = location.getBlockZ();
            sendTeleportBlocks(player, location);

            Random r = new Random();
            int xx = r.nextInt(2) - 1;
            int zz = r.nextInt(2) - 1;
            if (xx == 0 && zz == 0) {
                xx = 1;
            }
            x = x + xx;
            z = z + zz;

            plugin.getTeleportManager().addPlayer(player, new Location(location.getWorld(), x + .5,
                    location.getBlockY(), z + .5), clan.getName());
        }
    }

    private void dropItems(Player player) {
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.keep-items")) {
            return;
        }
        List<Material> itemsList = plugin.getSettingsManager().getItemsList();
        PlayerInventory inv = player.getInventory();
        boolean dropOnHome = plugin.getSettingsManager().isDropOnHome();
        boolean keepOnHome = plugin.getSettingsManager().isKeepOnHome();
        ItemStack[] contents = inv.getContents();
        for (ItemStack item : contents) {
            if (item == null) {
                continue;
            }
            if ((dropOnHome && itemsList.contains(item.getType())) ||
                    (keepOnHome && !itemsList.contains(item.getType()))) {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                inv.remove(item);
            }
        }
    }

    private void startCounter() {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            waitingPlayers.values().removeIf(ts -> ts.getPlayer() == null);
            for (Iterator<TeleportState> iter = waitingPlayers.values().iterator(); iter.hasNext(); ) {
                TeleportState state = iter.next();
                Player player = state.getPlayer();
                if (state.isProcessing() || player == null) {
                    continue;
                }
                state.setProcessing(true);

                if (!Helper.isSameBlock(player.getLocation(), state.getLocation())) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("you.moved.teleport.cancelled", player));
                    iter.remove();
                    continue;
                }
                if (state.isTeleportTime()) {
                    teleport(state);
                    iter.remove();
                } else {
                    ChatBlock.sendMessage(player, ChatColor.AQUA + "" + state.getCounter());
                }

                state.setProcessing(false);
            }
        }, 0, 20L);
    }

    private void teleport(TeleportState state) {
        Player player = state.getPlayer();
        if (player == null) {
            return;
        }
        Location loc = state.getDestination();
        sendTeleportBlocks(player, loc);
        dropItems(player);
        loc.clone().add(.5, .5, .5);
        teleportToHome(player, loc, state.getClanName());
    }

    public void teleportToHome(@NotNull Player player, @NotNull Location destination, @NotNull String clanName) {
        PaperLib.teleportAsync(player, destination, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            if (result) {
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("now.at.homebase", player, clanName));
            } else {
                plugin.getLogger().log(Level.WARNING, "An error occurred while teleporting a player");
            }
        });
    }

    public void teleportToHome(@NotNull Player player, @NotNull Clan clan) {
        if (clan.getHomeLocation() == null) {
            return;
        }
        teleportToHome(player, clan.getHomeLocation(), clan.getName());
    }
}