package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_ENABLE;

public final class ChatManager {

    private final SimpleClans plugin;
    private final Set<ChatHandler> handlers = new HashSet<>();

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        handlers.add(new SpigotChatHandler());
        if (plugin.getSettingsManager().is(DISCORDCHAT_ENABLE)) {
            handlers.add(new DiscordChatHandler());
            setupDiscord();
        }
        handlers.add(new SpyChatHandler());
    }

    public void setupDiscord() {

    }

    public void processChat(Source source, @NotNull Channel channel, @NotNull ClanPlayer clanPlayer, String message) {
        if (message.isEmpty()) {
            return;
        }

        SCMessage scMessage = new SCMessage(source, channel, clanPlayer, message);
        for (ChatHandler ch : handlers) {
            if (ch.canHandle(source)) {
                ch.sendMessage(scMessage);
            }
        }
    }

    /*
        1. Gets all online players (clan players)
        2. If online player contains permission and not muted
        3. Subtracts this online players from the receivers to avoid repetition
        4. Sends message
     */
    private void sendToAllSeeing(String message, List<ClanPlayer> receivers) {
        List<ClanPlayer> onlinePlayers = plugin.getClanManager().getOnlineClanPlayers().stream()
                .filter(player -> plugin.getPermissionsManager().has(player.toPlayer(), "simpleclans.admin.all-seeing-eye"))
                .filter(player -> !player.isMuted())
                .collect(Collectors.toList());
        onlinePlayers.removeAll(receivers);
        onlinePlayers.forEach(receiver -> ChatBlock.sendMessage(receiver, message));
    }
}
