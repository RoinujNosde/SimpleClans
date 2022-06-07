package net.sacredlabyrinth.phaed.simpleclans.hooks.protection;

import org.bukkit.Location;

import java.util.Objects;

public class Coordinate {

    private final double x;
    private final double z;


    public Coordinate(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public Coordinate(Location location) {
        this.x = location.getX();
        this.z = location.getZ();
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return Double.compare(that.getX(), getX()) == 0 && Double.compare(that.getZ(), getZ()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getZ());
    }
}
