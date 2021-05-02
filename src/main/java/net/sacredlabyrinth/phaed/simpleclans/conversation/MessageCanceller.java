package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class MessageCanceller implements ConversationCanceller {

    @NotNull
    private final String closedMessage;
    @NotNull
    private final String escapeSequence;

    public MessageCanceller(@NotNull String escapeSequence, @NotNull String closedMessage) {
        this.escapeSequence = escapeSequence;
        this.closedMessage = closedMessage;
    }

    public MessageCanceller(@NotNull CommandSender sender, @NotNull String closedMessage) {
        this(lang("cancel", sender), closedMessage);
    }

    @Override
    public void setConversation(@NotNull Conversation conversation) {
    }

    @Override
    public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
        if (input.equalsIgnoreCase(escapeSequence)) {
            context.getForWhom().sendRawMessage(closedMessage);
            return true;
        }

        return false;
    }

    @NotNull
    @Override
    public ConversationCanceller clone() {
        return new MessageCanceller(this.escapeSequence, this.closedMessage);
    }
}
