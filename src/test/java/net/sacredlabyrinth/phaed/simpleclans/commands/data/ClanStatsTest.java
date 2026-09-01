package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ClanStats class.
 * <p>
 * This test class verifies the behavior of the ClanStats class,
 * including adding rows with different ClanPlayer configurations and handling various edge cases.
 *
 * @see ClanStats
 */
public class ClanStatsTest {

    private ClanStats clanStats;
    private SettingsManager sm;

    @BeforeEach
    void setUp() {
        SimpleClans plugin = mock(SimpleClans.class);
        sm = mock(SettingsManager.class);
        when(plugin.getSettingsManager()).thenReturn(sm);

        when(sm.getColored(PAGE_HEADINGS_COLOR)).thenReturn(ChatColor.YELLOW.toString());
        when(sm.getColored(PAGE_SUBTITLE_COLOR)).thenReturn(ChatColor.GRAY.toString());

        clanStats = new ClanStats(plugin, mock(CommandSender.class), mock(Clan.class));
    }

    @Test
    public void testAddRows() {
        ClanPlayer leader = createClanPlayer(true, false, "LeaderName", Locale.US, 5325, 0, 1, Integer.MAX_VALUE, 20.0F);
        ClanPlayer trusted = createClanPlayer(false, true, "TrustedName", new Locale("ru", "RU"), 1000, 50, 25, 1, 2.5F);
        ClanPlayer untrusted = createClanPlayer(false, false, "UntrustedName", new Locale("pt", "BR"), 1523, 0, 0, 0, 0.0F);

        when(sm.getColored(PAGE_LEADER_COLOR)).thenReturn(ChatColor.RED.toString());
        when(sm.getColored(PAGE_TRUSTED_COLOR)).thenReturn(ChatColor.GREEN.toString());
        when(sm.getColored(PAGE_UNTRUSTED_COLOR)).thenReturn(ChatColor.BLUE.toString());

        List<ClanPlayer> clanPlayers = Arrays.asList(leader, trusted, untrusted);

        // Act
        clanStats.addRows(clanPlayers);

        // Verify that rows the same size of clanplayers passed
        assertEquals(clanPlayers.size(), clanStats.chatBlock.size());

        // Verify that each row contains expected formatted strings
        String[] leaderContent = clanStats.chatBlock.getRows().get(0);
        String maxNumberFormatted = NumberFormat.getInstance(leader.getLocale()).format(Integer.MAX_VALUE);
        String[] expectedLeaderContent = {"§cLeaderName", "§e20", "§f5,325", "§70", "§81", "§4" + maxNumberFormatted};
        assertArrayEquals(expectedLeaderContent, leaderContent);

        String[] trustedContent = clanStats.chatBlock.getRows().get(1);
        String[] expectedTrustedContent = {"§aTrustedName", "§e2.5", "§f1 000", "§750", "§825", "§41"};
        assertArrayEquals(expectedTrustedContent, trustedContent);

        String[] untrustedContent = clanStats.chatBlock.getRows().get(2);
        String[] expectedUntrustedContent = {"§9UntrustedName", "§e0", "§f1.523", "§70", "§80", "§40"};
        assertArrayEquals(expectedUntrustedContent, untrustedContent);

        // Clear rows and verify chat block is empty
        clanStats.chatBlock.getRows().clear();
        assertTrue(clanStats.chatBlock.getRows().isEmpty(), "Expected no rows after clearing.");
    }


    /**
     * Tests adding an empty list of ClanPlayers and ensures no rows are added.
     */
    @Test
    public void testAddRowsWithEmptyList() {
        // Act
        clanStats.addRows(Collections.emptyList());

        // Assert
        assertTrue(clanStats.chatBlock.getRows().isEmpty(), "Expected no rows to be added for an empty clanplayer list.");
    }

    /**
     * Helper method to create a mock ClanPlayer with specified attributes.
     *
     * @param isLeader whether the player is a leader
     * @param isTrusted whether the player is trusted
     * @param name the player's name
     * @param locale the player's locale
     * @param rivalKills the number of rival kills
     * @param neutralKills the number of neutral kills
     * @param civilianKills the number of civilian kills
     * @param deaths the number of deaths
     * @param kdr the kill/death ratio
     * @return a mock ClanPlayer configured with the specified attributes
     */
    private ClanPlayer createClanPlayer(
            boolean isLeader, boolean isTrusted, String name, Locale locale,
            int rivalKills, int neutralKills, int civilianKills, int deaths, float kdr) {

        ClanPlayer player = mock(ClanPlayer.class);
        when(player.isLeader()).thenReturn(isLeader);
        when(player.isTrusted()).thenReturn(isTrusted);
        when(player.getName()).thenReturn(name);
        when(player.getLocale()).thenReturn(locale);
        when(player.getRivalKills()).thenReturn(rivalKills);
        when(player.getNeutralKills()).thenReturn(neutralKills);
        when(player.getCivilianKills()).thenReturn(civilianKills);
        when(player.getDeaths()).thenReturn(deaths);
        when(player.getKDR()).thenReturn(kdr);

        return player;
    }
}
