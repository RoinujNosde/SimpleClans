package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

public class GetServer extends MessageListener {

    public GetServer(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        String name = data.readUTF();
        bungee.setServerName(name);
    }
}
