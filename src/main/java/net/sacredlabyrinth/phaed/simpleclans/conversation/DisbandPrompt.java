package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Arrays;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

public class DisbandPrompt extends ConfirmationPrompt {

    @Override
    protected Prompt confirm(ClanPlayer sender, Clan clan, String input) {
        clan.disband(sender.toPlayer(), true, false);
        return new MessagePromptImpl(RED + lang("clan.has.been.disbanded", sender, clan.getName()));
    }

    @Override
    protected Prompt decline(ClanPlayer sender, Clan clan, String input) {
        return new MessagePromptImpl(RED + lang("disband.request.cancelled", sender));
    }

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext cc) {
        Player player = (Player) cc.getForWhom();
        return RED + MessageFormat.format(
                lang("disband.confirmation", player), Arrays.asList(
                        lang("yes", player), lang("cancel", player)));
    }
}
