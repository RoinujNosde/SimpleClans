package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelExistsException extends DiscordHookException {

    public ChannelExistsException(String message, String translateKey) {
        super(message, translateKey);
    }
}
