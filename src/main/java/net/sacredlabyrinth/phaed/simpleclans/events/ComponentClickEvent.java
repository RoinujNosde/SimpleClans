package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author RoinujNosde
 */
public class ComponentClickEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final SCFrame frame;
    private final SCComponent component;
    private boolean cancelled;

    public ComponentClickEvent(@NotNull Player who, @NotNull SCFrame frame, @NotNull SCComponent component) {
        super(who);
        this.frame = frame;
        this.component = component;
    }

    public @NotNull SCFrame getFrame() {
        return frame;
    }

    public @NotNull SCComponent getComponent() {
        return component;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
