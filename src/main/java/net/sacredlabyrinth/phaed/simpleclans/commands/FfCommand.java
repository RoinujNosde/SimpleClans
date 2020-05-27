package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class FfCommand {
    public FfCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.ff")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.ff.allow.auto",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];

        if (action.equalsIgnoreCase(lang("allow",player))) {
            cp.setFriendlyFire(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("personal.friendly.fire.is.set.to.allowed",player));
            return;
        }

        if (action.equalsIgnoreCase(lang("auto",player))) {
            cp.setFriendlyFire(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("friendy.fire.is.now.managed.by.your.clan",player));
            return;
        }

        ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.ff.allow.auto",player), plugin.getSettingsManager().getCommandClan()));
    }
}
