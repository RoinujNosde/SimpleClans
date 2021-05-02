package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class RequestCanceller implements ConversationCanceller {

    private final String closedMessage;
    private final String escapeSequence;

    public RequestCanceller(@NotNull String escapeSequence, @Nullable String closedMessage) {
        this.escapeSequence = escapeSequence;
        this.closedMessage = closedMessage;
    }

    public RequestCanceller(@NotNull CommandSender sender, @Nullable String closedMessage) {
        this(lang("cancel.no", sender), closedMessage);
    }

    @Override
    public void setConversation(@NotNull Conversation conversation) {
    }

    @Override
    public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
        if (closedMessage != null) {
            context.getForWhom().sendRawMessage(closedMessage);
        }

        return input.equals(escapeSequence);
    }

    @NotNull
    @Override
    public ConversationCanceller clone() {
        return new RequestCanceller(this.escapeSequence, this.closedMessage);
    }
}
