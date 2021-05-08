package net.sacredlabyrinth.phaed.simpleclans.storage;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import org.bukkit.command.CommandSender;

public interface BankLogger {
    void log(CommandSender sender, Clan clan, EconomyResponse economyResponse, Operation operation, double amount);

    enum Operation {
        DEPOSIT, WITHDRAW, SET
    }
}
