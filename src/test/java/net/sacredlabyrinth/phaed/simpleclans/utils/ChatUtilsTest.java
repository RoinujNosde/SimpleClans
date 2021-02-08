package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatUtilsTest {

    @Test
    void getLastColors() {
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
}