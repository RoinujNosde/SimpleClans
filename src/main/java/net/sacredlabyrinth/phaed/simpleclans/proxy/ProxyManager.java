package net.sacredlabyrinth.phaed.simpleclans.proxy;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;

public interface ProxyManager {

    boolean isOnline(String playerName);

    void sendMessage(SCMessage message);

    void sendUpdate(Clan clan);

    void sendUpdate(ClanPlayer cp);

    void sendDelete(Clan clan);

    void sendDelete(ClanPlayer cp);
}
