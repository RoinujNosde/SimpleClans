package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;
import biz.princeps.landlord.api.events.LandPreClaimEvent;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Bukkit;
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
import java.util.UUID;

@SuppressWarnings("unused")
public class Landlord4Provider implements ProtectionProvider {

    private ILandLord landlord;

    @Override
    public void setup() throws LinkageError, Exception {
        //noinspection ConstantConditions
        landlord = (ILandLord) Bukkit.getPluginManager().getPlugin("Landlord");
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        IOwnedLand ownedLand = landlord.getWGManager().getRegion(location);
        if (ownedLand == null) {
            return Collections.emptySet();
        }
        HashSet<Land> lands = new HashSet<>();
        lands.add(getLand(ownedLand));
        return lands;
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        HashSet<Land> lands = new HashSet<>();
        Set<IOwnedLand> ownedLands = landlord.getWGManager().getRegions(player.getUniqueId(), world);
        for (IOwnedLand ownedLand : ownedLands) {
            lands.add(getLand(ownedLand));
        }
        return lands;
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "ll";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        id = id.replace(getIdPrefix(), "");

        landlord.getWGManager().unclaim(world, id);
    }

    @Override
    public @Nullable Class<? extends Event> getCreateLandEvent() {
        return LandPreClaimEvent.class;
    }

    @Override
    public @Nullable Player getPlayer(Event event) {
        if (!(event instanceof LandPreClaimEvent)) {
            return null;
        }
        return ((LandPreClaimEvent) event).getPlayer();
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "Landlord";
    }

    private @NotNull Land getLand(@NotNull IOwnedLand ownedLand) {
        String id = getIdPrefix() + ownedLand.getName();
        HashSet<UUID> owners = new HashSet<>();
        owners.add(ownedLand.getOwner());

        return new Land(id, owners);
    }
}
