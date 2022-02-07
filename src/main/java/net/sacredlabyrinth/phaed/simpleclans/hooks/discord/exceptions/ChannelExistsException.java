package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

public class ChannelExistsException extends DiscordHookException {

    public ChannelExistsException(String translateKey, String clanTag) {
        super(translateKey, clanTag);
    }
}
