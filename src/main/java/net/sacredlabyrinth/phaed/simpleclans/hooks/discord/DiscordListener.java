package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.events.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public interface DiscordListener extends Listener {

    @EventHandler
    void onClanCreate(CreateClanEvent event);

    @EventHandler
    void onClanDisband(DisbandClanEvent event);

    @EventHandler
    void onPlayerClanJoin(PlayerJoinedClanEvent event);

    @EventHandler
    void onPlayerClanLeave(PlayerKickedClanEvent event);

    @EventHandler
    void onPlayerPromote(PlayerPromoteEvent event);

    @EventHandler
    void onPlayerDemote(PlayerDemoteEvent event);
}
