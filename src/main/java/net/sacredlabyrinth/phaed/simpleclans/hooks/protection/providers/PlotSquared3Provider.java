package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.object.Plot;
import com.plotsquared.bukkit.events.PlayerClaimPlotEvent;
import com.plotsquared.bukkit.util.BukkitUtil;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PlotSquared3Provider implements ProtectionProvider {

    private Method getLocation;
    private Method getPlots;

    @Override
    public void setup() throws ClassNotFoundException, NoSuchMethodException {
        Class.forName("com.intellectualcrafters.plot.object.Location");
        Class.forName("com.intellectualcrafters.plot.object.Plot");
        getLocation = BukkitUtil.class.getMethod("getLocation", Location.class);
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        Plot plot = getPlot(location);
        if (plot != null) {
            return Collections.singleton(getLand(plot));
        }
        return Collections.emptySet();
    }

    private @Nullable Plot getPlot(@NotNull Location location) {
        try {
            return Plot.getPlot((com.intellectualcrafters.plot.object.Location) getLocation.invoke(null, location));
        } catch (IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private Land getLand(@NotNull Plot plot) {
        String id = getIdPrefix() + plot.getId().toString();
        return new Land(id, plot.getOwners());
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        Set<Plot> plots = PS.get().getPlots(world.getName(), player.getUniqueId());

        return plots.stream().map(this::getLand).collect(Collectors.toSet());
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "ps";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        id = id.replace(getIdPrefix(), "");
        Collection<Plot> plots = PS.get().getPlots(world.getName());
        for (Plot plot : plots) {
            if (plot.getId().toString().equals(id)) {
                plot.deletePlot(null);
                break;
            }
        }
    }

    @Override
    public @Nullable Class<? extends Event> getCreateLandEvent() {
        return PlayerClaimPlotEvent.class;
    }

    @Override
    public @Nullable Player getPlayer(Event event) {
        if (event instanceof PlayerClaimPlotEvent) {
            return ((PlayerClaimPlotEvent) event).getPlayer();
        }
        return null;
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "PlotSquared";
    }
}
