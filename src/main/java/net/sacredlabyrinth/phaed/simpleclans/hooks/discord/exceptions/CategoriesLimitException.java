package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class CategoriesLimitException extends DiscordHookException {
    public CategoriesLimitException(String translateKey) {
        super(translateKey);
    }
}
