package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateCheckerTest {

    @Test
    public void compareVersions() {
        assertEquals(UpdateChecker.compareVersions("0.0", "0"), 0);
        assertTrue(UpdateChecker.compareVersions("0.1", "0") > 0);
        assertTrue(UpdateChecker.compareVersions("1", "1.1.1") < 0);
        assertTrue(UpdateChecker.compareVersions("1.1", "1") > 0);
        assertTrue(UpdateChecker.compareVersions("1", "0") > 0);
        assertEquals(UpdateChecker.compareVersions("1.0.0", "1"), 0);
        assertEquals(UpdateChecker.compareVersions("1.0", "1"), 0);
        assertEquals(UpdateChecker.compareVersions("1.0.0", "1.0"), 0);
        assertEquals(UpdateChecker.compareVersions("1.0.0", "1.0.0"), 0);
    }
}