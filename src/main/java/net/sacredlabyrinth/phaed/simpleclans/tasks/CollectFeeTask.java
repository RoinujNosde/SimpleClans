package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent.Cause;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TASKS_COLLECT_FEE_HOUR;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TASKS_COLLECT_FEE_MINUTE;
import static org.bukkit.ChatColor.AQUA;

/**
 *
 * @author roinujnosde
 */
public class CollectFeeTask extends BukkitRunnable {
	private final SimpleClans plugin;
    
	public CollectFeeTask() {
		plugin = SimpleClans.getInstance();
	}
	
    /**
     * Starts the repetitive task
     */
    public void start() {
    	SettingsManager sm = plugin.getSettingsManager();
    	
    	int hour = sm.getInt(TASKS_COLLECT_FEE_HOUR);
    	int minute = sm.getInt(TASKS_COLLECT_FEE_MINUTE);
        long delay = Helper.getDelayTo(hour, minute);
        
        this.runTaskTimer(plugin, delay * 20, 86400 * 20);
    }
    
    /**
     * (used internally)
     */
    @Override
    public void run() {
        PermissionsManager pm = plugin.getPermissionsManager();
        for (Clan clan : plugin.getClanManager().getClans()) {
            final double memberFee = clan.getMemberFee();
            if (!clan.isMemberFeeEnabled() || memberFee <= 0) {
                continue;
            }

            for (ClanPlayer cp : clan.getFeePayers()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(cp.getUniqueId());
                boolean success = pm.playerChargeMoney(player, memberFee);
                if (success) {
                    ChatBlock.sendMessage(cp, AQUA + lang("fee.collected", cp, memberFee));

                    clan.deposit(new BankOperator(cp, pm.playerGetMoney(player)), Cause.MEMBER_FEE, memberFee);
                    plugin.getStorageManager().updateClan(clan);
                } else {
                    clan.removePlayerFromClan(cp.getUniqueId());
                    clan.addBb(lang("bb.fee.player.kicked", cp.getName()));
                }
            }
        } 
    }
}
