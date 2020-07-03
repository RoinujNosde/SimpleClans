package net.sacredlabyrinth.phaed.simpleclans.events;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author RoinujNosde
 */
public class ChatEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();
    private String message;
    private final ClanPlayer sender;
    private final List<ClanPlayer> receivers;
    private final Map<String, String> placeholders = new HashMap<>();
    private final Type type;
    private boolean cancelled;
    
    public ChatEvent(String message, ClanPlayer sender, List<ClanPlayer> receivers, Type type) {
        if (message == null || sender == null || receivers == null || type == null) {
            throw new IllegalArgumentException("none of the args can be null");
        }
        this.message = message;
        this.sender = sender;
        this.receivers = receivers;
        this.type = type;
    }    

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public enum Type {
        ALLY, CLAN
    }
    
    /**
     * Gets the chat type
     * @return the chat type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the placeholders
     *
     * @return an unmodifiable map of the placeholders
     */
    public Map<String, String> getPlaceholders() {
        return Collections.unmodifiableMap(placeholders);
    }

    /**
     * Adds a placeholder and its value to the chat message
     *
     * @param placeholderName the placeholder name
     * @param value the String to be replaced
     * @throws IllegalArgumentException if one of the args are null; if one of
     * the args are empty; if the placeholder name equals "clan", "nick-color",
     * "player", "rank" or "message"
     */
    public void addPlaceholder(String placeholderName, String value) {
        if (placeholderName == null || value == null) {
            throw new IllegalArgumentException("placeholderName or value must not be null");
        }
        if (placeholderName.isEmpty() || value.isEmpty()) {
            throw new IllegalArgumentException("placeholderName or value must not be empty");
        }
        if (placeholderName.equalsIgnoreCase("clan") || placeholderName.equalsIgnoreCase("player") || placeholderName.equalsIgnoreCase("rank") || placeholderName.equalsIgnoreCase("message")) {
            throw new IllegalArgumentException("placeholderName must not be [clan, nick-color, player, rank, message]");
        }
        placeholders.put(placeholderName, value);
    }

    /**
     * Removes a placeholder from the chat message
     *
     * @param placeholderName the placeholder name
     */
    public void removePlaceholder(String placeholderName) {
        placeholders.remove(placeholderName);
    }

    /**
     * Gets the message receivers
     *
     * @return the receivers
     */
    public List<ClanPlayer> getReceivers() {
        return receivers;
    }

    /**
     * Changes the chat message
     * @param message the new message
     */
    public void setMessage(String message) {
        if (message == null) {
            throw new IllegalArgumentException("message must not be null");
        }
        this.message = message;
    }

    /**
     * Gets the message to be sent
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the message sender
     *
     * @return the sender
     */
    public ClanPlayer getSender() {
        return sender;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
