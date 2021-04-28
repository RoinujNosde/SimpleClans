package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Minat0_
 */
public class PlayerRankUpdateEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final ClanPlayer target;
    private final ClanPlayer issuer;

    private final Rank oldRank;
    private final Rank newRank;
    private final Clan clan;
    private boolean cancelled;

    public PlayerRankUpdateEvent(@NotNull ClanPlayer issuer, @NotNull ClanPlayer target, @NotNull Clan clan,
                                 @Nullable Rank oldRank, @Nullable Rank newRank) {
        this.target = target;
        this.issuer = issuer;
        this.oldRank = oldRank;
        this.clan = clan;
        this.newRank = newRank;
    }

    @Nullable
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

    @NotNull
    public ClanPlayer getIssuer() {
        return issuer;
    }

    @NotNull
    public ClanPlayer getTarget() {
        return target;
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
