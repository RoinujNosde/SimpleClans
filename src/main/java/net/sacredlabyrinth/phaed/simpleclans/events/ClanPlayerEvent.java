package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author ThiagoROX
 */
public class ClanPlayerEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final ClanPlayer clanPlayer;

    public ClanPlayerEvent(@NotNull ClanPlayer clanPlayer) {
        super(clanPlayer.toPlayer());
        this.clanPlayer = clanPlayer;
    }

    public ClanPlayer getClanPlayer() {
        return this.clanPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
