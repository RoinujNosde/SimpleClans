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
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

/**
 * @author roinujnosde
 */
public class ResignPrompt extends ConfirmationPrompt {

    @Override
    protected Prompt confirm(ClanPlayer sender, Clan clan, String input) {
        if (clan.isPermanent() || !sender.isLeader() || clan.getLeaders().size() > 1) {
            clan.addBb(sender.getName(), AQUA + lang("0.has.resigned", sender.getName()));
            sender.addResignTime(clan.getTag());
            clan.removePlayerFromClan(sender.getUniqueId());

            return new MessagePromptImpl(AQUA + lang("resign.success", sender));
        } else if (sender.isLeader() && clan.getLeaders().size() == 1) {
            clan.disband(sender.toPlayer(), true, false);
            return new MessagePromptImpl(RED + lang("clan.has.been.disbanded", sender, clan.getName()));
        } else {
            return new MessagePromptImpl(RED + lang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan", sender));
        }
    }

    @Override
    protected Prompt decline(ClanPlayer sender, Clan clan, String input) {
        return new MessagePromptImpl(RED + lang("resign.request.cancelled", sender));
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext cc) {
        Player player = (Player) cc.getForWhom();
        return RED + MessageFormat.format(
                lang("resign.confirmation", player), Arrays.asList(
                        lang("yes", player), lang("cancel", player)));
    }
}
