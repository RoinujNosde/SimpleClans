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
public class TrustCommand {
    public TrustCommand() {
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
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.trust.player",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID trusted = UUIDMigration.getForcedPlayerUUID(arg[0]);
        if (trusted == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }
        if (trusted.equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.cannot.trust.yourself",player));
            return;
        }
        if (!clan.isMember(trusted)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.player.is.not.a.member.of.your.clan",player));
            return;
        }
        if (clan.isLeader(trusted)) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("leaders.are.already.trusted",player));
            return;
        }

        ClanPlayer tcp = plugin.getClanManager().getClanPlayer(trusted);

        if (tcp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }

        if (tcp.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("this.player.is.already.trusted",player));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("has.been.given.trusted.status.by",player), arg[0], player.getName()));
        tcp.setTrusted(true);
        plugin.getStorageManager().updateClanPlayer(tcp);
    }

}







