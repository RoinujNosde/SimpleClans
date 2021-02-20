package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class WorldGuardProvider implements ProtectionProvider {

    private RegionContainer regionContainer;

    public void setup() {
        regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public @Nullable Set<Land> getLandsAt(@NotNull Location location) {
        RegionManager regionManager = getRegionManager(location.getWorld());
        if (regionManager == null) {
            return null;
        }
        ApplicableRegionSet regions = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(location));
        Set<Land> lands = new HashSet<>();
        for (ProtectedRegion region : regions) {
            lands.add(getLand(region));
        }
        return lands;
    }

    @NotNull
    private Land getLand(ProtectedRegion region) {
        return new Land(getIdPrefix() + region.getId(), region.getOwners().getUniqueIds());
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull Player player) {
        HashSet<Land> lands = new HashSet<>();
        RegionManager regionManager = getRegionManager(player.getLocation().getWorld());
        if (regionManager != null) {
            for (ProtectedRegion region : regionManager.getRegions().values()) {
                if (region.getOwners().contains(player.getUniqueId())) {
                    lands.add(getLand(region));
                }
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

    @Nullable
    private RegionManager getRegionManager(@Nullable World world) {
        if (world == null) {
            return null;
        }
        return regionContainer.get(BukkitAdapter.adapt(world));
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "WorldGuard";
    }
}
