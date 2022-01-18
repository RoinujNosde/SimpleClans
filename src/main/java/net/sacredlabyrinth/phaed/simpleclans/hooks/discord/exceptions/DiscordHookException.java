package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DEBUG;

public class DiscordHookException extends Exception {
    public DiscordHookException() {
        super();
    }

    public DiscordHookException(String message, @NotNull Object... args) {
        super(String.format(message, args));
        if (SimpleClans.getInstance().getSettingsManager().is(DEBUG)) {
            SimpleClans.getInstance().getLogger().warning(String.format(message, args));
        }
    }
}
