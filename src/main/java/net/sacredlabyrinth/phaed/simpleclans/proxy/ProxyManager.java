package net.sacredlabyrinth.phaed.simpleclans.proxy;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;

public interface ProxyManager {

    void sendMessage(SCMessage message);

    void sendDelete(Clan clan);

    void sendDelete(ClanPlayer cp);

    void sendUpdate(Clan clan);

    void sendUpdate(ClanPlayer cp);

    void sendInsert(Clan clan);

    void sendInsert(ClanPlayer cp);

}
