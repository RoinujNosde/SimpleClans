package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class InviteCommand {
    public InviteCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();



        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        Clan clan = cp.getClan();

        Player invited = Helper.getPlayer(arg[0]);


        ClanPlayer cpInv = plugin.getClanManager().getAnyClanPlayer(invited.getUniqueId());

        if (cpInv != null) {
        	if (cpInv.getClan() != null) {
        		ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.already.member.of.another.clan",player));
        		return;
        	}
        	if (plugin.getSettingsManager().isRejoinCooldown()) {
	        	Long resign = cpInv.getResignTime(clan.getTag());
	        	if (resign != null) {
	        		long timePassed = Instant.ofEpochMilli(resign).until(Instant.now(), ChronoUnit.MINUTES);
	        		int cooldown = plugin.getSettingsManager().getRejoinCooldown();
	        		if (timePassed < cooldown) {
	        			ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("the.player.must.wait.0.before.joining.your.clan.again",player), cooldown - timePassed));
	        			return;
	        		}
	        	}
        	}
        }
        
        if (!plugin.getClanManager().purchaseInvite(player)) {
            return;
        }
        if (clan.getSize() >= plugin.getSettingsManager().getMaxMembers()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.members.reached.limit",player));
            return;
        }
        
        plugin.getRequestManager().addInviteRequest(cp, invited.getName(), clan);
        ChatBlock.sendMessage(player, ChatColor.AQUA + MessageFormat.format(lang("has.been.asked.to.join",player), invited.getName(), clan.getName()));
    }
}
