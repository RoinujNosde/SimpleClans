package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * @author ThiagoROX
 */
public class PlayerResetKdrEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final ClanPlayer clanPlayer;
    private boolean cancelled;

    /**
     * Event called before a player's kill death rate is reset
     *
     * @param clanPlayer The ClanPlayer whose kill death rate going to be reset
     */
    public PlayerResetKdrEvent(@NotNull ClanPlayer clanPlayer) {
        this.clanPlayer = clanPlayer;
    }

    /**
     * Gets the player whose kill death rate going to be reset
     *
     * @return The player whose kill death rate going to be reset
     */
    @NotNull
    public ClanPlayer getClanPlayer() {
        return clanPlayer;
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
