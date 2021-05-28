package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import net.sacredlabyrinth.phaed.simpleclans.events.ChatEvent;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public final class ChatManager {

    private final SimpleClans plugin;

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
    }

    public void processChat(@NotNull ClanPlayer.Channel channel, @NotNull ClanPlayer clanPlayer, String message) {
        if (message.isEmpty()) {
            return;
        }

        String command = message.split(" ")[0];
        String channelLower = channel.name().toLowerCase();
        Channel playerChannel = clanPlayer.getChannel();

        if (channel == Channel.CLAN || channel == Channel.ALLY) {
            if (command.equals(lang("join", clanPlayer))) {
                if (playerChannel != channel) {
                    clanPlayer.setChannel(channel);
                    plugin.getStorageManager().updateClanPlayer(clanPlayer);
                    ChatBlock.sendMessage(clanPlayer, lang("joined." + channelLower + ".chat"));
                } else {
                    ChatBlock.sendMessage(clanPlayer, lang("already.joined." + channelLower + ".chat"));
                }
            } else if (command.equals(lang("leave", clanPlayer))) {
                clanPlayer.setChannel(ClanPlayer.Channel.NONE);
                plugin.getStorageManager().updateClanPlayer(clanPlayer);
                ChatBlock.sendMessage(clanPlayer, lang("left." + channelLower + ".chat", clanPlayer));
            } else if (command.equals(lang("mute", clanPlayer))) {
                if (!clanPlayer.isMuted()) {
                    clanPlayer.mute(channel, true);
                    ChatBlock.sendMessage(clanPlayer, lang("muted." + channelLower + ".chat", clanPlayer));
                } else {
                    clanPlayer.mute(channel, false);
                    ChatBlock.sendMessage(clanPlayer, lang("unmuted." + channelLower + ".chat", clanPlayer));
                }
            } else {
                Clan clan = clanPlayer.getClan();
                if (clan == null) {
                    return;
                }

                List<ClanPlayer> receivers = clan.getOnlineMembers().stream().filter(member -> !member.isMuted()).collect(Collectors.toList());
                if (channel == Channel.ALLY) {
                    receivers.addAll(clan.getOnlineAllyMembers().stream().filter(allymember -> !allymember.isMutedAlly()).collect(Collectors.toList()));
                }

            /*
              TODO: Make it async, change Type to Channel
              (and move it to another method, probably?)
             */
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ChatEvent event = new ChatEvent(message, clanPlayer, receivers, ChatEvent.Type.ALLY);
                        Bukkit.getServer().getPluginManager().callEvent(event);

                        if (event.isCancelled()) {
                            return;
                        }

                        String message = formatChat(channel, clanPlayer, event.getMessage(), event.getPlaceholders());
                        String eyeMessage = formatSpyClanChat(clanPlayer, message);
                        plugin.getLogger().info(message);

                        for (ClanPlayer cp : receivers) {
                            ChatBlock.sendMessage(cp, message);
                        }

                        sendToAllSeeing(eyeMessage, receivers);
                    }
                }.runTask(plugin);
            }
        }
    }

    private String formatChat(Channel channel, ClanPlayer cp, String msg, Map<String, String> placeholders) {
        SettingsManager sm = plugin.getSettingsManager();

        String leaderColor = sm.getClanChatLeaderColor();
        String memberColor = sm.getClanChatMemberColor();
        String trustedColor = sm.getClanChatTrustedColor();

        if (channel == Channel.ALLY) {
            leaderColor = sm.getAllyChatLeaderColor();
            memberColor = sm.getAllyChatMemberColor();
            trustedColor = sm.getClanChatTrustedColor();
        }

        String rank = cp.getRankId().isEmpty() ? null : ChatUtils.parseColors(cp.getRankDisplayName());
        String rankFormat = rank != null ? ChatUtils.parseColors(sm.getClanChatRank()).replace("%rank%", rank) : "";

        String message = replacePlaceholders(sm.getClanChatFormat(), cp, leaderColor, trustedColor, memberColor, rankFormat, msg);

        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                message = message.replace("%" + e.getKey() + "%", e.getValue());
            }
        }
        return message;
    }

    private String formatSpyClanChat(ClanPlayer cp, String msg) {
        msg = ChatUtils.stripColors(msg);

        if (msg.contains(ChatUtils.stripColors(cp.getClan().getColorTag()))) {
            return ChatColor.DARK_GRAY + msg;
        } else {
            return ChatColor.DARK_GRAY + "[" + cp.getTag() + "] " + msg;
        }
    }

    private void sendToAllSeeing(String msg, List<ClanPlayer> cps) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")) {
                boolean alreadySent = false;

                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null && cp.isMuted()) {
                    continue;
                }

                for (ClanPlayer cpp : cps) {
                    if (cpp.getName().equalsIgnoreCase(player.getName())) {
                        alreadySent = true;
                    }
                }

                if (!alreadySent) {
                    ChatBlock.sendMessage(player, msg);
                }
            }
        }
    }

    private String replacePlaceholders(String messageFormat,
                                       ClanPlayer cp,
                                       String leaderColor,
                                       String trustedColor,
                                       String memberColor,
                                       String rankFormat,
                                       String msg) {
        return ChatUtils.parseColors(messageFormat)
                .replace("%clan%", Objects.requireNonNull(cp.getClan()).getColorTag())
                .replace("%nick-color%", (cp.isLeader() ? leaderColor : cp.isTrusted() ? trustedColor : memberColor))
                .replace("%player%", cp.getName())
                .replace("%rank%", rankFormat)
                .replace("%message%", msg);
    }
}
