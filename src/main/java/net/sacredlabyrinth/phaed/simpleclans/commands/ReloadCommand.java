package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ReloadCommand
{
    public ReloadCommand()
    {
    }

    /**
     * Execute the command
     *
     * @param sender
     * @param arg
     */
    public void execute(CommandSender sender, String[] arg)
    {
        SimpleClans plugin = SimpleClans.getInstance();

        if (sender instanceof Player && !plugin.getPermissionsManager().has((Player)sender, "simpleclans.admin.reload"))
        {
        	ChatBlock.sendMessage(sender, ChatColor.RED + "Does not match a clan command");
        	return;
        }
        
        plugin.getStorageManager().saveModified();
        plugin.reloadConfig();
        LanguageResource.clearCache();
        plugin.getSettingsManager().load();
        plugin.getStorageManager().importFromDatabase();
        SimpleClans.getInstance().getPermissionsManager().loadPermissions();

        for (Clan clan : plugin.getClanManager().getClans())
        {
            SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);
        }
        ChatBlock.sendMessage(sender, ChatColor.AQUA + lang("configuration.reloaded",sender));

    }
}
