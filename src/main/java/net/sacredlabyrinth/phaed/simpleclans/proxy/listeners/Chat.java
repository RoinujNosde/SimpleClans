package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

public class Chat extends MessageListener {

    public Chat(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        SCMessage message = getGson().fromJson(data.readUTF(), SCMessage.class);
        if (message.getSender().getClan() == null) {
            return;
        }
        bungee.getPlugin().getChatManager().processChat(message);
    }

    @Override
    public boolean isBungeeSubchannel() {
        return false;
    }
}
