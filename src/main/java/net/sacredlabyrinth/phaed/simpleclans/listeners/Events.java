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
        if (parentEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) parentEvent;
            Entity damager = sub.getDamager();

            if (damager instanceof Player) {
                return (Player) damager;
            }

            if (damager instanceof Projectile) {
                Projectile projectile = (Projectile) damager;
                if (projectile.getShooter() instanceof Player) {
                    return (Player) projectile.getShooter();
                }
            }

            if (damager instanceof TNTPrimed) {
                Entity source = ((TNTPrimed) damager).getSource();
                if (source instanceof Player) {
                    return (Player) source;
                }

            }

        }
        return null;
    }
}