package net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.events.ClaimCreatedEvent;
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

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

@SuppressWarnings("unused")
public class GriefPreventionProvider implements ProtectionProvider {

    @Override
    public void setup() throws NoSuchMethodException {
        //noinspection ResultOfMethodCallIgnored
        Claim.class.getMethod("getOwnerID");
    }

    @Override
    public @NotNull Set<Land> getLandsAt(@NotNull Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claim == null) {
            return Collections.emptySet();
        }
        return Collections.singleton(getLand(claim));
    }

    @Nullable
    private Land getLand(@Nullable Claim claim) {
        if (claim == null) {
            return null;
        }
        debug(String.format("id %s name %s uuid %s", claim.getID(), claim.getOwnerName(), claim.getOwnerID()));

        return new Land(getIdPrefix() + claim.getID().toString(), Collections.singleton(claim.getOwnerID()));
    }

    @Override
    public @NotNull Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        HashSet<Land> lands = new HashSet<>();
        for (Claim claim : GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId()).getClaims()) {
            if (claim == null) {
                continue;
            }
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
    public @Nullable Class<? extends Event> getCreateLandEvent() {
        return ClaimCreatedEvent.class;
    }

    @Override
    public @Nullable Player getPlayer(Event event) {
        if (event instanceof ClaimCreatedEvent) {
            if (((ClaimCreatedEvent) event).getCreator() instanceof Player) {
                return ((Player) ((ClaimCreatedEvent) event).getCreator());
            }
        }
        return null;
    }

    @Override
    public @Nullable String getRequiredPluginName() {
        return "GriefPrevention";
    }
}
