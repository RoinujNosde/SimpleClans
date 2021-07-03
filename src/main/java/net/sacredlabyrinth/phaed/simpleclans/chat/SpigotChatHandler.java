package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.events.ChatEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.ALLY;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.BUNGEE;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class SpigotChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        ClanPlayer clanPlayer = message.getSender();

        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        List<ClanPlayer> receivers = new ArrayList<>();
        switch (message.getChannel()) {
            case ALLY:
                if (!plugin.getSettingsManager().is(ALLYCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(getOnlineAllyMembers(clan).stream().filter(allymember -> !allymember.isMutedAlly()).collect(Collectors.toList()));
                receivers.add(clanPlayer);
                break;
            case CLAN:
                if (!plugin.getSettingsManager().is(CLANCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(clan.getOnlineMembers().stream().filter(member -> !member.isMuted()).collect(Collectors.toList()));
        }
        message.setReceivers(new HashSet<>(receivers));

        /*
          TODO: Make it async, change Type to Channel in 3.0
        */

        new BukkitRunnable() {
            @Override
            public void run() {
                ChatEvent event = new ChatEvent(message.getContent(), clanPlayer, receivers, ChatEvent.Type.valueOf(message.getChannel().name()));
                Bukkit.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }

                String formattedMessage = formatMessage(message, event.getPlaceholders());
                plugin.getLogger().info(formattedMessage);

                for (ClanPlayer cp : receivers) {
                    ChatBlock.sendMessage(cp, formattedMessage);
                }
            }
        }.runTask(plugin);
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT || source == BUNGEE;
    }

    @Override
    public String formatMessage(SCMessage message) {
        return formatMessage(message, null);
    }

    public String formatMessage(SCMessage message, Map<String, String> placeholders) {
        SettingsManager sm = plugin.getSettingsManager();

        String leaderColor = sm.getColored(CLANCHAT_LEADER_COLOR);
        String memberColor = sm.getColored(ALLYCHAT_MEMBER_COLOR);
        String trustedColor = sm.getColored(CLANCHAT_LEADER_COLOR);
        String rank = message.getSender().getRankId().isEmpty() ? null : ChatUtils.parseColors(message.getSender().getRankDisplayName());
        String rankFormat = rank != null ? sm.getColored(CLANCHAT_RANK).replace("%rank%", rank) : "";
        String formattedMessage = replacePlaceholders(sm.get(CLANCHAT_FORMAT), message.getSender(), leaderColor, trustedColor, memberColor, rankFormat, message.getContent());

        if (message.getChannel() == ALLY) {
            leaderColor = sm.getColored(ALLYCHAT_LEADER_COLOR);
            memberColor = sm.getColored(ALLYCHAT_MEMBER_COLOR);
            trustedColor = sm.getColored(ALLYCHAT_TRUSTED_COLOR);
            rankFormat = rank != null ? sm.getColored(ALLYCHAT_FORMAT).replace("%rank%", rank) : "";
            formattedMessage = replacePlaceholders(sm.get(ALLYCHAT_FORMAT), message.getSender(), leaderColor, trustedColor, memberColor, rankFormat, message.getContent());

        }

        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                formattedMessage = formattedMessage.replace("%" + e.getKey() + "%", e.getValue());
            }
        }

        return formattedMessage;
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

    private Set<ClanPlayer> getOnlineAllyMembers(Clan clan) {
        return clan.getAllAllyMembers().stream().filter(clanPlayer -> clanPlayer.toPlayer() != null).collect(Collectors.toSet());
    }
}
