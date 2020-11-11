package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Iterator;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
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

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
        Channel channel = cp != null && cp.getClan() != null ? cp.getChannel() : null;
        if (channel != null) {
            PermissionsManager pm = plugin.getPermissionsManager();
            if ((channel == Channel.ALLY && !pm.has(player, "simpleclans.member.ally")) ||
                    (channel == CLAN && !pm.has(player, "simpleclans.member.chat"))) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions", player));
                return;
            }
            switch (channel) {
                case CLAN:
                    plugin.getClanManager().processClanChat(player, event.getMessage());
                    break;
                case ALLY:
                    plugin.getClanManager().processAllyChat(player, event.getMessage());
            }
            event.setCancelled(true);
            return;
        }

        if (plugin.getSettingsManager().isCompatMode()) {
            if (plugin.getSettingsManager().isChatTags()) {
                if (cp != null && cp.isTagEnabled()) {

                    String tagLabel = cp.getClan().getTagLabel(cp.isLeader());

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
            plugin.getClanManager().updateDisplayName(player);
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
