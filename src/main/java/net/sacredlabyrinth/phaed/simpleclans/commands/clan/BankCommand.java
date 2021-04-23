package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
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

        switch (clan.withdraw(player, amount)) {
            case SUCCESS:
                if (SimpleClans.getInstance().getPermissionsManager().playerGrantMoney(player, amount)) {
                    player.sendMessage(AQUA + lang("player.clan.withdraw", player, amount));
                    clan.addBb(player.getName(), AQUA + lang("bb.clan.withdraw", amount));
                }
                break;
            case NEGATIVE_VALUE:
                player.sendMessage(lang("you.can.t.define.negative.value", player));
                break;
            case NOT_ENOUGH_BALANCE:
                player.sendMessage(lang("clan.bank.not.enough.money", player));
                break;
            default:
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

        if (SimpleClans.getInstance().getPermissionsManager().playerHasMoney(player, amount)) {
            if (SimpleClans.getInstance().getPermissionsManager().playerChargeMoney(player, amount)) {
                switch (clan.deposit(player, amount)) {
                    case SUCCESS:
                        player.sendMessage(AQUA + lang("player.clan.deposit", player, amount));
                        clan.addBb(player.getName(), AQUA + lang("bb.clan.deposit", amount));
                        break;
                    case NEGATIVE_VALUE:
                        player.sendMessage(lang(RED + "you.can.t.define.negative.value", player));
                        break;
                    default:
                        break;
                }
            } else {
                player.sendMessage(AQUA + lang("not.sufficient.money", player));
            }
        } else {
            player.sendMessage(AQUA + lang("not.sufficient.money", player));
        }
    }
}
