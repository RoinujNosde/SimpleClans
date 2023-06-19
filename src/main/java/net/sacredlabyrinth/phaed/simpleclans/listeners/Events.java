package net.sacredlabyrinth.phaed.simpleclans.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class Events {

    private Events() {
        // Can't instantiate this class
    }

    @Nullable
    @Contract("null -> null")
    public static Player getAttacker(@Nullable EntityDamageEvent parentEvent) {
        if (parentEvent instanceof EntityDamageByEntityEvent sub) {
            Entity damager = sub.getDamager();

            if (damager instanceof Player attacker) {
                return attacker;
            }

            if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player attacker) {
                return attacker;
            }

            if (damager instanceof TNTPrimed tnt && tnt.getSource() instanceof Player attacker) {
                return attacker;
            }

        }
        return null;
    }
}
