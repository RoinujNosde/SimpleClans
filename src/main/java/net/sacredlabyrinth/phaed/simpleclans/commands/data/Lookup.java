package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

public class Lookup extends Sendable {

    private final UUID targetUuid;
    private final ClanPlayer target;
    private final Clan senderClan;
    private final Clan targetClan;

    public Lookup(@NotNull SimpleClans plugin, @NotNull CommandSender sender, @NotNull UUID targetUuid) {
        super(plugin, sender);
        this.targetUuid = targetUuid;
        target = cm.getAnyClanPlayer(targetUuid);
        ClanPlayer senderCp = !isPlayer() ? null : cm.getClanPlayer(getPlayer().getUniqueId());
        senderClan = senderCp == null ? null : senderCp.getClan();
        targetClan = target.getClan();
    }

    //TODO Test
    @Override
    public void send() {
        if (target != null) {
            String lookup = lang("player.lookup", sender)
                    .replace("%player_name%", target.getName())
                    .replace("%clan_name%", getClanName())
                    .replace("%player_rank%", Helper.parseColors(target.getRankDisplayName()))
                    .replace("%player_status%", getPlayerStatus())
                    .replace("%player_kdr%", KDRFormat.format(target.getKDR()))
                    .replace("%player_rival_kills%", String.valueOf(target.getRivalKills()))
                    .replace("%player_neutral_kills%", String.valueOf(target.getNeutralKills()))
                    .replace("%player_civilian_kills%", String.valueOf(target.getCivilianKills()))
                    .replace("%player_deaths%", String.valueOf(target.getDeaths()))
                    .replace("%player_join_date%", target.getJoinDateString())
                    .replace("%player_last_seen%", target.getLastSeenString(sender))
                    .replace("%player_past_clans%", target.getPastClansString(headColor + ", "))
                    .replace("%player_inactive_days%", String.valueOf(target.getInactiveDays()))
                    .replace("%player_max_inactive_days%", String.valueOf(sm.getPurgePlayers()))
                    .replace("%kill_type_line%", getKillTypeLine());
            sender.sendMessage(lookup);
        } else {
            ChatBlock.sendMessage(sender, RED + lang("no.player.data.found", sender));

            if (isOtherPlayer() && senderClan != null) {
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, lang("kill.type.civilian", sender, DARK_GRAY));
            }
        }
    }

    @NotNull
    private String getClanName() {
        String clanName = lang("none", sender);
        if (targetClan != null) {
            clanName = lang("player.lookup.clanname")
                    .replace("%clan_color_tag%", targetClan.getColorTag())
                    .replace("%clan_name%", targetClan.getName());
        }
        return clanName;
    }

    @NotNull
    private String getPlayerStatus() {
        return targetClan == null ? lang("free.agent", sender) : (target.isLeader() ?
                sm.getPageLeaderColor() + lang("leader", sender) : (target.isTrusted() ?
                sm.getPageTrustedColor() + lang("trusted", sender) : sm.getPageUnTrustedColor() +
                lang("untrusted", sender)));
    }

    @NotNull
    private String getKillTypeLine() {
        String killTypeLine = "";
        if (isOtherPlayer()) {
            String killType = GRAY + lang("neutral", sender);

            if (targetClan == null) {
                killType = DARK_GRAY + lang("civilian", sender);
            } else if (senderClan != null && senderClan.isRival(targetClan.getTag())) {
                killType = WHITE + lang("rival", sender);
            }

            killTypeLine = lang("player.lookup.killtype", sender).replace("%player_kill_type%", killType);
        }
        return killTypeLine;
    }

    private boolean isPlayer() {
        return sender instanceof Player;
    }

    private boolean isOtherPlayer() {
        if (isPlayer()) {
            return !getPlayer().getUniqueId().equals(targetUuid);
        }
        return true;
    }

    private Player getPlayer() {
        return (Player) sender;
    }
}
