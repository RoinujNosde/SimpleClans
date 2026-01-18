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
		long interval = plugin.getSettingsManager().getMinutes(PERFORMANCE_SAVE_INTERVAL);
		plugin.getScheduler().runTimerAsync(this, interval, interval);
	}

	@Override
	public void run() {
		plugin.getStorageManager().saveModified();
	}
}
