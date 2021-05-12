package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import org.bukkit.command.CommandSender;

public interface BankLogger {
    void log(CommandSender sender, Clan clan, EconomyResponse economyResponse, Operation operation, ClanBalanceUpdateEvent.Cause cause, double amount);

    enum Operation {
        DEPOSIT, WITHDRAW, SET
    }
}
