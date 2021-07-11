package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;

public class DiscordChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {

    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return false;
    }
}
