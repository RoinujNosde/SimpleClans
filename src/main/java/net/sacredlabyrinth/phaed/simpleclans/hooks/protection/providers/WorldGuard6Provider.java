package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Coordinate;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class WorldGuard6Provider implements ProtectionProvider {

    private WorldGuardPlugin worldGuard;
    private Method getRegionManager;
    private Method getApplicableRegions;
    private Method getPoints;
    private Method getX;
    private Method getZ;

    @SuppressWarnings("JavaReflectionMemberAccess")
    @Override
    public void setup() throws NoSuchMethodException, ClassNotFoundException {
        worldGuard = WorldGuardPlugin.inst();
        getRegionManager = worldGuard.getClass().getMethod("getRegionManager", World.class);
        getApplicableRegions = RegionManager.class.getMethod("getApplicableRegions", Location.class);
        getPoints = ProtectedRegion.class.getMethod("getPoints");
        Class<?> blockVector = Class.forName("com.sk89q.worldedit.BlockVector2");
        getX = blockVector.getMethod("getX");
        getZ = blockVector.getMethod("getZ");
    }

    private @Nullable RegionManager getRegionManager(@Nullable World world) {
        if (world != null) {
            try {
                return (RegionManager) getRegionManager.invoke(worldGuard, world);
            } catch (IllegalAccessException | InvocationTargetException ignored) {
            }
        }
        return null;
    }

    private Set<ProtectedRegion> getApplicableRegions(RegionManager regionManager, Location location) {
        try {
            return ((ApplicableRegionSet) getApplicableRegions.invoke(regionManager, location)).getRegions();
        } catch (IllegalAccessException | InvocationTargetException e) {
            return Collections.emptySet();
        }
    }

    @NotNull
    private Land getLand(ProtectedRegion region) {
        List<Coordinate> coordinates = getCoordinates(region);

        return new Land(getIdPrefix() + region.getId(), region.getOwners().getUniqueIds(), coordinates);
    }

    @NotNull
    private List<Coordinate> getCoordinates(ProtectedRegion region) {
        List<Coordinate> coordinates = new ArrayList<>();
        try {
            List<?> points = (List<?>) getPoints.invoke(region);
            for (Object point : points) {
                double x = (double) getX.invoke(point);
                double z = (double) getZ.invoke(point);
                coordinates.add(new Coordinate(x, z));
            }
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
        return coordinates;
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        HashSet<Land> lands = new HashSet<>();
        RegionManager regionManager = getRegionManager(location.getWorld());

        if (regionManager != null) {
            for (ProtectedRegion region : getApplicableRegions(regionManager, location)) {
                lands.add(getLand(region));
            }
        }
        return lands;
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        HashSet<Land> lands = new HashSet<>();
        RegionManager regionManager = getRegionManager(world);

        if (regionManager != null) {
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                if (!region.getOwners().getUniqueIds().contains(player.getUniqueId())) {
                    continue;
                }
                lands.add(getLand(region));
            }
        }
        return lands;
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "wg";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        id = id.replaceFirst(getIdPrefix(), "");
        RegionManager regionManager = getRegionManager(world);
        if (regionManager != null) {
            regionManager.removeRegion(id);
        }
    }

    @Override
    public @Nullable Class<? extends Event> getCreateLandEvent() {
        return null;
    }

    @Override
    public @Nullable Player getPlayer(Event event) {
        return null;
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "WorldGuard";
    }
}
