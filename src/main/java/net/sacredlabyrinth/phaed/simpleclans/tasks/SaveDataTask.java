package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.scheduler.BukkitRunnable;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.PERFORMANCE_SAVE_INTERVAL;

/**
 * 
 * @author RoinujNosde
 * @since 2.10.2
 *
 */
public class SaveDataTask extends BukkitRunnable {
	SimpleClans plugin = SimpleClans.getInstance();

    /**
     * Starts the repetitive task
     */
	public void start() {
		long interval = plugin.getSettingsManager().getInt(PERFORMANCE_SAVE_INTERVAL) * 20L;
		runTaskTimerAsynchronously(plugin, interval, interval);
	}

	@Override
	public void run() {
		plugin.getStorageManager().saveModified();
	}
}
