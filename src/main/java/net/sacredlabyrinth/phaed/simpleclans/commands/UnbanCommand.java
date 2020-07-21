package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

/**
 * @author phaed
 */
public class UnbanCommand {
    public UnbanCommand() {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.ban.unban",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (uuid == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }

        if (!plugin.getSettingsManager().isBanned(uuid)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("this.player.is.not.banned",player));
            return;
        }

        Player pl = Bukkit.getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, ChatColor.AQUA + lang("you.have.been.unbanned.from.clan.commands",player));
        }

        plugin.getSettingsManager().removeBanned(uuid);
        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("player.removed.from.the.banned.list",player));
    }
}



