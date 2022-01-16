package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public class InvalidChannelException extends Exception
{
    public InvalidChannelException(String message) {
        super(message);
    }

    public InvalidChannelException(String message, @NotNull Object... args) {
        super(String.format(message, args));
        SimpleClans.getInstance().getLogger().warning(String.format(message, args));
    }
}
