package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.Player;

@CommandAlias("%ally_chat")
@Description("{@@command.description.ally}")
@CommandPermission("simpleclans.member.ally")
@Conditions("%basic_conditions|clan_member|rank:name=ALLY_CHAT")
public class AllyChatCommand extends BaseCommand {

    @Dependency
    private ClanManager clanManager;
    @Dependency
    private SettingsManager settingsManager;

    @Default
    @HelpSearchTags("chat")
    public void sendMessage(Player player, @Name("message") String message) {
        if (!settingsManager.isAllyChatEnable()) {
            return;
        }
        clanManager.processAllyChat(player, message);
    }

}
