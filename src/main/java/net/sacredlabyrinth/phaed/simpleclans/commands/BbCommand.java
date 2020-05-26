package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class BbCommand {

    public BbCommand() {
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

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            return;
        }

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bb")) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                return;
            }
            clan.displayBb(player);
            return;
        }

        if (arg.length == 1 && arg[0].equalsIgnoreCase("clear")) {
            if (!plugin.getPermissionsManager().has(player, RankPermission.BB_CLEAR, PermissionLevel.LEADER, true)) {
                return;
            }
            cp.getClan().clearBb();
            ChatBlock.sendMessage(player, ChatColor.RED + lang("cleared.bb",player));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, RankPermission.BB_ADD, PermissionLevel.TRUSTED, true)) {
            return;
        }

        String msg = Helper.toMessage(arg);
        clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
        plugin.getStorageManager().updateClan(clan);
    }
}