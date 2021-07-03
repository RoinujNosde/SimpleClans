package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.*;

public class Leaderboard extends Sendable {

    private final RankingNumberResolver<ClanPlayer, BigDecimal> rankingResolver;
    private final List<ClanPlayer> clanPlayers;

    public Leaderboard(@NotNull SimpleClans plugin, @NotNull CommandSender sender) {
        super(plugin, sender);
        clanPlayers = cm.getAllClanPlayers();
        rankingResolver = new RankingNumberResolver<>(clanPlayers,
                c -> KDRFormat.toBigDecimal(c.getKDR()), false, sm.getRankingType());
    }

    @Override
    public void send() {
        configureAndSendHeader();
        addLines();
        sendBlock();
    }

    private void addLines() {
        for (ClanPlayer cp : clanPlayers) {
            boolean online = cp.toPlayer() != null;

            String name = (cp.isLeader() ? sm.getColor(PAGE_LEADER_COLOR) : (cp.isTrusted() ? sm.getColor(PAGE_TRUSTED_COLOR) :
                    sm.getColor(PAGE_UNTRUSTED_COLOR))) + cp.getName();
            String lastSeen = online ? GREEN + lang("online", sender) : WHITE + cp.getLastSeenDaysString(sender);

            String clanTag = WHITE + lang("free.agent", sender);

            if (cp.getClan() != null) {
                clanTag = cp.getClan().getColorTag();
            }

            chatBlock.addRow("  " + rankingResolver.getRankingNumber(cp),
                    name, YELLOW + "" + KDRFormat.format(cp.getKDR()), WHITE + clanTag, lastSeen);
        }
    }

    private void configureAndSendHeader() {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.get(SERVER_NAME) + subColor + " " + lang("leaderboard.command", sender)
                + " " + headColor + Helper.generatePageSeparator(sm.get(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("total.clan.players.0", sender, subColor +
                clanPlayers.size()));
        ChatBlock.sendBlank(sender);

        chatBlock.setAlignment("c", "l", "c", "c", "c", "c");
        chatBlock.addRow("  " + headColor + lang("rank", sender), lang("player", sender),
                lang("kdr", sender), lang("clan", sender), lang("seen", sender));
    }
}
