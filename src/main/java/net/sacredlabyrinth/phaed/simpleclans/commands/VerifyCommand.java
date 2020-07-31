package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

/**
 * @author phaed
 */
public class VerifyCommand {

    public VerifyCommand() {
    }

    /**
     * Execute the command
     *
     * @param sender
     * @param arg
     */
    public void execute(CommandSender sender, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (sender instanceof Player) {
            Player player = (Player) sender;

            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            Clan clan = cp == null ? null : cp.getClan();

            boolean isNonVerified = clan != null && !clan.isVerified();
            boolean isBuyer = isNonVerified && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification();

            if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.verify")) {
                //if the player does specify a tag, but does not have mod permission to verify
                //if the player does not specify a tag, but does not have leader permission to verify
                if (arg.length != 0 || !plugin.getPermissionsManager().has(player, "simpleclans.leader.verify")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                    return;
                }
                if (isBuyer) {
                    int minToVerify = plugin.getSettingsManager().getMinToVerify();
                    if (minToVerify > clan.getAllMembers().size()) {
                        ChatBlock.sendMessage(player, lang("your.clan.must.have.members.to.verify", player, minToVerify));
                        return;
                    }
                    if (plugin.getClanManager().purchaseVerification(player)) {
                        clan.verifyClan();
                        clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("clan.0.has.been.verified"), clan.getName()));
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("the.clan.has.been.verified", player));
                    }
                    return;
                }
                ChatBlock.sendMessage(player, lang("staff.member.verify.clan", player));
            } else {
                if (arg.length != 1) {
                    ChatBlock.sendMessage(sender, ChatColor.RED + MessageFormat.format(lang("usage.0.verify.tag",player), plugin.getSettingsManager().getCommandClan()));
                    return;
                }
                verify(player, arg[0]);
            }
        } else {
            if (arg.length != 1) {
                ChatBlock.sendMessage(sender, ChatColor.RED + MessageFormat.format(lang("usage.0.verify.tag",sender), plugin.getSettingsManager().getCommandClan()));
                return;
            }
            verify(sender, arg[0]);
        }
    }

    private void verify(CommandSender player, String clanName) {
        SimpleClans plugin = SimpleClans.getInstance();
        Clan clan = plugin.getClanManager().getClan(clanName);

        if (clan != null) {
            if (!clan.isVerified()) {
                clan.verifyClan();
                clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("clan.0.has.been.verified"), clan.getName()));
                ChatBlock.sendMessage(player, ChatColor.AQUA + lang("the.clan.has.been.verified",player));
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.is.already.verified",player));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("the.clan.does.not.exist",player));
        }
    }
}
