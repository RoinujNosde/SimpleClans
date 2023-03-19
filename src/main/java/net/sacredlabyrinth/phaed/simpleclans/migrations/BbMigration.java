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
public class BbMigration implements Migration {

    private final SettingsManager settingsManager;

    public BbMigration(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    @Override
    public void migrate() {
        if (settingsManager.getConfig().contains("bb.accent-color")) {
            settingsManager.getConfig().set("bb.accent-color", null);
        }

        if (settingsManager.getConfig().contains("bb.color")) {
            settingsManager.getConfig().set("bb.color", null);
        }
    }
}
