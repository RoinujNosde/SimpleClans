package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
@Subcommand("%admin %bank")
public class BankCommand extends BaseCommand {

    @Dependency
    private PermissionsManager permissions;

    @Subcommand("%status")
    @CommandPermission("simpleclans.admin.bank.status")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.status}")
    public void status(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.balance", sender, clan.getName(), clan.getBalanceFormatted()));
    }

    @Subcommand("%take")
    @CommandPermission("simpleclans.admin.bank.take")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.take}")
    public void take(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);
        BankOperator operator = new BankOperator(sender, sender instanceof Player ? permissions.playerGetMoney((Player) sender) : 0);

        EconomyResponse economyResponse = clan.withdraw(operator, ClanBalanceUpdateEvent.Cause.COMMAND, amount);
        switch (economyResponse) {
            case SUCCESS:
                ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.take", sender, ChatUtils.formatPrice(amount), clan.getName()));
                clan.addBb(sender.getName(), lang("bb.clan.take", sender, ChatUtils.formatPrice(amount), sender.getName()));
                break;
            case NOT_ENOUGH_BALANCE:
                sender.sendMessage(RED + lang("clan.admin.bank.not.enough.money", sender, clan.getName()));
                break;
        }
    }

    @Subcommand("%give")
    @CommandPermission("simpleclans.admin.bank.give")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.give}")
    public void give(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);
        BankOperator operator = new BankOperator(sender, sender instanceof Player ? permissions.playerGetMoney((Player) sender) : 0);

        EconomyResponse economyResponse = clan.deposit(operator, ClanBalanceUpdateEvent.Cause.COMMAND, amount);
        if (economyResponse == EconomyResponse.SUCCESS) {
            ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.give", sender, ChatUtils.formatPrice(amount), clan.getName()));
            clan.addBb(sender.getName(), lang("bb.clan.give", sender, ChatUtils.formatPrice(amount), sender.getName()));
        }
    }

    @Subcommand("%set")
    @CommandPermission("simpleclans.admin.bank.set")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.set}")
    public void set(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);
        BankOperator operator = new BankOperator(sender, sender instanceof Player ? permissions.playerGetMoney((Player) sender) : 0);

        EconomyResponse response = clan.setBalance(operator, ClanBalanceUpdateEvent.Cause.COMMAND, BankLogger.Operation.SET, amount);
        if (response == EconomyResponse.SUCCESS) {
            ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.set", sender, clan.getName(), ChatUtils.formatPrice(amount)));
            clan.addBb(sender.getName(), lang("bb.clan.set", sender, ChatUtils.formatPrice(amount), sender.getName()));
        }
    }
}
