package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.*;

public class ClanRoster extends Sendable {

    private final Clan clan;

    public ClanRoster(@NotNull SimpleClans plugin, @NotNull CommandSender sender, @NotNull Clan clan) {
        super(plugin, sender);
        this.clan = clan;
    }

    @Override
    public void send() {
        configureAndSendHeader();
        addLeaders();
        addMembers();

        sendBlock();
    }

    private void addMembers() {
        List<ClanPlayer> members = clan.getNonLeaders();
        plugin.getClanManager().sortClanPlayersByLastSeen(members);
        for (ClanPlayer cp : members) {
            Player p = cp.toPlayer();

            String name = (cp.isTrusted() ? sm.getColored(PAGE_TRUSTED_COLOR) : sm.getColored(PAGE_UNTRUSTED_COLOR)) + cp.getName();
            String lastSeen = p != null && p.isOnline() && !VanishUtils.isVanished(sender, p) ? GREEN + lang("online", sender) : WHITE + cp.getLastSeenDaysString(sender);

            chatBlock.addRow("  " + name, YELLOW + ChatUtils.parseColors(cp.getRankDisplayName()) + RESET, lastSeen);
        }
    }

    private void addLeaders() {
        List<ClanPlayer> leaders = clan.getLeaders();
        plugin.getClanManager().sortClanPlayersByLastSeen(leaders);
        for (ClanPlayer cp : leaders) {
            Player p = cp.toPlayer();

            String name = sm.getColored(PAGE_LEADER_COLOR) + cp.getName();
            String lastSeen = p != null && p.isOnline() && !VanishUtils.isVanished(sender, p) ? GREEN + lang("online", sender)
                    : WHITE + cp.getLastSeenDaysString(sender);

            chatBlock.addRow("  " + name, YELLOW + ChatUtils.parseColors(cp.getRankDisplayName()) + RESET, lastSeen);
        }
    }

    private void configureAndSendHeader() {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.getColored(PAGE_CLAN_NAME_COLOR) + clan.getName() + subColor + " " +
                lang("roster", sender) + " " + headColor + Helper.generatePageSeparator(sm.get(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("legend", sender) + " " + sm.getColored(PAGE_LEADER_COLOR) +
                lang("leader", sender) + headColor + ", " + sm.getColored(PAGE_TRUSTED_COLOR) + lang("trusted", sender) +
                headColor + ", " + sm.getColored(PAGE_UNTRUSTED_COLOR) + lang("untrusted", sender));
        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(false, true, false, true);
        chatBlock.addRow("  " + headColor + lang("player", sender), lang("rank", sender),
                lang("seen", sender));
    }

}
