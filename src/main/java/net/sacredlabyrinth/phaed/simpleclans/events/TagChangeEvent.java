package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class TagChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final @NotNull  Clan clan;
    private @NotNull String newTag;
    private boolean cancelled;

    public TagChangeEvent(@NotNull Player player, @NotNull Clan clan, @NotNull String newTag) {
        super(player);
        this.clan = clan;
        this.newTag = newTag;
    }

    public @NotNull Clan getClan() {
        return clan;
    }

    public @NotNull String getNewTag() {
        return newTag;
    }

    public void setNewTag(@NotNull String newTag) {
        this.newTag = newTag;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
