package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.RED;

public class TagValidator {

    private final SimpleClans plugin;
    private final Player player;
    private final String tag;
    private String error;

    public TagValidator(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull String tag) {
        this.plugin = plugin;
        this.player = player;
        this.tag = tag;
    }

    /**
     * Returns an error message or null if the tag is valid
     *
     * @return an error message or null
     */
    @Nullable
    public String getErrorMessage() {
        String cleanTag = ChatUtils.stripColors(this.tag);
        if (tag.length() > 255 && plugin.getSettingsManager().is(MYSQL_ENABLE)) {
            return lang("your.clan.color.tag.cannot.be.longer.than.characters", player, 255);
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass")) {
            if (plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase())) {
                error = RED + lang("that.tag.name.is.disallowed", player);
            }
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.coloredtag") && tag.contains("&")) {
                error = RED + lang("your.tag.cannot.contain.color.codes", player);
            }
            if (cleanTag.length() < plugin.getSettingsManager().getInt(TAG_MIN_LENGTH)) {
                error = RED +
                        lang("your.clan.tag.must.be.longer.than.characters", player,
                                plugin.getSettingsManager().getInt(TAG_MIN_LENGTH));
            }
            if (cleanTag.length() > plugin.getSettingsManager().getInt(TAG_MAX_LENGTH)) {
                error = RED +
                        lang("your.clan.tag.cannot.be.longer.than.characters", player,
                                plugin.getSettingsManager().getInt(TAG_MAX_LENGTH));
            }
            if (plugin.getSettingsManager().hasDisallowedColor(tag)) {
                error = RED +
                        lang("your.tag.cannot.contain.the.following.colors", player,
                                plugin.getSettingsManager().getDisallowedColorString());
            }
        }
        checkAlphabet();

        return error;
    }

    private void checkAlphabet() {
        String cleanTag = Helper.cleanTag(tag);
        String alphabetError = RED + lang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes", player);
        if (plugin.getSettingsManager().is(ACCEPT_OTHER_ALPHABETS_LETTERS)) {
            for (char c : cleanTag.toCharArray()) {
                if (!Character.isLetterOrDigit(c) || Character.isSpaceChar(c)) {
                    error = alphabetError;
                    return;
                }
            }
            return;
        }
        if (!cleanTag.matches("[0-9a-zA-Z]*")) {
            error = alphabetError;
        }
    }
}
