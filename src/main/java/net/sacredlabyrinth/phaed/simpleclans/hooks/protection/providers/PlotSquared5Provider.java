package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.plot.Plot;
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
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PlotSquared5Provider implements ProtectionProvider {

    @Override
    public void setup() throws NoSuchMethodException {
        //noinspection ResultOfMethodCallIgnored
        BukkitUtil.class.getMethod("getPlot", Location.class);
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        Plot plot = BukkitUtil.getPlot(location);
        if (plot != null) {
            return Collections.singleton(getLand(plot));
        }
        return Collections.emptySet();
    }

    @NotNull
    private Land getLand(@NotNull Plot plot) {
        return new Land(getIdPrefix() + plot.getId(), plot.getOwners());
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        Set<Plot> plots = PlotSquared.get().getPlots(world.getName(), player.getUniqueId());
        return plots.stream().map(this::getLand).collect(Collectors.toSet());
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "ps";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        id = id.replace(getIdPrefix(), "");
        for (Plot plot : PlotSquared.get().getPlots(world.getName())) {
            if (plot.getId().toString().equals(id)) {
                plot.deletePlot(null);
                break;
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
}
