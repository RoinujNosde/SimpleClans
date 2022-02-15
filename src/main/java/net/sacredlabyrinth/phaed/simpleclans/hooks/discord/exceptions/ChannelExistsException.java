package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelExistsException extends DiscordHookException {

    public ChannelExistsException(String debugMessage, String messageKey) {
        super(debugMessage, messageKey);
    }
}
