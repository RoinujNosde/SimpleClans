package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCondition implements IdentifiableCondition {

    protected final SimpleClans plugin;
    protected final PermissionsManager permissionsManager;
    protected final ClanManager clanManager;
    protected final RequestManager requestManager;
    protected final SettingsManager settingsManager;
    protected final ProtectionManager protectionManager;

    public AbstractCondition(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
        permissionsManager = plugin.getPermissionsManager();
        clanManager = plugin.getClanManager();
        requestManager = plugin.getRequestManager();
        settingsManager = plugin.getSettingsManager();
        protectionManager = plugin.getProtectionManager();
    }
}
