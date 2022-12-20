package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

public abstract class AbstractListener<P extends AbstractProvider> implements DiscordListener {
    protected final P provider;
    protected final SettingsManager settingsManager;
    protected final ChatManager chatManager;
    protected final ClanManager clanManager;

    public AbstractListener(P provider) {
        this.provider = provider;

        SimpleClans plugin = this.provider.getPlugin();
        settingsManager = plugin.getSettingsManager();
        chatManager = plugin.getChatManager();
        clanManager = plugin.getClanManager();
    }
}
