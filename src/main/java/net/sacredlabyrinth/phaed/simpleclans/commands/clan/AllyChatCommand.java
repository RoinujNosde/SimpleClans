package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;

import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.ALLY;
import static net.sacredlabyrinth.phaed.simpleclans.ClanPlayer.Channel.NONE;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;

@CommandAlias("%ally_chat")
@Description("{@@command.description.ally}")
@CommandPermission("simpleclans.member.ally")
@Conditions("%basic_conditions|clan_member|can_chat:type=ALLY|rank:name=ALLY_CHAT")
public class AllyChatCommand extends BaseCommand {

    @Dependency
    private ChatManager chatManager;
    @Dependency
    private SettingsManager settingsManager;
    @Dependency
    private StorageManager storageManager;

    @Default
    @HelpSearchTags("chat")
    public void sendMessage(ClanPlayer cp, @Name("message") String message) {
        if (!settingsManager.isAllyChatEnable()) {
            return;
        }
        chatManager.proceedChat(SPIGOT, ALLY, cp, message);
    }

    @Subcommand("%join")
    public void join(ClanPlayer clanPlayer) {
        if (clanPlayer.getChannel() == ALLY) {
            ChatBlock.sendMessage(clanPlayer, lang("already.joined.ally.chat"));
            return;
        }

        clanPlayer.setChannel(ALLY);
        storageManager.updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(clanPlayer, lang("joined.ally.chat"));
    }

    @Subcommand("%leave")
    public void leave(ClanPlayer clanPlayer) {
        if (clanPlayer.getChannel() == ALLY) {
            clanPlayer.setChannel(NONE);
            storageManager.updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(clanPlayer, lang("left.ally.chat", clanPlayer));
        }
    }

    @Subcommand("%mute")
    public void mute(ClanPlayer clanPlayer) {
        if (!clanPlayer.isMutedAlly()) {
            clanPlayer.mute(ALLY, true);
            ChatBlock.sendMessage(clanPlayer, lang("muted.ally.chat", clanPlayer));
        } else {
            clanPlayer.mute(ALLY, false);
            ChatBlock.sendMessage(clanPlayer, lang("unmuted.ally.chat", clanPlayer));
        }
    }
}
