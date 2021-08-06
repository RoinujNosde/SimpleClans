package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;

public class SpyChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        ConfigField configField = ConfigField.valueOf(String.format("%sCHAT_SPYFORMAT",
                message.getSource() == SPIGOT ? message.getChannel() : message.getSource()));
        String format = settingsManager.getString(configField);

        String formattedMessage = chatManager.parseChatFormat(format, message);

        List<ClanPlayer> onlinePlayers = getOnlineClanPlayers().stream()
                .filter(player -> plugin.getPermissionsManager().has(player.toPlayer(), "simpleclans.admin.all-seeing-eye"))
                .filter(player -> !player.isMuted())
                .collect(Collectors.toList());

        onlinePlayers.removeAll(message.getReceivers());
        onlinePlayers.forEach(receiver -> ChatBlock.sendMessage(receiver, formattedMessage));
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT || source == DISCORD;
    }

    private List<ClanPlayer> getOnlineClanPlayers() {
        return plugin.getClanManager().getAllClanPlayers().stream().
                filter(cp -> cp.toPlayer() != null).
                collect(Collectors.toList());
    }
}
