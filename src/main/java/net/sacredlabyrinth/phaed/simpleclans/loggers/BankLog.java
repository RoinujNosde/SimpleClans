package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BankLog {
    private final DecimalFormat decimalFormat = new DecimalFormat("##.##");

    private final CommandSender sender;
    private final Clan clan;
    private final EconomyResponse economyResponse;
    private final BankLogger.Operation operation;
    private final ClanBalanceUpdateEvent.Cause cause;
    private final double amount;

    public BankLog(@Nullable CommandSender sender, @NotNull Clan clan, @NotNull EconomyResponse economyResponse,
                   @NotNull BankLogger.Operation operation, ClanBalanceUpdateEvent.Cause cause, double amount) {
        this.sender = sender;
        this.clan = clan;
        this.economyResponse = economyResponse;
        this.operation = operation;
        this.cause = cause;
        this.amount = amount;
    }

    public static List<String> getHeader() {
        return Arrays.asList("Date", "Sender", "Clan Name", "Response", "Operation", "Cause", "Sender Balance", "Amount", "Clan Balance");
    }

    public List<String> getValues() {
        double senderMoney = (sender instanceof Player) ? SimpleClans.getInstance().getPermissionsManager().playerGetMoney((Player) sender) : 0;

        List<String> values = new ArrayList<>();
        values.add(sender != null ? sender.getName() : "API");
        values.add(clan.getName());
        values.add(economyResponse.name());
        values.add(operation.name());
        values.add(cause.name());
        values.add(decimalFormat.format(senderMoney));
        values.add(decimalFormat.format(amount));
        values.add(decimalFormat.format(clan.getBalance()));

        return values;
    }

    public CommandSender getSender() {
        return sender;
    }

    public Clan getClan() {
        return clan;
    }

    public EconomyResponse getEconomyResponse() {
        return economyResponse;
    }

    public BankLogger.Operation getOperation() {
        return operation;
    }

    public ClanBalanceUpdateEvent.Cause getCause() {
        return cause;
    }

    public double getAmount() {
        return amount;
    }
}
