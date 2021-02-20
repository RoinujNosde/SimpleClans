package net.sacredlabyrinth.phaed.simpleclans.hooks.protection;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ProtectionProvider {

    void setup();

    @Nullable
    Set<Land> getLandsAt(@NotNull Location location);

    @NotNull
    Set<Land> getLandsOf(@NotNull Player player);

    @NotNull
    String getIdPrefix();

    void deleteLand(@NotNull String id, @NotNull World world);

    // TODO Create event?

    @Nullable
    String getRequiredPluginName();
}
