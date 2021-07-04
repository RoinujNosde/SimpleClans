package net.sacredlabyrinth.phaed.simpleclans.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_ENABLE;

public final class ChatManager {

    private final SimpleClans plugin;
    private final Set<ChatHandler> handlers = new HashSet<>();

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    public void setupDiscord() {

    }

    public void processChat(Source source, @NotNull Channel channel, @NotNull ClanPlayer clanPlayer, String message) {
        if (message.isEmpty()) {
            return;
        }

        SCMessage scMessage = new SCMessage(source, channel, clanPlayer, PlaceholderAPI.setPlaceholders(clanPlayer.toPlayer(), message));
        for (ChatHandler ch : handlers) {
            if (ch.canHandle(source)) {
                ch.sendMessage(scMessage);
            }
        }
    }

    private void registerHandlers() {
        Reflections reflections = new Reflections("net.sacredlabyrinth.phaed.simpleclans.chat");
        Set<Class<? extends ChatHandler>> chatHandlers = reflections.getSubTypesOf(ChatHandler.class);
        plugin.getLogger().log(Level.INFO, "Registering {0} chat handlers...", chatHandlers.size());

        for (Class<? extends ChatHandler> handler : chatHandlers) {
            try {
                if (plugin.getSettingsManager().is(DISCORDCHAT_ENABLE)) {
                    continue;
                }
                handlers.add(handler.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to register {0} handler: " + ex.getMessage(), handler.getSimpleName());
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
