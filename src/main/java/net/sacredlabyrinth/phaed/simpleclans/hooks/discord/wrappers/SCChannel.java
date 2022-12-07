package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

public class SCChannel {
    private final TextChannel textChannel;

    public SCChannel(@NotNull TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public TextChannel getTextChannel() {
        return textChannel;
    }
}
