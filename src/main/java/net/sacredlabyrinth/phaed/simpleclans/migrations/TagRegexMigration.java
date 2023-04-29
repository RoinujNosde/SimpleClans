package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TAG_REGEX;

public class TagRegexMigration extends ConfigMigration {

    public TagRegexMigration(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public void migrate() {
        int maxLength = config.getInt("tag.min-length", 5),
                minLength = config.getInt("tag.min-length", 2);

        // Adjust min and max tag length to new `settings.tag-regex`
        adjustTagLength("tag.max-length", "5", maxLength);
        adjustTagLength("tag.min-length", "2", minLength);

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

    private void adjustTagLength(String path, String fromNumber, int toNumber) {
        String tagRegex = settings.getString(TAG_REGEX);
        if (config.contains(path)) {
            config.set(path, null);

            tagRegex = tagRegex.replace(fromNumber, String.valueOf(toNumber));
            config.set("settings.tag-regex", tagRegex);
        }
    }
}
