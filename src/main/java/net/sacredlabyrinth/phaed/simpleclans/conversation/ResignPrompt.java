package net.sacredlabyrinth.phaed.simpleclans.conversation;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 *
 * @author roinujnosde
 */
public class ResignPrompt extends StringPrompt {

    @Override
    public Prompt acceptInput(ConversationContext cc, String input) {
        final SimpleClans plugin = (SimpleClans) cc.getPlugin();

        Player player = (Player) cc.getForWhom();
        String yes = lang("resign.yes", player);
        ClanManager cm = Objects.requireNonNull(plugin).getClanManager();
        ClanPlayer cp = cm.getClanPlayer(player);
        Clan clan = cp.getClan();
        		
        if (yes.equalsIgnoreCase(input)) {
            if (!clan.isLeader(player) || clan.getLeaders().size() > 1) {
                clan.addBb(player.getName(), ChatColor.AQUA + lang("0.has.resigned", player, player.getName()));
                cp.addResignTime(clan.getTag());
                clan.removePlayerFromClan(player.getUniqueId());
                
                return new MessagePromptImpl(ChatColor.AQUA + lang("resign.success", player));
            } else if (clan.isLeader(player) && clan.getLeaders().size() == 1) {
                clan.disband();
                String msgKey = "clan.has.been.disbanded";
                //message for the server
                plugin.getClanManager().serverAnnounce(ChatColor.AQUA + lang(msgKey, clan.getName()));
                //message for the player
                return new MessagePromptImpl(ChatColor.AQUA + lang(msgKey, player,  clan.getName()));
            } else {
                return new MessagePromptImpl(ChatColor.RED + lang("last.leader.cannot.resign.you.must.appoint.another.leader.or.disband.the.clan", player));
            }
        } else {
        	return new MessagePromptImpl(ChatColor.RED + lang("resign.request.cancelled", player));
        }
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext cc) {
        Player player = (Player) cc.getForWhom();
        return ChatColor.RED + MessageFormat.format(
                lang("resign.confirmation", player), Arrays.asList(
                    lang("resign.yes", player), lang("resign.no", player)));
    }

}
