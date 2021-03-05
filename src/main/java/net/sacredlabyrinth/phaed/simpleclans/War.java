package net.sacredlabyrinth.phaed.simpleclans;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class War {
    private final Map<Clan, Integer> clans = new HashMap<>();

    public War(@NotNull Clan clan1, @NotNull Clan clan2) {
        clans.put(clan1, 0);
        clans.put(clan2, 0);
    }

    public List<Clan> getClans() {
        return new ArrayList<>(clans.keySet());
    }

    public int getTotalCasualties() {
        return clans.values().stream().mapToInt(value -> value).sum();
    }

    public int getCasualties(@NotNull Clan clan) {
        return clans.getOrDefault(clan, 0);
    }

    public void increaseCasualties(@NotNull Clan clan) {
        clans.computeIfPresent(clan, (c, i) -> i + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof War) {
            return clans.equals(((War) obj).clans);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clans.hashCode();
    }
}
