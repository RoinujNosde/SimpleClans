package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandCompletions.CommandCompletionHandler;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSyncCompletion extends AbstractCompletion
        implements CommandCompletionHandler<BukkitCommandCompletionContext> {
    public AbstractSyncCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }
}
