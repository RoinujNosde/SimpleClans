package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
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
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PlotSquared6Provider implements ProtectionProvider {

    @Override
    public void setup() {

    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        Plot plot = BukkitUtil.adapt(location).getPlot();
        if (plot != null) {
            return Collections.singleton(getLand(plot));
        }
        return Collections.emptySet();
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        Set<Plot> plots = new HashSet<>();
        for (PlotArea plotArea : PlotSquared.get().getPlotAreaManager().getPlotAreasSet(world.getName())) {
            plots.addAll(plotArea.getPlots(player.getUniqueId()));
        }
        return plots.stream().map(this::getLand).collect(Collectors.toSet());
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "ps";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        id = id.replace(getIdPrefix(), "");
        Set<Plot> plots = new HashSet<>();
        for (PlotArea plotArea : PlotSquared.get().getPlotAreaManager().getPlotAreasSet(world.getName())) {
            for (Plot plot : plotArea.getPlots()) {
                if (plot.getId().toString().equals(id)) {
                    plot.unclaim();
                    return;
                }
            }
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
        return "PlotSquared";
    }

    @NotNull
    private Land getLand(@NotNull Plot plot) {
        return new Land(getIdPrefix() + plot.getId(), plot.getOwners());
    }
}
