package net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.PlaceholderResolver;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Map;

@SuppressWarnings("unused")
public class PlayerColorResolver extends PlaceholderResolver {
    public PlayerColorResolver(@NotNull SimpleClans plugin) {
        super(plugin);
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
            return plugin.getSettingsManager().getClanChatLeaderColor();
        }
        if (player.isTrusted()) {
            return plugin.getSettingsManager().getClanChatTrustedColor();
        }
        if (player.getClan() != null) {
            return plugin.getSettingsManager().getClanChatMemberColor();
        }
        return "";
    }

    private String getAllyChatColor(ClanPlayer player) {
        if (player.isLeader()) {
            return plugin.getSettingsManager().getAllyChatLeaderColor();
        }
        if (player.isTrusted()) {
            return plugin.getSettingsManager().getAllyChatTrustedColor();
        }
        if (player.getClan() != null) {
            return plugin.getSettingsManager().getAllyChatMemberColor();
        }
        return "";
    }
}
