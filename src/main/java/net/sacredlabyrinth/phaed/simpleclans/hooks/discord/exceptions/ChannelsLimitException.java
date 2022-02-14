package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelsLimitException extends DiscordHookException {

    public ChannelsLimitException(String message, String translateKey) {
        super(message, translateKey);
    }
}
