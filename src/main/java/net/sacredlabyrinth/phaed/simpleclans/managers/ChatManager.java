package net.sacredlabyrinth.phaed.simpleclans.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import github.scarsz.discordsrv.dependencies.jda.api.JDA;
import me.clip.placeholderapi.PlaceholderAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DiscordHook;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.Bukkit.getPluginManager;

public final class ChatManager {

    private final SimpleClans plugin;
    private final Set<ChatHandler> handlers = new HashSet<>();
    private DiscordHook discordHook;

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        registerHandlers();
        if (getPluginManager().getPlugin("DiscordSRV") != null && plugin.getSettingsManager().is(DISCORDCHAT_ENABLE)) {
            DiscordSRV.api.subscribe(this);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void registerDiscord(DiscordReadyEvent event) {
        discordHook = new DiscordHook(plugin);
        DiscordSRV.api.subscribe(discordHook);
        getPluginManager().registerEvents(discordHook, plugin);
    }

    @Nullable
    public DiscordHook getDiscordHook() {
        // Manually instantiate, if JDA did load faster than SC
        if (discordHook == null && DiscordSRV.getPlugin().getJda().getStatus() == JDA.Status.CONNECTED) {
            registerDiscord(new DiscordReadyEvent());
        }

        return discordHook;
    }

    public void processChat(@NotNull Source source, @NotNull Channel channel,
                            @NotNull ClanPlayer clanPlayer, String message) {
        Clan clan = Objects.requireNonNull(clanPlayer.getClan(), "Clan cannot be null");
        List<ClanPlayer> receivers = new ArrayList<>();

        switch (channel) {
            case ALLY:
                if (!plugin.getSettingsManager().is(ALLYCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(getOnlineAllyMembers(clan).stream().filter(allyMember ->
                        !allyMember.isMutedAlly()).collect(Collectors.toList()));
                receivers.addAll(clan.getOnlineMembers().stream().filter(allyMember ->
                        !allyMember.isMutedAlly()).collect(Collectors.toList()));
                break;
            case CLAN:
                if (!plugin.getSettingsManager().is(CLANCHAT_ENABLE)) {
                    return;
                }

                receivers.addAll(clan.getOnlineMembers().stream().
                        filter(member -> !member.isMuted()).
                        collect(Collectors.toList()));
        }

        SCMessage scMessage = new SCMessage(source, channel, clanPlayer, ChatUtils.stripColors(message), receivers);

        for (ChatHandler ch : handlers) {
            if (ch.canHandle(source)) {
                ch.sendMessage(scMessage);
            }
        }
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
                message.getSource() == SPIGOT ? message.getChannel() : message.getSource()));
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

        return getPluginManager().getPlugin("PlaceholderAPI") != null ?
                PlaceholderAPI.setPlaceholders(Bukkit.getOfflinePlayer(message.getSender().getUniqueId()), parsedFormat)
                : parsedFormat;
    }

    private void registerHandlers() {
        Set<Class<? extends ChatHandler>> chatHandlers =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.chat.handlers", ChatHandler.class);
        plugin.getLogger().log(Level.INFO, "Registering {0} chat handlers...", chatHandlers.size());

        for (Class<? extends ChatHandler> handler : chatHandlers) {
            try {
                handlers.add(handler.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error while trying to register {0} handler: " +
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
