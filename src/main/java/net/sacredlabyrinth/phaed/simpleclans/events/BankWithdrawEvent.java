package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class BankWithdrawEvent extends PlayerEvent implements Cancellable {

    private final Clan clan;
    private double amount;
    private boolean cancelled;
    private static final HandlerList HANDLER_LIST = new HandlerList();

    public BankWithdrawEvent(@NotNull Player who, @NotNull Clan clan, double amount) {
        super(who);
        this.clan = clan;
        this.amount = amount;
    }

    public @NotNull Clan getClan() {
        return clan;
    }

    public double getOldBalance() {
        return clan.getBalance();
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
