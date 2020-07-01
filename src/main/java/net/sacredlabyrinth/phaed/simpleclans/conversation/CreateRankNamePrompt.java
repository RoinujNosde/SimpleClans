package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CreateRankNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return lang("insert.rank.name");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        Clan clan = (Clan) context.getSessionData("clan");
        if (clan == null || plugin == null) return END_OF_CONVERSATION;
        if (input == null) return this;

        String rank = input.toLowerCase();
        if (clan.hasRank(rank)) {
            return new MessagePromptImpl(ChatColor.RED + lang("rank.already.exists"), this);
        }

        clan.createRank(rank);
        plugin.getStorageManager().updateClan(clan, true);
        return new MessagePromptImpl(ChatColor.AQUA + lang("rank.created"));
    }
}
