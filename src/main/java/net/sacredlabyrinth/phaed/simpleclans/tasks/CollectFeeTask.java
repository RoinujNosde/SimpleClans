package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

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
    	
    	int hour = sm.getTasksCollectFeeHour();
    	int minute = sm.getTasksCollectFeeMinute();
        long delay = Helper.getDelayTo(hour, minute);
        
        this.runTaskTimerAsynchronously(plugin, delay * 20, 86400 * 20);
    }
    
    /**
     * (used internally)
     */
    @Override
    public void run() {
        for (Clan clan : plugin.getClanManager().getClans()) {
            final double memberFee = clan.getMemberFee();
            
            if (clan.isMemberFeeEnabled() && memberFee > 0) {
                for (ClanPlayer cp : clan.getFeePayers()) {
                	                	
					final boolean success = plugin.getPermissionsManager()
							.playerChargeMoney(Bukkit.getOfflinePlayer(cp.getUniqueId()), memberFee);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (success) {
                                ChatBlock.sendMessage(cp.toPlayer(), ChatColor.AQUA + 
                                        MessageFormat.format(lang("fee.collected",cp.toPlayer()), memberFee));
                                clan.deposit(cp.toPlayer(), ClanBalanceUpdateEvent.Cause.MEMBER_FEE, memberFee);
                                plugin.getStorageManager().updateClan(clan);
                            } else {
                            	clan.removePlayerFromClan(cp.getUniqueId());
                                clan.addBb(ChatColor.AQUA + 
                                        MessageFormat.format(lang("bb.fee.player.kicked"), cp.getName()));
                            }
                        }
                    }.runTask(plugin);   
                }
            }
        } 
    }
}
