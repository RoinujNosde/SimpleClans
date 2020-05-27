package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
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
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isLeader(player)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
            return;
        }
        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.clanff",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        String action = arg[0];

        if (action.equalsIgnoreCase(lang("allow",player))) {
            clan.addBb(player.getName(), ChatColor.AQUA + lang("clan.wide.friendly.fire.is.allowed",player));
            clan.setFriendlyFire(true);
            plugin.getStorageManager().updateClan(clan);
            return;
        }

        if (!action.equalsIgnoreCase(lang("block",player))) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.clanff",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + lang("clan.wide.friendly.fire.blocked",player));
        clan.setFriendlyFire(false);
        plugin.getStorageManager().updateClan(clan);
    }
}
