package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author phaed
 */
public class DemoteCommand {

    public DemoteCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.demote.leader"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String demotedName = arg[0];
        UUID uuid = UUIDMigration.getForcedPlayerUUID(demotedName);

        if (uuid == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
            return;
        }
        ClanPlayer toDemote = plugin.getClanManager().getAnyClanPlayer(uuid);
        
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.demote")) {
            if (!toDemote.isLeader()) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.any.clan"));
                return;
            }
            final Clan clan = toDemote.getClan();

            if (clan.getLeaders().size() == 1) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.cannot.demote.the.last.leader"));
                return;
            }

            clan.demote(toDemote.getUniqueId());
                
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(plugin.getLang("demoted.back.to.member"), demotedName));
            ChatBlock.sendMessage(player, ChatColor.AQUA + plugin.getLang("player.successfully.demoted"));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.leader.permissions"));
            return;
        }        

        if (!clan.enoughLeadersOnlineToDemote(toDemote)) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.enough.leaders.online.to.vote.on.demotion"));
            return;
        }

		if (!clan.isLeader(uuid)) {
			ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("player.is.not.a.leader.of.your.clan"));
			return;
		}
		if (clan.getLeaders().size() > 2 && plugin.getSettingsManager().isConfirmationForDemote()) {
			plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
			ChatBlock.sendMessage(player,
					ChatColor.AQUA + plugin.getLang("demotion.vote.has.been.requested.from.all.leaders"));
			return;
		}
		clan.addBb(player.getName(), ChatColor.AQUA
				+ MessageFormat.format(plugin.getLang("demoted.back.to.member"), demotedName));
		clan.demote(uuid);
    }
}
