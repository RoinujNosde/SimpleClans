package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import org.jetbrains.annotations.NotNull;

public class InvalidChannelException extends DiscordHookException {

    public InvalidChannelException(String message, @NotNull Object... args) {
        super(message, args);
    }
}
