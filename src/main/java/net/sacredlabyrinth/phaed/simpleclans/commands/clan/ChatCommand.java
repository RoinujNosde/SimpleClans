package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

@CommandAlias("%clan_chat")
@Conditions("%basic_conditions|clan_member")
@CommandPermission("simpleclans.member.chat")
@Description("{@@command.description.chat}")
public class ChatCommand extends BaseCommand {

    @Dependency
    private ChatManager chatManager;
    @Dependency
    private SettingsManager settingsManager;

    @Default
    @HelpSearchTags("chat")
    @CommandCompletion("@chat_subcommands")
    public void sendMessage(ClanPlayer cp, @Name("message") String message) {
        if (!settingsManager.getClanChatEnable()) {
            return;
        }
        chatManager.processChat(ClanPlayer.Channel.CLAN, cp, message);
    }
}
