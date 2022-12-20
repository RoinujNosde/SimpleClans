package net.sacredlabyrinth.phaed.simpleclans.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordHook;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordProvider;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.SupportedProviders;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.Bukkit.getPluginManager;

public final class ChatManager {

    private final SimpleClans plugin;
    private final Set<ChatHandler> handlers = new HashSet<>();
    private DiscordProvider discordProvider;
    private @Deprecated DiscordHook discordHook;

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    @Deprecated
    public DiscordHook getDiscordHook() {
        if (discordHook == null) {
            discordHook = new DiscordHook(plugin);
        }

        return discordHook;
    }

    public Optional<DiscordProvider> getDiscordProvider() {
        if (discordProvider == null) {
            Function<? super SupportedProviders, ? extends DiscordProvider> mapper = supportedProvider -> {
                try {
                    return (DiscordProvider) supportedProvider.getProvider().getDeclaredConstructor(SimpleClans.class).newInstance(plugin);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException ex) {
                    Object[] args = new Object[]{supportedProvider.getPluginName(), ex.getMessage()};
                    SimpleClans.getInstance().getLogger().log(Level.SEVERE, "Provider {} can't be loaded: {}", args);
                }

                return null;
            };

            discordProvider = findSupportedProvider().map(mapper).orElse(null);
            return Optional.ofNullable(discordProvider);

        }

        return Optional.of(discordProvider);
    }

    public void processChat(@NotNull SCMessage message) {
        Clan clan = Objects.requireNonNull(message.getSender().getClan(), "Clan cannot be null");

        List<ClanPlayer> receivers = new ArrayList<>();
        switch (message.getChannel()) {
            case ALLY:
                if (!plugin.getSettingsManager().is(ALLYCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(getOnlineAllyMembers(clan).stream().filter(allyMember ->
                        !allyMember.isMutedAlly()).collect(Collectors.toList()));
                receivers.addAll(clan.getOnlineMembers().stream().filter(onlineMember ->
                        !onlineMember.isMutedAlly()).collect(Collectors.toList()));
                break;
            case CLAN:
                if (!plugin.getSettingsManager().is(CLANCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(clan.getOnlineMembers().stream().filter(member -> !member.isMuted()).
                        collect(Collectors.toList()));
        }
        message.setReceivers(receivers);

        for (ChatHandler ch : handlers) {
            if (ch.canHandle(message.getSource())) {
                ch.sendMessage(message.clone());
            }
        }
    }

    public void processChat(@NotNull Source source, @NotNull Channel channel,
                            @NotNull ClanPlayer clanPlayer, String message) {
        Objects.requireNonNull(clanPlayer.getClan(), "Clan cannot be null");
        processChat(new SCMessage(source, channel, clanPlayer, message));
    }

    public String parseChatFormat(String format, SCMessage message) {
        return parseChatFormat(format, message, new HashMap<>());
    }

    public String parseChatFormat(String format, SCMessage message, Map<String, String> placeholders) {
        SettingsManager sm = plugin.getSettingsManager();
        ClanPlayer sender = message.getSender();

        String leaderColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_LEADER_COLOR"));
        String memberColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_MEMBER_COLOR"));
        String trustedColor = sm.getColored(ConfigField.valueOf(message.getChannel() + "CHAT_TRUSTED_COLOR"));

        String rank = sender.getRankId().isEmpty() ? null : ChatUtils.parseColors(sender.getRankDisplayName());
        ConfigField configField = ConfigField.valueOf(String.format("%sCHAT_RANK",
                message.getSource() == DISCORD ? "DISCORD" : message.getChannel()));
        String rankFormat = (rank != null) ? sm.getColored(configField).replace("%rank%", rank) : "";

        if (placeholders != null) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                format = format.replace("%" + e.getKey() + "%", e.getValue());
            }
        }

        String parsedFormat = ChatUtils.parseColors(format)
                .replace("%clan%", Objects.requireNonNull(sender.getClan()).getColorTag())
                .replace("%clean-tag%", sender.getClan().getTag())
                .replace("%nick-color%",
                        (sender.isLeader() ? leaderColor : sender.isTrusted() ? trustedColor : memberColor))
                .replace("%player%", sender.getName())
                .replace("%rank%", rankFormat)
                .replace("%message%", message.getContent());

        return parseWithPapi(message.getSender(), parsedFormat);
    }

    private Optional<SupportedProviders> findSupportedProvider() {
        if (!plugin.getSettingsManager().is(DISCORDCHAT_ENABLE)) {
            return Optional.empty();
        }

        Predicate<? super SupportedProviders> nonNullProvider =
                provider -> Objects.nonNull(getPluginManager().getPlugin(provider.getPluginName()));

        return Arrays.stream(SupportedProviders.values()).filter(nonNullProvider).findFirst();
    }

    private String parseWithPapi(ClanPlayer cp, String message) {
        if (getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return message;
        }
        OfflinePlayer sender = Bukkit.getOfflinePlayer(cp.getUniqueId());
        message = PlaceholderAPI.setPlaceholders(sender, message);

        // If there are still placeholders left, try to parse them
        // E.g. if the user has a placeholder as LuckPerms prefix/suffix
        if (message.contains("%")) {
            message = PlaceholderAPI.setPlaceholders(sender, message);
        }
        return message;
    }

    private void registerHandlers() {
        Set<Class<? extends ChatHandler>> chatHandlers =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.chat.handlers", ChatHandler.class);
        plugin.getLogger().log(Level.INFO, "Registering {0} chat handlers...", chatHandlers.size());

        for (Class<? extends ChatHandler> handler : chatHandlers) {
            try {
                handlers.add(handler.getConstructor().newInstance());
            } catch (ReflectiveOperationException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to register {0}: " +
                        ex.getMessage(), handler.getSimpleName());
            }
        }
    }

    private List<ClanPlayer> getOnlineAllyMembers(Clan clan) {
        return clan.getAllAllyMembers().stream().
                filter(allyPlayer -> allyPlayer.toPlayer() != null).
                collect(Collectors.toList());
    }
}
