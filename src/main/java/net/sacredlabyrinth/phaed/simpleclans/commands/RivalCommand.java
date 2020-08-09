package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class RivalCommand {
    public RivalCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.rival")) {
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
        if (clan.isUnrivable()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("your.clan.cannot.create.rivals",player));
            return;
        }
        if (arg.length != 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.rival",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        if (clan.getSize() < plugin.getSettingsManager().getClanMinSizeToRival()) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("min.players.rivalries",player), plugin.getSettingsManager().getClanMinSizeToRival()));
            return;
        }

        String action = arg[0];
        Clan rival = plugin.getClanManager().getClan(arg[1]);

        if (rival == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clan.matched",player));
            return;
        }
        if (clan.getTag().equals(rival.getTag()))
        {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.rival.your.own.clan",player));
            return;
        }
            
        if (plugin.getSettingsManager().isUnrivable(rival.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.cannot.be.rivaled",player));
            return;
        }
        if (!rival.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("cannot.rival.an.unverified.clan",player));
            return;
        }

        if (action.equals(lang("add",player))) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.RIVAL_ADD, PermissionLevel.LEADER, true)) {
            	return;
            }
            if (!clan.reachedRivalLimit()) {
                if (!clan.isRival(rival.getTag())) {
                    clan.addRival(rival);
                    rival.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("has.initiated.a.rivalry"), clan.getName(), rival.getName()), false);
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("has.initiated.a.rivalry"), player.getName(), rival.getName()));
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("your.clans.are.already.rivals",player));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("rival.limit.reached",player));
            }
            return;
        }

        if (action.equals(lang("remove",player))) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.RIVAL_REMOVE, PermissionLevel.LEADER, true)) {
            	return;
            }
            if (clan.isRival(rival.getTag())) {
                plugin.getRequestManager().addRivalryBreakRequest(cp, rival, clan);
                ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("leaders.asked.to.end.rivalry",player), rival.getName()));
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("your.clans.are.not.rivals",player));
            }
            return;
        }

        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.rival",player), plugin.getSettingsManager().getCommandClan()));
    }
}







