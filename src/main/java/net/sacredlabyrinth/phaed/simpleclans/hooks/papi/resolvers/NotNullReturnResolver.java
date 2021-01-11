package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.PlaceholderResolver;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

import static me.clip.placeholderapi.PlaceholderAPIPlugin.booleanFalse;
import static me.clip.placeholderapi.PlaceholderAPIPlugin.booleanTrue;

@SuppressWarnings("unused")
public class NotNullReturnResolver extends PlaceholderResolver {
    public NotNullReturnResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "not_null_return";
    }

    @Override
    public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config) {
        return invoke(object, method, placeholder) != null ? booleanTrue() : booleanFalse();
    }
}
