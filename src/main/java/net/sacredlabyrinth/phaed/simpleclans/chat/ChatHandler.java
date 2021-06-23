package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

public interface ChatHandler {
    SimpleClans plugin = SimpleClans.getInstance();

    void send(SCMessage message);
}
