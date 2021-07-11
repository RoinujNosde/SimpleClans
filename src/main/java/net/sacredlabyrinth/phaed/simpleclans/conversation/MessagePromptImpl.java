package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.jetbrains.annotations.NotNull;

/**
 * @author roinujnosde
 */
public class MessagePromptImpl extends org.bukkit.conversations.MessagePrompt {
    private final String message;
    private Prompt nextPrompt;
    public MessagePromptImpl(String message) {
        this.message = message;
    }

    public MessagePromptImpl(String message, Prompt nextPrompt) {
        this(message);
        this.nextPrompt = nextPrompt;
    }

    @Override
    protected Prompt getNextPrompt(@NotNull ConversationContext cc) {
        return nextPrompt != null ? nextPrompt : END_OF_CONVERSATION;
    }

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext cc) {
        return message;
    }
}
