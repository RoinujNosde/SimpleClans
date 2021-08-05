package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import org.bukkit.Bukkit;

import java.util.Optional;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_ENABLE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_FORMAT_TO;

public class SpigotToDiscordHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        String format = plugin.getSettingsManager().getString(DISCORDCHAT_FORMAT_TO);
        String formattedMessage = plugin.getChatManager().parseChatFormat(format, message);

        Clan clan = message.getSender().getClan();
        if (clan == null) {
            return;
        }

        Optional<TextChannel> channel = plugin.getChatManager().getDiscordHook().getChannel(clan.getTag());
        channel.ifPresent(textChannel -> DiscordUtil.sendMessage(textChannel, formattedMessage));
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT &&
                plugin.getSettingsManager().is(DISCORDCHAT_ENABLE) &&
                Bukkit.getServer().getPluginManager().getPlugin("DiscordSRV") != null;
    }
}
