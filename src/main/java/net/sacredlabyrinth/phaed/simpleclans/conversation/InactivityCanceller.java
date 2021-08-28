package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.InactivityConversationCanceller;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

public class InactivityCanceller extends InactivityConversationCanceller {

    /**
     * Creates an InactivityConversationCanceller.
     *
     * @param plugin         The owning plugin.
     * @param timeoutSeconds The number of seconds of inactivity to wait.
     */
    public InactivityCanceller(@NotNull Plugin plugin, int timeoutSeconds) {
        super(plugin, timeoutSeconds);
    }

    @Override
    protected void cancelling(@NotNull Conversation conversation) {
        Player forWhom = (Player) conversation.getForWhom();
        forWhom.spigot().sendMessage(new TextComponent(RED + lang("you.did.not.answer.in.time", forWhom)));
    }
}
