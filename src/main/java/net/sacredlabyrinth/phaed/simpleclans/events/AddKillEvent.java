package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AddKillEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private final ClanPlayer victim;
    private final ClanPlayer attacker;

    public AddKillEvent(@NotNull ClanPlayer attacker, @NotNull ClanPlayer victim) {
        this.attacker = attacker;
        this.victim = victim;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public ClanPlayer getAttacker() {
        return attacker;
    }

    public ClanPlayer getVictim() {
        return victim;
    }


    @Override
    public void setCancelled(boolean value) {
        cancelled = value;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
