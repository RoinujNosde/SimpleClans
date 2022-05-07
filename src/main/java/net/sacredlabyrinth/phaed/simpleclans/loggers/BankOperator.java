package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BankOperator {

    public static final BankOperator API = new BankOperator("API", 0);
    public static final BankOperator INTERNAL = new BankOperator("Internal", 0);
    private final String name;
    private final double balance;

    public BankOperator(@NotNull CommandSender sender, double balance) {
        this(sender.getName(), balance);
    }

    public BankOperator(@NotNull ClanPlayer sender, double balance) {
        this(sender.getName(), balance);
    }

    public BankOperator(@NotNull String name, double balance) {
        this.name = name;
        this.balance = balance;
    }

    public @NotNull String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }
}
