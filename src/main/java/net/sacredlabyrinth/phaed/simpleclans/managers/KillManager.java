package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Kill;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class KillManager {
    private final ClanManager clanManager;
    public final HashMap<ClanPlayer, List<Kill>> kills = new HashMap<ClanPlayer, List<Kill>>();

    public KillManager(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    /**
     * Adds a kill to the memory
     *
     * @param kill
     */
    public void addKill(Kill kill) {
        if (kill == null) {
            return;
        }

        List<Kill> list = kills.get(kill.getKiller());
        if (list == null) {
            list = new ArrayList<Kill>();
            kills.put(kill.getKiller(), list);
        }

        Iterator<Kill> iterator = list.iterator();
        while (iterator.hasNext()) {
            Kill oldKill = iterator.next();
            if (oldKill.getVictim().equals(kill.getKiller())) {
                iterator.remove();
                continue;
            }

            //cleaning
            final int delay = clanManager.plugin.getSettingsManager().getDelayBetweenKills();
            long timePassed = oldKill.getTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            if (timePassed >= delay) {
                iterator.remove();
            }
        }

        list.add(kill);
    }

    /**
     * Checks if this kill respects the delay
     *
     * @param kill
     * @return
     */
    public boolean isKillBeforeDelay(Kill kill) {
        if (kill == null) {
            return false;
        }
        List<Kill> list = kills.get(kill.getKiller());
        if (list == null) {
            return false;
        }

        Iterator<Kill> iterator = list.iterator();
        while (iterator.hasNext()) {

            Kill oldKill = iterator.next();
            if (oldKill.getVictim().equals(kill.getVictim())) {

                final int delay = clanManager.plugin.getSettingsManager().getDelayBetweenKills();
                long timePassed = oldKill.getTime().until(kill.getTime(), ChronoUnit.MINUTES);
                if (timePassed < delay) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Reset a player's KDR
     *
     * @param clanPlayer
     */
    public void resetKdr(ClanPlayer clanPlayer) {
        clanPlayer.setCivilianKills(0);
        clanPlayer.setNeutralKills(0);
        clanPlayer.setRivalKills(0);
        clanPlayer.setDeaths(0);
        clanManager.plugin.getStorageManager().updateClanPlayer(clanPlayer);
    }

    /**
     * Sort clans by kdr
     *  @param clans
     * @param asc
     */
    public void sortClansByKDR(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return ((Float) c1.getTotalKDR()).compareTo(c2.getTotalKDR()) * o;
        });
    }
}