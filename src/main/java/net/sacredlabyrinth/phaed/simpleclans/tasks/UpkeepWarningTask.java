package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

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
    	int hour = sm.get(TASKS_COLLECT_UPKEEP_WARNING_HOUR);
    	int minute = sm.get(TASKS_COLLECT_UPKEEP_WARNING_MINUTE);
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
        	if (sm.is(ECONOMY_UPKEEP_REQUIRES_MEMBER_FEE) && !clan.isMemberFeeEnabled()) {
        		return;
        	}
            final double balance = clan.getBalance();
            double upkeep = sm.getDouble(ECONOMY_UPKEEP);
            if (sm.is(ECONOMY_MULTIPLY_UPKEEP_BY_CLAN_SIZE)) {
                upkeep = upkeep * clan.getSize();
            }
            if (balance < upkeep) {
                clan.addBb(MessageFormat.format(lang("balance.is.not.enough.for.upkeep"), upkeep), false);
            }
        });
    }

}
