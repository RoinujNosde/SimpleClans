package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_TEXT_WHITELIST;

public abstract class AbstractProvider implements DiscordProvider {

    public static final int MAX_CHANNELS_PER_CATEGORY = 50;
    public static final int MAX_CHANNELS_PER_GUILD = 500;

    protected final SimpleClans plugin;
    protected final SettingsManager settingsManager;
    protected final ClanManager clanManager;
    protected final List<String> whitelist;
    protected final List<String> clanTags;

    public AbstractProvider(SimpleClans plugin) {
        this.plugin = plugin;

        settingsManager = plugin.getSettingsManager();
        clanManager = plugin.getClanManager();

        whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);
        clanTags = clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList());
    }

    public SimpleClans getPlugin() {
        return plugin;
    }

    protected abstract void setupDiscord();
}
