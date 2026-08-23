package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanPlayerTeleportEvent;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

/**
 * Class responsible for managing teleports and its queue
 */
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
        PermissionsManager pm = plugin.getPermissionsManager();

        int secs = SimpleClans.getInstance().getSettingsManager().getInt(CLAN_TELEPORT_DELAY);
        if (pm.has(player, "simpleclans.mod.bypass") || pm.has(player, "simpleclans.vip.teleport-delay")) {
            secs = 0;
        }
        waitingPlayers.put(player.getUniqueId().toString(), new TeleportState(player, destination, clanName, secs));

        if (secs > 0) {
            ChatBlock.sendMessage(player, AQUA + lang("waiting.for.teleport.stand.still.for.0.seconds", player, secs));
        }
    }

    /**
     * Teleports all online and non-vanished members of this {@link Clan} to the specified {@link Location}
     *
     * @param requester the Player requesting the teleport
     * @param clan      the Clan
     * @param location  the Location
     */
    public void teleport(@NotNull Player requester, @NotNull Clan clan, @NotNull Location location) {
        teleport(clan, location, VanishUtils.getNonVanished(requester, clan));
    }

    /**
     * Teleports all online and non-vanished members of this {@link Clan} to the specified {@link Location}
     *
     * @param clan     the Clan
     * @param location the Location
     */
    public void teleport(Clan clan, Location location) {
        teleport(clan, location, VanishUtils.getNonVanished(null, clan));
    }

    public void teleportToHome(@NotNull Player player, @NotNull Location destination, @NotNull String clanName) {
        plugin.getScheduler().teleportAsync(player, getSafe(destination), PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            if (result) {
                ChatBlock.sendMessage(player, AQUA + lang("now.at.homebase", player, clanName));
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

    private boolean isSameBlock(Location loc, Location loc2) {
        return loc.getBlockX() == loc2.getBlockX() && loc.getBlockY() == loc2.getBlockY() &&
                loc.getBlockZ() == loc2.getBlockZ();
    }

    /**
     * Converts the specified {@link Location} to a safe one, i.e. where there is no risk of suffocation
     *
     * @param location the Location
     * @return the safe Location
     */
    public @NotNull Location getSafe(@NotNull Location location) {
        int counter = 0;
        while (counter < 256) { //max world height
            counter++;
            Block bottom = location.getBlock();
            Block top = location.add(0, 1, 0).getBlock();
            if (!isAir(bottom)) {
                continue;
            }
            if (!isAir(top)) {
                location.add(0, 1, 0); //skips checking the same block again
                continue;
            }
            location.subtract(0, 1, 0); //remove what was added above
            return location;
        }

        //noinspection ConstantConditions
        location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
        return location;
    }

    private void dropItems(Player player) {
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.keep-items")) {
            return;
        }
        List<Material> itemsList = plugin.getSettingsManager().getItemList();
        PlayerInventory inv = player.getInventory();
        boolean dropOnHome = plugin.getSettingsManager().is(DROP_ITEMS_ON_CLAN_HOME);
        boolean keepOnHome = plugin.getSettingsManager().is(KEEP_ITEMS_ON_CLAN_HOME);
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
        plugin.getScheduler().runTimer(() -> {
            waitingPlayers.values().removeIf(ts -> ts.getPlayer() == null);
            for (Iterator<TeleportState> iter = waitingPlayers.values().iterator(); iter.hasNext(); ) {
                TeleportState state = iter.next();
                Player player = state.getPlayer();
                if (state.isProcessing() || player == null) {
                    continue;
                }
                state.setProcessing(true);

                if (!isSameBlock(player.getLocation(), state.getLocation())) {
                    ChatBlock.sendMessage(player, RED + lang("you.moved.teleport.cancelled", player));
                    iter.remove();
                    continue;
                }
                if (state.isTeleportTime()) {
                    teleport(state);
                    iter.remove();
                } else {
                    ChatBlock.sendMessage(player, AQUA + "" + state.getCounter());
                }

                state.setProcessing(false);
            }
        }, 0, 20);
    }

    private void teleport(TeleportState state) {
        Player player = state.getPlayer();
        if (player == null) {
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getCreateClanPlayer(player.getUniqueId());
        ClanPlayerTeleportEvent event = new ClanPlayerTeleportEvent(cp, state.getLocation(), state.getDestination());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Location loc = state.getDestination();
        sendTeleportBlocks(player, loc);
        dropItems(player);
        loc.clone().add(.5, .5, .5);
        teleportToHome(player, loc, state.getClanName());
    }

    @SuppressWarnings("deprecation")
    private void sendTeleportBlocks(Player player, Location loc) {
        int x = loc.getBlockX();
        int z = loc.getBlockZ();

        if (plugin.getSettingsManager().is(TELEPORT_BLOCKS)) {
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

    private void teleport(Clan clan, Location location, List<ClanPlayer> members) {
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
                    location.getBlockY(), z + .5, location.getYaw(), location.getPitch()), clan.getName());
        }
    }

    /**
     * Checks if all passed blocks are some kind of AIR
     *
     * @param blocks blocks to test
     * @return true if all blocks are AIR
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isAir(@NotNull Block... blocks) {
        for (Block b : blocks) {
            if (!b.getType().name().contains("AIR")) {
                return false;
            }
        }
        return true;
    }

}