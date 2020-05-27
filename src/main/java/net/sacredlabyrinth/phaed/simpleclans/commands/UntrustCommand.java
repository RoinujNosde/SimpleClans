package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

/**
 * @author phaed
 */
public class UntrustCommand {
    public UntrustCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust")) {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.untrust.player",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID trustedId = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (trustedId == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }
        if (trustedId.equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.untrust.yourself",player));
            return;
        }
        if (!clan.isMember(trustedId)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.not.a.member.of.your.clan",player));
            return;
        }
        if (clan.isLeader(trustedId)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("leaders.cannot.be.untrusted",player));
            return;
        }

        ClanPlayer tcp = plugin.getClanManager().getClanPlayer(trustedId);

        if (tcp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }
        if (!tcp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("this.player.is.already.untrusted",player));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("has.been.given.untrusted.status.by",player), arg[0], player.getName()));
        tcp.setTrusted(false);
        plugin.getStorageManager().updateClanPlayer(tcp);
    }
}







