package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
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
public class MemberStatusResolver extends PlaceholderResolver {
    public MemberStatusResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "member_status";
    }

    @Override
    public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config) {
        boolean result = false;
        if (object instanceof ClanPlayer) {
            ClanPlayer cp = (ClanPlayer) object;
            if (placeholder.equals("is_member")) {
                result = cp.getClan() != null && !cp.isTrusted();
            }
            if (placeholder.equals("is_trusted")) {
                result = cp.getClan() != null && !cp.isLeader() && cp.isTrusted();
            }
        }
        return result ? booleanTrue() : booleanFalse();
    }
}
