package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import org.bukkit.command.CommandSender;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
@Subcommand("%admin %bank")
public class BankCommand extends BaseCommand {

    @Subcommand("%status")
    @CommandPermission("simpleclans.admin.bank.status")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.status}")
    public void status(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.balance", sender, clan.getName(), clan.getBalance()));
    }

    @Subcommand("%take")
    @CommandPermission("simpleclans.admin.bank.take")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.take}")
    public void take(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);

        EconomyResponse economyResponse = clan.withdraw(sender, ClanBalanceUpdateEvent.Cause.COMMAND, amount);
        switch (economyResponse) {
            case SUCCESS:
                ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.take", sender, amount, clan.getName()));
                clan.addBb(sender.getName(), AQUA + lang("bb.clan.take", sender, amount));
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

        EconomyResponse economyResponse = clan.deposit(sender, ClanBalanceUpdateEvent.Cause.COMMAND, amount);
        if (economyResponse == EconomyResponse.SUCCESS) {
            ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.give", sender, amount, clan.getName()));
            clan.addBb(sender.getName(), AQUA + lang("bb.clan.give", sender, amount));
        }
    }

    @Subcommand("%set")
    @CommandPermission("simpleclans.admin.bank.set")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.set}")
    public void set(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);

        EconomyResponse response = clan.setBalance(sender, ClanBalanceUpdateEvent.Cause.COMMAND, BankLogger.Operation.SET, amount);
        if (response == EconomyResponse.SUCCESS) {
            ChatBlock.sendMessage(sender, AQUA + lang("clan.admin.set", sender, clan.getName(), amount));
            clan.addBb(sender.getName(), AQUA + lang("bb.clan.set", sender, amount));
        }
    }
}
