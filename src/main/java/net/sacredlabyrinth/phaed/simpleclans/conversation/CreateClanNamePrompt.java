package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CreateClanNamePrompt extends StringPrompt {
    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        return lang("insert.clan.name");
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String clanName) {
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        Player player = (Player) context.getForWhom();
        if (plugin == null || clanName == null) return this;

        Prompt errorPrompt = validateName(plugin, player, clanName);
        if (errorPrompt != null) return errorPrompt;

        Bukkit.getScheduler().runTask(plugin, () -> {
            //noinspection ConstantConditions
            processClanCreation(plugin, player, (String) context.getSessionData("tag"), clanName);
        });

        return END_OF_CONVERSATION;
    }

    private void processClanCreation(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull String tag,
                                     @NotNull String name) {
        if (plugin.getClanManager().purchaseCreation(player)) {
            plugin.getClanManager().createClan(player, tag, name);

            Clan clan = plugin.getClanManager().getClan(tag);
            clan.addBb(player.getName(), ChatColor.AQUA +
                    MessageFormat.format(plugin.getLang("clan.created"), name));
            plugin.getStorageManager().updateClan(clan);

            if (plugin.getSettingsManager().isRequireVerification()) {
                boolean verified = !plugin.getSettingsManager().isRequireVerification()
                        || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                if (!verified) {
                    ChatBlock.sendMessage(player, ChatColor.AQUA +
                            plugin.getLang("get.your.clan.verified.to.access.advanced.features"));
                }
            }
        }
    }

    @Nullable
    private Prompt validateName(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull String input) {
        boolean bypass = plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass");
        if (bypass) {
            if (Helper.stripColors(input).length() > plugin.getSettingsManager().getClanMaxLength()) {
                return new MessagePromptImpl(ChatColor.RED +
                        MessageFormat.format(plugin.getLang("your.clan.name.cannot.be.longer.than.characters"),
                                plugin.getSettingsManager().getClanMaxLength()), this);
            }
            if (Helper.stripColors(input).length() <= plugin.getSettingsManager().getClanMinLength()) {
                return new MessagePromptImpl(ChatColor.RED +
                        MessageFormat.format(plugin.getLang("your.clan.name.must.be.longer.than.characters"),
                                plugin.getSettingsManager().getClanMinLength()), this);
            }
        }
        if (input.contains("&")) {
            return new MessagePromptImpl(ChatColor.RED +
                    plugin.getLang("your.clan.name.cannot.contain.color.codes"), this);
        }

        return null;
    }
}
