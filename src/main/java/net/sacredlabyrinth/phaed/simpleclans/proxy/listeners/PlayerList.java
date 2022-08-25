package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

import java.util.Arrays;
import java.util.List;

public class PlayerList extends MessageListener {

    public PlayerList(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        List<String> players = Arrays.asList(data.readUTF().split(", "));
        bungee.setOnlinePlayers(players);
    }
}
