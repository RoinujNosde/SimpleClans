package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

public class IgnoredListMigration extends ConfigMigration {

    public IgnoredListMigration(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public void migrate() {
        if (config.contains("war-and-protection.listeners.ignored-list")) {
            config.set("war-and-protection.listeners.ignored-list", null);
        }
    }
}
