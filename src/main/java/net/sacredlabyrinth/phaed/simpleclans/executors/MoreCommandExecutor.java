package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

public class MoreCommandExecutor implements CommandExecutor {
    SimpleClans plugin;

    public MoreCommandExecutor() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("banned",player));
            return false;
        }

        ChatBlock chatBlock = plugin.getStorageManager().getChatBlock(player);

        if (chatBlock == null || chatBlock.size() <= 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("nothing.more.to.see",player));
            return false;
        }

        chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (chatBlock.size() > 0) {
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, plugin.getSettingsManager().getPageHeadingsColor() + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }
        ChatBlock.sendBlank(player);
        return true;
    }
}
