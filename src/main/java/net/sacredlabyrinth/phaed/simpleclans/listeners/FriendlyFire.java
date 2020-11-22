package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FriendlyFire implements Listener {

    private final SimpleClans plugin;

    public FriendlyFire(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) ||
                plugin.getSettingsManager().isBlacklistedWorld(event.getEntity().getWorld())) {
            return;
        }
        Player victim = (Player) event.getEntity();
        Player attacker = Events.getAttacker(event);

        if (attacker == null || attacker.getUniqueId().equals(victim.getUniqueId())) {
            return;
        }

        ClanPlayer vcp = plugin.getClanManager().getClanPlayer(victim);

        Clan victimClan = vcp == null ? null : vcp.getClan();
        Clan attackerClan = plugin.getClanManager().getClanByPlayerUniqueId(attacker.getUniqueId());

        process(event, attacker, vcp, victimClan, attackerClan);
    }

    private void process(EntityDamageEvent event,
                         Player attacker,
                         @Nullable ClanPlayer vcp,
                         @Nullable Clan victimClan,
                         @Nullable Clan attackerClan) {
        if (vcp == null || victimClan == null || attackerClan == null) {
            if (plugin.getSettingsManager().getSafeCivilians()) {
                ChatBlock.sendMessageKey(attacker, "cannot.attack.civilians");
                event.setCancelled(true);
            }
            return;
        }

        if (vcp.isFriendlyFire() || victimClan.isFriendlyFire() || plugin.getSettingsManager().isGlobalff()) {
            return;
        }

        if (victimClan.equals(attackerClan)) {
            ChatBlock.sendMessageKey(attacker, "cannot.attack.clan.member");
            event.setCancelled(true);
            return;
        }

        if (victimClan.isAlly(attackerClan.getTag())) {
            ChatBlock.sendMessageKey(attacker, "cannot.attack.ally");
            event.setCancelled(true);
        }
    }
}
