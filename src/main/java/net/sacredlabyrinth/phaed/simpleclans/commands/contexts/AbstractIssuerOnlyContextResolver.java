package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.IssuerOnlyContextResolver;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractIssuerOnlyContextResolver<T> extends AbstractContextResolver<T>
        implements IssuerOnlyContextResolver<T, BukkitCommandExecutionContext> {
    public AbstractIssuerOnlyContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }
}
