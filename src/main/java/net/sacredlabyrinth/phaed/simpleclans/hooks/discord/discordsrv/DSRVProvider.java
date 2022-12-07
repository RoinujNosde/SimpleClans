package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.exceptions.ErrorResponseException;
import github.scarsz.discordsrv.dependencies.jda.api.requests.Response;
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordProvider;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.wrappers.*;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
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
import static net.sacredlabyrinth.phaed.simpleclans.hooks.discord.discordsrv.DSRVProvider.DiscordAction.ADD;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.Bukkit.getPluginManager;

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
public class DSRVProvider implements DiscordProvider {

    private static final int MAX_CHANNELS_PER_CATEGORY = 50;
    private static final int MAX_CHANNELS_PER_GUILD = 500;
    public static DSRVProvider hook;
    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final ChatManager chatManager;
    private final ClanManager clanManager;
    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();
    private final Guild guild = DiscordSRV.getPlugin().getMainGuild();
    private final List<String> textCategories;
    private final List<String> discordClanTags;
    private final List<String> clanTags;
    private final Role leaderRole;
    private final List<String> whitelist;

    public DSRVProvider(@NotNull SimpleClans plugin) {
        this.plugin = plugin;

        DSRVListener listener = new DSRVListener(hook);

        DiscordSRV.api.subscribe(listener);
        getPluginManager().registerEvents(listener, plugin);

        settingsManager = plugin.getSettingsManager();
        chatManager = plugin.getChatManager();
        clanManager = plugin.getClanManager();

        whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);
        clanTags = clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList());
        discordClanTags = getCachedChannels().stream().map(GuildChannel::getName).collect(Collectors.toList());
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).
                stream().filter(this::categoryExists).collect(Collectors.toList());

        leaderRole = getLeaderRole().getRole();

        setupDiscord();
    }

    public void setupDiscord() {
        clearChannels();
        resetPermissions();
        createChannels();
    }

    @Override
    public SCGuild getGuildWrapper() {
        return new SCGuild(guild);
    }

    public SimpleClans getPlugin() {
        return plugin;
    }

    /**
     * @return A leader role from guild, otherwise creates one.
     */
    @NotNull
    @Override
    public SCRole getLeaderRole() {
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

        return new SCRole(role);
    }

    @Override
    public void sendMessage(String clanTag, String formattedMessage) {
        Optional<TextChannel> channel = getCachedChannel(clanTag);
        channel.ifPresent(textChannel -> DiscordUtil.sendMessage(textChannel, formattedMessage));
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
    @Override
    public SCCategory createCategory() {
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

        return new SCCategory(category);
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
    @Override
    public void createChannel(@NotNull String clanTag)
            throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException, ChannelExistsException {
        validateChannel(clanTag);
        Map<ClanPlayer, Member> discordClanPlayers = getDiscordPlayers(clanManager.getClan(clanTag));

        if (getChannels().size() >= settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)) {
            throw new ChannelsLimitException("Discord reached the channels limit", "discord.reached.channels.limit");
        }

        Category availableCategory = getCachedCategories().stream().
                filter(category -> category.getTextChannels().size() < MAX_CHANNELS_PER_CATEGORY).
                findAny().orElseGet(() -> createCategory().getCategory());

        if (availableCategory == null) {
            throw new CategoriesLimitException("Discord reached the categories limit", "discord.reached.category.limit");
        }

        try {
            availableCategory.createTextChannel(clanTag).complete();
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
    @Override
    public boolean categoryExists(@NotNull String categoryId) {
        return guild.getCategoryById(categoryId) != null;
    }

    /**
     * Checks if a channel with the specified clan tag exists
     *
     * @see #categoryExists(String)
     */
    @Override
    public boolean channelExists(@NotNull String clanTag) {
        return getChannels().stream().map(scChannel -> scChannel.getTextChannel()).anyMatch(name -> name.equals(clanTag));
    }

    /**
     * Deletes channel from SimpleClans categories.
     * If there are no channels, removes category as well.
     *
     * @param channelName the channel name
     * @return true, if channel was deleted and false if not.
     */
    @Override
    @SuppressWarnings("UnusedReturnValue")
    public boolean deleteChannel(@NotNull String channelName) {
        if (channelExists(channelName)) {
            for (Category category : getCachedCategories()) {
                if (category.getTextChannels().size() > 0) {
                    for (TextChannel textChannel : category.getTextChannels()) {
                        if (textChannel.getName().equals(channelName)) {
                            textChannel.delete().complete();
                            return true;
                        }
                    }

                    if (category.getTextChannels().size() == 0) {
                        textCategories.remove(category.getId());
                        settingsManager.set(DISCORDCHAT_TEXT_CATEGORY_IDS, textCategories);
                        settingsManager.save();
                        category.delete().complete();
                    }
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
    public List<SCCategory> getCategories() {
        return guild.getCategoriesByName(settingsManager.getString(DISCORDCHAT_TEXT_CATEGORY_FORMAT), false)
                .stream().map(SCCategory::new)
                .collect(Collectors.toList());
    }

    /**
     * In most cases, you will use {@link #getCachedChannels()}.
     *
     * @return all channels from guild
     */
    @Override
    public List<SCChannel> getChannels() {
        return getCategories().stream().map(scCategory -> scCategory.getCategory().getTextChannels()).flatMap(Collection::stream).map(SCChannel::new).
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
    public SCMember getMember(@NotNull ClanPlayer clanPlayer) {
        String discordId = accountManager.getDiscordId(clanPlayer.getUniqueId());
        return new SCMember(DiscordUtil.getMemberById(discordId));
    }

    private void clearChannels() {
        // Removes abandoned channels
        ArrayList<String> clansToDelete = new ArrayList<>(discordClanTags);
        clansToDelete.removeAll(clanTags);
        clansToDelete.forEach(this::deleteChannel);

        // Removes invalid channels
        for (String clanTag : clanTags) {
            try {
                validateChannel(clanTag);
            } catch (InvalidChannelException ex) {
                SimpleClans.debug(ex.getMessage());
                deleteChannel(clanTag);

            } catch (ChannelExistsException | ChannelsLimitException ex) {
                SimpleClans.debug(ex.getMessage());
            }
        }
    }

    private void resetPermissions() {
        getCachedChannels().stream().
                map(TextChannel::getMemberPermissionOverrides).
                flatMap(Collection::stream).
                filter(permissionOverride -> !Objects.equals(permissionOverride.getPermissionHolder(), guild.getPublicRole())).
                filter(permissionOverride -> !Objects.equals(permissionOverride.getMember(), guild.getSelfMember())).
                forEach(permissionOverride -> permissionOverride.delete().queue());

        clanTags.stream().map(clanManager::getClan).
                filter(Objects::nonNull).
                map(Clan::getMembers).
                flatMap(Collection::stream).
                forEach(clanPlayer -> {
                    Member member = getMember(clanPlayer).getMember();
                    Clan clan = clanPlayer.getClan();
                    if (member != null && clan != null) {
                        updateViewPermission(member, clan, ADD);
                    }
                });
    }

    private void createChannels() {
        if (!settingsManager.is(DISCORDCHAT_AUTO_CREATION)) {
            return;
        }
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
    void sendPrivateMessage(TextChannel textChannel, Message eventMessage, String message) {
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
        if (discordClanPlayers.size() == 0) {
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
            Member member = getMember(cp).getMember();
            if (member != null) {
                discordClanPlayers.put(cp, member);
            }
        }
        return discordClanPlayers;
    }

    void updateLeaderRole(@NotNull Member member, @NotNull ClanPlayer clanPlayer, DiscordAction action) {
        if (!clanPlayer.isLeader()) {
            return;
        }

        if (action == ADD) {
            guild.addRoleToMember(member, leaderRole).queue();
        } else {
            guild.removeRoleFromMember(member, leaderRole).queue();
        }
    }

    void updateViewPermission(@NotNull Member member, @NotNull Clan clan, DiscordAction action) {
        Optional<TextChannel> channel = getCachedChannel(clan.getTag());
        if (channel.isPresent()) {
            TextChannel textChannel = channel.get();

            if (action == ADD) {
                textChannel.upsertPermissionOverride(member).
                        setPermissions(Collections.singletonList(VIEW_CHANNEL), Collections.emptyList()).queue();
            } else {
                textChannel.getManager().removePermissionOverride(member).queue();
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean createChannelSilently(ClanPlayer clanPlayer) {
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
