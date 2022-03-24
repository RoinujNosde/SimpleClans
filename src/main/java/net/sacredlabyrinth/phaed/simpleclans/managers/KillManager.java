package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Kill;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.KDR_DELAY_BETWEEN_KILLS;

public class KillManager {
    SimpleClans plugin;

    public SimpleClans getPlugin() {
        return plugin;
    }

    public void setPlugin(SimpleClans plugin) {
        this.plugin = plugin;
    }

    final HashMap<ClanPlayer, List<Kill>> kills = new HashMap<ClanPlayer, List<Kill>>();

    public HashMap<ClanPlayer, List<Kill>> getKills() {
        return kills;
    }

    public KillManager() {
    }

    /**
     * Adds a kill to the memory
     */
    public void addKill(Kill kill) {
        if (kill == null) {
            return;
        }

        List<Kill> list = kills.computeIfAbsent(kill.getKiller(), k -> new ArrayList<Kill>());

        Iterator<Kill> iterator = list.iterator();
        while (iterator.hasNext()) {
            Kill oldKill = iterator.next();
            if (oldKill.getVictim().equals(kill.getKiller())) {
                iterator.remove();
                continue;
            }

            //cleaning
            final int delay = plugin.getSettingsManager().getInt(KDR_DELAY_BETWEEN_KILLS);
            long timePassed = oldKill.getTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            if (timePassed >= delay) {
                iterator.remove();
            }
        }

        list.add(kill);
    }

    /**
     * Checks if this kill respects the delay
     */
    public boolean isKillBeforeDelay(Kill kill) {
        if (kill == null) {
            return false;
        }
        List<Kill> list = kills.get(kill.getKiller());
        if (list == null) {
            return false;
        }

        for (Kill oldKill : list) {
            if (oldKill.getVictim().equals(kill.getVictim())) {

                final int delay = plugin.getSettingsManager().getInt(KDR_DELAY_BETWEEN_KILLS);
                long timePassed = oldKill.getTime().until(kill.getTime(), ChronoUnit.MINUTES);
                if (timePassed < delay) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Sort clans by kdr
     */
    public void sortClansByKDR(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return Float.compare(c1.getTotalKDR(), c2.getTotalKDR()) * o;
        });
    }

    /**
     * Sort clans by KDR
     */
    public void sortClansByKDR(List<Clan> clans) {
        clans.sort((c1, c2) -> {
            Float o1 = c1.getTotalKDR();
            Float o2 = c2.getTotalKDR();

            return o2.compareTo(o1);
        });
    }

    /**
     * Sort clan players by KDR
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> cps) {
        cps.sort((c1, c2) -> {
            Float o1 = c1.getKDR();
            Float o2 = c2.getKDR();

            return o2.compareTo(o1);
        });
    }
}