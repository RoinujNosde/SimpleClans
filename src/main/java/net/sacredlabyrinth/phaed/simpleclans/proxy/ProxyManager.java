package net.sacredlabyrinth.phaed.simpleclans.proxy;

import com.google.gson.Gson;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;

public interface ProxyManager {

    String getServerName();

    boolean isOnline(String playerName);

    void sendMessage(SCMessage message);

    void sendMessage(String target, String message);

    void sendUpdate(Clan clan);

    void sendUpdate(ClanPlayer cp);

    void sendDelete(Clan clan);

    void sendDelete(ClanPlayer cp);

    Gson getGson();
}
