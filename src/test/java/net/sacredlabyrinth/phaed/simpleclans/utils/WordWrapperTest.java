package net.sacredlabyrinth.phaed.simpleclans.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordWrapperTest {

    @Test
    void wrap() {
        String[] wrap = new WordWrapper("a§aa", 2).wrap();
        assertArrayEquals(new String[] {"§fa§aa"}, wrap);
        wrap = new WordWrapper("a§aa a§aa", 2).wrap();
        assertArrayEquals(new String[] {"§fa§aa", "§aa§aa"}, wrap);
    }

    @Test
    void split() {
        String s = "§x§f§f§f§f§f§f";
        List<String> split = WordWrapper.split(s, 1);
        assertEquals(1, split.size());
        assertLinesMatch(Collections.singletonList("§x§f§f§f§f§f§f"), split);
        s = "§x§f§f§f§f§f§faa";
        split = WordWrapper.split(s, 1);
        assertEquals(2, split.size());
        assertLinesMatch(Arrays.asList("§x§f§f§f§f§f§fa", "a"), split);
        s = "§x§fa§tfa§fa§f§f§faa";
        split = WordWrapper.split(s, 3);
        assertEquals(3, split.size());
        assertLinesMatch(Arrays.asList("§x§fa§t", "fa§fa", "§f§f§faa"), split);
    }
}