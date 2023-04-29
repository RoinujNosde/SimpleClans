package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.MYSQL_ENABLE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TAG_REGEX;
import static org.bukkit.ChatColor.RED;

public class TagValidator {
    private final Player player;
    private final String tag;
    private final Pattern tagRegex;
    private final SettingsManager settings;
    private final PermissionsManager permissions;
    private String error;

    public TagValidator(@NotNull SettingsManager settings, @NotNull PermissionsManager permissions,
                        @NotNull Player player, @NotNull String tag) {
        this.settings = settings;
        this.permissions = permissions;
        this.player = player;
        this.tag = tag;
        tagRegex = Pattern.compile(settings.getString(TAG_REGEX));
    }

    /**
     * Returns an error message or null if the tag is valid
     *
     * @return an error message or null
     */
    @Nullable
    public String getErrorMessage() {
        String cleanTag = Helper.cleanTag(tag);
        if (tag.length() > 255 && settings.is(MYSQL_ENABLE)) {
            return lang("your.clan.color.tag.cannot.be.longer.than.characters", player, 255);
        }

        if (!permissions.has(player, "simpleclans.mod.bypass")) {
            if (settings.isDisallowedWord(cleanTag)) {
                error = RED + lang("that.tag.name.is.disallowed", player);
            }
            if (!permissions.has(player, "simpleclans.leader.coloredtag") && tag.contains("&")) {
                error = RED + lang("your.tag.cannot.contain.color.codes", player);
            }
            if (settings.hasDisallowedColor(tag)) {
                error = RED + lang("your.tag.cannot.contain.the.following.colors", player, settings.getDisallowedColorString());
            }
        }

        if (!tagRegex.matcher(cleanTag).matches()) {
            error = lang("your.tag.doesnt.meet.the.requirements", player);
        }

        return error;
    }
}
