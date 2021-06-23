package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.CLAN;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.NONE;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;

@CommandAlias("%clan_chat")
@Conditions("%basic_conditions|clan_member|clan_chat")
@CommandPermission("simpleclans.member.chat")
@Description("{@@command.description.chat}")
public class ChatCommand extends BaseCommand {

    @Dependency
    private ChatManager chatManager;
    @Dependency
    private SettingsManager settingsManager;
    @Dependency
    private StorageManager storageManager;

    @Default
    @HelpSearchTags("chat")
    public void sendMessage(ClanPlayer cp, @Name("message") String message) {
        if (!settingsManager.getClanChatEnable()) {
            return;
        }
        chatManager.proceedChat(SPIGOT, CLAN, cp, message);
    }

    @Subcommand("%join")
    public void join(ClanPlayer clanPlayer) {
        if (clanPlayer.getChannel() == CLAN) {
            ChatBlock.sendMessage(clanPlayer, lang("already.joined.clan.chat"));
            return;
        }

        clanPlayer.setChannel(CLAN);
        storageManager.updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(clanPlayer, lang("joined.clan.chat"));
    }

    @Subcommand("%leave")
    public void leave(ClanPlayer clanPlayer) {
        if (clanPlayer.getChannel() == CLAN) {
            clanPlayer.setChannel(NONE);
            storageManager.updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(clanPlayer, lang("left.clan.chat", clanPlayer));
        }
    }

    @Subcommand("%mute")
    public void mute(ClanPlayer clanPlayer) {
        if (!clanPlayer.isMuted()) {
            clanPlayer.mute(CLAN, true);
            ChatBlock.sendMessage(clanPlayer, lang("muted.clan.chat", clanPlayer));
        } else {
            clanPlayer.mute(CLAN, false);
            ChatBlock.sendMessage(clanPlayer, lang("unmuted.clan.chat", clanPlayer));
        }
    }
}
