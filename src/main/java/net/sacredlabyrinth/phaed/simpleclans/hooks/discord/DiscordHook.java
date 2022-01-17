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
import github.scarsz.discordsrv.dependencies.jda.api.requests.RestAction;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import github.scarsz.discordsrv.util.MessageUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.CategoriesLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.ChannelsLimitException;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.InvalidChannelException;
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
 *     <li>Player linking</li>
 *     <li>ClanPlayer promoting/demoting</li>
 * </ul>
 * <p>
 * Currently, works with clan chat only.
 */
public class DiscordHook implements Listener {

    private static final int MAX_CHANNELS_PER_CATEGORY = 1;
    private static final int MAX_CHANNELS_PER_GUILD = 500;

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

    public DiscordHook(SimpleClans plugin) {
        this.plugin = plugin;
        settingsManager = plugin.getSettingsManager();
        chatManager = plugin.getChatManager();
        clanManager = plugin.getClanManager();
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).stream().
                filter(this::categoryExists).collect(Collectors.toList());
        whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);

        clanTags = clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList());
        discordClanTags = getCachedChannels().stream().map(GuildChannel::getName).collect(Collectors.toList());

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
            createChannel(event.getClan().getTag());
        } catch (InvalidChannelException | CategoriesLimitException | ChannelsLimitException ignored) {
            // Clan is not following the conditions, categories are fulled or discord reaches the limit, nothing to do here.
        }
    }

    @EventHandler
    public void onPlayerClanLeave(PlayerKickedClanEvent event) {
        DiscordClanPlayer dsClanPlayer = new DiscordClanPlayer(event.getClanPlayer());

        updatePermissions(dsClanPlayer, event.getClan(), REMOVE);
        updateRole(dsClanPlayer, REMOVE);
    }

    @EventHandler
    public void onPlayerClanJoin(PlayerJoinedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        // TODO: Channel creation?
        updatePermissions(new DiscordClanPlayer(event.getClanPlayer()), clanPlayer.getClan(), ADD);
    }

    @EventHandler
    public void onPlayerPromote(PlayerPromoteEvent event) {
        updateRole(new DiscordClanPlayer(event.getClanPlayer()), ADD);
    }

    @EventHandler
    public void onPlayerDemote(PlayerDemoteEvent event) {
        updateRole(new DiscordClanPlayer(event.getClanPlayer()), REMOVE);
    }

    @Subscribe
    public void onPlayerLinking(AccountLinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        if (clanPlayer == null) {
            return;
        }

        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        if (!channelExists(clan.getTag())) {
            try {
                createChannel(clan.getTag());
            } catch (InvalidChannelException | CategoriesLimitException | ChannelsLimitException ignored) {
                // Clan is not following the conditions, categories are fulled or discord reaches the limit, nothing to do here.
            }
        }

        updatePermissions(new DiscordClanPlayer(clanPlayer), clanPlayer.getClan(), ADD);
    }

    @Subscribe
    public void onPlayerUnlinking(AccountUnlinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        Member member = guild.getMember(event.getDiscordUser());
        if (clanPlayer == null || member == null) {
            return;
        }

        DiscordClanPlayer discordClanPlayer = new DiscordClanPlayer(clanPlayer, member);
        // TODO: Safety-delete?
        updatePermissions(discordClanPlayer, clanPlayer.getClan(), REMOVE);
        updateRole(discordClanPlayer, REMOVE);
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
     * Creates a new {@link ClanPlayer.Channel} in available SimpleClans' categories,
     * otherwise creates one.
     *
     * <p>Sets positive {@link Permission#VIEW_CHANNEL} permission to all linked clan members.</p>
     *
     * @throws InvalidChannelException  clan is not verified or permanent,
     *                                  no one member is linked or clan is not in the whitelist.
     * @throws CategoriesLimitException if categories reached the limit.
     * @throws ChannelsLimitException   if discord reached the channels limit.
     */
    public void createChannel(@NotNull String clanTag)
            throws InvalidChannelException, CategoriesLimitException, ChannelsLimitException {
        if (channelExists(clanTag)) {
            throw new InvalidChannelException("Channel %s is already exist", clanTag);
        }

        Clan clan = clanManager.getClan(clanTag);
        if (clan == null) {
            return;
        }

        if (!clan.isVerified() && !clan.isPermanent()) {
            throw new InvalidChannelException("Clan %s is not verified or permanent", clanTag);
        }

        if (clan.getMembers().stream().map(DiscordClanPlayer::new).noneMatch(dsClanPlayer -> dsClanPlayer.getMember() != null)) {
            throw new InvalidChannelException("Clan %s doesn't have any linked players", clanTag);
        }

        if (!whitelist.isEmpty() && !whitelist.contains(clan.getTag())) {
            throw new InvalidChannelException("Clan %s is not listed on the whitelist", clanTag);
        }

        if (getChannels().size() >= settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)) {
            throw new ChannelsLimitException();
        }

        Category availableCategory = getCachedCategories().stream().
                filter(category -> category.getTextChannels().size() < MAX_CHANNELS_PER_CATEGORY).
                findAny().orElseGet(this::createCategory);

        if (availableCategory == null) {
            throw new CategoriesLimitException();
        }

        TextChannel textChannel = availableCategory.createTextChannel(clanTag).complete();

        for (ClanPlayer clanMember : clan.getMembers()) {
            updatePermissions(new DiscordClanPlayer(clanMember), clan, ADD);
        }
    }

    /**
     * Retrieves channel in SimpleClans categories.
     *
     * @see #getCachedCategories() retreive categories.
     */
    public Optional<TextChannel> getCachedChannel(@NotNull String channelName) {
        return getCachedChannels().stream().filter(textChannel -> textChannel.getName().equals(channelName)).findFirst();
    }

    /**
     * Checks if a category can be obtained by id.
     *
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
        return getChannels().stream().map(TextChannel::getName).anyMatch(name -> name.equals(clanTag));
    }

    /**
     * Deletes channel from SimpleClans categories.
     * If there are no channels, removes category as well.
     */
    public void deleteChannel(@NotNull String channelName) {
        for (Category category : getCachedCategories()) {
            if (category.getTextChannels().size() > 0) {
                for (TextChannel textChannel : category.getTextChannels()) {
                    if (textChannel.getName().equals(channelName)) {
                        textChannel.delete().complete();
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

    /**
     * Creates a new {@link ClanPlayer.Channel} in defined {@link Category}
     *
     * <p>Sets positive {@link Permission#VIEW_CHANNEL} permission to all linked clan members.</p>
     *
     * @throws InvalidChannelException a clan is not verified or permanent,
     *                                 no one member is linked or clan is not in the whitelist.
     * @throws ChannelsLimitException  if discord reached the channels limit.
     */
    private void createChannel(@NotNull Category category, @NotNull String clanTag)
            throws InvalidChannelException, ChannelsLimitException {
        Clan clan = clanManager.getClan(clanTag);
        if (clan == null) {
            return;
        }

        if (!clan.isVerified() && !clan.isPermanent()) {
            throw new InvalidChannelException("Clan %s is not verified or permanent", clanTag);
        }

        if (clan.getMembers().stream().noneMatch(clanPlayer -> getMember(clanPlayer) != null)) {
            throw new InvalidChannelException("Clan %s doesn't have any linked players", clanTag);
        }

        if (!whitelist.isEmpty() && !whitelist.contains(clan.getTag())) {
            throw new InvalidChannelException("Clan %s is not listed on the whitelist", clanTag);
        }

        if (getChannels().size() >= settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)) {
            throw new ChannelsLimitException();
        }

        TextChannel textChannel = category.createTextChannel(clanTag).complete();

        for (ClanPlayer clanMembers : clan.getMembers()) {
            updatePermissions(new DiscordClanPlayer(clanMembers), clan, ADD);
        }
    }

    private void removeInvalidChannels() {
        ArrayList<String> clansToDelete = new ArrayList<>(discordClanTags);
        clansToDelete.removeAll(clanTags);
        clansToDelete.forEach(this::deleteChannel);
    }

    private void resetPermissions() {
        getCachedChannels().stream().
                map(TextChannel::getMemberPermissionOverrides).
                flatMap(Collection::stream).
                filter(permissionOverride -> !Objects.equals(permissionOverride.getPermissionHolder(), guild.getPublicRole())).
                forEach(permissionOverride -> permissionOverride.delete().queue());

        clanTags.stream().map(clanManager::getClan).
                filter(Objects::nonNull).
                map(Clan::getMembers).
                flatMap(Collection::stream).
                forEach(clanPlayer -> updatePermissions(new DiscordClanPlayer(clanPlayer), clanPlayer.getClan(), ADD));
    }

    private void createChannels() {
        clanTags.removeAll(discordClanTags);
        for (String clan : clanTags) {
            try {
                createChannel(clan);
            } catch (CategoriesLimitException | ChannelsLimitException ex) {
                break;
            } catch (InvalidChannelException ignored) {
                // Clan is not following the conditions, nothing to do here.
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void sendPrivateMessage(TextChannel textChannel, Message eventMessage, String message) {
        RestAction<PrivateChannel> privateChannelAction = eventMessage.getAuthor().openPrivateChannel();
        textChannel.deleteMessageById(eventMessage.getId()).queue(unused -> {
            String channelLink = "<#" + textChannel.getId() + ">";
            privateChannelAction.flatMap(privateChannel -> privateChannel.sendMessage(message));
        });
    }

    private void updateRole(DiscordClanPlayer dsClanPlayer, DiscordAction action) {
        if (dsClanPlayer.getClanPlayer() == null || dsClanPlayer.getMember() == null) {
            return;
        }

        if (!dsClanPlayer.getClanPlayer().isLeader()) {
            return;
        }

        if (action == ADD) {
            guild.addRoleToMember(dsClanPlayer.getMember(), leaderRole).queue();
        } else {
            guild.removeRoleFromMember(dsClanPlayer.getMember(), leaderRole).queue();
        }
    }

    private void updatePermissions(@NotNull DiscordClanPlayer dsClanPlayer, @Nullable Clan clan, DiscordAction action) {
        Member member = dsClanPlayer.getMember();
        if (clan == null || member == null) {
            return;
        }

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

    enum DiscordAction {
        ADD, REMOVE
    }
}
