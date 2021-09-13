package net.sacredlabyrinth.phaed.simpleclans.conversation;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationCanceller;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SCConversation extends Conversation {
    private static final Map<UUID, SCConversation> conversations = new HashMap<>();

    public SCConversation(@NotNull Plugin plugin, @NotNull Conversable forWhom, @Nullable Prompt firstPrompt) {
        this(plugin, forWhom, firstPrompt, new HashMap<>(), 10);
    }

    public SCConversation(@NotNull Plugin plugin, @NotNull Conversable forWhom, @Nullable Prompt firstPrompt, int timeout) {
        this(plugin, forWhom, firstPrompt, new HashMap<>(), timeout);
    }

    public SCConversation(@NotNull Plugin plugin, @NotNull Conversable forWhom, @Nullable Prompt firstPrompt, @NotNull Map<Object, Object> initialSessionData) {
        this(plugin, forWhom, firstPrompt, initialSessionData, 10);
    }

    public SCConversation(@NotNull Plugin plugin, @NotNull Conversable forWhom, @Nullable Prompt firstPrompt, @NotNull Map<Object, Object> initialSessionData, int timeout) {
        super(plugin, forWhom, firstPrompt, initialSessionData);
        this.setLocalEchoEnabled(true);
        this.addConversationCanceller(new InactivityCanceller(plugin, timeout));
    }

    @Override
    public void begin() {
        UUID uniqueId = ((Player) getForWhom()).getUniqueId();
        SCConversation oldConversation = conversations.get(uniqueId);

        if (oldConversation != this && oldConversation != null) {
            getForWhom().abandonConversation(oldConversation);
        }

        conversations.put(uniqueId, this);
        super.begin();
    }

    public void addConversationCanceller(@NotNull ConversationCanceller canceller) {
        canceller.setConversation(this);
        cancellers.add(canceller);
    }
}
