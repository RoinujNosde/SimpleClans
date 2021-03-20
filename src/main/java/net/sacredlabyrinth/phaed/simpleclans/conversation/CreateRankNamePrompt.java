package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateRankEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PreCreateRankEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CreateRankNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return lang("insert.rank.name", (Player) context.getForWhom());
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        Player player = (Player) context.getForWhom();
        Clan clan = (Clan) context.getSessionData("clan");
        if (clan == null || plugin == null) return END_OF_CONVERSATION;
        if (input == null) return this;

        String rank = input.toLowerCase().replace(" ", "_");
        if (clan.hasRank(rank)) {
            return new MessagePromptImpl(ChatColor.RED + lang("rank.already.exists", player), this);
        }

        PreCreateRankEvent event = new PreCreateRankEvent(player, clan, rank);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            clan.createRank(rank);
            Bukkit.getServer().getPluginManager().callEvent(new CreateRankEvent(player, clan, clan.getRank(rank)));
            plugin.getStorageManager().updateClan(clan, true);
            return new MessagePromptImpl(ChatColor.AQUA + lang("rank.created", player));
        }

        return null;
    }
}
