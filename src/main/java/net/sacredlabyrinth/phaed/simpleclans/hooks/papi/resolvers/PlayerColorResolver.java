package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.PlaceholderResolver;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

@SuppressWarnings("unused")
public class PlayerColorResolver extends PlaceholderResolver {

    private final SettingsManager settings;

    public PlayerColorResolver(@NotNull SimpleClans plugin) {
        super(plugin);
        settings = plugin.getSettingsManager();
    }

    @Override
    public @NotNull String getId() {
        return "player_color";
    }

    @Override
    public @NotNull String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                                   @NotNull String placeholder, @NotNull Map<String, String> config) {
        ClanPlayer cp = object instanceof ClanPlayer ? ((ClanPlayer) object) : null;
        if (cp == null) return "";
        switch (placeholder) {
            case "clanchat_player_color": {
                return getClanChatColor(cp);
            }
            case "allychat_player_color": {
                return getAllyChatColor(cp);
            }
        }
        return "";
    }

    private String getClanChatColor(ClanPlayer player) {
        if (player.isLeader()) {
            return settings.getColored(CLANCHAT_LEADER_COLOR);
        }
        if (player.isTrusted()) {
            return settings.getColored(CLANCHAT_TRUSTED_COLOR);
        }
        if (player.getClan() != null) {
            return settings.getColored(CLANCHAT_MEMBER_COLOR);
        }
        return "";
    }

    private String getAllyChatColor(ClanPlayer player) {
        if (player.isLeader()) {
            return settings.getColored(ALLYCHAT_LEADER_COLOR);
        }
        if (player.isTrusted()) {
            return settings.getColored(ALLYCHAT_TRUSTED_COLOR);
        }
        if (player.getClan() != null) {
            return settings.getColored(ALLYCHAT_MEMBER_COLOR);
        }
        return "";
    }
}
