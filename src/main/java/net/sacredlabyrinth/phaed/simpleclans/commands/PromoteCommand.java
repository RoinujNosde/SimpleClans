package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class PromoteCommand {
    public PromoteCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        PermissionsManager pm = plugin.getPermissionsManager();
        boolean isAdmin = pm.has(player, "simpleclans.admin.promote");

        if (!pm.has(player, "simpleclans.leader.promote") && !isAdmin) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions", player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isLeader(player) && !isAdmin) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.promote.member",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        Player promoted = Helper.getPlayer(arg[0]);

        if (promoted == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.member.to.be.promoted.must.be.online",player));
            return;
        }
        if (!pm.has(promoted, "simpleclans.leader.promotable")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.does.not.have.the.permissions.to.lead.a.clan",player));
            return;
        }
        if (promoted.getName().equals(player.getName()) && !isAdmin) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.promote.yourself",player));
            return;
        }
        if (!clan.isMember(promoted) && !isAdmin) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.not.a.member.of.your.clan",player));
            return;
        }
        if (clan.isLeader(promoted)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.already.a.leader",player));
            return;
        }
        
        if (plugin.getSettingsManager().isConfirmationForPromote() && clan.getLeaders().size() > 1 && !isAdmin) {
        	plugin.getRequestManager().addPromoteRequest(cp, promoted.getName(), clan);
			ChatBlock.sendMessage(player,
					ChatColor.AQUA + lang("promotion.vote.has.been.requested.from.all.leaders",player));
        	return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("promoted.to.leader"), promoted.getName()));
        clan.promote(promoted.getUniqueId());
    }
}

