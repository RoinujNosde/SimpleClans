package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions.AsyncCommandCompletionHandler;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractAsyncCompletion extends AbstractCompletion
        implements AsyncCommandCompletionHandler<BukkitCommandCompletionContext> {
    public AbstractAsyncCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }
}
