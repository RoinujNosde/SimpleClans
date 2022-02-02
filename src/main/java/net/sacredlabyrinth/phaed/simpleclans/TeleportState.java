package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;


public class TeleportState {

    private final OfflinePlayer offlinePlayer;
    private final Location origin;
    private final Location destination;
    private int counter;
    private final String clanName;
    private boolean processing;

    public TeleportState(Player player, Location destination, String clanName, int counter) {
        this.offlinePlayer = player;
        this.destination = destination;
        this.origin = player.getLocation();
        this.clanName = clanName;
        this.counter = counter;
    }


    public Location getLocation() {
        return this.origin;
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
    	return offlinePlayer.getPlayer();
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

}
