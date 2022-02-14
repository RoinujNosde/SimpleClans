package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class CategoriesLimitException extends DiscordHookException {
    public CategoriesLimitException(String debugMessage, String messageKey) {
        super(debugMessage, messageKey);
    }
}
