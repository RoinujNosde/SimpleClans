package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.junit.Test;

public class BungeeManagerTest {

    @Test
    public void updateFields() throws IllegalAccessException {
        ClanPlayer cpOrigin = new ClanPlayer();
        ClanPlayer cpDest = new ClanPlayer();
        BungeeManager.updateFields(cpOrigin, cpDest);

        Clan clanOrigin = new Clan();
        Clan clanDest = new Clan();
        BungeeManager.updateFields(clanOrigin, clanDest);
    }
}