package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractContextResolver<T> {

    protected final @NotNull SimpleClans plugin;
    protected final @NotNull ClanManager clanManager;

    public AbstractContextResolver(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
        clanManager = plugin.getClanManager();
    }

    public abstract Class<T> getType();
}
