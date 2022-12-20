package net.sacredlabyrinth.phaed.simpleclans.hooks.discord.providers.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.AccountLinkedEvent;
import github.scarsz.discordsrv.api.events.AccountUnlinkedEvent;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.commons.lang3.StringUtils;
import github.scarsz.discordsrv.dependencies.emoji.EmojiParser;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Message;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.MessageUtil;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.DummyListener;
import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.exceptions.DiscordHookException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.discord.providers.discordsrv.DSRVProvider.DiscordAction.ADD;
import static net.sacredlabyrinth.phaed.simpleclans.hooks.discord.providers.discordsrv.DSRVProvider.DiscordAction.REMOVE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_AUTO_CREATION;

public class DSRVListener extends DummyListener<DSRVProvider> {

    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();

    public DSRVListener(DSRVProvider provider) {
        super(provider);
    }

    @Subscribe
    public void onMessageReceived(DiscordGuildMessageReceivedEvent event) {
        Optional<TextChannel> channel = provider.getCachedChannel(event.getChannel().getName());

        if (channel.isPresent()) {
            Message eventMessage = event.getMessage();
            User Author = event.getAuthor();
            TextChannel textChannel = channel.get();
            UUID uuid = accountManager.getUuid(Author.getId());

            if (uuid == null) {
                provider.sendPrivateMessage(textChannel, eventMessage, lang("you.did.not.link.your.account"));
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
                provider.sendPrivateMessage(textChannel, eventMessage, lang("cannot.send.discord.message", clanPlayer, channelLink));
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

    @Subscribe
    public void onPlayerLinking(AccountLinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        Member member = provider.getGuild().getMember(event.getUser());
        if (clanPlayer == null || member == null) {
            return;
        }

        Clan clan = clanPlayer.getClan();
        if (clan == null) {
            return;
        }

        if (!provider.createChannelSilently(clanPlayer)) {
            return;
        }

        provider.updateViewPermission(member, clan, ADD);
        provider.updateLeaderRole(member, clanPlayer, ADD);
    }

    @Subscribe
    public void onPlayerUnlinking(AccountUnlinkedEvent event) {
        ClanPlayer clanPlayer = clanManager.getClanPlayer(event.getPlayer());
        Member member = provider.getGuild().getMember(event.getDiscordUser());
        if (clanPlayer == null || clanPlayer.getClan() == null || member == null) {
            return;
        }

        provider.updateViewPermission(member, clanPlayer.getClan(), REMOVE);
        provider.updateLeaderRole(member, clanPlayer, REMOVE);
    }

    @Override
    public void onClanCreate(CreateClanEvent event) {
        try {
            if (settingsManager.is(DISCORDCHAT_AUTO_CREATION)) {
                provider.createChannel(event.getClan().getTag());
            }
        } catch (DiscordHookException ex) {
            // Clan is not following the conditions, categories are fulled or discord reaches the limit, nothing to do here.
            SimpleClans.debug(ex.getMessage());
        }
    }

    @Override
    public void onClanDisband(DisbandClanEvent event) {
        provider.deleteChannel(event.getClan().getTag());
    }

    @Override
    public void onPlayerClanJoin(PlayerJoinedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Clan clan = event.getClan();
        Member member = provider.getMember(clanPlayer);
        if (member == null || clan == null) {
            return;
        }

        if (!provider.createChannelSilently(clanPlayer)) {
            return;
        }

        provider.updateViewPermission(member, clan, ADD);
    }

    @Override
    public void onPlayerClanLeave(PlayerKickedClanEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Clan clan = event.getClan();
        Member member = provider.getMember(clanPlayer);
        if (member == null || clan == null) {
            return;
        }

        provider.updateViewPermission(member, clan, REMOVE);
        provider.updateLeaderRole(member, clanPlayer, REMOVE);
    }

    @Override
    public void onPlayerPromote(PlayerPromoteEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Member member = provider.getMember(clanPlayer);
        if (member == null) {
            return;
        }

        provider.updateLeaderRole(member, clanPlayer, ADD);
    }

    @Override
    public void onPlayerDemote(PlayerDemoteEvent event) {
        ClanPlayer clanPlayer = event.getClanPlayer();
        Member member = provider.getMember(clanPlayer);
        if (member == null) {
            return;
        }

        provider.updateLeaderRole(member, clanPlayer, REMOVE);
    }
}
