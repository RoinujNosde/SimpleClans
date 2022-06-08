package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

abstract public class ConfirmationPrompt extends StringPrompt {

    protected abstract Prompt confirm(ClanPlayer sender, Clan clan);

    protected Prompt decline(ClanPlayer sender) {
        return new MessagePromptImpl(RED + lang(getDeclineTextKey(), sender));
    }

    protected abstract String getPromptTextKey();

    protected abstract String getDeclineTextKey();

    @NotNull
    @Override
    public String getPromptText(@NotNull ConversationContext context) {
        Player player = (Player) context.getForWhom();
        List<String> options = Arrays.asList(lang("yes", player), lang("cancel", player));

        return RED + lang(getPromptTextKey(), player, options);
    }

    @Override
    public Prompt acceptInput(@NotNull ConversationContext cc, @Nullable String input) {
        final SimpleClans plugin = (SimpleClans) cc.getPlugin();

        Player player = (Player) cc.getForWhom();
        String yes = lang("yes", player);
        ClanManager cm = Objects.requireNonNull(plugin).getClanManager();
        ClanPlayer cp = cm.getCreateClanPlayer(player.getUniqueId());
        Clan clan = cp.getClan();
        if (clan == null) {
            return END_OF_CONVERSATION;
        }

        return yes.equalsIgnoreCase(input) ? confirm(cp, clan) : decline(cp);
    }
}
