package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;

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
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

public class DescriptionCommand {

	/**
	 * Execute the command
	 *
	 * @param player
	 * @param args
	 */
	public void execute(Player player, String[] args) {
		final SimpleClans plugin = SimpleClans.getInstance();
		final SettingsManager sm = plugin.getSettingsManager();

		if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.description")) {
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
        if (!plugin.getPermissionsManager().has(player, RankPermission.DESCRIPTION, PermissionLevel.LEADER, true)) {
        	return;
        }
        
        if (args.length < 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.description",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }
        
        String description = Helper.toMessage(args);
        if (description.length() < sm.getClanMinDescriptionLength()) {
        	ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("your.clan.description.must.be.longer.than",player), sm.getClanMinDescriptionLength()));
        	return;
        }
        if (description.length() > sm.getClanMaxDescriptionLength()) {
        	ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("your.clan.description.cannot.be.longer.than",player), sm.getClanMaxDescriptionLength()));
        	return;
        }

        clan.setDescription(description);
        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("description.changed",player));
        plugin.getStorageManager().updateClan(clan);
	}
}
