package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;

/**
 * Handles delivering messages from {@link Source#SPIGOT} or {@link Source#DISCORD} to internal spy chat.
 */
public class SpyChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        ConfigField formatField = ConfigField.valueOf(String.format("%sCHAT_SPYFORMAT",
                message.getSource() == SPIGOT ? message.getChannel() : message.getSource()));
        String format = settingsManager.getString(formatField);
        message.setContent(ChatUtils.stripColors(message.getContent()));
        String formattedMessage = chatManager.parseChatFormat(format, message);

        List<ClanPlayer> onlineSpies = getOnlineSpies();

        // Don't send a duplicate message if a spy is inside the clan
        onlineSpies.removeAll(message.getReceivers());
        onlineSpies.forEach(receiver -> ChatBlock.sendMessage(receiver, formattedMessage));
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT || (source == DISCORD && chatManager.isDiscordHookEnabled());
    }

    private List<ClanPlayer> getOnlineSpies() {
        return Bukkit.getOnlinePlayers().stream().
                filter(Objects::nonNull).
                filter(player -> plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")).
                map(player -> plugin.getClanManager().getCreateClanPlayer(player.getUniqueId())).
                filter(Objects::nonNull).
                filter(clanPlayer -> !clanPlayer.isMuted()).
                collect(Collectors.toList());
    }
}
