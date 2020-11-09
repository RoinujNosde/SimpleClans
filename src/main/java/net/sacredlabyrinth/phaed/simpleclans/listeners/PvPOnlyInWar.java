package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class PvPOnlyInWar implements Listener {

    private final SimpleClans plugin;

    public PvPOnlyInWar(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) ||
                plugin.getSettingsManager().isBlacklistedWorld(event.getEntity().getWorld())) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Player attacker = Events.getAttacker(event);

        if (attacker == null || victim.getUniqueId().equals(attacker.getUniqueId())) {
            return;
        }

        Clan attackerClan = plugin.getClanManager().getClanByPlayerUniqueId(attacker.getUniqueId());
        Clan victimClan = plugin.getClanManager().getClanByPlayerUniqueId(victim.getUniqueId());

        if (plugin.getSettingsManager().isPvpOnlywhileInWar()) {
            process(event, attacker, victim, attackerClan, victimClan);
        }
    }

    private void process(EntityDamageEvent event, Player attacker, Player victim, Clan attackerClan, Clan victimClan) {
        if (attackerClan == null || victimClan == null) {
            ChatBlock.sendMessageKey(attacker, "must.be.in.clan.to.pvp", victim.getName());
            event.setCancelled(true);
            return;
        }

        if (plugin.getPermissionsManager().has(victim, "simpleclans.mod.nopvpinwar")) {
            event.setCancelled(true);
            return;
        }

        if (!attackerClan.isWarring(victimClan)) {
            ChatBlock.sendMessageKey(attacker, "clans.not.at.war.pvp.denied", victimClan.getName());
            event.setCancelled(true);
        }
    }
}
