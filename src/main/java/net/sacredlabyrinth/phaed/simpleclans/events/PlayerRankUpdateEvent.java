package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Minat0_
 */
public class PlayerRankUpdateEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Rank oldRank;
    private final Rank newRank;
    private final Clan clan;
    private boolean cancelled;

    public PlayerRankUpdateEvent(@NotNull Player who, Clan clan, Rank oldRank, Rank newRank) {
        super(who);
        this.oldRank = oldRank;
        this.clan = clan;
        this.newRank = newRank;
    }

    @NotNull
    public Rank getOldRank() {
        return oldRank;
    }

    @NotNull
    public Clan getClan() {
        return clan;
    }

    @Nullable
    public Rank getNewRank() {
        return newRank;
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
