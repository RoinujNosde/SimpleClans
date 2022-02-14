package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelsLimitException extends DiscordHookException {

    public ChannelsLimitException(String debugMessage, String messageKey) {
        super(debugMessage, messageKey);
    }
}
