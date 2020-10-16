package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class ListTypeCompletion extends AbstractStaticCompletion {
    public ListTypeCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Collection<String> getCompletions() {
        return Arrays.asList(
                lang("list.type.size"), lang("list.type.kdr"), lang("list.type.name"),
                lang("list.type.founded"), lang("list.type.active"));
    }

    @Override
    public @NotNull String getId() {
        return "clan_list_type";
    }
}
