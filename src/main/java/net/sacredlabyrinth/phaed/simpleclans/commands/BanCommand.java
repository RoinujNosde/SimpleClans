package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
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
public class BanCommand {
    public BanCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.ban")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, MessageFormat.format(lang("usage.ban.unban",player), ChatColor.RED, plugin.getSettingsManager().getCommandClan()));
            return;
        }

		UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);
		if (uuid == null) {
			ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
			return;
		}
		
		if (plugin.getSettingsManager().isBanned(uuid)) {
			ChatBlock.sendMessage(player, ChatColor.RED + lang("this.player.is.already.banned",player));
			return;
		}

		plugin.getClanManager().ban(uuid);
		ChatBlock.sendMessage(player, ChatColor.AQUA + lang("player.added.to.banned.list",player));

		Player pl = SimpleClans.getInstance().getServer().getPlayer(uuid);
		if (pl != null) {
			ChatBlock.sendMessage(pl, ChatColor.AQUA + lang("you.banned",player));
		}
    }
}
