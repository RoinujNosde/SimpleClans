package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author ThiagoROX
 */
public class PlayerResetKdrEvent extends ClanPlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    /**
     * Event called when a player has their KDR reset
     *
     * @param player The player whose kill death rate was reset
     */
    public PlayerResetKdrEvent(@NotNull ClanPlayer player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
