package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.providers.discordsrv.DSRVProvider;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.CategoriesLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelExistsException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelsLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.InvalidChannelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @deprecated use {@link DSRVProvider}
 */
public class DiscordHook extends DSRVProvider {

    public DiscordHook(SimpleClans plugin) {
        super(plugin);
    }

    @NotNull
    public Guild getGuild() {
        return super.getGuild();
    }

    /**
     * @return A leader role from guild, otherwise creates one.
     */
    @NotNull
    public Role getLeaderRole() {
        return super.getLeaderRole();
    }

    /**
     * @return A leader color from configuration
     */
    public Color getLeaderColor() {
        return super.getLeaderColor();
    }

    /**
     * Creates a new SimpleClans {@link Category}
     *
     * @return Category or null, if reached the limit
     */
    @Nullable
    public Category createCategory() {
        return super.createCategory();
    }

    /**
     * Creates a new {@link TextChannel} in available SimpleClans' categories,
     * otherwise creates one.
     *
     * <p>Sets positive {@link Permission#VIEW_CHANNEL} permission to all linked clan members.</p>
     *
     * @param clanTag the clan tag
     *
     * @throws InvalidChannelException  clan is not verified or permanent,
     *                                  no one member is linked or clan is not in the whitelist.
     * @throws ChannelExistsException   if channel is already exist
     * @throws CategoriesLimitException if categories reached the limit.
     * @throws ChannelsLimitException   if discord reached the channels limit.
     */
    public void createChannel(@NotNull String clanTag)
            throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException, ChannelExistsException {
        validateChannel(clanTag);
        super.createChannel(clanTag);
    }

    /**
     * Retrieves channel in SimpleClans categories.
     *
     * @param channelName the channel name
     *
     * @see #getCachedCategories() retreive categories.
     *
     * @return the channel
     */
    public Optional<TextChannel> getCachedChannel(@NotNull String channelName) {
        return super.getCachedChannel(channelName);
    }

    /**
     * Checks if a category can be obtained by id.
     *
     * @param categoryId the category id
     * @see #channelExists(String)
     *
     * @return true if the category exists
     */
    public boolean categoryExists(@NotNull String categoryId) {
        return super.categoryExists(categoryId);
    }

    /**
     * Checks if a channel with the specified clan tag exists
     *
     * @see #categoryExists(String)
     */
    public boolean channelExists(@NotNull String clanTag) {
        return super.channelExists(clanTag);
    }

    /**
     * Deletes channel from SimpleClans categories.
     * If there are no channels, removes category as well.
     *
     * @param channelName the channel name
     *
     * @return true, if channel was deleted and false if not.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteChannel(@NotNull String channelName) {
        return super.deleteChannel(channelName);
    }

    /**
     * @return categories from config
     */
    public List<Category> getCachedCategories() {
        return super.getCachedCategories();
    }

    /**
     * In most cases, you will use {@link #getCachedCategories()}.
     *
     * @return categories from guild
     */
    public List<Category> getCategories() {
        return new ArrayList<>(super.getCategories());
    }

    /**
     * In most cases, you will use {@link #getCachedChannels()}.
     *
     * @return all channels from guild
     */
    public List<TextChannel> getChannels() {
        return new ArrayList<>(super.getChannels());
    }

    /**
     * @return All channels in categories
     */
    public List<TextChannel> getCachedChannels() {
        return super.getCachedChannels();
    }

    @Nullable
    public Member getMember(@NotNull ClanPlayer clanPlayer) {
        return super.getMember(clanPlayer);
    }
}
