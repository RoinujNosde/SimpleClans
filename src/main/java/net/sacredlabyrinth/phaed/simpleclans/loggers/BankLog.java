package net.sacredlabyrinth.phaed.simpleclans.loggers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Class responsible for logging bank related transactions.
 *
 * @since 2.15.3
 */
public class BankLog {
    private final DecimalFormat decimalFormat = new DecimalFormat("##.##");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd - HH:mm");

    private final BankOperator operator;
    private final Clan clan;
    private final EconomyResponse economyResponse;
    private final BankLogger.Operation operation;
    private final ClanBalanceUpdateEvent.Cause cause;
    private final double amount;

    public BankLog(@NotNull BankOperator operator, @NotNull Clan clan, @NotNull EconomyResponse economyResponse,
                   @NotNull BankLogger.Operation operation, ClanBalanceUpdateEvent.Cause cause, double amount) {
        this.operator = operator;
        this.clan = clan;
        this.economyResponse = economyResponse;
        this.operation = operation;
        this.cause = cause;
        this.amount = amount;
    }

    public static List<String> getHeader() {
        return Arrays.asList("Date", "Sender", "Clan Name", "Response",
                "Operation", "Cause", "Sender Balance", "Amount", "Clan Balance");
    }

    public List<String> getValues() {
        List<String> values = new ArrayList<>();
        values.add(dateFormat.format(new Date()));
        values.add(operator.getName());
        values.add(clan.getName());
        values.add(economyResponse.name());
        values.add(operation.name());
        values.add(cause.name());
        values.add(decimalFormat.format(operator.getBalance()));
        values.add(decimalFormat.format(amount));
        values.add(decimalFormat.format(clan.getBalance()));

        return values;
    }

    public BankOperator getOperator() {
        return operator;
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
