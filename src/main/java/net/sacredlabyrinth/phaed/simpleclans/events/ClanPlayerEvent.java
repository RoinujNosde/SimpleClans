package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author ThiagoROX
 */
public class ClanPlayerEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private ClanPlayer clanPlayer;

    public ClanPlayerEvent(Player player, ClanPlayer clanPlayer) {
        super(player);
        this.clanPlayer = clanPlayer;
    }

    @Nullable
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
