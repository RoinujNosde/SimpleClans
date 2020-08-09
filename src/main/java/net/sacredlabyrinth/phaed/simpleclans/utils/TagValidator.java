package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

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
        String cleanTag = Helper.stripColors(this.tag);
        if (tag.length() > 25) {
            return lang("your.clan.color.tag.cannot.be.longer.than.characters", player, 25);
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.mod.bypass")) {
            if (plugin.getSettingsManager().isDisallowedWord(cleanTag.toLowerCase())) {
                error = ChatColor.RED + lang("that.tag.name.is.disallowed", player);
            }
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.coloredtag") && tag.contains("&")) {
                error =  ChatColor.RED + lang("your.tag.cannot.contain.color.codes", player);
            }
            if (cleanTag.length() < plugin.getSettingsManager().getTagMinLength()) {
                error = ChatColor.RED +
                        lang("your.clan.tag.must.be.longer.than.characters", player,
                                plugin.getSettingsManager().getTagMinLength());
            }
            if (cleanTag.length() > plugin.getSettingsManager().getTagMaxLength()) {
                error = ChatColor.RED +
                        lang("your.clan.tag.cannot.be.longer.than.characters", player,
                                plugin.getSettingsManager().getTagMaxLength());
            }
            if (plugin.getSettingsManager().hasDisallowedColor(tag)) {
                error = ChatColor.RED +
                        lang("your.tag.cannot.contain.the.following.colors", player,
                                plugin.getSettingsManager().getDisallowedColorString());
            }
            checkAlphabet();
        }

        return error;
    }

    private void checkAlphabet() {
        String cleanTag = Helper.cleanTag(tag);
        String alphabetError = ChatColor.RED + lang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes", player);
        if (plugin.getSettingsManager().isAcceptOtherAlphabetsLettersOnTag()) {
            for (char c : cleanTag.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
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
