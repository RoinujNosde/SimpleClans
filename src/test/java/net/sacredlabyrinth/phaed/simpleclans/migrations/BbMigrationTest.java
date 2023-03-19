package net.sacredlabyrinth.phaed.simpleclans.migrations;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BbMigrationTest {

    @Test
    public void bb() {
        String actual = "1679069633622_§e§bpandas is no longer at war with turtles";
        String expected = "1679069633622_§8* §bpandas is no longer at war with turtles";

        actual = BbMigration.fromOldBb(actual, "§8", "§b");

        Assertions.assertEquals(expected, actual);
    }
}
