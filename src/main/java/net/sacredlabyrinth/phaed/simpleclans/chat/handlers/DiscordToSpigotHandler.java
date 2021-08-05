package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import org.bukkit.Bukkit;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_ENABLE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_FORMAT_FROM;

public class DiscordToSpigotHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        String format = plugin.getSettingsManager().getString(DISCORDCHAT_FORMAT_FROM);
        String formattedMessage = plugin.getChatManager().parseChatFormat(format, message);

        plugin.getLogger().info(formattedMessage);

        for (ClanPlayer cp : message.getReceivers()) {
            ChatBlock.sendMessage(cp, formattedMessage);
        }
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == DISCORD &&
                plugin.getSettingsManager().is(DISCORDCHAT_ENABLE) &&
                Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null;
    }
}
