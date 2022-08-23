package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.junit.Assert;
import org.junit.Test;

public class ObjectUtilsTest {

    @Test
    public void updateFields() throws IllegalAccessException {
        ClanPlayer cpOrigin = new ClanPlayer();
        cpOrigin.setName("Test");
        ClanPlayer cpDest = new ClanPlayer();
        ObjectUtils.updateFields(cpOrigin, cpDest);
        Assert.assertEquals("Test", cpDest.getName());

        Clan clanOrigin = new Clan();
        clanOrigin.setMemberFee(0.222);
        Clan clanDest = new Clan();
        ObjectUtils.updateFields(clanOrigin, clanDest);
        Assert.assertEquals(0.222, clanDest.getMemberFee(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateFieldsDifferentType() throws IllegalAccessException {
        ObjectUtils.updateFields(new Clan(), new ClanPlayer());
    }
}
