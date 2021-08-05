package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;

public class SpigotToSpyHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        String format = plugin.getSettingsManager().getString(ConfigField.valueOf(message.getChannel() + "CHAT_SPYFORMAT"));
        String formattedMessage = plugin.getChatManager().parseChatFormat(format, message);

        List<ClanPlayer> onlinePlayers = getOnlineClanPlayers().stream()
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

    private List<ClanPlayer> getOnlineClanPlayers() {
        return SimpleClans.getInstance().getClanManager().getAllClanPlayers().stream().
                filter(cp -> Bukkit.getOfflinePlayer(cp.getUniqueId()).isOnline()).
                collect(Collectors.toList());
    }
}
