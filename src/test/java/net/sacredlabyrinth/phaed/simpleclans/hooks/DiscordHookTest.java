package net.sacredlabyrinth.phaed.simpleclans.hooks;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DiscordHookTest {

    @Test
    @DisplayName("Checks if colors from configuration can be transformed to java.awt.Color")
    public void leaderColorTest() throws IllegalArgumentException {
        Color expectedColor = new Color(231, 76, 60, 100);
        String[] colors = "231, 76, 60, 100".replaceAll("\\s", "").split(",");

        int red = Integer.parseInt(colors[0]);
        int green = Integer.parseInt(colors[1]);
        int blue = Integer.parseInt(colors[2]);
        int alpha = Integer.parseInt(colors[3]);
        Color actualColor = new Color(red, green, blue, alpha);

        assertEquals(expectedColor, actualColor);
    }
}
