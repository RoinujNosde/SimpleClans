package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class InvalidChannelException extends DiscordHookException {

    public InvalidChannelException(String translateKey, String clanTag) {
        super(translateKey, clanTag);
    }

    public InvalidChannelException() {
        super();
    }
}
