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

import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

abstract public class ConfirmationPrompt extends StringPrompt {

    protected Prompt confirm(ClanPlayer sender, Clan clan, String input) {
        return END_OF_CONVERSATION;
    }

    protected Prompt decline(ClanPlayer sender, Clan clan, String input) {
        return END_OF_CONVERSATION;
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

        return yes.equalsIgnoreCase(input) ? confirm(cp, clan, input) : decline(cp, clan, input);
    }
}
