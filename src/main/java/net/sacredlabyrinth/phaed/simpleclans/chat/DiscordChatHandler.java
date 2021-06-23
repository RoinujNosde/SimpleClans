package net.sacredlabyrinth.phaed.simpleclans.chat;

public class DiscordChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {

    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return false;
    }

    @Override
    public String formatMessage(SCMessage message) {
        return null;
    }
}
