package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author NeT32
 */
public class RivalClanRemoveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Clan clanFirst;
    private final Clan clanSecond;

    public RivalClanRemoveEvent(Clan clanFirst, Clan clanSecond) {
        this.clanFirst = clanFirst;
        this.clanSecond = clanSecond;
    }

    public Clan getClanFirst() {
        return this.clanFirst;
    }

    public Clan getClanSecond() {
        return this.clanSecond;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
