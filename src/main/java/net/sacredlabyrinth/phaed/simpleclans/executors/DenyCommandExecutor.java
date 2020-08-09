package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class DenyCommandExecutor implements CommandExecutor {
    SimpleClans plugin;

    public DenyCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("banned",player));
            return false;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            Clan clan = cp.getClan();

            if (!clan.isLeader(player)) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
                return false;
            }
            if (!plugin.getRequestManager().hasRequest(clan.getTag())) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("nothing.to.deny",player));
                return false;
            }
            if (cp.getVote() != null) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("you.have.already.voted",player));
                return false;
            }

            clan.leaderAnnounce(ChatColor.RED + MessageFormat.format(lang("has.voted.to.deny",player), player.getName()));
			plugin.getRequestManager().deny(cp);
        } else {
            if (!plugin.getRequestManager().hasRequest(player.getName().toLowerCase())) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("nothing.to.deny",player));
                return false;
            }
            cp = plugin.getClanManager().getCreateClanPlayer(player.getUniqueId());
            
            plugin.getRequestManager().deny(cp);
        }

        return true;
    }
}
