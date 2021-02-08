package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.PlaceholderResolver;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ListSizeResolver extends PlaceholderResolver {
    public ListSizeResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "list_size";
    }

    @SuppressWarnings("unchecked")
    @Override
    public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config) {
        Object result = invoke(object, method, placeholder);
        String size = "";
        if (result instanceof List) {
            size = String.valueOf(((List<?>) result).size());
            if (config.containsKey("filter_vanished")) {
                size = String.valueOf(VanishUtils.getNonVanished(player != null ? player.getPlayer() : null,
                        (List<ClanPlayer>) result).size());
            }
        }
        return size;
    }
}
