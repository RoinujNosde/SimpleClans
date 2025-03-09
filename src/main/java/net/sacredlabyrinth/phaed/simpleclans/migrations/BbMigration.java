package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * Class responsible to migrate from old bb format to new one.
 * <p>
 * `bb.accent-color` and `bb.color` replaced to `bb.prefix`.
 * </p>
 *
 * @since 2.19.0
 */
public class BbMigration extends ConfigMigration {

    public BbMigration(SettingsManager settingsManager) {
        super(settingsManager);
    }

    @Override
    public void migrate() {
        if (config.contains("bb.accent-color")) {
            config.set("bb.accent-color", null);
        }

        if (config.contains("bb.color")) {
            config.set("bb.color", null);
        }
    }
}
