package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CreateClanTagPrompt extends StringPrompt {
    public static final String TAG_KEY = "tag";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        Player forWhom = (Player) context.getForWhom();
        if (context.getSessionData(TAG_KEY) != null) {
            return "";
        }
        return lang("insert.clan.tag", forWhom, lang("cancel", forWhom));
    }

    @Override
    public boolean blocksForInput(@NotNull ConversationContext context) {
        return context.getSessionData(TAG_KEY) == null;
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String input) {
        Player player = (Player) context.getForWhom();
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        input = input != null ? input : (String) context.getSessionData(TAG_KEY);
        context.setSessionData(TAG_KEY, null);
        if (input == null || plugin == null) return this;

        Prompt errorPrompt = validateTag(plugin, player, input);
        if (errorPrompt != null) return errorPrompt;
        context.setSessionData(TAG_KEY, input);
        return new CreateClanNamePrompt();
    }

    @Nullable
    private Prompt validateTag(SimpleClans plugin, Player player, @NotNull String clanTag) {
        String cleanTag = ChatUtils.stripColors(clanTag);
        if (plugin.getClanManager().isClan(cleanTag)) {
            return new MessagePromptImpl(ChatColor.RED +
                    lang("clan.with.this.tag.already.exists", player), this);
        }

        Optional<String> validationError = plugin.getTagValidator().validate(player, clanTag);
        return validationError.map(error -> new MessagePromptImpl(error, this)).orElse(null);
    }
}
