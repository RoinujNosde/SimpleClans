package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Kill;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.events.AddKillEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class PlayerDeath implements Listener {

    private final SimpleClans plugin;

    public PlayerDeath() {
        plugin = SimpleClans.getInstance();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(PlayerDeathEvent event) {
        SimpleClans.debug("A player died");
        Player victim = event.getEntity();
        if (plugin.getSettingsManager().isBlacklistedWorld(victim.getLocation().getWorld())
                || victim.hasMetadata("NPC")) {
            SimpleClans.debug("Blacklisted world");
            return;
        }

        Player attacker = Events.getAttacker(victim.getLastDamageCause());
        if (isInvalidKill(victim, attacker)) return;

        ClanPlayer victimCp = plugin.getClanManager().getCreateClanPlayer(victim.getUniqueId());
        ClanPlayer attackerCp = plugin.getClanManager().getCreateClanPlayer(attacker.getUniqueId());

        addKill(victimCp, attackerCp);
        giveMoneyReward(victimCp, attackerCp);

        // record death for victim
        victimCp.addDeath();
        plugin.getStorageManager().updateClanPlayer(victimCp);
    }

    private void addKill(@NotNull ClanPlayer victim, @NotNull ClanPlayer attacker) {
        if (victim.getClan() == null || attacker.getClan() == null || !victim.getClan().isVerified() ||
                !attacker.getClan().isVerified()) {
            addKill(Kill.Type.CIVILIAN, attacker, victim);
        } else if (attacker.getClan().isRival(victim.getTag())) {
            addKill(Kill.Type.RIVAL, attacker, victim);
        } else {
            addKill(Kill.Type.NEUTRAL, attacker, victim);
        }
    }

    private void giveMoneyReward(@NotNull ClanPlayer victim, @NotNull ClanPlayer attacker) {
        if (!plugin.getSettingsManager().isMoneyPerKill()) {
            return;
        }
        Clan attackerClan = attacker.getClan();
        if (attackerClan == null) {
            return;
        }
        double reward = calculateReward(attacker, victim);
        if (reward != 0) {
            for (ClanPlayer cp : attackerClan.getOnlineMembers()) {
                double money = Math.round((reward / attacker.getClan().getOnlineMembers().size()) * 100D) / 100D;
                Player player = cp.toPlayer();
                if (player == null) {
                    continue;
                }
                player.sendMessage(ChatColor.AQUA + lang("player.got.money", player, money,
                        victim.getName(), attacker.getKDR()));
                plugin.getPermissionsManager().playerGrantMoney(player, money);
            }
        }
    }

    @Contract("_, null -> true")
    private boolean isInvalidKill(@NotNull Player victim, @Nullable Player attacker) {
        if (attacker == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            SimpleClans.debug("Attacker is not a player or victim and attacker have the same UUID");
            return true;
        }

        if (SimpleClans.getInstance().getSettingsManager().isDenySameIPKills()) {
            InetSocketAddress attackerAddress = attacker.getAddress();
            InetSocketAddress victimAddress = victim.getAddress();
            if (attackerAddress != null && victimAddress != null) {
                if (attackerAddress.getHostString().equals(victimAddress.getHostString())) {
                    plugin.getLogger().log(Level.INFO, "Blocked same IP kill calculating: {0} killed {1}. IP: {2}",
                            new Object[]{attacker.getDisplayName(), victim.getDisplayName(),
                                    attackerAddress.getHostString()});
                    return true;
                }
            }
        }
        AddKillEvent addKillEvent = new AddKillEvent(plugin.getClanManager().getCreateClanPlayer(
                attacker.getUniqueId()), plugin.getClanManager().getCreateClanPlayer(victim.getUniqueId()));
        Bukkit.getServer().getPluginManager().callEvent(addKillEvent);
        if (addKillEvent.isCancelled()) {
            return true;
        }

        String kdrExempt = "simpleclans.other.kdr-exempt";
        PermissionsManager pm = plugin.getPermissionsManager();
        return pm.has(attacker, kdrExempt) || pm.has(victim, kdrExempt);
    }

    private void addKill(Kill.Type type, ClanPlayer attacker, ClanPlayer victim) {
        if (type == null || attacker == null || victim == null) {
            return;
        }
        final Kill kill = new Kill(attacker, victim, LocalDateTime.now());
        if (plugin.getSettingsManager().isDelayBetweenKills() && plugin.getClanManager().isKillBeforeDelay(kill)) {
            return;
        }

        if (plugin.getSettingsManager().isMaxKillsPerVictim()) {
            plugin.getStorageManager().getKillsPerPlayer(attacker.getName(), data -> {
                final int max = plugin.getSettingsManager().getMaxKillsPerVictim();
                Integer kills = data.get(kill.getVictim().getName());
                if (kills != null) {
                    if (kills < max) {
                        saveKill(kill, type);
                    }
                }
            });
    		return;
    	}
    	saveKill(kill, type);
    }

	private void saveKill(Kill kill, Kill.Type type) {
		plugin.getClanManager().addKill(kill);
    	ClanPlayer killer = kill.getKiller();
    	ClanPlayer victim = kill.getVictim();
		killer.addKill(type);
		plugin.getStorageManager().insertKill(killer, victim, type.getShortname());
	}

    private double calculateReward(@NotNull ClanPlayer attacker, @NotNull ClanPlayer victim) {
        double reward;
        double multiplier = plugin.getSettingsManager().getKDRMultipliesPerKill();
        double kdr = attacker.getKDR() * multiplier;
        Clan attackerClan = attacker.getClan();
        Clan victimClan = victim.getClan();
        if (attackerClan == null || !attackerClan.isVerified() || victimClan == null || !victimClan.isVerified()) {
            return 0;
        }
        if (attackerClan.isRival(victimClan.getTag())) {
            if (attackerClan.isWarring(victimClan)) {
                reward = kdr * 4;
            } else reward = kdr * 2;
        } else if (attackerClan.isAlly(victimClan.getTag())) {
            reward = kdr * -1;
        } else {
            reward = kdr;
        }
        return reward;
    }
}
