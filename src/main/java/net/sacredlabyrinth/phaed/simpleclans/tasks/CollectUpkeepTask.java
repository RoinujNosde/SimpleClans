package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.EconomyResponse;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.CurrencyFormat;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 *
 * @author roinujnosde
 */
public class CollectUpkeepTask extends BukkitRunnable {
	private final SimpleClans plugin;
	private final SettingsManager settingsManager;
	
	public CollectUpkeepTask() {
		plugin = SimpleClans.getInstance();
		settingsManager = plugin.getSettingsManager();
	}

    /**
     * Starts the repetitive task
     */
    public void start() {
    	int hour = settingsManager.getInt(TASKS_COLLECT_UPKEEP_HOUR);
    	int minute = settingsManager.getInt(TASKS_COLLECT_UPKEEP_MINUTE);
        long delay = Helper.getDelayTo(hour, minute);

        this.runTaskTimer(plugin, delay * 20, 86400 * 20);
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
        	if (settingsManager.is(ECONOMY_UPKEEP_REQUIRES_MEMBER_FEE) && !clan.isMemberFeeEnabled()) {
        		return;
        	}
            double upkeep = settingsManager.getDouble(ECONOMY_UPKEEP);
            if (settingsManager.is(ECONOMY_MULTIPLY_UPKEEP_BY_CLAN_SIZE)) {
                upkeep = upkeep * clan.getSize();
            }

            EconomyResponse response = clan.withdraw(BankOperator.INTERNAL, ClanBalanceUpdateEvent.Cause.UPKEEP, upkeep);
            if (response == EconomyResponse.NOT_ENOUGH_BALANCE) {
                clan.disband(null, true, false);
            }
            if (response == EconomyResponse.SUCCESS) {
                clan.addBb(MessageFormat.format(lang("upkeep.collected"), CurrencyFormat.format(upkeep)), false);
            }
        });
    }

}
