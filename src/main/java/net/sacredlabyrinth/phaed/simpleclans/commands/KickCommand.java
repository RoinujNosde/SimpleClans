package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
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
public class KickCommand {
    public KickCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.kick")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!plugin.getPermissionsManager().has(player, RankPermission.KICK, PermissionLevel.LEADER, true)) {
        	return;
        }

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.kick.player",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID kicked = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (kicked == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }
        if (kicked.equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.kick.yourself",player));
            return;
        }
        if (!clan.isMember(kicked)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.not.a.member.of.your.clan",player));
            return;
        }
        if (clan.isLeader(kicked)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.kick.another.leader",player));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("has.been.kicked.by",player), arg[0], player.getName()));
        clan.removePlayerFromClan(kicked);
    }
}
