package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class ModtagCommand {
    public ModtagCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.modtag")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

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
        if (!plugin.getPermissionsManager().has(player, RankPermission.MODTAG, PermissionLevel.LEADER, true)) {
        	return;
        }

        if (arg.length != 1) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.modtag.tag",player), plugin.getSettingsManager().getCommandClan()));
            ChatBlock.sendMessage(player, ChatColor.RED + lang("example.clan.modtag.4kfo.4l",player));
            return;
        }

        String newtag = arg[0];
        String cleantag = Helper.cleanTag(newtag);

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.coloredtag") && newtag.contains("&")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("your.tag.cannot.contain.color.codes",player));
            return;
        }
        if (Helper.stripColors(newtag).length() > plugin.getSettingsManager().getTagMaxLength()) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("your.clan.tag.cannot.be.longer.than.characters",player), plugin.getSettingsManager().getTagMaxLength()));
            return;
        }
        if (plugin.getSettingsManager().hasDisallowedColor(newtag)) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("your.tag.cannot.contain.the.following.colors",player), plugin.getSettingsManager().getDisallowedColorString()));
            return;
        }
        if (!Helper.stripColors(newtag).matches("[0-9a-zA-Z]*")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes",player));
            return;
        }
        if (!cleantag.equals(clan.getTag())) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.can.only.modify.the.color.and.case.of.the.tag",player));
            return;
        }

        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("tag.changed.to.0",player), Helper.parseColors(newtag)));
        clan.changeClanTag(newtag);
        plugin.getClanManager().updateDisplayName(player.getPlayer());
    }
}
