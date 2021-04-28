package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.NONE;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class SCPlayerListener implements Listener {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;

    public SCPlayerListener() {
        plugin = SimpleClans.getInstance();
        settingsManager = plugin.getSettingsManager();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().substring(1).split(" ");
        String command = split[0];

        if (settingsManager.isTagBasedClanChat()) {
            Clan clan = plugin.getClanManager().getClan(command);
            if (clan == null || !clan.isMember(event.getPlayer())) {
                return;
            }
            String replaced = event.getMessage().replaceFirst(command, settingsManager.getCommandClanChat());
            event.setMessage(replaced);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        if (cp == null || settingsManager.isBlacklistedWorld(player.getLocation().getWorld())) {
            return;
        }

        Channel channel = cp.getChannel();
        if (channel != NONE) {
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
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleChatTags(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (settingsManager.isBlacklistedWorld(player.getWorld())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
        String tagLabel = cp != null && cp.isTagEnabled() ? cp.getTagLabel() : null;

        if (settingsManager.isCompatMode() && settingsManager.isChatTags()) {
            if (tagLabel != null) {
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

        if (settingsManager.isBbShowOnLogin() && cp.isBbEnabled() && cp.getClan() != null) {
            cp.getClan().displayBb(player, settingsManager.getBbLoginSize());
        }

        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!settingsManager.isTeleportOnSpawn() || settingsManager.isBlacklistedWorld(player.getWorld())) {
            return;
        }

        Clan clan = plugin.getClanManager().getClanByPlayerUniqueId(player.getUniqueId());
        if (clan != null) {
            Location home = clan.getHomeLocation();
            if (home != null) {
                event.setRespawnLocation(home);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());
        if (cp != null) {
            Clan clan = Objects.requireNonNull(cp.getClan());
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (clan.getOnlineMembers().isEmpty()) {
                    plugin.getProtectionManager().setWarExpirationTime(cp.getClan(),
                            settingsManager.getWarDisconnectExpirationTime());
                }
            });
        }
        if (settingsManager.isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }


        SimpleClans.getInstance().getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (settingsManager.isBlacklistedWorld(event.getPlayer().getLocation().getWorld())) {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }
}
