package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Category;
import org.jetbrains.annotations.NotNull;

public class SCCategory {
    private final Category category;

    public SCCategory(@NotNull Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }
}
