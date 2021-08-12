package net.sacredlabyrinth.phaed.simpleclans.hooks;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.emoji.EmojiParser;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerJoinedClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerKickedClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static github.scarsz.discordsrv.dependencies.jda.api.Permission.VIEW_CHANNEL;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.DiscordHook.PermissionAction.ADD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.DiscordHook.PermissionAction.REMOVE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * Hooks SimpleClans and Discord, using DiscordSRV.
 * <p>
 * On server' startup:
 * </p>
 * <ul>
 *  <li>Creates categories and channels, respecting discord's limits.</li>
 *  <li>Removes invalid channels, resets permissions.</li>
 * </ul>
 * <p>
 * Manages events:
 * </p>
 * <ul>
 *     <li>Clan creation/deletion</li>
 *     <li>ClanPlayer joining/resigning</li>
 *     <li>Player linking</li>
 * </ul>
 * <p>
 * Currently, works with clan chat only.
 */
public class DiscordHook implements Listener {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final ChatManager chatManager;
    private final ClanManager clanManager;
    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();

    private final Guild guild = DiscordSRV.getPlugin().getMainGuild();
    private final List<String> textCategories;
    private final List<TextChannel> channels;
    private final List<String> discordClanTags;
    private final List<String> clanTags;

    private static final int MAX_CHANNELS_PER_CATEGORY = 50;
    private static final int MAX_CHANNELS_PER_GUILD = 500;

    public DiscordHook(SimpleClans plugin) {
        this.plugin = plugin;
        settingsManager = plugin.getSettingsManager();
        chatManager = plugin.getChatManager();
        clanManager = plugin.getClanManager();
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).stream().
                filter(this::categoryExists).collect(Collectors.toList());

