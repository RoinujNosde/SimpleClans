package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import org.bukkit.Bukkit;

public class Broadcast extends MessageListener {

    public Broadcast(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        String message = data.readUTF();
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(message));
    }

    @Override
    public boolean isBungeeSubchannel() {
        return false;
    }
}
