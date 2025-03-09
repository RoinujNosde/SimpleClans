package net.sacredlabyrinth.phaed.simpleclans.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ReloadEvent extends Event {
    
    private static final HandlerList HANDLER_LIST = new HandlerList();
    
    private final CommandSender sender;
    
    public ReloadEvent(CommandSender sender) {
        this.sender = sender;
    }
    
    public static @NotNull HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
    
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
    
    public CommandSender getSender() {
        return sender;
    }
}