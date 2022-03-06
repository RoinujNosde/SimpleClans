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
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.NONE;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * @author phaed
 */
public class SCPlayerListener extends SCListener {

    private final SettingsManager settingsManager;

    public SCPlayerListener(@NotNull SimpleClans plugin) {
        super(plugin);
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
        if (isBlacklistedWorld(player)) {
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
        if (isBlacklistedWorld(player)) {
            return;
        }

        ClanPlayer cp;
        if (settingsManager.is(PERFORMANCE_USE_BUNGEECORD)) {
            cp = plugin.getClanManager().getClanPlayerJoinEvent(player);
        } else {
            cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
        }

        updatePlayerName(cp);
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
        if (!settingsManager.is(TELEPORT_HOME_ON_SPAWN) || isBlacklistedWorld(player)) {
            return;
        }

        Clan clan = plugin.getClanManager().getClanByPlayerUniqueId(player.getUniqueId());
        Location home;
        if (clan != null && (home = clan.getHomeLocation()) != null) {
            event.setRespawnLocation(plugin.getTeleportManager().getSafe(home));
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
        if (isBlacklistedWorld(event.getPlayer())) {
            return;
        }


        plugin.getPermissionsManager().removeClanPlayerPermissions(cp);
        plugin.getClanManager().updateLastSeen(event.getPlayer());
        plugin.getRequestManager().endPendingRequest(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (isBlacklistedWorld(event.getPlayer())) {
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
            if (cp == null || isBlacklistedWorld(player)) {
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

    private void updatePlayerName(@Nullable ClanPlayer cp) {
        if (cp == null) {
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            ClanPlayer duplicate = null;
            for (ClanPlayer other : plugin.getClanManager().getAllClanPlayers()) {
                if (other.getName().equals(cp.getName()) && !other.getUniqueId().equals(cp.getUniqueId())) {
                    duplicate = other;
                    break;
                }
            }

            if (duplicate != null) {
                plugin.getLogger().warning(String.format("Found duplicate for %s, UUIDs: %s, %s", cp.getName(),
                        cp.getUniqueId(), duplicate.getUniqueId()));
                duplicate.setName(duplicate.getName() + "_duplicate");
                plugin.getStorageManager().updatePlayerName(duplicate);
            }
            plugin.getStorageManager().updatePlayerName(cp);
        });
    }

}
