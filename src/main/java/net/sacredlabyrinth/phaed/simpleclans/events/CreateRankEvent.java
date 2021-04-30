package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author Minat0_
 */
public class CreateRankEvent extends PlayerEvent {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Rank rank;
    private final Clan clan;

    public CreateRankEvent(Player who, Clan clan, Rank rank) {
        super(who);
        this.clan = clan;
        this.rank = rank;
    }

    @NotNull
    public Rank getRank() {
        return rank;
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
}
