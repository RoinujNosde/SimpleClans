package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.conversations.Prompt;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

public class DisbandPrompt extends ConfirmationPrompt {

    @Override
    protected Prompt confirm(ClanPlayer sender, Clan clan) {
        if (clan.isPermanent()) {
            return new MessagePromptImpl(RED + lang("cannot.disband.permanent", sender));
        }

        clan.disband(sender.toPlayer(), true, false);
        return new MessagePromptImpl(RED + lang("clan.has.been.disbanded", sender, clan.getName()));
    }

    @Override
    protected String getPromptTextKey() {
        return "disband.confirmation";
    }

    @Override
    protected String getDeclineTextKey() {
        return "disband.request.cancelled";
    }

}
