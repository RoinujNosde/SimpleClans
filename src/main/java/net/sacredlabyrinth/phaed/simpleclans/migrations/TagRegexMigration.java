package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TAG_REGEX;

public class TagRegexMigration extends ConfigMigration {

    public TagRegexMigration(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public void migrate() {
        int maxLength = 5, minLength = 2;

        String tagRegex = settings.getString(TAG_REGEX);
        if (config.contains("tag.max-length")) {
            maxLength = config.getInt("tag.max-length");
            config.set("tag.max-length", null);

            tagRegex = tagRegex.replace("5", String.valueOf(maxLength));
            config.set("settings.tag-regex", tagRegex);
        }

        if (config.contains("tag.min-length")) {
            minLength = config.getInt("tag.min-length");
            config.set("tag.min-length", null);

            tagRegex = tagRegex.replace("2", String.valueOf(minLength));
            config.set("settings.tag-regex", tagRegex);
        }

        if (config.contains("settings.accept-other-alphabets-letters-on-tag")) {
            if (config.getBoolean("settings.accept-other-alphabets-letters-on-tag")) {
                // Regex: ^[\p{L}\d]{2,5}$
                // Accepts any unicode characters and digits from minLength to maxLength
                String unicodeRegex = String.format("^[%s]{%d,%d}$", "\\p{L}\\d", minLength, maxLength);
                config.set("settings.tag-regex", unicodeRegex);
            }

            config.set("settings.accept-other-alphabets-letters-on-tag", null);
        }
    }
}
