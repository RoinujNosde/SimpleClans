package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.TAMABLE_MOBS_SHARING;

public class TamableMobsSharing implements Listener {

    private final SettingsManager settings;
    private final ClanManager clanManager;

    public TamableMobsSharing(@NotNull SimpleClans plugin) {
        settings = plugin.getSettingsManager();
        clanManager = plugin.getClanManager();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (settings.is(TAMABLE_MOBS_SHARING)) {
            if (event.getEntity() instanceof Wolf && event.getDamager() instanceof Player) {
                ClanPlayer cp = clanManager.getAnyClanPlayer(event.getDamager().getUniqueId());
                if (cp == null || cp.getClan() == null) {
                    return;
                }
                Wolf wolf = (Wolf) event.getEntity();
                AnimalTamer owner = wolf.getOwner();
                if (owner != null && cp.getClan().isMember(owner.getUniqueId())) {
                    // Sets the wolf to friendly if the attacker is one out of his clan
                    wolf.setAngry(false);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (settings.is(TAMABLE_MOBS_SHARING)) {
            if (event.getEntity() instanceof Tameable && event.getTarget() instanceof Player) {
                ClanPlayer cp = clanManager.getAnyClanPlayer(event.getTarget().getUniqueId());
                if (cp == null || cp.getClan() == null) {
                    return;
                }
                Tameable wolf = (Tameable) event.getEntity();
                AnimalTamer owner = wolf.getOwner();
                if (owner == null) {
                    return;
                }
                if (cp.getClan().isMember(owner.getUniqueId())) {
                    // cancels the event if the attacker is one out of his clan
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        if (settings.is(TAMABLE_MOBS_SHARING) && event.getRightClicked() instanceof Tameable) {
            Player player = event.getPlayer();
            ClanPlayer cp = clanManager.getAnyClanPlayer(player.getUniqueId());
            if (cp == null || cp.getClan() == null) {
                return;
            }
            Tameable tamed = (Tameable) event.getRightClicked();

            if (tamed.getOwner() != null) {
                if (tamed instanceof Wolf && !((Wolf) tamed).isSitting()) {
                    return;
                }
                if (cp.getClan().isMember(tamed.getOwner().getUniqueId())) {
                    tamed.setOwner(player);
                }
            }
        }
    }
}
