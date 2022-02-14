package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class CategoriesLimitException extends DiscordHookException {
    public CategoriesLimitException(String message, String translateKey) {
        super(message, translateKey);
    }
}
