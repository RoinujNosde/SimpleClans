package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class SetRankCommand {
    public SetRankCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

		ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.rank.create",player),
        		plugin.getSettingsManager().getCommandClan()));
        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.rank.assign",player),
        		plugin.getSettingsManager().getCommandClan()));
        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.rank.setdisplayname",player),
        		plugin.getSettingsManager().getCommandClan()));
    }
}





