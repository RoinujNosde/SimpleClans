package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.emoji.EmojiParser;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.requests.Response;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static github.scarsz.discordsrv.dependencies.jda.api.Permission.MANAGE_CHANNEL;
import static github.scarsz.discordsrv.dependencies.jda.api.Permission.VIEW_CHANNEL;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordHook.DiscordAction.ADD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordHook.DiscordAction.REMOVE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * Hooks SimpleClans and Discord, using DiscordSRV.
 * <p>
 * On server' startup:
 * </p>
 * <ul>
 *  <li>Creates categories and channels, respecting discord's limits.</li>
 *  <li>Removes invalid channels, resets permissions and roles.</li>
 * </ul>
 * <p>
 * Manages events:
 * </p>
 * <ul>
 *     <li>Clan creation/deletion</li>
 *     <li>ClanPlayer joining/resigning</li>
 *     <li>Player linking/unlinking</li>
 *     <li>ClanPlayer promoting/demoting</li>
 * </ul>
 * <p>
 * Currently, works with clan chat only.
 */
public class DiscordHook implements Listener {

    private static final int MAX_CHANNELS_PER_CATEGORY = 50;
    private static final int MAX_CHANNELS_PER_GUILD = 500;
    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final ChatManager chatManager;
    private final ClanManager clanManager;
    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();
    private final Guild guild = DiscordSRV.getPlugin().getMainGuild();
    private final List<String> textCategories;
    private final List<String> clanTags;
    private final Role leaderRole;
    private final List<String> whitelist;

