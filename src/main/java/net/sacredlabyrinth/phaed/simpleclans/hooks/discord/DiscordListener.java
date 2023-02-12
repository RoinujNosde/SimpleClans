package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.events.*;
import org.bukkit.event.Listener;

public interface DiscordListener extends Listener {

    void onClanCreate(CreateClanEvent event);

    void onClanDisband(DisbandClanEvent event);

    void onPlayerClanJoin(PlayerJoinedClanEvent event);

    void onPlayerClanLeave(PlayerKickedClanEvent event);

    void onPlayerPromote(PlayerPromoteEvent event);

    void onPlayerDemote(PlayerDemoteEvent event);
}
