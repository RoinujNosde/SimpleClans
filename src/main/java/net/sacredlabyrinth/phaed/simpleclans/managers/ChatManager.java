package net.sacredlabyrinth.phaed.simpleclans.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.Permission;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Category;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.GuildChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.requests.restaction.ChannelAction;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

@SuppressWarnings({"ResultOfMethodCallIgnored", "UnusedReturnValue"})
public final class ChatManager {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final Set<ChatHandler> handlers = new HashSet<>();

    private Guild guild;
    private List<String> textCategories;

    private static final int MAX_CHANNELS_PER_CATEGORY = 50;

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        this.settingsManager = plugin.getSettingsManager();
        registerHandlers();
    }

    @Subscribe
    public void setupDiscord(DiscordReadyEvent event) {
        guild = DiscordSRV.getPlugin().getMainGuild();
        List<String> clanTags = getClanTags();
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).stream().
                filter(this::categoryExists).collect(Collectors.toList());

        int categoriesToCreate = ((int) Math.ceil((double) clanTags.size() / MAX_CHANNELS_PER_CATEGORY)) - textCategories.size();
        for (int i = 0; i < categoriesToCreate; i++) {
            createCategory();
        }

        for (String textCategory : textCategories) {
            Category category = Objects.requireNonNull(guild.getCategoryById(textCategory),
                    "Category " + textCategory + " can't be created!");
            Iterator<String> tagIter = clanTags.iterator();
            while (tagIter.hasNext()) {
                if (createChannel(category, tagIter.next())) {
                    tagIter.remove();
                } else {
                    break;
                }
            }
        }
    }

    public boolean createChannel(@NotNull Category category, @NotNull String clanTag) {
        ChannelAction<TextChannel> action = category.createTextChannel(clanTag);

        if (category.getTextChannels().size() >= MAX_CHANNELS_PER_CATEGORY) {
            return false;
        }

        for (Long discordId : getDiscordPlayersId(clanTag)) {
            action.addMemberPermissionOverride(discordId,
                    Collections.singletonList(Permission.VIEW_CHANNEL),
                    Collections.emptyList());
        }
        action.complete();
        return true;
    }

    public boolean categoryExists(String categoryId) {
        return guild.getCategoryById(categoryId) != null;
    }

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
                            Collections.singletonList(Permission.VIEW_CHANNEL)).
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

    public void processChat(Source source, @NotNull Channel channel, @NotNull ClanPlayer clanPlayer, String message) {
        Clan clan = Objects.requireNonNull(clanPlayer.getClan(), "Clan cannot be null");
        List<ClanPlayer> receivers = new ArrayList<>();

        switch (channel) {
            case ALLY:
                if (!plugin.getSettingsManager().is(ALLYCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(getOnlineAllyMembers(clan).stream().filter(allymember ->
                        !allymember.isMutedAlly()).collect(Collectors.toList()));
                receivers.addAll(clan.getOnlineMembers().stream().filter(allymember ->
                        !allymember.isMutedAlly()).collect(Collectors.toList()));
                break;
            case CLAN:
                if (!plugin.getSettingsManager().is(CLANCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(clan.getOnlineMembers().stream().filter(member -> !member.isMuted()).collect(Collectors.toList()));
        }

        SCMessage scMessage;
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            scMessage = new SCMessage(source, channel, clanPlayer,
                    PlaceholderAPI.setPlaceholders(clanPlayer.toPlayer(), message), receivers);
        } else {
            scMessage = new SCMessage(source, channel, clanPlayer, message, receivers);
        }

        for (ChatHandler ch : handlers) {
            if (ch.canHandle(source)) {
                ch.sendMessage(scMessage);
            }
        }
    }

    public String parseChatFormat(String format, SCMessage message) {
        return parseChatFormat(format, message, new HashMap<>());
    }

    public List<Long> getDiscordPlayersId(String clanTag) {
        AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        return plugin.getClanManager().getClan(clanTag).getMembers().stream().
                map(ClanPlayer::getUniqueId).
                map(accountLinkManager::getDiscordId).
                filter(Objects::nonNull).
                map(Long::valueOf).
                collect(Collectors.toList());
    }

    public String parseChatFormat(String format, SCMessage message, Map<String, String> placeholders) {
        SettingsManager sm = plugin.getSettingsManager();
        ClanPlayer sender = message.getSender();
        Channel channel = message.getChannel();

        String leaderColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_LEADER_COLOR"));
        String memberColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_MEMBER_COLOR"));
        String trustedColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_TRUSTED_COLOR"));

        String rank = sender.getRankId().isEmpty() ? null : ChatUtils.parseColors(sender.getRankDisplayName());
        String rankFormat = (rank != null) ?
                sm.getColored(ConfigField.valueOf(channel + "_RANK")).replace("%rank%", rank) : "";

        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                format = format.replace("%" + e.getKey() + "%", e.getValue());
            }
        }

        return ChatUtils.parseColors(format)
                .replace("%clan%", Objects.requireNonNull(sender.getClan()).getColorTag())
                .replace("%nick-color%", (sender.isLeader() ?
                        leaderColor : sender.isTrusted() ? trustedColor : memberColor))
                .replace("%player%", sender.getName())
                .replace("%rank%", rankFormat)
                .replace("%message%", message.getContent());
    }

    @NotNull
    public Guild getGuild() {
        return guild;
    }

    private void registerHandlers() {
        Set<Class<? extends ChatHandler>> chatHandlers =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.chat.handlers", ChatHandler.class);
        plugin.getLogger().log(Level.INFO, "Registering {0} chat handlers...", chatHandlers.size());

        for (Class<? extends ChatHandler> handler : chatHandlers) {
            try {
                if (plugin.getSettingsManager().is(DISCORDCHAT_ENABLE)) {
                    continue;
                }
                handlers.add(handler.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to register {0} handler: " +
                        ex.getMessage(), handler.getSimpleName());
            }
        }
    }

    private List<ClanPlayer> getOnlineAllyMembers(Clan clan) {
        return clan.getAllAllyMembers().stream().filter(allyPlayer -> allyPlayer.toPlayer() != null).collect(Collectors.toList());
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
