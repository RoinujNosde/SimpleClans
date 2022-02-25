package net.sacredlabyrinth.phaed.simpleclans.hooks.papi;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Level;

public abstract class PlaceholderResolver {

    protected final SimpleClans plugin;

    public PlaceholderResolver(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public abstract String getId();

    /**
     * Resolves the placeholder and returns its value
     *
     * @param player      the player involved in the placeholder request
     * @param object      the subject of the Placeholder, usually a {@link Clan} or a {@link ClanPlayer}
     * @param method      the annotated method
     * @param placeholder the placeholder to resolve
     * @param config      configuration for the resolver
     *
     * @return the resolved placeholder
     */
    @NotNull
    public abstract String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config);

    @Nullable
    protected Object invoke(@NotNull Object object, @NotNull Method method, @NotNull String placeholder) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error parsing placeholder %s", placeholder), e);
        }
        return "";
    }
}
