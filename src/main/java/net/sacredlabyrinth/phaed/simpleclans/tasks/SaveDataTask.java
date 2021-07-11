package net.sacredlabyrinth.phaed.simpleclans.tasks;

import org.bukkit.scheduler.BukkitRunnable;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

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
		long interval = plugin.getSettingsManager().getSaveInterval() * 20L;
		runTaskTimerAsynchronously(plugin, interval, interval);
	}

	@Override
	public void run() {
		plugin.getStorageManager().saveModified();
	}
}
