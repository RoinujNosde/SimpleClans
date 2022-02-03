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
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * @author phaed
 */
public class SCPlayerListener implements Listener {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;

    public SCPlayerListener() {
        plugin = SimpleClans.getInstance();
        settingsManager = plugin.getSettingsManager();
        registerChatListener();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String[] split = event.getMessage().substring(1).split(" ");
        String command = split[0];

        if (settingsManager.is(CLANCHAT_TAG_BASED)) {
            Clan clan = plugin.getClanManager().getClan(command);
            if (clan == null || !clan.isMember(event.getPlayer())) {
                return;
            }
            String replaced = event.getMessage().replaceFirst(command, settingsManager.getString(COMMANDS_CLAN_CHAT));
            event.setMessage(replaced);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleChatTags(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (settingsManager.getStringList(BLACKLISTED_WORLDS).contains(getWorldName(event))) {
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
        String tagLabel = cp != null && cp.isTagEnabled() ? cp.getTagLabel() : null;
        if (settingsManager.is(CHAT_COMPATIBILITY_MODE) && settingsManager.is(DISPLAY_CHAT_TAGS)) {
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

        if (settingsManager.getStringList(BLACKLISTED_WORLDS).contains(getWorldName(event))) {
            return;
        }

        ClanPlayer cp;
        if (settingsManager.is(PERFORMANCE_USE_BUNGEECORD)) {
            cp = plugin.getClanManager().getClanPlayerJoinEvent(player);
        } else {
            cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
        }

        plugin.getStorageManager().updatePlayerNameAsync(player);
        plugin.getClanManager().updateLastSeen(player);
        plugin.getClanManager().updateDisplayName(player);

        if (cp == null) {
            return;
        }
        cp.setName(player.getName());

        plugin.getPermissionsManager().addPlayerPermissions(cp);

        if (settingsManager.is(BB_SHOW_ON_LOGIN) && cp.isBbEnabled() && cp.getClan() != null) {
            cp.getClan().displayBb(player, settingsManager.getInt(BB_LOGIN_SIZE));
        }

        plugin.getPermissionsManager().addClanPermissions(cp);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (!settingsManager.is(TELEPORT_HOME_ON_SPAWN) || settingsManager.getStringList(BLACKLISTED_WORLDS)
                .contains(getWorldName(event))) {
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
                            settingsManager.getMinutes(WAR_DISCONNECT_EXPIRATION_TIME));
                }
            });
        }
        if (settingsManager.getStringList(BLACKLISTED_WORLDS).contains(getWorldName(event))) {
            return;
        }


        plugin.getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (settingsManager.getStringList(BLACKLISTED_WORLDS).contains(getWorldName(event))) {
            return;
        }

        plugin.getClanManager().updateLastSeen(event.getPlayer());
    }

    private void registerChatListener() {
        EventPriority priority = EventPriority.valueOf(settingsManager.getString(CLANCHAT_LISTENER_PRIORITY));
        plugin.getServer().getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this, priority, (l, e) -> {
            if (!(e instanceof AsyncPlayerChatEvent)) {
                return;
            }
            AsyncPlayerChatEvent event = (AsyncPlayerChatEvent) e;
            Player player = event.getPlayer();
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
            if (cp == null || settingsManager.getStringList(BLACKLISTED_WORLDS).contains(getWorldName(event))) {
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
                plugin.getChatManager().processChat(SPIGOT, channel, cp, event.getMessage());
                event.setCancelled(true);
            }
        }, plugin, true);
    }

    private String getWorldName(PlayerEvent event) {
        return event.getPlayer().getWorld().getName();
    }
}
