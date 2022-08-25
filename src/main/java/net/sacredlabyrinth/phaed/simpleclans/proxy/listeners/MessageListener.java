package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.gson.Gson;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;

public abstract class MessageListener {

    protected final BungeeManager bungee;

    public MessageListener(BungeeManager bungee) {
        this.bungee = bungee;
    }

    public abstract void accept(ByteArrayDataInput data);

    protected ClanManager getClanManager() {
        return bungee.getPlugin().getClanManager();
    }

    protected Gson getGson() {
        return bungee.getGson();
    }

}
