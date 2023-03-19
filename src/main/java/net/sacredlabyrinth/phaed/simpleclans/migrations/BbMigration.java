package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

/**
 * Class responsible to migrate from old bb format,
 * where prefix sign (`*`) wasn't printed in bb.
 *
 * <pre>
 * Example:
 * `1679069633622_§e§bpandas is no longer at war with turtles`
 * to
 * `1679069633622_§8* §bpandas is no longer at war with turtles`
 * </pre>
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
