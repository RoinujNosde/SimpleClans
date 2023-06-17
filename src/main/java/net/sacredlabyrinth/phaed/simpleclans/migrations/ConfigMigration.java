package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class ConfigMigration implements Migration {

    protected final SettingsManager settings;
    protected final FileConfiguration config;

    public ConfigMigration(SettingsManager settingsManager) {
        this.settings = settingsManager;
        this.config = settings.getConfig();

        migrate();
        settings.save();
    }
}
