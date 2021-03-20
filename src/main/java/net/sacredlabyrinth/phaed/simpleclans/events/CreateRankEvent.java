package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Minat0_
 */
public class CreateRankEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final String rankName;
    private final Clan clan;
    private boolean cancelled;

    public CreateRankEvent(Player who, Clan clan, String rankName) {
        super(who);
        this.clan = clan;
        this.rankName = rankName;
    }

    @NotNull
    public String getRankName() {
        return rankName;
    }

    public Rank getRank() {
        return clan.getRank(rankName);
    }

    @NotNull
    public Clan getClan() {
        return clan;
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
