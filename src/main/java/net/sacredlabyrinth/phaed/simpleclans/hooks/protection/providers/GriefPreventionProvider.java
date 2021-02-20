package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class GriefPreventionProvider implements ProtectionProvider {

    @Override
    public void setup() {}

    @Override
    public @Nullable Set<Land> getLandsAt(@NotNull Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        return Collections.singleton(getLand(claim));
    }

    @NotNull
    private Land getLand(Claim claim) {
        return new Land(getIdPrefix() + claim.getID().toString(), Collections.singleton(claim.getOwnerID()));
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull Player player) {
        HashSet<Land> lands = new HashSet<>();
        for (Claim claim : GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getClaims()) {
            lands.add(getLand(claim));
        }
        return lands;
    }

    @Override
    public @NotNull String getIdPrefix() {
        return "gp";
    }

    @Override
    public void deleteLand(@NotNull String id, @NotNull World world) {
        DataStore dataStore = GriefPrevention.instance.dataStore;
        Claim claim = dataStore.getClaim(Long.parseLong(id.replaceFirst(getIdPrefix(), "")));
        if (claim != null) {
            dataStore.deleteClaim(claim);
        }
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "GriefPrevention";
    }
}
