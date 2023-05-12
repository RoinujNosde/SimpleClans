package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ObjectUtilsTest {

    private ClanPlayer cpOrigin;
    private ClanPlayer cpDest;
    private Clan clanOrigin;
    private Clan clanDest;

    @BeforeEach
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
        assertEquals("Test", cpDest.getName());

        clanOrigin.setMemberFee(0.222);
        ObjectUtils.updateFields(clanOrigin, clanDest);
        assertEquals(0.222, clanDest.getMemberFee(), 0);
    }

    @Test
    public void updateFieldsDifferentType() {
        assertThrows(IllegalArgumentException.class, () -> ObjectUtils.updateFields(clanOrigin, cpOrigin));
    }
}
