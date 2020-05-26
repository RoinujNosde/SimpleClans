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

/**
 * @author phaed
 */
public class BankCommand {

    public BankCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bank")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        double plmoney = plugin.getPermissionsManager().playerGetMoney(player);
        double money = 0;
        Clan clan = cp.getClan();

        if (clan == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        double clanbalance = clan.getBalance();

        if (clan.isMember(player)) {
            if (!clan.isVerified()) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
                return;
            }

            if (arg.length == 1) {
                if (arg[0].equalsIgnoreCase("status")) {
                    if (!plugin.getPermissionsManager().has(player, RankPermission.BANK_BALANCE, PermissionLevel.TRUSTED, true)) {
                    	return;
                    }
                    player.sendMessage(ChatColor.AQUA + MessageFormat.format(lang("clan.balance",player), clanbalance));
                }
            } else if (arg.length == 2) {
                if (arg[1].matches("[0-9]+")) {
                    money = Double.parseDouble(arg[1]);
                }
                if (arg[0].equalsIgnoreCase("deposit")) {
                    if (!clan.isAllowDeposit()) {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("deposit.not.allowed",player));
                        return;
                    }
                    if (!plugin.getPermissionsManager().has(player, RankPermission.BANK_DEPOSIT, PermissionLevel.LEADER, true)) {
                    	return;
                    }
                    if (arg[1].equalsIgnoreCase("all")) {
                        clan.deposit(plmoney, player);
                    } else {
                        clan.deposit(money, player);
                    }
                } else if (arg[0].equalsIgnoreCase("withdraw")) {
                    if (!clan.isAllowWithdraw()) {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("withdraw.not.allowed",player));
                        return;
                    }
                    if (!plugin.getPermissionsManager().has(player, RankPermission.BANK_WITHDRAW, PermissionLevel.LEADER, true)) {
                    	return;
                    }
                    if (arg[1].equalsIgnoreCase("all")) {
                        clan.withdraw(clanbalance, player);
                    } else {
                        clan.withdraw(money, player);
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.bank",player), plugin.getSettingsManager().getCommandClan()));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.bank",player), plugin.getSettingsManager().getCommandClan()));
            }
        }

    }
}
