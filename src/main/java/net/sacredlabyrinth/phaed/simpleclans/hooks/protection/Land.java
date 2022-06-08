package net.sacredlabyrinth.phaed.simpleclans.hooks.protection;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Land {

    private final String id;
    private final Set<UUID> owners;
    private final List<Coordinate> coordinates;

    public Land(@NotNull String id, @NotNull Set<UUID> owners) {
        this(id, owners, Collections.emptyList());
    }

    public Land(@NotNull String id, @NotNull Set<UUID> owners, @NotNull List<Coordinate> coordinates) {
        this.id = id;
        this.owners = owners;
        this.coordinates = coordinates;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    public Set<UUID> getOwners() {
        return owners;
    }

    public @NotNull List<Coordinate> getCoordinates() {
        return coordinates;
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
