package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClanBalanceUpdateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final CommandSender updater;
    private final Clan clan;
    private final Cause cause;
    private final double balance;
    private double newBalance;
    private boolean cancelled;

    public ClanBalanceUpdateEvent(@Nullable CommandSender updater,
                                  @NotNull Clan clan,
                                  double balance,
                                  double newBalance,
                                  @NotNull Cause cause) {
        this.updater = updater;
        this.balance = balance;
        this.newBalance = newBalance;
        this.clan = clan;
        this.cause = cause;
    }

    /**
     * @return the balance updater, may be null
     */
    public @Nullable CommandSender getUpdater() {
        return updater;
    }

    /**
     * @return the Clan involved
     */
    public @NotNull Clan getClan() {
        return clan;
    }

    /**
     * @return the Clan's current balance
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @return the Clan's new balance
     */
    public double getNewBalance() {
        return newBalance;
    }

    /**
     * Sets the Clan's new balance
     *
     * @param newBalance the new balance
     * @throws IllegalArgumentException if newBalance is negative
     */
    public void setNewBalance(double newBalance) {
        if (newBalance < 0) {
            throw new IllegalArgumentException("newBalance cannot be negative");
        }
        this.newBalance = newBalance;
    }

    /**
     * @return the update cause
     */
    public @NotNull Cause getCause() {
        return cause;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * @throws IllegalStateException if the cause of the event is {@link Cause#REVERT}
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        if (getCause() == Cause.REVERT) {
            throw new IllegalStateException("cannot cancel REVERT update");
        }
        this.cancelled = isCancelled;
    }

    public enum Cause {
        UPKEEP,
        MEMBER_FEE,
        /**
         * When a command such as /clan bank deposit causes the update
         */
        COMMAND,
        /**
         * When the balance is updated via API methods
         */
        API,
        /**
         * When the clan data is being loaded and the balance is set, usually on server start up or plugin reload
         */
        LOADING,
        /**
         * When a failed deposit is being refunded, cannot be cancelled
         */
        REVERT
    }
}
