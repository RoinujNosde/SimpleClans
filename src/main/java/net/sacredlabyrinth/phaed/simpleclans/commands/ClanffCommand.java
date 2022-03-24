package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class ClanffCommand {
    public ClanffCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.ff")) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
            return;
        }

        Clan clan = cp.getClan();

        if (!plugin.getPermissionsManager().has(player, RankPermission.FRIENDLYFIRE, true)) {
            return;
        }

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];

        if (action.equalsIgnoreCase(plugin.getLang("allow"))) {
            clan.addBb(player.getName(), ChatColor.AQUA + plugin.getLang("clan.wide.friendly.fire.is.allowed"));
            clan.setFriendlyFire(true);
            plugin.getStorageManager().updateClan(clan);
            return;
        }

        if (!action.equalsIgnoreCase(plugin.getLang("block"))) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("usage.clanff"), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + plugin.getLang("clan.wide.friendly.fire.blocked"));
        clan.setFriendlyFire(false);
        plugin.getStorageManager().updateClan(clan);
    }
}
