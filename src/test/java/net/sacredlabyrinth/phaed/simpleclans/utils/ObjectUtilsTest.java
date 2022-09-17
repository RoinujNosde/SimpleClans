package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ObjectUtilsTest {

    private ClanPlayer cpOrigin;
    private ClanPlayer cpDest;
    private Clan clanOrigin;
    private Clan clanDest;

    @Before
    public void setup() {
        cpOrigin = new ClanPlayer();
        cpOrigin.setName("Test");
        cpDest = new ClanPlayer();
        clanOrigin = new Clan();
        clanDest = new Clan();
    }

    @Test
    public void updateFields() throws IllegalAccessException {
        ObjectUtils.updateFields(cpOrigin, cpDest);
        Assert.assertEquals("Test", cpDest.getName());

        clanOrigin.setMemberFee(0.222);

        ObjectUtils.updateFields(clanOrigin, clanDest);
        Assert.assertEquals(0.222, clanDest.getMemberFee(), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateFieldsDifferentType() throws IllegalAccessException {
        ObjectUtils.updateFields(clanOrigin, cpOrigin);
    }
}
