package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.configuration.file.FileConfiguration;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 *
 * @author RoinujNosde
 */
@SuppressWarnings("deprecation")
public class ChatFormatMigration {

    SimpleClans plugin = SimpleClans.getInstance();

    
    public void migrateClanChat() {
        SettingsManager sm = plugin.getSettingsManager();
        FileConfiguration c = sm.getConfig();

        if (c.getString("clanchat.name-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append('&');
        sb.append(sm.getColored(CLANCHAT_BRACKET_COLOR));
        sb.append(sm.getString(CLANCHAT_BRACKET_LEFT));
        sb.append("%clan%");
        sb.append("&");
        sb.append(sm.getColored(CLANCHAT_BRACKET_COLOR));
        sb.append(sm.getString(CLANCHAT_BRACKET_RIGHT));
        sb.append(" ");
        sb.append('&');
        sb.append(sm.getColored(CLANCHAT_NAME_COLOR));
        sb.append(sm.getString(CLANCHAT_PLAYER_BRACKET_LEFT));
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(sm.getColored(CLANCHAT_NAME_COLOR));
        sb.append(sm.getString(CLANCHAT_PLAYER_BRACKET_RIGHT));
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(sm.getColored(CLANCHAT_MESSAGE_COLOR));
        sb.append("%message%");

        c.set("clanchat.format", sb.toString());
        c.set("clanchat.rank", "&f[%rank%&f]");
        c.set("clanchat.rank.color", null);
        c.set("clanchat.name-color", null);
        c.set("clanchat.player-bracket", null);
        c.set("clanchat.message-color", null);
        c.set("clanchat.tag-bracket", null);
        sm.save();
    }

    public void migrateAllyChat() {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();
        FileConfiguration c = sm.getConfig();

        //Checks if the old format is still in use
        if (c.getString("allychat.tag-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(sm.getString(ALLYCHAT_BRACKET_lEFT));
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_TAG_COLOR));
        sb.append(sm.getString(COMMANDS_ALLY));
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(sm.getString(ALLYCHAT_BRACKET_RIGHT));
        sb.append(" ");
        sb.append("&4<%clan%&4> ");
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(sm.getString(ALLYCHAT_PLAYER_BRACKET_LEFT));
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(sm.getString(ALLYCHAT_PLAYER_BRACKET_RIGHT));
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(sm.getColored(ALLYCHAT_MESSAGE_COLOR));
        sb.append("%message%");

        c.set("allychat.format", sb.toString());
        c.set("allychat.rank", "&f[%rank%&f]");
        c.set("allychat.tag-color", null);
        c.set("allychat.name-color", null);
        c.set("allychat.player-bracket", null);
        c.set("allychat.message-color", null);
        c.set("allychat.tag-bracket", null);
        sm.save();
    }
}
