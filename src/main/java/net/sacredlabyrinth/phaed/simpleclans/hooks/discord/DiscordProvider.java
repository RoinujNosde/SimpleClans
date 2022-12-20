package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.CategoriesLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelExistsException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelsLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.InvalidChannelException;
import org.jetbrains.annotations.NotNull;

public interface DiscordProvider {
    void sendMessage(String clanTag, String message);

    boolean categoryExists(String categoryId);

    boolean channelExists(@NotNull String clanTag);

    void createChannel(String clanTag) throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException, ChannelExistsException;

    boolean deleteChannel(@NotNull String clanTag);
}
