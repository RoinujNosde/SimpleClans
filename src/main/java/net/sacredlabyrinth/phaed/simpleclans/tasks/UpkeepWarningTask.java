package net.sacredlabyrinth.phaed.simpleclans.tasks;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 *
 * @author roinujnosde
 */
public class UpkeepWarningTask extends BukkitRunnable {
	private final SimpleClans plugin;
	private final SettingsManager sm;

	public UpkeepWarningTask() {
		plugin = SimpleClans.getInstance();
		sm = plugin.getSettingsManager();
	}
	
    /**
     * Starts the repetitive task
     *
     */
    public void start() {
    	int hour = sm.getTasksCollectUpkeepWarningHour();
    	int minute = sm.getTasksCollectUpkeepWarningMinute();
        long delay = Helper.getDelayTo(hour, minute);
        
		this.runTaskTimerAsynchronously(plugin, delay * 20, 86400 * 20);
    }

    /**
     * (used internally)
     */
    @Override
    public void run() {
    	if (plugin == null) {
    		throw new IllegalStateException("Use the start() method!");
    	}
        plugin.getClanManager().getClans().forEach((clan) -> {
        	if (sm.isChargeUpkeepOnlyIfMemberFeeEnabled() && !clan.isMemberFeeEnabled()) {
        		return;
        	}
            final double balance = clan.getBalance();
            double upkeep = sm.getClanUpkeep();
            if (sm.isMultiplyUpkeepBySize()) {
                upkeep = upkeep * clan.getSize();
            }
            if (balance < upkeep) {
                clan.addBb(MessageFormat.format(lang("balance.is.not.enough.for.upkeep"), upkeep), false);
            }
        });
    }

}
