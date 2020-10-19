package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.Player;

@CommandAlias("%clan_chat")
@Conditions("%basic_conditions")
@CommandPermission("simpleclans.member.chat")
@Description("{@@command.description.chat}")
public class ChatCommand extends BaseCommand {

    @Dependency
    private ClanManager clanManager;
    @Dependency
    private SettingsManager settingsManager;

    @Default
    @HelpSearchTags("chat")
    public void sendMessage(Player player, ClanPlayer cp, @Name("message") String message) {
        if (!settingsManager.getClanChatEnable()) {
            return;
        }
        clanManager.processClanChat(player, cp.getTag(), message);
    }
}
