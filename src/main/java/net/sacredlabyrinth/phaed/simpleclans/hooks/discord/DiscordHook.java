package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.*;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerJoinedClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerKickedClanEvent;
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
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;


@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
public class DiscordHook implements Listener {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();

    private final Guild guild = DiscordSRV.getPlugin().getMainGuild();
    private final List<String> textCategories;

    private static final int MAX_CHANNELS_PER_CATEGORY = 50;

    public DiscordHook(SimpleClans plugin) {
        this.plugin = plugin;
        settingsManager = plugin.getSettingsManager();
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).stream().
                filter(this::categoryExists).collect(Collectors.toList());

        setupDiscord();
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
        ClanPlayer clanPlayer = event.getClanPlayer();
        String abandonedClan = event.getClan().getName();

        Member member = getMember(clanPlayer);
        if (member == null) return;

        getChannel(abandonedClan).ifPresent(channel -> channel.getManager().removePermissionOverride(member));
    }

    @EventHandler
    public void onPlayerClanJoin(PlayerJoinedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        addPermissions(clanPlayer);
    }

    @Subscribe
    public void onPlayerLinking(AccountLinkedEvent event) {
        ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(event.getPlayer());
        if (clanPlayer == null) {
            return;
        }

        addPermissions(clanPlayer);
    }

    protected void setupDiscord() {
        Iterator<String> tagIter = getClanTags().iterator();
        while (tagIter.hasNext()) {
            if (createChannel(tagIter.next())) {
                tagIter.remove();
            } else {
                break;
            }
        }
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }

    /**
     * Creates a new {@link Category} with numbering
     */
    @Nullable
    public Category createCategory() {
        String categoryNumeric = String.valueOf(textCategories.size() == 0 ? "" : textCategories.size());
        String categoryName = settingsManager.getString(DISCORDCHAT_TEXT_CATEGORY_FORMAT).concat(" ").concat(categoryNumeric);

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
     * <p>
     * Makes a new category ({@link #createCategory()}) if no one available.
     * </p>
     */
    public boolean createChannel(@NotNull String clanTag) {
        Category availableCategory = getCategories().stream().
                filter(category -> category.getTextChannels().size() < MAX_CHANNELS_PER_CATEGORY).
                findAny().orElseGet(this::createCategory);

        return availableCategory != null && createChannel(availableCategory, clanTag);
    }

    public Optional<TextChannel> getChannel(@NotNull String channelName) {
        for (Category category : getCategories()) {
            return category.getTextChannels().stream().filter(textChannel -> textChannel.getName().equals(channelName)).findFirst();
        }

        return Optional.empty();
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
     * @param category The category, where channel will be created
     * @param clanTag  The clan tag
     * @return true, if channel was created. False, if category reached the limit.
     */
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
     * Deletes channel from discord and configuration
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
                category.delete().complete();
            }
        }

        settingsManager.set(DISCORDCHAT_TEXT_CATEGORY_IDS, textCategories);
        settingsManager.save();
    }

    /**
     * @return All linked clan players by clan tag.
     */
    public List<Long> getDiscordPlayersId(String clanTag) {
        return plugin.getClanManager().getClan(clanTag).getMembers().stream().
                map(ClanPlayer::getUniqueId).
                map(accountManager::getDiscordId).
                filter(Objects::nonNull).
                map(Long::valueOf).
                collect(Collectors.toList());
    }

    @Nullable
    public Member getMember(ClanPlayer clanPlayer) {
        String discordId = accountManager.getDiscordId(clanPlayer.getUniqueId());
        return DiscordUtil.getMemberById(discordId);
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

    public void addPermissions(@NotNull ClanPlayer clanPlayer, @NotNull String channelName,
                               Collection<Permission> grant, Collection<Permission> deny) {
        Optional<TextChannel> channel = getChannel(channelName);
        Member member = getMember(clanPlayer);

        if (member != null && channel.isPresent()) {
            channel.get().upsertPermissionOverride(member).setPermissions(grant, deny).queue();
        }
    }

    private void addPermissions(@NotNull ClanPlayer clanPlayer) {
        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        addPermissions(clanPlayer, clan.getName(), Collections.singletonList(VIEW_CHANNEL), Collections.emptyList());
    }

    @NotNull
    private List<String> getClanTags() {
        List<String> whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);

        List<String> clanTags = plugin.getClanManager().getClans().stream().
                filter(Clan::isVerified).
                filter(clan -> !getDiscordPlayersId(clan.getTag()).isEmpty() || clan.isPermanent()).
                map(Clan::getTag).
                filter(clanTag -> whitelist.isEmpty() || whitelist.contains(clanTag)).
                limit(settingsManager.getInt(DISCORDCHAT_TEXT_LIMIT)).
                collect(Collectors.toList());

        List<String> channels = guild.getTextChannels().stream().
                map(GuildChannel::getName).
                collect(Collectors.toList());

        clanTags.removeAll(channels);
        return clanTags;
    }
}
