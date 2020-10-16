package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Iterator;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class SCPlayerListener implements Listener {

    private final SimpleClans plugin;

    public SCPlayerListener() {
        plugin = SimpleClans.getInstance();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().substring(1).split(" ");
        String command = split[0];

        if (plugin.getSettingsManager().isTagBasedClanChat()) {
            Clan clan = plugin.getClanManager().getClan(command);
            if (clan == null || !clan.isMember(event.getPlayer())) {
                return;
            }
            String replaced = event.getMessage().replaceFirst(command, plugin.getSettingsManager().getCommandClanChat());
            event.setMessage(replaced);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }

        String message = event.getMessage();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());

        if (cp != null && !event.isCancelled()) {
            if (cp.getChannel().equals(ClanPlayer.Channel.CLAN)) {
                event.setCancelled(true);
                if (!plugin.getPermissionsManager().has(cp.toPlayer(), "simpleclans.member.chat")) {
                    ChatBlock.sendMessage(cp.toPlayer(), ChatColor.RED + lang("insufficient.permissions",cp.toPlayer()));
                    return;
                }

                plugin.getClanManager().processClanChat(event.getPlayer(), message);
            } else if (cp.getChannel().equals(ClanPlayer.Channel.ALLY)) {
                event.setCancelled(true);
                if (!plugin.getPermissionsManager().has(cp.toPlayer(), "simpleclans.member.ally")) {
                    ChatBlock.sendMessage(cp.toPlayer(), ChatColor.RED + lang("insufficient.permissions",cp.toPlayer()));
                    return;
                }

                plugin.getClanManager().processAllyChat(event.getPlayer(), message);
            }
        }

        if (!plugin.getPermissionsManager().has(event.getPlayer(), "simpleclans.mod.nohide")) {
            boolean isClanChat = event.getMessage().contains("" + ChatColor.RED + ChatColor.WHITE + ChatColor.RED + ChatColor.BLACK);
            boolean isAllyChat = event.getMessage().contains("" + ChatColor.AQUA + ChatColor.WHITE + ChatColor.AQUA + ChatColor.BLACK);

            for (Iterator<Player> iter = event.getRecipients().iterator(); iter.hasNext();) {
                Player player = iter.next();

                ClanPlayer rcp = plugin.getClanManager().getClanPlayer(player);

                if (rcp != null) {
                    if (!rcp.isClanChat() && isClanChat) {
                        iter.remove();
                        continue;
                    }

                    if (!rcp.isAllyChat() && isAllyChat) {
                        iter.remove();
                        continue;
                    }

                    if (!rcp.isGlobalChat() && !isAllyChat && !isClanChat) {
                        iter.remove();
                    }
                }
            }
        }

        if (plugin.getSettingsManager().isCompatMode()) {
            if (plugin.getSettingsManager().isChatTags()) {
                if (cp != null && cp.isTagEnabled()) {
                    String tagLabel = cp.getClan().getTagLabel(cp.isLeader());

                    Player player = event.getPlayer();

                    if (player.getDisplayName().contains("{clan}")) {
                        player.setDisplayName(player.getDisplayName().replace("{clan}", tagLabel));
                    } else if (event.getFormat().contains("{clan}")) {
                        event.setFormat(event.getFormat().replace("{clan}", tagLabel));
                    } else {
                        String format = event.getFormat();
                        event.setFormat(tagLabel + format);
                    }
                } else {
                    event.setFormat(event.getFormat().replace("{clan}", ""));
                    event.setFormat(event.getFormat().replace("tagLabel", ""));
                }
            }
        } else {
            plugin.getClanManager().updateDisplayName(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (SimpleClans.getInstance().getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld())) {
            return;
        }

        ClanPlayer cp;
        if (SimpleClans.getInstance().getSettingsManager().getUseBungeeCord()) {
            cp = SimpleClans.getInstance().getClanManager().getClanPlayerJoinEvent(player);
        } else {
            cp = SimpleClans.getInstance().getClanManager().getAnyClanPlayer(player.getUniqueId());
        }

        SimpleClans.getInstance().getStorageManager().updatePlayerNameAsync(player);
        SimpleClans.getInstance().getClanManager().updateLastSeen(player);
        SimpleClans.getInstance().getClanManager().updateDisplayName(player);

        if (cp == null) {
            return;
        }
        cp.setName(player.getName());

        SimpleClans.getInstance().getPermissionsManager().addPlayerPermissions(cp);

        if (plugin.getSettingsManager().isBbShowOnLogin() && cp.isBbEnabled() && cp.getClan() != null) {
            cp.getClan().displayBb(player, plugin.getSettingsManager().getBbLoginSize());
        }

        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }

        if (plugin.getSettingsManager().isTeleportOnSpawn()) {
            Player player = event.getPlayer();

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Location loc = cp.getClan().getHomeLocation();

                if (loc != null) {
                    event.setRespawnLocation(loc);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());

        SimpleClans.getInstance().getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }
}
