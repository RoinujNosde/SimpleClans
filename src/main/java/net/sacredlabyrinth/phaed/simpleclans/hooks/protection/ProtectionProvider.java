package net.sacredlabyrinth.phaed.simpleclans.hooks.protection;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface ProtectionProvider {

    void setup();

    @NotNull
    Set<Land> getLandsAt(@NotNull Location location);

    @NotNull
    Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world);

    @NotNull
    String getIdPrefix();

    void deleteLand(@NotNull String id, @NotNull World world);

    @Nullable Class<? extends Event> getCreateLandEvent();

    @Nullable
    Player getPlayer(Event event);

    @Nullable
    String getRequiredPluginName();
}
