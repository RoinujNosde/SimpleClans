package net.sacredlabyrinth.phaed.simpleclans.conversation;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.PreCreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt.TAG_KEY;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.CLAN_DEFAULTRANK;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

public class CreateClanNamePrompt extends StringPrompt {
    public static final String NAME_KEY = "name";

    @Override
    public @NotNull String getPromptText(@NotNull ConversationContext context) {
        if (context.getSessionData(NAME_KEY) != null) {
            return "";
        }
        return lang("insert.clan.name", (Player) context.getForWhom());
    }

    @Override
    public boolean blocksForInput(@NotNull ConversationContext context) {
        return context.getSessionData(NAME_KEY) == null;
    }

    @Override
    public @Nullable Prompt acceptInput(@NotNull ConversationContext context, @Nullable String clanName) {
        SimpleClans plugin = (SimpleClans) context.getPlugin();
        Player player = (Player) context.getForWhom();
        clanName = clanName != null ? clanName : (String) context.getSessionData(NAME_KEY);
        context.setSessionData(NAME_KEY, null);
        if (plugin == null || clanName == null) return this;

        Prompt errorPrompt = validateName(plugin, player, clanName);
        if (errorPrompt != null) return errorPrompt;

        String finalClanName = clanName;
        Bukkit.getScheduler().runTask(plugin, () -> {
            String tag = (String) context.getSessionData(TAG_KEY);
            //noinspection ConstantConditions
            PreCreateClanEvent event = new PreCreateClanEvent(player, tag, finalClanName);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                processClanCreation(plugin, player, tag, finalClanName);
            }
        });

        return END_OF_CONVERSATION;
    }

    private void processClanCreation(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull String tag,
                                     @NotNull String name) {
        if (plugin.getClanManager().purchaseCreation(player)) {
            plugin.getClanManager().createClan(player, tag, name);

            Clan clan = plugin.getClanManager().getClan(tag);
            clan.addBb(player.getName(), lang("clan.created", name));
            plugin.getStorageManager().updateClan(clan);
            if (!plugin.getSettingsManager().getString(CLAN_DEFAULTRANK).isEmpty()) {
                clan.setDefaultRank(plugin.getSettingsManager().getString(CLAN_DEFAULTRANK));


                if (plugin.getSettingsManager().is(REQUIRE_VERIFICATION)) {
                    boolean verified = !plugin.getSettingsManager().is(REQUIRE_VERIFICATION)
                            || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

                    if (!verified) {
                        ChatBlock.sendMessage(player, AQUA +
                                lang("get.your.clan.verified.to.access.advanced.features", player));
                    }
                }
            }
        }
    }

    @Nullable
    private Prompt validateName(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull String input) {
        boolean bypass = plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass");
        if (!bypass) {
            if (ChatUtils.stripColors(input).length() > plugin.getSettingsManager().getInt(CLAN_MAX_LENGTH)) {
                return new MessagePromptImpl(RED +
                        lang("your.clan.name.cannot.be.longer.than.characters", player,
                                plugin.getSettingsManager().getInt(CLAN_MAX_LENGTH)), this);
            }
            if (ChatUtils.stripColors(input).length() <= plugin.getSettingsManager().getInt(CLAN_MIN_LENGTH)) {
                return new MessagePromptImpl(RED +
                        lang("your.clan.name.must.be.longer.than.characters", player,
                                plugin.getSettingsManager().getInt(CLAN_MIN_LENGTH)), this);
            }
        }
        if (input.contains("&")) {
            return new MessagePromptImpl(RED +
                    lang("your.clan.name.cannot.contain.color.codes", player), this);
        }

        return null;
    }
}
