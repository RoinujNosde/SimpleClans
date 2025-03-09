package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.text.SimpleDateFormat;
import java.util.Date;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DATE_TIME_PATTERN;

public class DateFormat {

    private static final SimpleClans plugin = SimpleClans.getInstance();
    private static SimpleDateFormat format;

    static {
        String pattern = plugin.getSettingsManager().getString(DATE_TIME_PATTERN);
        try {
            format = new SimpleDateFormat(pattern);
        } catch (IllegalArgumentException ex) {
            plugin.getLogger().warning(String.format("%s is not a valid pattern!", (pattern)));
            format = new SimpleDateFormat("HH:mm - dd/MM/yyyy");
        }
    }

    public static String formatDateTime(long date) {
        return format.format(new Date(date));
    }

}
