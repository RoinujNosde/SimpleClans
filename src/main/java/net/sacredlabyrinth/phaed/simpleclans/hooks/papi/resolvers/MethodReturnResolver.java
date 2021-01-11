package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.PlaceholderResolver;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("unused")
public class MethodReturnResolver extends PlaceholderResolver {

    public MethodReturnResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "method_return";
    }

    @Override
    public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config) {
        Object result = invoke(object, method, placeholder);
        if (result == null) {
            return "";
        }
        if (result instanceof Boolean) {
            return ((Boolean) result) ? PlaceholderAPIPlugin.booleanTrue() : PlaceholderAPIPlugin.booleanFalse();
        }
        return String.valueOf(result);
    }
}
