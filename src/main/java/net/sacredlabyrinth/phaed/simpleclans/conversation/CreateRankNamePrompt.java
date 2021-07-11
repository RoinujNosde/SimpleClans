package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateRankEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PreCreateRankEvent;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class CreateRankNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        Player forWhom = (Player) context.getForWhom();
        return lang("insert.rank.name", forWhom, lang("cancel", forWhom));
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        Player player = (Player) context.getForWhom();
        Clan clan = (Clan) context.getSessionData("clan");
        if (clan == null || plugin == null) return END_OF_CONVERSATION;
        if (input == null) return this;

        String rank = input.toLowerCase().replace(" ", "_");
        PreCreateRankEvent event = new PreCreateRankEvent(player, clan, rank);
        Bukkit.getServer().getPluginManager().callEvent(event);
        rank = event.getRankName();

        if (event.isCancelled()) {
            return null;
        }

        if (clan.hasRank(rank)) {
            return new MessagePromptImpl(RED + lang("rank.already.exists", player), this);
        }

        clan.createRank(rank);
        Bukkit.getServer().getPluginManager().callEvent(new CreateRankEvent(player, clan, clan.getRank(rank)));
        plugin.getStorageManager().updateClan(clan, true);
        return new MessagePromptImpl(AQUA + lang("rank.created", player));
    }
}
