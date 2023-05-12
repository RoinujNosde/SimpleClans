package net.sacredlabyrinth.phaed.simpleclans.utils;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatUtilsTest {

    @Test
    public void getLastColors() {
        String lastColors = ChatUtils.getLastColors("§aa mensagem§x termina com §4vermelho");
        assertEquals("§4", lastColors);

        lastColors = ChatUtils.getLastColors("§aa mensagem§x termina com §a§4vermelho");
        assertEquals("§a§4", lastColors);

        lastColors = ChatUtils.getLastColors("§aa mensagem§x termina com §a§4ver§fmel§§§§§§§§§§ho");
        assertEquals("§f", lastColors);

        lastColors = ChatUtils.getLastColors("§aa mensagem§x termina com");
        assertEquals("§x", lastColors);

        lastColors = ChatUtils.getLastColors("§aa mensagem§ ax termina com");
        assertEquals("§a", lastColors);
    }

    @Test
    public void parseColors() {
        assertEquals("Hello", ChatUtils.parseColors("Hello"));
        assertEquals("§aHello §x§a§a§a§a§a§aWorld", ChatUtils.parseColors("&aHello &#aaaaaaWorld"));
    }

    @Test
    public void stripColors() {
        assertEquals("Hello", ChatUtils.stripColors("Hello"));
        assertEquals("&xHello", ChatUtils.stripColors("&xHello"));
        assertEquals("Hello", ChatUtils.stripColors("&#000000&#000000Hello"));
        assertEquals("&aHello", ChatUtils.stripColors("&#000000&&#000000aHello"));

    }
}