        List<String> whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);

        clanTags = clanManager.getClans().stream().
                filter(Clan::isVerified).
                filter(clan -> !getDiscordPlayersId(clan.getTag()).isEmpty() || clan.isPermanent()).
                map(Clan::getTag).
                filter(clanTag -> whitelist.isEmpty() || whitelist.contains(clanTag)).
                limit(settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)).
                collect(Collectors.toList());

        channels = getChannels();
        discordClanTags = channels.stream().
                map(GuildChannel::getName).
                collect(Collectors.toList());

        setupDiscord();
    }

    @Subscribe
    public void onMessageReceived(DiscordGuildMessageReceivedEvent event) {
        Optional<TextChannel> channel = getChannel(event.getChannel().getName());
        if (channel.isPresent()) {
            User author = event.getAuthor();
            RestAction<PrivateChannel> privateChannelAction = author.openPrivateChannel();
            TextChannel textChannel = channel.get();

            UUID uuid = accountManager.getUuid(author.getId());
            if (uuid == null) {
                textChannel.deleteMessageById(event.getMessage().getId()).queue(unused ->
                        privateChannelAction.flatMap(privateChannel ->
                                privateChannel.sendMessage(lang("you.did.not.link.your.account"))).queue());
                return;
            }

            ClanPlayer clanPlayer = clanManager.getClanPlayer(uuid);
            if (clanPlayer == null) {
                return;
            }

            Clan clan = clanPlayer.getClan();
            if (clan == null) {
                return;
            }

            if (!textChannel.getName().equals(clan.getTag())) {
                textChannel.deleteMessageById(event.getMessage().getId()).queue(unused -> {
                    String channelLink = "<#" + textChannel.getId() + ">";
                    privateChannelAction.flatMap(privateChannel -> privateChannel.sendMessage(
                            lang("cannot.send.discord.message", clanPlayer, channelLink))
                    ).queue();
                });
                return;
            }

            String emojiBehavior = DiscordSRV.config().getString("DiscordChatChannelEmojiBehavior");
            Component component = MessageUtil.reserializeToMinecraft(event.getMessage().getContentRaw());
            String message = MessageUtil.toLegacy(component);
            boolean hideEmoji = emojiBehavior.equalsIgnoreCase("hide");
            if (hideEmoji && StringUtils.isBlank(EmojiParser.removeAllEmojis(message))) {
                DiscordSRV.debug("Ignoring message from " + event.getAuthor() + " because it became completely blank after removing unicode emojis");
                return;
            }

            if (emojiBehavior.equalsIgnoreCase("show")) {
                // emojis already exist as unicode
            } else if (hideEmoji) {
                message = EmojiParser.removeAllEmojis(message);
            } else {
                // parse emojis from unicode back to :code:
                message = EmojiParser.parseToAliases(message);
            }

            chatManager.processChat(DISCORD, CLAN, clanPlayer, message);
        }
    }

    @EventHandler
    public void onClanDisband(DisbandClanEvent event) {
        deleteChannel(event.getClan().getTag());
    }

    @EventHandler
    public void onClanCreate(CreateClanEvent event) {
        createChannel(event.getClan().getTag());
    }

    @EventHandler
    public void onPlayerClanLeave(PlayerKickedClanEvent event) {
        updatePermissions(event.getClanPlayer(), event.getClan().getTag(), REMOVE);
    }

    @EventHandler
    public void onPlayerClanJoin(PlayerJoinedClanEvent event) {
        updatePermissions(event.getClanPlayer());
    }

    @Subscribe
    public void onPlayerLinking(AccountLinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        if (clanPlayer == null) {
            return;
        }

        updatePermissions(clanPlayer);
    }

    protected void setupDiscord() {
        removeInvalidChannels();
        resetPermissions();
        createChannels();
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }

    /**
     * Creates a new SimpleClans {@link Category}
     *
     * @return Category or null, if reached the limit
     */
    @Nullable
    public Category createCategory() {
        if (guild.getChannels().size() >= MAX_CHANNELS_PER_GUILD) {
            return null;
        }

        String categoryName = settingsManager.getString(DISCORDCHAT_TEXT_CATEGORY_FORMAT);
        Category category = null;
        try {
            category = guild.createCategory(categoryName).
                    addRolePermissionOverride(
                            guild.getPublicRole().getIdLong(),
                            Collections.emptyList(),
                            Collections.singletonList(VIEW_CHANNEL)).
                    submit().get();

            textCategories.add(category.getId());
            settingsManager.set(DISCORDCHAT_TEXT_CATEGORY_IDS, textCategories);
            settingsManager.save();
        } catch (InterruptedException | ExecutionException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error while trying to create {0} category: " +
                    ex.getMessage(), categoryName);
        }

        return category;
    }

    /**
     * Creates a new {@link ClanPlayer.Channel} in available categories,
     * otherwise creates one
     */
    public boolean createChannel(@NotNull String clanTag) {
        Category availableCategory = getCategories().stream().
                filter(category -> category.getTextChannels().size() < MAX_CHANNELS_PER_CATEGORY).
                findAny().orElseGet(this::createCategory);

        return availableCategory != null && createChannel(availableCategory, clanTag);
    }

    /**
     * Retrieves channel in SimpleClans categories.
     *
     * @see #getCategories() retreive categories.
     */
    public Optional<TextChannel> getChannel(@NotNull String channelName) {
        return channels.stream().filter(textChannel -> textChannel.getName().equals(channelName)).findFirst();
    }

    /**
     * Checks if category can be obtained by id.
     */
    public boolean categoryExists(String categoryId) {
        return guild.getCategoryById(categoryId) != null;
    }

    /**
     * Creates a new {@link ClanPlayer.Channel} in defined {@link Category}
     *
     * <p>Sets {@link Permission#VIEW_CHANNEL} permission to all linked clan members.</p>
     *
     * @return true, if channel was created. False, if category reached the limit.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean createChannel(@NotNull Category category, @NotNull String clanTag) {
        ChannelAction<TextChannel> action = category.createTextChannel(clanTag);

        if (category.getTextChannels().size() >= MAX_CHANNELS_PER_CATEGORY) {
            return false;
        }

        for (Long discordId : getDiscordPlayersId(clanTag)) {
            action.addMemberPermissionOverride(discordId,
                    Collections.singletonList(VIEW_CHANNEL),
                    Collections.emptyList());
        }
        action.complete();
        return true;
    }

    /**
     * Deletes channel from SimpleClans categories.
     * If there are no channels, removes category as well.
     */
    public void deleteChannel(@NotNull String channelName) {
        for (Category category : getCategories()) {
            if (category.getTextChannels().size() > 0) {
                for (TextChannel textChannel : category.getTextChannels()) {
                    if (textChannel.getName().equals(channelName)) {
                        textChannel.delete().complete();
                    }
                }
            } else {
                textCategories.remove(category.getId());
                settingsManager.set(DISCORDCHAT_TEXT_CATEGORY_IDS, textCategories);
                settingsManager.save();
                category.delete().complete();
            }
        }
    }

    /**
     * @return All linked clan players as discord ids in clan.
     */
    public List<Long> getDiscordPlayersId(String clanTag) {
        return clanManager.getClan(clanTag).getMembers().stream().
                map(ClanPlayer::getUniqueId).
                map(accountManager::getDiscordId).
                filter(Objects::nonNull).
                map(Long::valueOf).
                collect(Collectors.toList());
    }

    /**
     * @return All categories, which contains in configuration.
     */
    public List<Category> getCategories() {
        return textCategories.stream().
                filter(this::categoryExists).
                map(guild::getCategoryById).
                collect(Collectors.toList());
    }

    /**
     * @return All channels in categories
     */
    public List<TextChannel> getChannels() {
        return getCategories().stream().map(Category::getTextChannels).flatMap(Collection::stream).
                collect(Collectors.toList());
    }

    enum PermissionAction {
        ADD, REMOVE
    }

    private void updatePermissions(@NotNull ClanPlayer clanPlayer) {
        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        updatePermissions(clanPlayer, clan.getTag(), ADD);
    }

    private void updatePermissions(@NotNull ClanPlayer clanPlayer, @NotNull String channelName, PermissionAction action) {
        Optional<TextChannel> channel = getChannel(channelName);
        String discordId = accountManager.getDiscordId(clanPlayer.getUniqueId());
        Member member = DiscordUtil.getMemberById(discordId);
        if (member != null && channel.isPresent()) {
            switch (action) {
                case ADD:
                    channel.get().upsertPermissionOverride(member).setPermissions(
                                    Collections.singletonList(VIEW_CHANNEL), Collections.emptyList()).
                            queue();
                    break;
                case REMOVE:
                    channel.get().getManager().removePermissionOverride(member).queue();
            }
        }
    }

    private void removeInvalidChannels() {
        ArrayList<String> clansToDelete = new ArrayList<>(discordClanTags);
        clansToDelete.removeAll(clanTags);
        clansToDelete.forEach(this::deleteChannel);
    }

    private void resetPermissions() {
        channels.stream().
                map(TextChannel::getMemberPermissionOverrides).
                flatMap(Collection::stream).
                filter(permissionOverride -> !Objects.equals(permissionOverride.getPermissionHolder(), guild.getPublicRole())).
                forEach(permissionOverride -> permissionOverride.delete().queue());

        clanTags.stream().map(clanManager::getClan).
                filter(Objects::nonNull).
                map(Clan::getMembers).
                flatMap(Collection::stream).
                forEach(this::updatePermissions);
    }

    private void createChannels() {
        clanTags.removeAll(discordClanTags);
        Iterator<String> tagIter = clanTags.iterator();
        while (tagIter.hasNext()) {
            if (createChannel(tagIter.next())) {
                tagIter.remove();
            } else {
                break;
            }
        }
    }
}
