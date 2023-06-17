package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("unused")
public class LocaleCompletion extends AbstractStaticCompletion {

    private final List<String> availableLocales;

    public LocaleCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
        availableLocales = LanguageResource.getAvailableLocales().stream().
                map(locale -> locale.toLanguageTag().replace("-", "_")).
                toList();
    }

    @Override
    public @NotNull Collection<String> getCompletions() {
        return availableLocales;
    }

    @Override
    public @NotNull String getId() {
        return "locales";
    }
}
