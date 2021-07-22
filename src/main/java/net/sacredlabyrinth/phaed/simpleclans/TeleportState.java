package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.CLAN_TELEPORT_DELAY;

public class TeleportState {
    private final UUID playerUniqueId;
    private final Location playerLocation;
    private final Location destination;
    private int counter;
    private final String clanName;
    private boolean processing;

    public TeleportState(Player player, Location destination, String clanName) {
        this.destination = destination;
        this.playerLocation = player.getLocation();
        this.clanName = clanName;
        this.counter = SimpleClans.getInstance().getSettingsManager().getInt(CLAN_TELEPORT_DELAY);
        this.playerUniqueId = player.getUniqueId();
    }


    public Location getLocation() {
        return this.playerLocation;
    }

    public boolean isTeleportTime() {
        if (this.counter > 1) {
            this.counter--;
            return false;
        }

        return true;
    }

    /**
     * The player that is waiting for teleport
     *
     * @return the player
     */
    public @Nullable Player getPlayer() {
    	return SimpleClans.getInstance().getServer().getPlayer(this.playerUniqueId);
    }

    /**
     * Get seconds left before teleport
     *
     * @return the counter
     */
    public int getCounter() {
        return this.counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getClanName() {
        return this.clanName;
    }

    public Location getDestination() {
        return this.destination;
    }

    public boolean isProcessing() {
        return this.processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public UUID getUniqueId() {
        return this.playerUniqueId;
    }
}
