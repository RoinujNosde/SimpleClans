package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class WorldGuard6Provider implements ProtectionProvider {

    private WorldGuardPlugin worldGuard;
    private Method getRegionManager;
    private Method getApplicableRegions;

    @Override
    public void setup() {
        worldGuard = WorldGuardPlugin.inst();
        try {
            getRegionManager = worldGuard.getClass().getMethod("getRegionManager", World.class);
            //noinspection JavaReflectionMemberAccess
            getApplicableRegions = RegionManager.class.getMethod("getApplicableRegions", Location.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
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
        return new Land(getIdPrefix() + region.getId(), region.getOwners().getUniqueIds());
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
