package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordHook;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_FORMAT_TO;

/**
 * Handles delivering messages from {@link Source#SPIGOT} to {@link Source#DISCORD}.
 */
public class DiscordChatHandler implements ChatHandler {

    @Override
    public void sendMessage(@NotNull SCMessage message) {
        if (message.getChannel() != CLAN) {
            return;
        }

        String format = settingsManager.getString(DISCORDCHAT_FORMAT_TO);
        String formattedMessage = ChatUtils.stripColors(chatManager.parseChatFormat(format, message));

        Clan clan = message.getSender().getClan();
        if (clan == null) {
            return;
        }

        DiscordHook discordHook = Objects.requireNonNull(chatManager.getDiscordHook(), "DiscordHook cannot be null");
        Optional<TextChannel> channel = discordHook.getCachedChannel(clan.getTag());
        channel.ifPresent(textChannel -> DiscordUtil.sendMessage(textChannel, formattedMessage));
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT && chatManager.isDiscordHookEnabled();
    }
}
