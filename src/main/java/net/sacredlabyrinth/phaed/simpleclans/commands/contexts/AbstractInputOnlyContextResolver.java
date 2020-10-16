package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.contexts.ContextResolver;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractInputOnlyContextResolver<T> extends AbstractContextResolver<T>
        implements ContextResolver<T, BukkitCommandExecutionContext> {
    public AbstractInputOnlyContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }
}
