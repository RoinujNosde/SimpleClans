package net.sacredlabyrinth.phaed.simpleclans.migrations;

import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * @author RoinujNosde
 */
public class ChatFormatMigration extends ConfigMigration {

    public ChatFormatMigration(SettingsManager settingsManager) {
        super(settingsManager);
    }

    public void migrateClanChat() {
        if (config.getString("clanchat.name-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();

        sb.append('&');
        sb.append(settings.getColored(CLANCHAT_BRACKET_COLOR));
        sb.append(settings.getString(CLANCHAT_BRACKET_LEFT));
        sb.append("%clan%");
        sb.append("&");
        sb.append(settings.getColored(CLANCHAT_BRACKET_COLOR));
        sb.append(settings.getString(CLANCHAT_BRACKET_RIGHT));
        sb.append(" ");
        sb.append('&');
        sb.append(settings.getColored(CLANCHAT_NAME_COLOR));
        sb.append(settings.getString(CLANCHAT_PLAYER_BRACKET_LEFT));
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(settings.getColored(CLANCHAT_NAME_COLOR));
        sb.append(settings.getString(CLANCHAT_PLAYER_BRACKET_RIGHT));
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(settings.getColored(CLANCHAT_MESSAGE_COLOR));
        sb.append("%message%");

        config.set("clanchat.format", sb.toString());
        config.set("clanchat.rank", "&f[%rank%&f]");
        config.set("clanchat.rank.color", null);
        config.set("clanchat.name-color", null);
        config.set("clanchat.player-bracket", null);
        config.set("clanchat.message-color", null);
        config.set("clanchat.tag-bracket", null);
    }

    public void migrateAllyChat() {
        //Checks if the old format is still in use
        if (config.getString("allychat.tag-color") == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(settings.getString(ALLYCHAT_BRACKET_lEFT));
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_TAG_COLOR));
        sb.append(settings.getString(COMMANDS_ALLY));
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(settings.getString(ALLYCHAT_BRACKET_RIGHT));
        sb.append(" ");
        sb.append("&4<%clan%&4> ");
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(settings.getString(ALLYCHAT_PLAYER_BRACKET_LEFT));
        sb.append("%nick-color%");
        sb.append("%player%");
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_BRACKET_COLOR));
        sb.append(settings.getString(ALLYCHAT_PLAYER_BRACKET_RIGHT));
        sb.append(" %rank%: ");
        sb.append('&');
        sb.append(settings.getColored(ALLYCHAT_MESSAGE_COLOR));
        sb.append("%message%");

        config.set("allychat.format", sb.toString());
        config.set("allychat.rank", "&f[%rank%&f]");
        config.set("allychat.tag-color", null);
        config.set("allychat.name-color", null);
        config.set("allychat.player-bracket", null);
        config.set("allychat.message-color", null);
        config.set("allychat.tag-bracket", null);
    }

    @Override
    public void migrate() {
        migrateClanChat();
        migrateAllyChat();
    }
}
