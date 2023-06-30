package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EconomyTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer player;
    private double amount;
    private final Cause cause;
    private final TransactionType transactionType;
    private boolean cancelled;

    public EconomyTransactionEvent(OfflinePlayer affected, double amount, @Nullable Cause cause, TransactionType transactionType) {
        this.player = affected;
        this.amount = amount;
        this.cause = cause;
        this.transactionType = transactionType;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Nullable
    public Cause getCause() {
        return cause;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * If cancelled, the transaction will result as denied.
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public enum Cause {
        CLAN_CREATION, CLAN_VERIFICATION, ADD_MEMBER_TO_CLAN, CLAN_BANK, DISCORD_CREATION, PLAYER_KILLED,
        SET_MEMBER_FEE, MEMBER_FEE, CLAN_HOME_TELEPORT, SET_CLAN_TP, RESET_CLAN_KDR, CLAN_REGROUP, CUSTOM;
    }

    public enum TransactionType {
        DEPOSIT_TO_PLAYER, WITHDRAW_FROM_PLAYER;
    }

}
