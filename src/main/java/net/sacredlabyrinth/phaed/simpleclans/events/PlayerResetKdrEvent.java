package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author ThiagoROX
 */
public class PlayerResetKdrEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final boolean isEnabled;
    private final double price;

    /**
     * Event called when a player resets his KDR
     *
     * @param who the player
     * @param isEnabled if the reset is enabled
     * @param price the price of the reset
     */
    public PlayerResetKdrEvent(@NotNull Player who, boolean isEnabled, double price) {
        super(who);
        this.isEnabled = isEnabled;
        this.price = price;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }


}
