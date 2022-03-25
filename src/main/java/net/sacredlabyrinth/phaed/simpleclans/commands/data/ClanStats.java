package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
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
        ChatBlock.saySingle(sender, sm.getColored(PAGE_CLAN_NAME_COLOR) + clan.getName() + subColor + " " +
                lang("stats", sender) + " " + headColor + Helper.generatePageSeparator(sm.getString(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("kdr", sender) + " = " + subColor +
                lang("kill.death.ratio", sender));
        ChatBlock.sendMessage(sender, headColor + lang("weights", sender) + " = " + lang("rival", sender)
                + ": " + subColor + sm.getDouble(KILL_WEIGHTS_RIVAL) + headColor + " " + lang("neutral", sender) + ": " + subColor +
                sm.getDouble(KILL_WEIGHTS_NEUTRAL) + headColor + " " + lang("civilian", sender) + ": " + subColor +
                sm.getDouble(KILL_WEIGHTS_CIVILIAN));
        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(true, false, false, false, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");
        chatBlock.addRow("  " + headColor + lang("name", sender), lang("kdr", sender),
                lang("rival", sender), lang("neutral", sender), lang("civilian.abbreviation", sender),
                lang("deaths", sender));
    }

    private void addRows(List<ClanPlayer> clanPlayers) {
        for (ClanPlayer cpm : clanPlayers) {
            String name = (cpm.isLeader() ? sm.getColored(PAGE_LEADER_COLOR) : (cpm.isTrusted() ? sm.getColored(PAGE_TRUSTED_COLOR) :
                    sm.getColored(PAGE_UNTRUSTED_COLOR)) + cpm.getName());
            String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
            String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
            String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
            String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
            String kdr = KDRFormat.format(cpm.getKDR());

            chatBlock.addRow("  " + name, YELLOW + kdr, WHITE + rival, GRAY + neutral, DARK_GRAY + civilian,
                    DARK_RED + deaths);
        }
    }

    protected void sendBlock() {
        SettingsManager sm = plugin.getSettingsManager();
        boolean more = chatBlock.sendBlock(sender, sm.getInt(PAGE_SIZE));

        if (more) {
            plugin.getStorageManager().addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, sm.getColored(PAGE_HEADINGS_COLOR) + lang("view.next.page", sender,
                    sm.getString(COMMANDS_MORE)));
        }
        ChatBlock.sendBlank(sender);
    }
}
