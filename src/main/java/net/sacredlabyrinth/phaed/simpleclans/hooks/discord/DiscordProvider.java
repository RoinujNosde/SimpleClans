package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.CategoriesLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelExistsException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelsLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.InvalidChannelException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface DiscordProvider {
    void setupDiscord();

    SCGuild getGuildWrapper();

    SCCategory createCategory();

    boolean categoryExists(@NotNull String categoryId);

    boolean channelExists(@NotNull String categoryId);

    List<SCChannel> getChannels();

    List<SCCategory> getCategories();

    void createChannel(@NotNull String clanTag) throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException, ChannelExistsException;

    boolean deleteChannel(@NotNull String channelName);

    SCMember getMember(@NotNull ClanPlayer clanPlayer);

    SCRole getLeaderRole();

    void sendMessage(String clanTag, String formattedMessage);
}
