package net.sacredlabyrinth.phaed.simpleclans.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.hooks.DiscordHook;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.Bukkit.getServer;

public final class ChatManager {

    private final SimpleClans plugin;

    private DiscordHook discordHook;
    private final Set<ChatHandler> handlers = new HashSet<>();

    public ChatManager(SimpleClans plugin) {
        this.plugin = plugin;
        registerHandlers();
    }

    @Subscribe
    public void registerDiscord(DiscordReadyEvent event) {
        discordHook = new DiscordHook(plugin);
        DiscordSRV.api.subscribe(discordHook);
        getServer().getPluginManager().registerEvents(discordHook, plugin);
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

    public DiscordHook getDiscordHook() {
        return discordHook;
    }

    private List<ClanPlayer> getOnlineAllyMembers(Clan clan) {
        return clan.getAllAllyMembers().stream().filter(allyPlayer -> allyPlayer.toPlayer() != null).collect(Collectors.toList());
    }
}
