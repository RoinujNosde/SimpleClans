package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.events.BankDepositEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.BankWithdrawEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

@CommandAlias("%clan")
@Subcommand("%bank")
@Conditions("%basic_conditions|economy|verified")
public class BankCommand extends BaseCommand {
    @Dependency
    private PermissionsManager permissions;

    @Subcommand("%status")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("rank:name=BANK_BALANCE")
    @Description("{@@command.description.bank.status}")
    public void bankStatus(Player player, Clan clan) {
        player.sendMessage(AQUA + lang("clan.balance", player, clan.getBalance()));
    }

    @Subcommand("%withdraw %all")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("rank:name=BANK_WITHDRAW")
    @Description("{@@command.description.bank.withdraw.all}")
    public void bankWithdraw(Player player, Clan clan) {
        processWithdraw(player, clan, clan.getBalance());
    }

    @Subcommand("%withdraw")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("rank:name=BANK_WITHDRAW")
    @Description("{@@command.description.bank.withdraw.amount}")
    public void bankWithdraw(Player player, Clan clan, double amount) {
        processWithdraw(player, clan, amount);
    }

    private void processWithdraw(Player player, Clan clan, double amount) {
        if (!clan.isAllowWithdraw()) {
            String message = getCurrentCommandManager().getCommandReplacements()
                    .replace(lang("withdraw.not.allowed", player));
            ChatBlock.sendMessage(player, RED + message);
            return;
        }
        amount = Math.abs(amount);
        /*
            TODO: Remove at SimpleClans 3.0
         */
        BankWithdrawEvent event = new BankWithdrawEvent(player, clan, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        /*
         * ——————————————————————————————————
         */
        switch (clan.withdraw(player, ClanBalanceUpdateEvent.Cause.COMMAND, amount)) {
            case SUCCESS:
                if (permissions.playerGrantMoney(player, amount)) {
                    player.sendMessage(AQUA + lang("player.clan.withdraw", player, amount));
                    clan.addBb(player.getName(), AQUA + lang("bb.clan.withdraw", amount));
                } else {
                    clan.setBalance(player, ClanBalanceUpdateEvent.Cause.REVERT, BankLogger.Operation.WITHDRAW, clan.getBalance() + amount);
                }
                break;
            case NOT_ENOUGH_BALANCE:
                player.sendMessage(lang("clan.bank.not.enough.money", player));
                break;
        }
    }

    @Subcommand("%deposit %all")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("rank:name=BANK_DEPOSIT")
    @Description("{@@command.description.bank.deposit.all}")
    public void bankDeposit(Player player, Clan clan) {
        processDeposit(player, clan, permissions.playerGetMoney(player));
    }

    @Subcommand("%deposit")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("rank:name=BANK_DEPOSIT")
    @Description("{@@command.description.bank.deposit.amount}")
    public void bankDeposit(Player player, Clan clan, double amount) {
        processDeposit(player, clan, amount);
    }

    private void processDeposit(Player player, Clan clan, double amount) {
        if (!clan.isAllowDeposit()) {
            String message = getCurrentCommandManager().getCommandReplacements()
                    .replace(lang("deposit.not.allowed", player));
            ChatBlock.sendMessage(player, RED + message);
            return;
        }
        amount = Math.abs(amount);

        /*
            TODO: Remove at SimpleClans 3.0
         */
        BankDepositEvent event = new BankDepositEvent(player, clan, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        /*
        * ——————————————————————————————————
        */

        if (!permissions.playerHasMoney(player, amount)) {
            player.sendMessage(AQUA + lang("not.sufficient.money", player));
            return;
        }
        EconomyResponse response = clan.deposit(player, ClanBalanceUpdateEvent.Cause.COMMAND, amount);
        if (response == EconomyResponse.SUCCESS) {
            if (permissions.playerChargeMoney(player, amount)) {
                player.sendMessage(AQUA + lang("player.clan.deposit", player, amount));
                clan.addBb(player.getName(), AQUA + lang("bb.clan.deposit", amount));
            } else {
                //Reverts the deposit if something went wrong with Vault
                clan.setBalance(player, ClanBalanceUpdateEvent.Cause.REVERT, BankLogger.Operation.DEPOSIT, clan.getBalance() - amount);
            }
        }
    }
}
