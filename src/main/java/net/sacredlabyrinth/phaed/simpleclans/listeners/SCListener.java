package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SCListener implements Listener {

    protected final SimpleClans plugin;

    public SCListener(SimpleClans plugin) {
        this.plugin = plugin;
    }

    public boolean isBlacklistedWorld(@NotNull Entity entity) {
        List<String> words = plugin.getSettingsManager().getStringList(ConfigField.BLACKLISTED_WORLDS);

        if (words.contains(entity.getWorld().getName())) {
            SimpleClans.debug("Blacklisted world");
            return true;
        }
        return false;
    }

}
