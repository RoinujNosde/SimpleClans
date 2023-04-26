package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author ThiagoROX
 */
public class ClanPlayerEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private SimpleClans plugin;
    private ClanManager cm;

    public ClanPlayerEvent(Player player) {
        super(player);
    }

    @Nullable
    public ClanPlayer getClanPlayer() {
        plugin = SimpleClans.getInstance();
        cm = plugin.getClanManager();
        return cm.getClanPlayer(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
