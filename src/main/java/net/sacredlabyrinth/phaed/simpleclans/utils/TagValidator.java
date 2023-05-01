package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.regex.Pattern;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class TagValidator {
    private final SettingsManager settings;
    private final PermissionsManager permissions;
    @Nullable
    private Pattern tagRegex;
    @Nullable
    private String error;

    public TagValidator(@NotNull SettingsManager settings, @NotNull PermissionsManager permissions) {
        this.settings = settings;
        this.permissions = permissions;

        String regex = settings.getString(TAG_REGEX);
        if (!regex.isEmpty()) {
            tagRegex = Pattern.compile(regex);
        }
    }

    /**
     * Validates a clan tag
     *
     * @param player player who tried to create a clan
     * @param tag    clan tag
     * @return error message if any
     */
    public Optional<String> validate(@NotNull Player player, @NotNull String tag) {
        String cleanTag = Helper.cleanTag(tag);
        if (tag.length() > 255 && settings.is(MYSQL_ENABLE)) {
            return Optional.of(lang("your.clan.color.tag.cannot.be.longer.than.characters", player, 255));
        }

        if (!permissions.has(player, "simpleclans.mod.bypass")) {
            if (settings.isDisallowedWord(cleanTag)) {
                error = lang("that.tag.name.is.disallowed", player);
            }
            if (!permissions.has(player, "simpleclans.leader.coloredtag") && tag.contains("&")) {
                error = lang("your.tag.cannot.contain.color.codes", player);
            }
            int minLength = settings.getInt(TAG_MIN_LENGTH);
            if (cleanTag.length() < minLength) {
                error = lang("your.clan.tag.must.be.longer.than.characters", player, minLength);
            }
            int maxLength = settings.getInt(TAG_MAX_LENGTH);
            if (cleanTag.length() > maxLength) {
                error = lang("your.clan.tag.cannot.be.longer.than.characters", player, maxLength);
            }
            if (settings.hasDisallowedColor(tag)) {
                error = lang("your.tag.cannot.contain.the.following.colors", player, settings.getDisallowedColorString());
            }
        }

        if (tagRegex != null && !tagRegex.matcher(cleanTag).matches()) {
            error = lang("your.tag.doesnt.meet.the.requirements", player);
        } else {
            checkAlphabet(player, cleanTag);
        }

        return Optional.ofNullable(error);
    }

    private void checkAlphabet(@NotNull Player player, @NotNull String cleanTag) {
        String alphabetError = lang("your.clan.tag.can.only.contain.letters.numbers.and.color.codes", player);
        if (settings.is(ACCEPT_OTHER_ALPHABETS_LETTERS)) {
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
