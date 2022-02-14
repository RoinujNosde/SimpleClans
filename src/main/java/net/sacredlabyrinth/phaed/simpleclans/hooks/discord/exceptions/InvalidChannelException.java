package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class InvalidChannelException extends DiscordHookException {

    public InvalidChannelException(String message, String translateKey) {
        super(message, translateKey);
    }

    public InvalidChannelException(String message) {
        super(message);
    }
}
