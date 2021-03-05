package net.sacredlabyrinth.phaed.simpleclans.hooks.protection;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Land {

    private final String id;
    private final Set<UUID> owners;

    public Land(@NotNull String id, @NotNull Set<UUID> owners) {
        this.id = id;
        this.owners = owners;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Set<UUID> getOwners() {
        return owners;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Land land = (Land) o;
        return id.equals(land.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
