package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import static net.md_5.bungee.api.ChatColor.RED;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
@Subcommand("%admin %bank")
public class BankCommands extends BaseCommand {

    @Subcommand("%status")
    @CommandPermission("simpleclans.admin.bankstatus")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.status}")
    public void status(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        sender.sendMessage(AQUA + lang("clan.admin.balance", sender, clan.getName(), clan.getBalance()));
    }

    @Subcommand("%take")
    @CommandPermission("simpleclans.admin.take")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.take}")
    public void take(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        EconomyResponse economyResponse = clan.withdraw(sender, amount);
        amount = Math.abs(amount);

        switch (economyResponse) {
            case SUCCESS:
                sender.sendMessage(lang("clan.admin.take", sender, amount, clan.getName()));
                clan.addBb(sender.getName(), AQUA + lang("bb.clan.take", sender, amount));
                break;
            case NEGATIVE_VALUE:
                sender.sendMessage(RED + lang("you.can.t.define.negative.value", sender));
                break;
            case NOT_ENOUGH_BALANCE:
                sender.sendMessage(RED + lang("clan.admin.bank.not.enough.money", sender, clan.getName()));
                break;
            default:
                break;
        }
    }

    @Subcommand("%give")
    @CommandPermission("simpleclans.admin.give")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.give}")
    public void give(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        EconomyResponse economyResponse = clan.deposit(sender, amount);
        amount = Math.abs(amount);

        switch (economyResponse) {
            case SUCCESS:
                sender.sendMessage(lang("clan.admin.give", sender, amount, clan.getName()));
                clan.addBb(sender.getName(), AQUA + lang("bb.clan.give", sender, amount));
                break;
            case NEGATIVE_VALUE:
                sender.sendMessage(RED + lang("you.can.t.define.negative.value", sender));
                break;
            case NOT_ENOUGH_BALANCE:
                sender.sendMessage(RED + lang("clan.admin.bank.not.enough.money", sender, clan.getName()));
                break;
            default:
                break;
        }
    }

    @Subcommand("%set")
    @CommandPermission("simpleclans.admin.set")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.set}")
    public void set(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {
        Clan clan = clanInput.getClan();
        amount = Math.abs(amount);

        ClanBalanceUpdateEvent event = new ClanBalanceUpdateEvent(sender, clan, clan.getBalance(), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        sender.sendMessage(AQUA + lang("clan.admin.set", sender, amount, clan.getName()));
        clan.addBb(sender.getName(), AQUA + lang("bb.clan.set", sender));
        clan.setBalance(amount);
    }
}
