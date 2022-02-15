package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import org.jetbrains.annotations.Nullable;

/**
 * Superclass of all discord exceptions
 *
 * @see InvalidChannelException
 * @see ChannelsLimitException
 * @see ChannelExistsException
 * @see CategoriesLimitException
 */
public class DiscordHookException extends Exception {

    private String messageKey;

    public DiscordHookException(String debugMessage) {
        super(debugMessage);
    }

    public DiscordHookException(String debugMessage, String messageKey) {
        super(debugMessage);
        this.messageKey = messageKey;
    }

    /**
     * @return message key or null if this key is not used in-game
     */
    public @Nullable String getMessageKey() {
        return messageKey;
    }
}
