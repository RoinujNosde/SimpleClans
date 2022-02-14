package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class InvalidChannelException extends DiscordHookException {

    public InvalidChannelException(String debugMessage, String messageKey) {
        super(debugMessage, messageKey);
    }

    public InvalidChannelException(String debugMessage) {
        super(debugMessage);
    }
}
