package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class GlobalffCommand {
    public GlobalffCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(CommandSender player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.globalff.allow.auto",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];

        if (action.equalsIgnoreCase(lang("allow",player))) {
            if (plugin.getSettingsManager().isGlobalff()) {
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("global.friendly.fire.is.already.being.allowed",player));
            } else {
                plugin.getSettingsManager().setGlobalff(true);
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("global.friendly.fire.is.set.to.allowed",player));
            }
            return;
        }

        if (action.equalsIgnoreCase(lang("auto",player))) {
            if (!plugin.getSettingsManager().isGlobalff()) {
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("global.friendy.fire.is.already.being.managed.by.each.clan",player));
            } else {
                plugin.getSettingsManager().setGlobalff(false);
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("global.friendy.fire.is.now.managed.by.each.clan",player));
            }
            return;
        }
        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.globalff.allow.auto",player), plugin.getSettingsManager().getCommandClan()));
    }
}
