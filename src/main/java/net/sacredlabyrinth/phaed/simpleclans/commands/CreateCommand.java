package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class CreateCommand {
    public CreateCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.create")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions", player));
            return;
        }

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED +
                    lang("usage.create", player, plugin.getSettingsManager().getCommandClan()));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp != null) {
            ChatBlock.sendMessage(player, ChatColor.RED +
                    MessageFormat.format(lang("you.must.first.resign", player), cp.getClan().getName()));
            return;
        }

        Conversation conversation = new ConversationFactory(plugin).withFirstPrompt(new CreateClanTagPrompt())
                .withLocalEcho(true).buildConversation(player);
        conversation.begin();
    }
}
