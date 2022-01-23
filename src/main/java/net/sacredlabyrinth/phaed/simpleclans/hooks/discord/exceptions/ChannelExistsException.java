package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import org.jetbrains.annotations.NotNull;

public class ChannelExistsException extends DiscordHookException {

    public ChannelExistsException(String message, @NotNull Object... args) {
        super(message, args);
    }
}
