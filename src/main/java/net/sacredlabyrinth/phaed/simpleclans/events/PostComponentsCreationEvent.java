package net.sacredlabyrinth.phaed.simpleclans.events;

import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PostComponentsCreationEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final @NotNull SCFrame frame;

    public PostComponentsCreationEvent(@NotNull SCFrame frame) {
        this.frame = frame;
    }

    public SCFrame getFrame() {
        return frame;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
