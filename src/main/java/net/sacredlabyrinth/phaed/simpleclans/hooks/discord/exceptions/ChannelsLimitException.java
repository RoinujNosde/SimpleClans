package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelsLimitException extends DiscordHookException {

    public ChannelsLimitException(String translateKey) {
        super(translateKey);
    }
}
