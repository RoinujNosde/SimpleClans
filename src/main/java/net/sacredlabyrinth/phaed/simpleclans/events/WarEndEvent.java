package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.War;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WarEndEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final War war;
    private final Reason reason;

    public WarEndEvent(@NotNull War war, @NotNull Reason reason) {
        this.war = war;
        this.reason = reason;
    }

    @NotNull
    public War getWar() {
        return war;
    }

    @NotNull
    public Reason getReason() {
        return reason;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public enum Reason {
        REQUEST, EXPIRATION
    }
}
