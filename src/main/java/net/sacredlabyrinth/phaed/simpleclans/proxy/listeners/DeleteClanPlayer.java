package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public class DeleteClanPlayer extends MessageListener {

    public DeleteClanPlayer(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        UUID uuid = UUID.fromString(data.readUTF());
        getClanManager().deleteClanPlayerFromMemory(uuid);
        debug(String.format("Deleted cp %s", uuid));
    }
}
