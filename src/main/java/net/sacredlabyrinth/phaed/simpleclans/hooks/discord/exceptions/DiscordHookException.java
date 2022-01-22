package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import org.jetbrains.annotations.NotNull;

public class DiscordHookException extends Exception {
    public DiscordHookException() {
        super();
    }

    public DiscordHookException(String message, @NotNull Object... args) {
        super(String.format(message, args));
    }
}
