package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCompletion implements IdentifiableCompletion {

    protected final SimpleClans plugin;
    protected final ClanManager clanManager;

    public AbstractCompletion(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
        this.clanManager = plugin.getClanManager();
    }
}
