package net.sacredlabyrinth.phaed.simpleclans.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public class Events {

    private Events() {}

    @Nullable
    @Contract("null -> null")
    public static Player getAttacker(@Nullable EntityDamageEvent parentEvent) {
        if (parentEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) parentEvent;

            if (sub.getDamager() instanceof Player) {
                return  (Player) sub.getDamager();
            }

            if (sub.getDamager() instanceof Projectile) {
                Projectile projectile = (Projectile) sub.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    return  (Player) projectile.getShooter();
                }
            }
        }
        return null;
    }
}
