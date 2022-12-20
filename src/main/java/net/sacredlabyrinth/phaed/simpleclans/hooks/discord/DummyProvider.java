package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_TEXT_CATEGORY_IDS;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_TEXT_WHITELIST;

public abstract class DummyProvider implements DiscordProvider {

    public static final int MAX_CHANNELS_PER_CATEGORY = 50;
    public static final int MAX_CHANNELS_PER_GUILD = 500;

    protected final SimpleClans plugin;
    protected final SettingsManager settingsManager;
    protected final ClanManager clanManager;
    protected final List<String> whitelist;
    protected final List<String> clanTags;
    protected final List<String> textCategories;

    public DummyProvider(SimpleClans plugin) {
        this.plugin = plugin;

        settingsManager = plugin.getSettingsManager();
        clanManager = plugin.getClanManager();

        whitelist = settingsManager.getStringList(DISCORDCHAT_TEXT_WHITELIST);
        clanTags = clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList());
        textCategories = settingsManager.getStringList(DISCORDCHAT_TEXT_CATEGORY_IDS).
                stream().filter(this::categoryExists).collect(Collectors.toList());
    }

    public SimpleClans getPlugin() {
        return plugin;
    }


}
