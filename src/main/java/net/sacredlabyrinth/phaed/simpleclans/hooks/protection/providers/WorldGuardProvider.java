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
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unused"})
public class WorldGuardProvider implements ProtectionProvider {

    private RegionContainer regionContainer;

    public void setup() {
        regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        RegionManager regionManager = getRegionManager(location.getWorld());
        if (regionManager == null) {
            return Collections.emptySet();
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
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        HashSet<Land> lands = new HashSet<>();
        RegionManager regionManager = getRegionManager(world);
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

    @Override
    public @Nullable Class<? extends Event> getCreateLandEvent() {
        return null;
    }

    @Override
    public @Nullable Player getPlayer(Event event) {
        return null;
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
