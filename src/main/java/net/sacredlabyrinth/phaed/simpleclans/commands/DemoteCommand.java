package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.demote.leader", player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String demotedName = arg[0];
        UUID uuid = UUIDMigration.getForcedPlayerUUID(demotedName);

        if (uuid == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched", player));
            return;
        }
        ClanPlayer toDemote = plugin.getClanManager().getAnyClanPlayer(uuid);
        if (toDemote == null || toDemote.getClan() == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("player.is.not.a.leader.of.any.clan", player));
            return;
        }
        
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.demote")) {
            final Clan clan = toDemote.getClan();

            if (clan.getLeaders().size() == 1) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.demote.the.last.leader", player));
                return;
            }

            clan.demote(toDemote.getUniqueId());
                
            clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("demoted.back.to.member"), demotedName));
            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("player.successfully.demoted", player));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions", player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan", player));
            return;
        }

        Clan clan = cp.getClan();

        //noinspection ConstantConditions
        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions", player));
            return;
        }        

        if (!clan.enoughLeadersOnlineToDemote(toDemote)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.enough.leaders.online.to.vote.on.demotion", player));
            return;
        }

		if (!clan.isLeader(uuid)) {
			ChatBlock.sendMessage(player, ChatColor.RED + lang("player.is.not.a.leader.of.your.clan", player));
			return;
		}
		if (clan.getLeaders().size() > 2 && plugin.getSettingsManager().isConfirmationForDemote()) {
			plugin.getRequestManager().addDemoteRequest(cp, demotedName, clan);
			ChatBlock.sendMessage(player,
					ChatColor.AQUA + lang("demotion.vote.has.been.requested.from.all.leaders", player));
			return;
		}
		clan.addBb(player.getName(), ChatColor.AQUA
				+ MessageFormat.format(lang("demoted.back.to.member",player), demotedName));
		clan.demote(uuid);
    }
}
