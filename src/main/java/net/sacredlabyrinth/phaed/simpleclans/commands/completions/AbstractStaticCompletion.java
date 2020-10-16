package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class AbstractStaticCompletion extends AbstractCompletion {

    public AbstractStaticCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @NotNull
    public abstract Collection<String> getCompletions();

}
