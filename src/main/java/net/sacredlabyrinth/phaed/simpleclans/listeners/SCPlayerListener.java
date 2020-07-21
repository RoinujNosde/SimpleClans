package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.executors.*;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.Iterator;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;

/**
 * @author phaed
 */
public class SCPlayerListener implements Listener {

    private SimpleClans plugin;

    /**
     *
     */
    public SCPlayerListener() {
        plugin = SimpleClans.getInstance();
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (player == null) {
            return;
        }

        if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        if (event.getMessage().length() == 0) {
            return;
        }

        String[] split = event.getMessage().substring(1).split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (plugin.getSettingsManager().isTagBasedClanChat() && plugin.getClanManager().isClan(command)) {
            if (!plugin.getSettingsManager().getClanChatEnable()) {
                return;
            }

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp == null) {
                return;
            }

            if (cp.getTag().equalsIgnoreCase(command)) {
                event.setCancelled(true);
                if (!plugin.getPermissionsManager().has(cp.toPlayer(), "simpleclans.member.chat")) {
                    ChatBlock.sendMessage(cp.toPlayer(), ChatColor.RED + lang("insufficient.permissions",cp.toPlayer()));
                    return;
                }

                if (split.length > 1) {
                    plugin.getClanManager().processClanChat(player, cp.getTag(), Helper.toMessage(Helper.removeFirst(split)));
                }
            }
        }
        if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandClanChat())) {
            if (!plugin.getSettingsManager().getClanChatEnable()) {
                return;
            }

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp == null) {
                return;
            }

            event.setCancelled(true);

            if (!plugin.getPermissionsManager().has(cp.toPlayer(), "simpleclans.member.chat")) {
                ChatBlock.sendMessage(cp.toPlayer(), ChatColor.RED + lang("insufficient.permissions",cp.toPlayer()));
                return;
            }
            if (split.length > 1) {
                plugin.getClanManager().processClanChat(player, cp.getTag(), Helper.toMessage(Helper.removeFirst(split)));
            }
        }

        if (plugin.getSettingsManager().isForceCommandPriority()) {
            if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandAlly())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandAlly()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandAlly()))) {
                    new AllyCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            } else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandGlobal())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandGlobal()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandGlobal()))) {
                    new GlobalCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            } else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandClan())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandClan()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandClan()))) {
                    new ClanCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            } else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandAccept())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandAccept()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandAccept()))) {
                    new AcceptCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            } else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandDeny())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandDeny()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandDeny()))) {
                    new DenyCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            } else if (command.equalsIgnoreCase(plugin.getSettingsManager().getCommandMore())) {
                if (!plugin.getServer().getPluginCommand(plugin.getSettingsManager().getCommandMore()).equals(plugin.getCommand(plugin.getSettingsManager().getCommandMore()))) {
                    new MoreCommandExecutor().onCommand(player, null, null, Helper.removeFirst(split));
                    event.setCancelled(true);
                }
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName())) {
            return;
        }

        if (event.getPlayer() == null) {
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

    /**
     * @param event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (SimpleClans.getInstance().getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
            return;
        }

        ClanPlayer cp;
        if (SimpleClans.getInstance().getSettingsManager().getUseBungeeCord()) {
            cp = SimpleClans.getInstance().getClanManager().getClanPlayerJoinEvent(player);
        } else {
            cp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);
        }

        SimpleClans.getInstance().getStorageManager().updatePlayerNameAsync(player);
        SimpleClans.getInstance().getClanManager().updateLastSeen(player);
        SimpleClans.getInstance().getClanManager().updateDisplayName(player);

        if (cp == null) {
            return;
        }
        cp.setName(player.getName());

        SimpleClans.getInstance().getPermissionsManager().addPlayerPermissions(cp);

        if (plugin.getSettingsManager().isBbShowOnLogin() && cp.isBbEnabled()) {
            cp.getClan().displayBb(player, plugin.getSettingsManager().getBbLoginSize());
        }

        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);

        if (event.getPlayer().isOp()) {
            for (String message : SimpleClans.getInstance().getMessages()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + message);
            }
        }
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName())) {
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

    /**
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName())) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(event.getPlayer());

        SimpleClans.getInstance().getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    /**
     * @param event
     */
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (plugin.getSettingsManager().isBlacklistedWorld(event.getPlayer().getLocation().getWorld().getName())) {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }
}
