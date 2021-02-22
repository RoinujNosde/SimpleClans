package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sacredlabyrinth.phaed.simpleclans.managers.ProtectionManager.Action.*;

public class ProtectionManager implements Listener {

    private final SimpleClans plugin;
    private final SettingsManager settingsManager;
    private final ClanManager clanManager;
    private final Logger logger;
    private final List<ProtectionProvider<?>> providers = new ArrayList<>();
    private final EventPriority priority;

    public ProtectionManager() {
        plugin = SimpleClans.getInstance();
        settingsManager = plugin.getSettingsManager();
        clanManager = plugin.getClanManager();
        priority = settingsManager.getWarListenerPriority();
        logger = plugin.getLogger();
        if (!settingsManager.isWarEnabled() && !settingsManager.isLandSharing()) {
            return;
        }
        registerListeners();
        //running on next tick, so all plugins are already loaded
        Bukkit.getScheduler().runTask(plugin, this::registerProviders);
    }

    @Nullable
    public Land getLandAt(@NotNull Location location) {
        for (ProtectionProvider<?> provider : providers) {
            Set<Land> lands = provider.getLandsAt(location);
            if (lands != null && lands.size() > 0) {
                return lands.stream().findAny().orElse(null);
            }
        }
        return null;
    }

    @NotNull
    public Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        Set<Land> lands = new HashSet<>();
        for (ProtectionProvider<?> provider : providers) {
            lands.addAll(provider.getLandsOf(player, world));
        }
        return lands;
    }

    public boolean can(@NotNull Action action, @NotNull Location location, @NotNull Player player) {
        for (ProtectionProvider<?> provider : providers) {
            Set<Land> lands = provider.getLandsAt(location);
            if (lands == null) {
                continue;
            }
            for (Land land : lands) {
                for (UUID owner : land.getOwners()) {
                    if (isWarringAndAllowed(action, owner, player) ||
                            isSameClanAndAllowed(action, owner, player, land.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSameClanAndAllowed(Action action, UUID owner, Player involved, String landId) {
        if (!settingsManager.isLandSharing()) {
            return false;
        }
        ClanPlayer cp = clanManager.getCreateClanPlayer(owner);
        Clan involvedClan = clanManager.getClanByPlayerUniqueId(involved.getUniqueId());
        if (cp.getClan() == null || !cp.getClan().equals(involvedClan)) {
            return false;
        }
        return cp.isAllowed(action, landId);
    }

    private boolean isWarringAndAllowed(@NotNull Action action, @NotNull UUID owner, @NotNull Player involved) {
        if (!settingsManager.isActionAllowedInWar(action) || !settingsManager.isWarEnabled()) {
            return false;
        }
        Clan ownerClan = clanManager.getClanByPlayerUniqueId(owner);
        Clan involvedClan = clanManager.getClanByPlayerUniqueId(involved.getUniqueId());
        if (ownerClan == null || involvedClan == null) {
            return false;
        }
        return ownerClan.isWarring(involvedClan);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerBlockBreakListener();
        registerBlockPlaceListener();
        registerEntityDamageListener();
        registerInteractEntityListener();
        registerInteractListener();
    }

    private void registerProviders() {
        for (String className : settingsManager.getProtectionProviders()) {
            Object instance = null;
            try {
                Class<?> clazz = Class.forName(className);
                instance = clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException ex) {
                logger.log(Level.WARNING, String.format("Provider %s not found!", className), ex);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                    IllegalAccessException ex) {
                logger.log(Level.WARNING, String.format("Error instantiating provider %s", className), ex);
            }
            if (instance instanceof ProtectionProvider) {
                ProtectionProvider<?> provider = (ProtectionProvider<?>) instance;
                String requiredPlugin = provider.getRequiredPluginName();
                if (requiredPlugin != null && Bukkit.getPluginManager().getPlugin(requiredPlugin) == null) {
                    logger.warning(String.format("Required plugin %s for the provider %s was not found!",
                            requiredPlugin, instance.getClass().getSimpleName()));
                    continue;
                }
                provider.setup();
                providers.add(provider);
                registerCreateLandEvent(provider, provider.getCreateLandEvent());
            } else {
                logger.warning(String.format("%s is not an instance of ProtectionProvider", className));
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerCreateLandEvent(ProtectionProvider provider, @Nullable Class<? extends Event> createLandEvent) {
        if (createLandEvent == null) return;
        Bukkit.getPluginManager().registerEvent(createLandEvent, this, EventPriority.NORMAL, (listener, event) -> {
            Player player = provider.getPlayer(event);
            if (player == null) return;
            Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
            if ((clan == null || !clan.isLeader(player)) && settingsManager.isOnlyLeadersCanCreateLands()) {
                cancelWithMessage(player, event, "only.leaders.can.create.lands");
                return;
            }
            if (settingsManager.isOnlyOneLandPerClan()) {
                if (clan == null) {
                    cancelWithMessage(player, event, "only.clan.members.can.create.lands");
                    return;
                }
                for (ClanPlayer member : clan.getAllMembers()) {
                    Set<Land> lands = getLandsOf(Bukkit.getOfflinePlayer(member.getUniqueId()), player.getWorld());
                    if (lands.size() > 0) {
                        cancelWithMessage(player, event, "only.one.land.per.clan");
                        return;
                    }
                }
            }
        }, plugin, true);
    }

    private void cancelWithMessage(Player player, Event event, String messageKey) {
        if (!(event instanceof Cancellable)) {
            return;
        }
        ChatBlock.sendMessageKey(player, messageKey);
        ((Cancellable) event).setCancelled(true);
    }

    private void registerBlockBreakListener() {
        Bukkit.getPluginManager().registerEvent(BlockBreakEvent.class, this, priority, (listener, event) -> {
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            if (can(BREAK, blockBreakEvent.getBlock().getLocation(), blockBreakEvent.getPlayer())) {
                blockBreakEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerBlockPlaceListener() {
        Bukkit.getPluginManager().registerEvent(BlockPlaceEvent.class, this, priority, (listener, event) -> {
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event;
            if (can(BREAK, blockPlaceEvent.getBlock().getLocation(), blockPlaceEvent.getPlayer())) {
                blockPlaceEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerEntityDamageListener() {
        Bukkit.getPluginManager().registerEvent(EntityDamageByEntityEvent.class, this, priority,
                (listener, event) -> {
                    EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
                    if (!(entityEvent.getDamager() instanceof Player)) {
                        return;
                    }
                    Player player = ((Player) entityEvent.getDamager());
                    if (can(BREAK, entityEvent.getEntity().getLocation(), player)) {
                        entityEvent.setCancelled(false);
                    }
                }, plugin, false);
    }

    private void registerInteractEntityListener() {
        Bukkit.getPluginManager().registerEvent(PlayerInteractEntityEvent.class, this, priority, (listener, event) -> {
            PlayerInteractEntityEvent interactEvent = (PlayerInteractEntityEvent) event;
            if (can(BREAK, interactEvent.getRightClicked().getLocation(), interactEvent.getPlayer())) {
                interactEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerInteractListener() {
        Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, this, priority, (listener, event) -> {
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            if (interactEvent.getClickedBlock() == null) {
                return;
            }
            if (can(BREAK, interactEvent.getClickedBlock().getLocation(), interactEvent.getPlayer())) {
                interactEvent.setCancelled(false);
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
    private void onInteractEntity(PlayerInteractEntityEvent event) {
        if (can(INTERACT_ENTITY, event.getRightClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        if (can(INTERACT, event.getClickedBlock().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    public enum Action {
        BREAK, INTERACT, INTERACT_ENTITY, PLACE, DAMAGE
    }
}
