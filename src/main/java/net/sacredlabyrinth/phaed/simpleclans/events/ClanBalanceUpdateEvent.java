package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ClanBalanceUpdateEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private @NotNull final CommandSender updater;

    private @NotNull final Clan clan;
    private double oldBalance;
    private double newBalance;
    private boolean cancelled;

    public ClanBalanceUpdateEvent(@NotNull CommandSender updater, @NotNull Clan clan, double oldBalance, double newBalance) {
        this.updater = updater;
        this.oldBalance = oldBalance;
        this.newBalance = oldBalance;
        this.clan = clan;
    }

    public @NotNull CommandSender getUpdater() {
        return updater;
    }

    public @NotNull Clan getClan() {
        return clan;
    }

    public double getOldBalance() {
        return oldBalance;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setOldBalance(double oldBalance) {
        this.oldBalance = oldBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
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

    @Override
    public void setCancelled(boolean isCancelled) {
        this.cancelled = isCancelled;
    }
}