    public DiscordHook(SimpleClans plugin) {
        this.plugin = plugin;
        settingsManager = plugin.getSettingsManager();
        chatManager = plugin.getChatManager();
        clanManager = plugin.getClanManager();

        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).stream().
                filter(this::categoryExists).collect(Collectors.toList());
        whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);

        clanTags = clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList());

        leaderRole = getLeaderRole();

        setupDiscord();
    }

    @Subscribe
    public void onMessageReceived(DiscordGuildMessageReceivedEvent event) {
        Optional<TextChannel> channel = getCachedChannel(event.getChannel().getName());

        if (channel.isPresent()) {
            Message eventMessage = event.getMessage();
            User Author = event.getAuthor();
            TextChannel textChannel = channel.get();
            UUID uuid = accountManager.getUuid(Author.getId());

            if (uuid == null) {
                sendPrivateMessage(textChannel, eventMessage, lang("you.did.not.link.your.account"));
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

            if (!Objects.equals(textChannel.getName(), clan.getTag())) {
                String channelLink = "<#" + textChannel.getId() + ">";
                sendPrivateMessage(textChannel, eventMessage, lang("cannot.send.discord.message", clanPlayer, channelLink));
                return;
            }
            // DiscordSRV start
            String emojiBehavior = DiscordSRV.config().getString("DiscordChatChannelEmojiBehavior");

            boolean hideEmoji = emojiBehavior.equalsIgnoreCase("hide");
            boolean nameEmoji = emojiBehavior.equalsIgnoreCase("name");

            Component component = MessageUtil.reserializeToMinecraft(eventMessage.getContentRaw());
            String message = MessageUtil.toLegacy(component);

            if (hideEmoji && StringUtils.isBlank(EmojiParser.removeAllEmojis(message))) {
                DiscordSRV.debug("Ignoring message from "
                        + Author.getName() +
                        " because it became completely blank after removing unicode emojis");
                return;
            }

            if (hideEmoji) {
                // remove all emojis
                message = EmojiParser.removeAllEmojis(message);
            } else if (nameEmoji) {
                // parse emojis from unicode back to :code:
                message = EmojiParser.parseToAliases(message);
            }
            // DiscordSRV end
            chatManager.processChat(DISCORD, CLAN, clanPlayer, message);
        }
    }

    @EventHandler
    public void onClanDisband(DisbandClanEvent event) {
        deleteChannel(event.getClan().getTag());
    }

    @EventHandler
    public void onClanCreate(CreateClanEvent event) {
        try {
            if (settingsManager.is(DISCORDCHAT_AUTO_CREATION)) {
                createChannel(event.getClan().getTag());
            }
        } catch (DiscordHookException ex) {
            // Clan is not following the conditions, categories are fulled or discord reaches the limit, nothing to do here.
            SimpleClans.debug(ex.getMessage());
        }
    }

    @EventHandler
    public void onPlayerClanLeave(PlayerKickedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Clan clan = event.getClan();
        Member member = getMember(clanPlayer);
        if (member == null || clan == null) {
            return;
        }

        updateViewPermission(member, clan, REMOVE);
        updateLeaderRole(member, clanPlayer, REMOVE);
    }

    @EventHandler
    public void onPlayerClanJoin(PlayerJoinedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Clan clan = event.getClan();
        Member member = getMember(clanPlayer);
        if (member == null || clan == null) {
            return;
        }

        if (!createChannelSilently(clanPlayer)) {
            return;
        }

        updateViewPermission(member, clan, ADD);
    }

    @EventHandler
    public void onPlayerPromote(PlayerPromoteEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Member member = getMember(clanPlayer);
        if (member == null) {
            return;
        }

        updateLeaderRole(member, clanPlayer, ADD);
    }

    @EventHandler
    public void onPlayerDemote(PlayerDemoteEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Member member = getMember(clanPlayer);
        if (member == null) {
            return;
        }

        updateLeaderRole(member, clanPlayer, REMOVE);
    }

    @Subscribe
    public void onPlayerLinking(AccountLinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        Member member = guild.getMember(event.getUser());
        if (clanPlayer == null || member == null) {
            return;
        }

        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        if (!createChannelSilently(clanPlayer)) {
            return;
        }

        updateViewPermission(member, clan, ADD);
        updateLeaderRole(member, clanPlayer, ADD);
    }

    @Subscribe
    public void onPlayerUnlinking(AccountUnlinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        Member member = guild.getMember(event.getDiscordUser());
        if (clanPlayer == null || clanPlayer.getClan() == null || member == null) {
            return;
        }

        updateViewPermission(member, clanPlayer.getClan(), REMOVE);
        updateLeaderRole(member, clanPlayer, REMOVE);
    }

    protected void setupDiscord() {
        Map<String, TextChannel> discordTagChannels = getCachedChannels().stream().
                collect(Collectors.toMap(TextChannel::getName, textChannel -> textChannel));
        SimpleClans.debug("DiscordTagChannels before clearing: " + String.join(",", discordTagChannels.keySet()));

        clearChannels(discordTagChannels);
        SimpleClans.debug("DiscordTagChannels after clearing: " + String.join(",", discordTagChannels.keySet()));

        resetPermissions(discordTagChannels);

        SimpleClans.debug("ClanTags before creating: " + String.join(",", clanTags));
        createChannels(discordTagChannels);
        SimpleClans.debug("ClanTags after creating: " + String.join(",", clanTags));
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }

    /**
     * @return A leader role from guild, otherwise creates one.
     */
    @NotNull
    public Role getLeaderRole() {
        Role role = guild.getRoleById(settingsManager.getString(DISCORDCHAT_LEADER_ID));

        if (role == null || !role.getName().equals(settingsManager.getString(DISCORDCHAT_LEADER_ROLE))) {
            role = guild.createRole().
                    setName(settingsManager.getString(DISCORDCHAT_LEADER_ROLE)).
                    setColor(getLeaderColor()).
                    setMentionable(true).
                    complete();

            settingsManager.set(DISCORDCHAT_LEADER_ID, role.getId());
            settingsManager.save();
        }

        return role;
    }

    /**
     * @return A leader color from configuration
     */
    public Color getLeaderColor() {
        String[] colors = settingsManager.getString(DISCORDCHAT_LEADER_COLOR).
                replaceAll("\\s", "").split(",");
        try {
            int red = Integer.parseInt(colors[0]);
            int green = Integer.parseInt(colors[1]);
            int blue = Integer.parseInt(colors[2]);
            int alpha = Integer.parseInt(colors[3]);

            return new Color(red, green, blue, alpha);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning("Color is invalid, using default color: " + ex.getMessage());
            return Color.RED;
        }
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
                    addMemberPermissionOverride(guild.getSelfMember().getIdLong(),
                            Arrays.asList(VIEW_CHANNEL, MANAGE_CHANNEL),
                            Collections.emptyList()).
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
     * Creates a new {@link TextChannel} in available SimpleClans' categories,
     * otherwise creates one.
     *
     * <p>Sets positive {@link Permission#VIEW_CHANNEL} permission to all linked clan members.</p>
     *
     * @param clanTag the clan tag
     * @throws InvalidChannelException  clan is not verified or permanent,
     *                                  no one member is linked or clan is not in the whitelist.
     * @throws ChannelExistsException   if channel is already exist
     * @throws CategoriesLimitException if categories reached the limit.
     * @throws ChannelsLimitException   if discord reached the channels limit.
     */
    public void createChannel(@NotNull String clanTag)
            throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException, ChannelExistsException {
        validateChannel(clanTag);
        Map<ClanPlayer, Member> discordClanPlayers = getDiscordPlayers(clanManager.getClan(clanTag));

        if (getChannels().size() >= settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)) {
            throw new ChannelsLimitException("Discord reached the channels limit", "discord.reached.channels.limit");
        }

        Category availableCategory = getCachedCategories().stream().
                filter(category -> category.getTextChannels().size() < MAX_CHANNELS_PER_CATEGORY).
                findAny().orElseGet(this::createCategory);

        if (availableCategory == null) {
            throw new CategoriesLimitException("Discord reached the categories limit", "discord.reached.category.limit");
        }

        try {
            availableCategory.createTextChannel(clanTag).complete();
            SimpleClans.debug(String.format("[%s] Creating a discord text channel for %s clan", Thread.currentThread().getId(), clanTag));
        } catch (ErrorResponseException ex) {
            Response response = ex.getResponse();
            plugin.getLogger().warning(String.format("Could not create a channel for clan %s, error %d - %s",
                    clanTag, response.code, response.message));
            return;
        }

        for (Map.Entry<ClanPlayer, Member> entry : discordClanPlayers.entrySet()) {
            // The map is formed from clan#getMembers (so the clan exists)
            //noinspection ConstantConditions
            updateViewPermission(entry.getValue(), entry.getKey().getClan(), ADD);
            updateLeaderRole(entry.getValue(), entry.getKey(), ADD);
        }
    }

    /**
     * Retrieves channel in SimpleClans categories.
     *
     * @param channelName the channel name
     * @return the channel
     * @see #getCachedCategories() retreive categories.
     */
    public Optional<TextChannel> getCachedChannel(@NotNull String channelName) {
        return getCachedChannels().stream().filter(textChannel -> textChannel.getName().equals(channelName)).findFirst();
    }

    /**
     * Checks if a category can be obtained by id.
     *
     * @param categoryId the category id
     * @return true if the category exists
     * @see #channelExists(String)
     */
    public boolean categoryExists(String categoryId) {
        return guild.getCategoryById(categoryId) != null;
    }

    /**
     * Checks if a channel with the specified clan tag exists
     *
     * @see #categoryExists(String)
     */
    public boolean channelExists(String clanTag) {
        return getChannel(clanTag).isPresent();
    }

    /**
     * Retrieves the channel in SimpleClans categories.
     *
     * @param channelName the channel name
     * @return the channel
     * @see #getCachedChannel(String) retreive the <b>cached</b> channel.
     */
    public Optional<TextChannel> getChannel(@NotNull String channelName) {
        return getChannels().stream().filter(textChannel -> textChannel.getName().equals(channelName)).findAny();
    }

    /**
     * Deletes channel from SimpleClans categories.
     * If there are no channels, removes category as well.
     *
     * @param channelName the channel name
     * @return true, if channel was deleted and false if not.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteChannel(@NotNull String channelName) {
        boolean deleted = false;

        if (channelExists(channelName)) {
            for (Category category : getCachedCategories()) {
                if (!category.getTextChannels().isEmpty()) {
                    for (TextChannel textChannel : category.getTextChannels()) {
                        if (textChannel.getName().equals(channelName)) {
                            textChannel.delete().complete();
                            deleted = true;
                            break;
                        }
                    }

                    if (category.getTextChannels().isEmpty()) {
                        textCategories.remove(category.getId());
                        settingsManager.set(DISCORDCHAT_TEXT_CATEGORY_IDS, textCategories);
                        settingsManager.save();
                        category.delete().complete();
                    }

                    return deleted;
                }
            }
        }

        return false;
    }

    /**
     * @return categories from config
     */
    public List<Category> getCachedCategories() {
        return textCategories.stream().
                filter(this::categoryExists).
                map(guild::getCategoryById).
                collect(Collectors.toList());
    }

    /**
     * In most cases, you will use {@link #getCachedCategories()}.
     *
     * @return categories from guild
     */
    public List<Category> getCategories() {
        return guild.getCategoriesByName(settingsManager.getString(DISCORDCHAT_TEXT_CATEGORY_FORMAT), false);
    }

    /**
     * In most cases, you will use {@link #getCachedChannels()}.
     *
     * @return all channels from guild
     */
    public List<TextChannel> getChannels() {
        return getCategories().stream().map(Category::getTextChannels).flatMap(Collection::stream).
                collect(Collectors.toList());
    }

    /**
     * @return All channels in categories
     */
    public List<TextChannel> getCachedChannels() {
        return getCachedCategories().stream().map(Category::getTextChannels).flatMap(Collection::stream).
                collect(Collectors.toList());
    }

    @Nullable
    public Member getMember(@NotNull ClanPlayer clanPlayer) {
        String discordId = accountManager.getDiscordId(clanPlayer.getUniqueId());
        return DiscordUtil.getMemberById(discordId);
    }

    private void clearChannels(Map<String, TextChannel> discordTagChannels) {
        // Removes abandoned channels
        ArrayList<String> clansToDelete = new ArrayList<>(discordTagChannels.keySet());
        clansToDelete.removeAll(clanTags);
        clansToDelete.forEach(clanChannel -> {
            deleteChannel(clanChannel);
            discordTagChannels.remove(clanChannel);
        });

        // Removes invalid channels
        Iterator<String> iterator = discordTagChannels.keySet().iterator();
        while (iterator.hasNext()) {
            String clanChannel = iterator.next();
            try {
                validateChannel(clanChannel);
            } catch (InvalidChannelException ex) {
                SimpleClans.debug(ex.getMessage());
                deleteChannel(clanChannel);
                iterator.remove();
            } catch (ChannelExistsException | ChannelsLimitException ex) {
                SimpleClans.debug(ex.getMessage());
            }
        }
    }

    private void resetPermissions(Map<String, TextChannel> discordClanChannels) {
        for (Map.Entry<String, TextChannel> channelEntry : discordClanChannels.entrySet()) {
            TextChannel channel = channelEntry.getValue();
            Clan clan = clanManager.getClan(channelEntry.getKey());
            Map<ClanPlayer, Member> discordPlayers = getDiscordPlayers(clan);

            for (Member member : discordPlayers.values()) {
                PermissionOverride override = channel.getPermissionOverride(member);
                if (override != null) {
                    override.delete().queue(afterSuccess -> updateViewPermission(member, channel, ADD));
                }
            }
        }
    }

    private void createChannels(Map<String, TextChannel> discordClanChannels) {
        if (!settingsManager.is(DISCORDCHAT_AUTO_CREATION)) {
            return;
        }

        // Removes already used discord channels from creation
        clanTags.removeAll(discordClanChannels.keySet());

        for (String clan : clanTags) {
            try {
                createChannel(clan);
            } catch (CategoriesLimitException | ChannelsLimitException ex) {
                SimpleClans.debug(ex.getMessage());
                break;
            } catch (InvalidChannelException | ChannelExistsException ignored) {
                // There is already debug on #clearChannels
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sendPrivateMessage(TextChannel textChannel, Message eventMessage, String message) {
        RestAction<PrivateChannel> privateChannelAction = eventMessage.getAuthor().openPrivateChannel();
        textChannel.deleteMessageById(eventMessage.getId()).queue(unused ->
                privateChannelAction.flatMap(privateChannel -> privateChannel.sendMessage(message)));
    }

    private void validateChannel(@NotNull String clanTag)
            throws InvalidChannelException, ChannelExistsException, ChannelsLimitException {
        Clan clan = clanManager.getClan(clanTag);
        if (clan == null) {
            throw new InvalidChannelException(String.format("Clan %s is null", clanTag));
        }
        if (!clan.isVerified() && !clan.isPermanent()) {
            throw new InvalidChannelException(String.format("Clan %s is not verified or permanent", clanTag));
        }

        Map<ClanPlayer, Member> discordClanPlayers = getDiscordPlayers(clan);
        if (discordClanPlayers.isEmpty()) {
            throw new InvalidChannelException(String.format("Clan %s doesn't have any linked players", clanTag),
                    "your.clan.doesnt.have.any.linked.player");
        }

        if (discordClanPlayers.size() < settingsManager.getInt(DISCORDCHAT_MINIMUM_LINKED_PLAYERS)) {
            throw new InvalidChannelException(String.format("Clan %s doesn't have minimum linked players", clanTag),
                    "your.clan.doesnt.have.minimum.linked.player");
        }

        if (!whitelist.isEmpty() && !whitelist.contains(clan.getTag())) {
            throw new InvalidChannelException(String.format("Clan %s is not listed on the whitelist", clanTag),
                    "your.clan.is.not.on.the.whitelist");
        }

        if (channelExists(clanTag)) {
            throw new ChannelExistsException(String.format("Channel %s is already exist", clanTag),
                    "your.clan.already.has.channel");
        }
    }

    @NotNull
    private Map<ClanPlayer, Member> getDiscordPlayers(@NotNull Clan clan) {
        Map<ClanPlayer, Member> discordClanPlayers = new HashMap<>();
        for (ClanPlayer cp : clan.getMembers()) {
            Member member = getMember(cp);
            if (member != null) {
                discordClanPlayers.put(cp, member);
            }
        }
        return discordClanPlayers;
    }

    private void updateLeaderRole(@NotNull Member member, @NotNull ClanPlayer clanPlayer, DiscordAction action) {
        if (action == ADD && clanPlayer.isLeader()) {
            guild.addRoleToMember(member, leaderRole).queue();
        } else {
            guild.removeRoleFromMember(member, leaderRole).queue();
        }
    }

    private void updateViewPermission(@Nullable Member member, @NotNull GuildChannel channel, @NotNull DiscordAction action) {
        if (member == null) {
            return;
        }

        if (action == ADD) {
            channel.upsertPermissionOverride(member).
                    setPermissions(Collections.singletonList(VIEW_CHANNEL), Collections.emptyList()).queue();
        } else {
            channel.getManager().removePermissionOverride(member).queue();
        }
    }

    private void updateViewPermission(@NotNull Member member, @NotNull Clan clan, DiscordAction action) {
        String tag = clan.getTag();
        Optional<TextChannel> channel = getCachedChannel(tag).map(Optional::of).orElse(getChannel(tag));
        if (channel.isPresent()) {
            TextChannel textChannel = channel.get();
            updateViewPermission(member, textChannel, action);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean createChannelSilently(ClanPlayer clanPlayer) {
        Clan clan = clanPlayer.getClan();
        if (clan == null || !settingsManager.is(DISCORDCHAT_AUTO_CREATION)) {
            return false;
        }

        try {
            createChannel(clan.getTag());
        } catch (DiscordHookException ex) {
            // Clan is not following the conditions, categories are fulled or discord reaches the limit, nothing to do here.
            SimpleClans.debug(ex.getMessage());
        }

        return true;
    }

    enum DiscordAction {
        ADD, REMOVE
    }
}
