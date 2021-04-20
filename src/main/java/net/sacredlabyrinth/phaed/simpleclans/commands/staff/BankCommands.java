package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.bukkit.command.CommandSender;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
public class BankCommands extends BaseCommand {

    @Subcommand("%admin %status")
    @CommandPermission("simpleclans.admin.bankstatus")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.status}")
    public void bankStatus(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        sender.sendMessage(AQUA + lang("clan.admin.balance", sender, clan.getName(), clan.getBalance()));
    }

    @Subcommand("%admin %take")
    @CommandPermission("simpleclans.admin.take")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.take}")
    public void bankTake(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {

    }

    @Subcommand("%admin %give")
    @CommandPermission("simpleclans.admin.give")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.give}")
    public void bankGive(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {

    }

    @Subcommand("%admin %set")
    @CommandPermission("simpleclans.admin.set")
    @CommandCompletion("@clans")
    @Description("{@@command.description.bank.admin.set}")
    public void bankSet(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("amount") double amount) {

    }
}
