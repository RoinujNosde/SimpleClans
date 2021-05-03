package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.ConversationContext;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class RequestCanceller implements ConversationCanceller {

    @NotNull
    private final String cancelledMessage;
    @NotNull
    private final String escapeSequence;

    public RequestCanceller(@NotNull String escapeSequence, @NotNull String cancelledMessage) {
        this.escapeSequence = escapeSequence;
        this.cancelledMessage = cancelledMessage;
    }

    public RequestCanceller(@NotNull CommandSender sender, @NotNull String cancelledMessage) {
        this(lang("cancel", sender), cancelledMessage);
    }

    @Override
    public void setConversation(@NotNull Conversation conversation) {
    }

    @Override
    public boolean cancelBasedOnInput(@NotNull ConversationContext context, @NotNull String input) {
        if (input.equalsIgnoreCase(escapeSequence)) {
            context.getForWhom().sendRawMessage(cancelledMessage);
            return true;
        }

        return false;
    }

    //a clone that is not a clone, nice one, Bukkit
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @NotNull
    @Override
    public ConversationCanceller clone() {
        return new RequestCanceller(this.escapeSequence, this.cancelledMessage);
    }
}
