package net.sacredlabyrinth.phaed.simpleclans.commands;

import io.papermc.lib.PaperLib;
import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import net.sacredlabyrinth.phaed.simpleclans.events.HomeRegroupEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerHomeSetEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author phaed
 */
public class HomeCommand {

    public HomeCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    @SuppressWarnings("deprecation")
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 2) {
            if (arg[0].equalsIgnoreCase("set") && plugin.getPermissionsManager().has(player, "simpleclans.mod.home")) {
                if (!plugin.getClanManager().purchaseHomeTeleportSet(player)) {
                    return;
                }

                Location loc = player.getLocation();
                Clan clan = plugin.getClanManager().getClan(arg[1]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.does.not.exist",player));
                    return;
                }

                clan.setHomeLocation(loc);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("hombase.mod.set",player), clan.getName()) + " " + ChatColor.YELLOW + Helper.toLocationString(loc));
                return;
            }

            if (arg[0].equalsIgnoreCase("tp") && plugin.getPermissionsManager().has(player, "simpleclans.mod.hometp")) {
                Clan clan = plugin.getClanManager().getClan(arg[1]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.does.not.exist",player));
                    return;
                }

                Location loc = clan.getHomeLocation();

                if (loc == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("hombase.not.set",player));
                    return;
                }

                PaperLib.teleportAsync(player, loc, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
                    if (result) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("now.at.homebase", player, clan.getName()));
                    } else {
                        plugin.getLogger().log(Level.WARNING, "An error occurred while teleporting a player");
                    }
                });
                return;
            }
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            return;
        }

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.HOME_TP, PermissionLevel.TRUSTED, true)) {
                return;
            }

            Location loc = clan.getHomeLocation();

            if (loc == null) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("hombase.not.set",player));
                return;
            }

            if (plugin.getClanManager().purchaseHomeTeleport(player)) {
                plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), clan.getName());
            }
            return;
        }

        if (arg[0].equalsIgnoreCase("set")) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.HOME_SET, PermissionLevel.LEADER, true)) {
                return;
            }
            if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home")) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("home.base.only.once",player));
                return;
            }

            PlayerHomeSetEvent homeSetEvent = new PlayerHomeSetEvent(clan, cp, player.getLocation());
            SimpleClans.getInstance().getServer().getPluginManager().callEvent(homeSetEvent);

            if (homeSetEvent.isCancelled() || !plugin.getClanManager().purchaseHomeTeleportSet(player)) {
                return;
            }

            clan.setHomeLocation(player.getLocation());
            ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("hombase.set",player), ChatColor.YELLOW + Helper.toLocationString(player.getLocation())));
            return;
        }

        if (arg[0].equalsIgnoreCase("clear")) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.HOME_SET, PermissionLevel.LEADER, true)) {
                return;
            }
            if (plugin.getSettingsManager().isHomebaseSetOnce() && clan.getHomeLocation() != null && !plugin.getPermissionsManager().has(player, "simpleclans.mod.home")) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("home.base.only.once",player));
                return;
            }

            clan.setHomeLocation(null);
            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("hombase.cleared",player));
            return;
        }

        if (arg[0].equalsIgnoreCase("regroup")) {
            if (!SimpleClans.getInstance().getSettingsManager().getAllowReGroupCommand()) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                return;
            }
            
            if (!plugin.getPermissionsManager().has(player, RankPermission.HOME_REGROUP, PermissionLevel.LEADER, true)) {
            	return;
            }
            
            Location loc;
            if (arg.length >= 2 && arg[1].equalsIgnoreCase("me")) {
                loc = player.getLocation();
            } else {
                loc = cp.getClan().getHomeLocation();
            }
            
            if(loc == null) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("hombase.not.set",player));
                return;
            }
            
            HomeRegroupEvent homeRegroupEvent = new HomeRegroupEvent(clan, cp, clan.getOnlineMembers(), loc);
            SimpleClans.getInstance().getServer().getPluginManager().callEvent(homeRegroupEvent);

            if (homeRegroupEvent.isCancelled() || !plugin.getClanManager().purchaseHomeRegroup(player)) {
                return;
            }

            List<ClanPlayer> members = clan.getOnlineMembers();
            for (ClanPlayer ccp : members) {
                Player pl = ccp.toPlayer();
                if (pl == null) {
                    continue;
                }
                int x = loc.getBlockX();
                int z = loc.getBlockZ();
                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                player.sendBlockChange(new Location(loc.getWorld(), x + 1, loc.getBlockY(), z - 1), Material.GLASS, (byte) 0);
                player.sendBlockChange(new Location(loc.getWorld(), x - 1, loc.getBlockY(), z + 1), Material.GLASS, (byte) 0);
                Random r = new Random();
                int xx = r.nextInt(2) - 1;
                int zz = r.nextInt(2) - 1;
                if (xx == 0 && zz == 0) {
                    xx = 1;
                }
                x = x + xx;
                z = z + zz;
                
                plugin.getTeleportManager().addPlayer(player, new Location(loc.getWorld(), x + .5, loc.getBlockY(), z + .5), clan.getName());
            }
            return;
        }
    }
}
