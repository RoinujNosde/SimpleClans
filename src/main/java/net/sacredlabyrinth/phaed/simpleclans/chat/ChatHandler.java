package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ChatManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

public interface ChatHandler {
    SimpleClans plugin = SimpleClans.getInstance();
    SettingsManager settingsManager = plugin.getSettingsManager();
    ChatManager chatManager = plugin.getChatManager();

    void sendMessage(SCMessage message);

    boolean canHandle(SCMessage.Source source);
}
