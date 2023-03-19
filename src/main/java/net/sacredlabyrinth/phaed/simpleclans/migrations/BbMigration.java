package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.BB_ACCENT_COLOR;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.BB_COLOR;

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
    private final StorageManager storageManager;
    private final ClanManager clanManager;

    public BbMigration(ClanManager clanManager, SettingsManager settingsManager, StorageManager storageManager) {
        this.clanManager = clanManager;
        this.settingsManager = settingsManager;
        this.storageManager = storageManager;
    }

    @Override
    public void migrate() {
        String bbAccentColor = settingsManager.getColored(BB_ACCENT_COLOR);
        String bbColor = settingsManager.getColored(BB_COLOR);

        for (Clan clan : clanManager.getClans()) {
            ArrayList<String> bb = new ArrayList<>();

            for (String msg : clan.getBb()) {
                msg = fromOldBb(msg, bbAccentColor, bbColor);
                if (msg == null) continue;

                bb.add(msg);
            }

            clan.clearBb();
            clan.setBb(bb);
            storageManager.updateClan(clan, false);
        }
    }

    @Nullable
    public static String fromOldBb(String msg, String bbAccentColor, String bbColor) {
        if (!msg.matches("^.+_§.\\*.+$")) {

            int index = msg.indexOf("_");
            if (index < 1) {
                return null;
            }

            msg = msg.substring(0, ++index) + bbAccentColor + "* " + bbColor + ChatUtils.stripColors(msg.substring(index));
        }

        return msg;
    }
}
