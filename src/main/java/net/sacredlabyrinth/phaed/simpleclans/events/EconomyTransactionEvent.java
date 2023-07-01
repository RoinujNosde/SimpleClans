package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when money is granted or charged from a player's account.
 * <p>Note: Cancelling this event will deny the transaction.</p>
 *
 * @see PermissionsManager#grantPlayer(OfflinePlayer, double, Cause)
 * @see PermissionsManager#chargePlayer(OfflinePlayer, double, Cause)
 */
public class EconomyTransactionEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final OfflinePlayer player;
    private double amount;
    private final Cause cause;
    private final TransactionType transactionType;
    private boolean cancelled;

    public EconomyTransactionEvent(@NotNull OfflinePlayer affected, double amount,
                                   @NotNull Cause cause, @NotNull TransactionType transactionType) {
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

    @NotNull
    public Cause getCause() {
        return cause;
    }

    @SuppressWarnings("unused")
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
        CLAN_CREATION,
        CLAN_VERIFICATION,
        CLAN_INVITATION,
        CLAN_REGROUP,
        CLAN_HOME_TELEPORT,
        CLAN_HOME_TELEPORT_SET,
        DISCORD_CREATION,
        PLAYER_KILLED,
        MEMBER_FEE_SET,
        RESET_KDR
    }

    public enum TransactionType {
        DEPOSIT, WITHDRAW
    }

}
