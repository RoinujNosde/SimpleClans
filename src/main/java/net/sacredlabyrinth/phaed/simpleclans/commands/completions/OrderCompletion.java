package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class OrderCompletion extends AbstractStaticCompletion {
    public OrderCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Collection<String> getCompletions() {
        return Arrays.asList(lang("list.order.asc"), lang("list.order.desc"));
    }

    @Override
    public @NotNull String getId() {
        return "order";
    }
}
