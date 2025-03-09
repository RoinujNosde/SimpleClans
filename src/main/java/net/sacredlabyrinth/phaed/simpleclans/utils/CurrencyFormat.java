package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;

import java.text.NumberFormat;

public class CurrencyFormat {

    private static final SimpleClans plugin = SimpleClans.getInstance();
    private static final NumberFormat fallbackFormat = NumberFormat.getCurrencyInstance();

    public static String format(double value) {
        PermissionsManager permissionsManager = plugin.getPermissionsManager();
        if (permissionsManager.hasEconomy()) {
            return permissionsManager.format(value);
        }
        return fallbackFormat.format(value);
    }

}
