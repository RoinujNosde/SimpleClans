package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public class DeleteClan extends MessageListener {

    public DeleteClan(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        String tag = data.readUTF();
        getClanManager().removeClan(tag);
        debug(String.format("Deleted clan %s", tag));
    }
}
