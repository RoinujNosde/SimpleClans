package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

@CommandAlias("%ally_chat")
@Description("{@@command.description.ally}")
@CommandPermission("simpleclans.member.ally")
@Conditions("%basic_conditions|clan_member|rank:name=ALLY_CHAT")
public class AllyChatCommand extends BaseCommand {

    @Dependency
    private ChatManager chatManager;
    @Dependency
    private SettingsManager settingsManager;

    @Default
    @HelpSearchTags("chat")
    @CommandCompletion("@chat_subcommands")
    public void sendMessage(ClanPlayer cp, @Name("message") String message) {
        if (!settingsManager.isAllyChatEnable()) {
            return;
        }
        chatManager.processChat(ClanPlayer.Channel.ALLY, cp, message);
    }

}
