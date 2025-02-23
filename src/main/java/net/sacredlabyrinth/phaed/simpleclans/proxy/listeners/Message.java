package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Message extends MessageListener {

    public Message(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        String target = data.readUTF();
        String message = data.readUTF();

        Player player = Bukkit.getPlayerExact(target);
        if (player != null) {
            player.sendMessage(message);
        }
    }
}
