package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.conversations.Prompt;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

public class ResetKdrPrompt extends ConfirmationPrompt {

    private final ClanManager cm;

    public ResetKdrPrompt(ClanManager cm) {
        this.cm = cm;
    }

    @Override
    protected Prompt confirm(ClanPlayer sender, Clan clan) {
        if (cm.purchaseResetKdr(sender.toPlayer())) {
            cm.resetKdr(sender);
            return new MessagePromptImpl(RED + lang("you.have.reseted.your.kdr", sender));
        } else {
            return END_OF_CONVERSATION;
        }
    }

    @Override
    protected String getPromptTextKey() {
        return "resetkdr.confirmation";
    }

    @Override
    protected String getDeclineTextKey() {
        return "resetkdr.request.cancelled";
    }

}
