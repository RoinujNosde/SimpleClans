package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.TagValidator;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CreateClanTagPrompt extends StringPrompt {
    public static final String TAG_KEY = "tag";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        if (context.getSessionData(TAG_KEY) != null) {
            return "";
        }
        return lang("insert.clan.tag", (Player) context.getForWhom());
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
        String cleanTag = Helper.stripColors(clanTag);
        if (plugin.getClanManager().isClan(cleanTag)) {
            return new MessagePromptImpl(ChatColor.RED +
                    lang("clan.with.this.tag.already.exists", player), this);
        }

        TagValidator validator = new TagValidator(plugin, player, clanTag);
        String errorMessage = validator.getErrorMessage();
        if (errorMessage == null) {
            return null;
        } else {
            return new MessagePromptImpl(errorMessage, this);
        }
    }
}
