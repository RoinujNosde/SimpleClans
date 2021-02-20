package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.managers.ProtectionManager.Action.*;

public class ProtectionManager implements Listener {

    private final List<ProtectionProvider> providers = new ArrayList<>();
    private final SimpleClans plugin = SimpleClans.getInstance();
    private final EventPriority priority;

    public ProtectionManager() {
        priority = plugin.getSettingsManager().getWarListenerPriority();
        if (!plugin.getSettingsManager().isWarEnabled() && !plugin.getSettingsManager().isLandSharing()) {
            return;
        }
        registerListeners();
        //running on next tick, so all plugins are already loaded
        Bukkit.getScheduler().runTask(plugin, this::registerProviders);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerBlockBreakListener();
    }

    private void registerBlockBreakListener() {
        Bukkit.getPluginManager().registerEvent(BlockBreakEvent.class, this, priority, (listener, event) -> {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            if (can(BREAK, blockBreakEvent.getBlock().getLocation(), blockBreakEvent.getPlayer())) {
                blockBreakEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBreak(BlockBreakEvent event) {
        if (can(BREAK, event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlace(BlockPlaceEvent event) {
        if (can(PLACE, event.getBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        if (can(DAMAGE, event.getEntity().getLocation(), (Player) damager)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        //TODO Interact at entity too
        if (event.getClickedBlock() == null) {
            return;
        }
        if (can(INTERACT, event.getClickedBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public boolean can(@NotNull Action action, @NotNull Location location, @NotNull Player player) {
        for (ProtectionProvider provider : providers) {
            Set<Land> lands = provider.getLandsAt(location);
            if (lands == null) {
                continue;
            }
            for (Land land : lands) {
                for (UUID owner : land.getOwners()) {
                    // TODO Check if allowed or if warring
                }
            }
        }
        return false;
    }

    private void registerProviders() {
        for (String className : plugin.getSettingsManager().getProtectionProviders()) {
            Object instance = null;
            try {
                Class<?> clazz = Class.forName(className);
                instance = clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException ex) {
                plugin.getLogger().log(Level.WARNING, String.format("Provider %s not found!", className), ex);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                    IllegalAccessException ex) {
                plugin.getLogger().log(Level.WARNING, String.format("Error instantiating provider %s", className), ex);
            }
            if (instance instanceof ProtectionProvider) {
                ProtectionProvider provider = (ProtectionProvider) instance;
                String requiredPlugin = provider.getRequiredPluginName();
                if (requiredPlugin != null && Bukkit.getPluginManager().getPlugin(requiredPlugin) == null) {
                    plugin.getLogger().warning(String.format("Required plugin %s for the provider %s was not found!",
                            requiredPlugin, instance.getClass().getSimpleName()));
                    continue;
                }
                provider.setup();
                providers.add(provider);
            } else {
                plugin.getLogger().warning(String.format("%s is not an instance of ProtectionProvider", className));
            }
        }
    }

    public enum Action {
        BREAK, INTERACT, PLACE, DAMAGE
    }
}
