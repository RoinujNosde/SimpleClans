package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
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

            String name = (cp.isTrusted() ? sm.getPageTrustedColor() : sm.getPageUnTrustedColor()) + cp.getName();
            String lastSeen = p != null && p.isOnline() && !Helper.isVanished(sender, p) ? GREEN + lang("online", sender) : WHITE + cp.getLastSeenDaysString(sender);

            chatBlock.addRow("  " + name, YELLOW + Helper.parseColors(cp.getRankDisplayName()) + RESET, lastSeen);
        }
    }

    private void addLeaders() {
        List<ClanPlayer> leaders = clan.getLeaders();
        plugin.getClanManager().sortClanPlayersByLastSeen(leaders);
        for (ClanPlayer cp : leaders) {
            Player p = cp.toPlayer();

            String name = sm.getPageLeaderColor() + cp.getName();
            String lastSeen = p != null && p.isOnline() && !Helper.isVanished(sender, p) ? GREEN + lang("online", sender)
                    : WHITE + cp.getLastSeenDaysString(sender);

            chatBlock.addRow("  " + name, YELLOW + Helper.parseColors(cp.getRankDisplayName()) + RESET, lastSeen);
        }
    }

    private void configureAndSendHeader() {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.getPageClanNameColor() + clan.getName() + subColor + " " +
                lang("roster", sender) + " " + headColor + Helper.generatePageSeparator(sm.getPageSep()));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("legend", sender) + " " + sm.getPageLeaderColor() +
                lang("leader", sender) + headColor + ", " + sm.getPageTrustedColor() + lang("trusted", sender) +
                headColor + ", " + sm.getPageUnTrustedColor() + lang("untrusted", sender));
        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(false, true, false, true);
        chatBlock.addRow("  " + headColor + lang("player", sender), lang("rank", sender),
                lang("seen", sender));
    }

}
