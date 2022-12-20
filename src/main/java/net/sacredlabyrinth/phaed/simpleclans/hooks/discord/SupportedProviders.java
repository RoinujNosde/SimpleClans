package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import net.sacredlabyrinth.phaed.simpleclans.hooks.discord.providers.discordsrv.DSRVProvider;

public enum SupportedProviders {
    DiscordSRV("DiscordSRV", DSRVProvider.class);

    private final Class<?> provider;

    private final String pluginName;

    SupportedProviders(String pluginName, Class<?> provider) {
        this.pluginName = pluginName;
        this.provider = provider;
    }

    public Class<?> getProvider() {
        return provider;
    }

    public String getPluginName() {
        return pluginName;
    }

}
