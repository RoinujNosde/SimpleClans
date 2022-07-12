package net.sacredlabyrinth.phaed.simpleclans.uuid;

import java.util.UUID;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author NeT32
 */
public class UUIDMigration {

	private UUIDMigration() {}
	
    public static boolean canReturnUUID() {
        try {
            Bukkit.class.getDeclaredMethod("getPlayer", UUID.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static UUID getForcedPlayerUUID(String playerName) {
        Player player = Bukkit.getPlayerExact(playerName);

        if (player != null) {
        	return player.getUniqueId();
        } else {
        	for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
        		if (cp.getName().equalsIgnoreCase(playerName)) {
        			return cp.getUniqueId();
        		}
        	}
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            return offlinePlayer.getUniqueId();
        }
    }

}
