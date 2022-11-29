/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sacredlabyrinth.phaed.simpleclans.events;

import java.util.Collections;
import java.util.List;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author edson
 */
public class HomeRegroupEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private final Clan clan;
    private final ClanPlayer cp;
    private final List<ClanPlayer> cps;
    private final Location loc;

    public HomeRegroupEvent(Clan clan, ClanPlayer cp, List<ClanPlayer> cps, Location loc) {
        this.clan = clan;
        this.cp = cp;
        this.cps = cps;
        this.loc = loc;
    }
    
    public Clan getClan() {
        return clan;
    }
    
    public ClanPlayer getIssuer() {
        return cp;
    }
    
    public List<ClanPlayer> getPlayers() {
        return Collections.unmodifiableList(cps);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
