package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class WarCommand {
    public WarCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.war")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            return;
        }
        if (arg.length != 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.war",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];
        Clan war = plugin.getClanManager().getClan(arg[1]);

        if (war == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clan.matched",player));
            return;
        }
        if (!clan.isRival(war.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.can.only.start.war.with.rivals",player));
            return;
        }

        if (action.equals(lang("start",player))) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.WAR_START, PermissionLevel.LEADER, true)) {
            	return;
            }
            if (!clan.isWarring(war.getTag())) {
                List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

                if (!onlineLeaders.isEmpty()) {
                    plugin.getRequestManager().addWarStartRequest(cp, war, clan);
                    ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("leaders.have.been.asked.to.accept.the.war.request",player), war.getName()));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("at.least.one.leader.accept.the.alliance",player));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clans.already.at.war",player));
            }
            return;
        }

        if (action.equals(lang("end",player))) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.WAR_END, PermissionLevel.LEADER, true)) {
            	return;
            }
            if (clan.isWarring(war.getTag())) {
                plugin.getRequestManager().addWarEndRequest(cp, war, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("leaders.asked.to.end.rivalry",player), war.getName()));
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clans.not.at.war",player));
            }
            return;
        }

        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.war",player), plugin.getSettingsManager().getCommandClan()));
    }
}

