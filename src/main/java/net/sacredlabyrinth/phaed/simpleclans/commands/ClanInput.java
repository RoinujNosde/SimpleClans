package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ClanInput {

    private final Clan clan;

    public ClanInput(@NotNull Clan clan) {
        this.clan = clan;
    }

    public Clan getClan() {
        return clan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClanInput clanInput = (ClanInput) o;
        return clan.equals(clanInput.clan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clan);
    }
}
