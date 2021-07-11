package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;

public class SpigotToSpyHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        SettingsManager sm = plugin.getSettingsManager();
        String format = sm.getString(ConfigField.valueOf(message.getChannel() + "CHAT_SPYFORMAT"));
        String formattedMessage = plugin.getChatManager().parseChatFormat(format, message);

        /*
        1. Gets all online players (clan players)
        2. If online player contains permission and not muted
        3. Subtracts this online players from the receivers to avoid repetition
        4. Sends message
        */
        List<ClanPlayer> onlinePlayers = plugin.getClanManager().getOnlineClanPlayers().stream()
                .filter(player -> plugin.getPermissionsManager().has(player.toPlayer(), "simpleclans.admin.all-seeing-eye"))
                .filter(player -> !player.isMuted())
                .collect(Collectors.toList());
        onlinePlayers.removeAll(message.getReceivers());
        onlinePlayers.forEach(receiver -> ChatBlock.sendMessage(receiver, formattedMessage));
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT;
    }
}
