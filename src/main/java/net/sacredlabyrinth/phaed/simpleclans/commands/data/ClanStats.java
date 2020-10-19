package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

public class ClanStats extends Sendable {

    private final Clan clan;

    public ClanStats(@NotNull SimpleClans plugin, @NotNull CommandSender sender, @NotNull Clan clan) {
        super(plugin, sender);
        this.clan = clan;
    }

    @Override
    public void send() {
        configureAndSendHeader();

        List<ClanPlayer> leaders = clan.getLeaders();
        cm.sortClanPlayersByKDR(leaders);
        addRows(leaders);

        List<ClanPlayer> members = clan.getNonLeaders();
        cm.sortClanPlayersByKDR(members);
        addRows(members);

        sendBlock();
    }

    private void configureAndSendHeader() {
        ChatBlock.saySingle(sender, sm.getPageClanNameColor() + clan.getName() + subColor + " " +
                lang("stats", sender) + " " + headColor + Helper.generatePageSeparator(sm.getPageSep()));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("kdr", sender) + " = " + subColor +
                lang("kill.death.ratio", sender));
        ChatBlock.sendMessage(sender, headColor + lang("weights", sender) + " = " + lang("rival", sender)
                + ": " + subColor + sm.getKwRival() + headColor + " " + lang("neutral", sender) + ": " + subColor +
                sm.getKwNeutral() + headColor + " " + lang("civilian", sender) + ": " + subColor +
                sm.getKwCivilian());
        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(true, false, false, false, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");
        chatBlock.addRow("  " + headColor + lang("name", sender), lang("kdr", sender),
                lang("rival", sender), lang("neutral", sender), lang("civilian.abbreviation", sender),
                lang("deaths", sender));
    }

    private void addRows(List<ClanPlayer> clanPlayers) {
        for (ClanPlayer cpm : clanPlayers) {
            String name = (cpm.isLeader() ? sm.getPageLeaderColor() : (cpm.isTrusted() ? sm.getPageTrustedColor() :
                    sm.getPageUnTrustedColor())) + cpm.getName();
            String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
            String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
            String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
            String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
            String kdr = KDRFormat.format(cpm.getKDR());

            chatBlock.addRow("  " + name, YELLOW + kdr, WHITE + rival, GRAY + neutral, DARK_GRAY + civilian,
                    DARK_RED + deaths);
        }
    }
}
