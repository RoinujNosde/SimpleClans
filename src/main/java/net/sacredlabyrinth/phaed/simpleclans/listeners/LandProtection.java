package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.ProtectionManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static net.sacredlabyrinth.phaed.simpleclans.managers.ProtectionManager.Action.*;

public class LandProtection implements Listener {

    private final SimpleClans plugin;
    private final ProtectionManager protectionManager;
    private final ClanManager clanManager;
    private final SettingsManager settingsManager;
    private final EventPriority priority;

    public LandProtection(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
        protectionManager = plugin.getProtectionManager();
        clanManager = plugin.getClanManager();
        settingsManager = plugin.getSettingsManager();
        priority = settingsManager.getWarListenerPriority();
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        registerBlockBreakListener();
        registerBlockPlaceListener();
        registerEntityDamageListener();
        registerInteractEntityListener();
        registerInteractListener();
        registerInventoryOpenListener();
    }

    public void registerCreateLandEvent(ProtectionProvider provider, @Nullable Class<? extends Event> createLandEvent) {
        if (createLandEvent == null) return;
        Bukkit.getPluginManager().registerEvent(createLandEvent, this, EventPriority.NORMAL, (listener, event) -> {
            if (!createLandEvent.isInstance(createLandEvent)) {
                return;
            }
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
                for (ClanPlayer member : clan.getMembers()) {
                    Set<Land> lands = protectionManager.getLandsOf(Bukkit.getOfflinePlayer(member.getUniqueId()), player.getWorld());
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
            if (!(event instanceof BlockBreakEvent)) {
                return;
            }
            BlockBreakEvent blockBreakEvent = (BlockBreakEvent) event;
            Block block = blockBreakEvent.getBlock();
            if (settingsManager.getIgnoredList(BREAK).contains(block.getType().name())) {
                return;
            }
            if (protectionManager.can(BREAK, block.getLocation(), blockBreakEvent.getPlayer())) {
                blockBreakEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerBlockPlaceListener() {
        Bukkit.getPluginManager().registerEvent(BlockPlaceEvent.class, this, priority, (listener, event) -> {
            if (!(event instanceof BlockPlaceEvent)) {
                return;
            }
            BlockPlaceEvent blockPlaceEvent = (BlockPlaceEvent) event;
            Block block = blockPlaceEvent.getBlock();
            if (settingsManager.getIgnoredList(PLACE).contains(block.getType().name())) {
                return;
            }
            if (protectionManager.can(PLACE, block.getLocation(), blockPlaceEvent.getPlayer())) {
                blockPlaceEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerEntityDamageListener() {
        Bukkit.getPluginManager().registerEvent(EntityDamageByEntityEvent.class, this, priority,
                (listener, event) -> {
                    if (!(event instanceof EntityDamageByEntityEvent)) {
                        return;
                    }
                    EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
                    if (!(entityEvent.getDamager() instanceof Player)) {
                        return;
                    }
                    Player attacker = ((Player) entityEvent.getDamager());
                    Player victim = entityEvent.getEntity() instanceof Player ? ((Player) entityEvent.getEntity()) : null;
                    if (protectionManager.can(DAMAGE, entityEvent.getEntity().getLocation(), attacker, victim)) {
                        entityEvent.setCancelled(false);
                    }
                }, plugin, false);
    }

    private void registerInteractEntityListener() {
        Bukkit.getPluginManager().registerEvent(PlayerInteractEntityEvent.class, this, priority, (listener, event) -> {
            if (!(event instanceof PlayerInteractEntityEvent)) {
                return;
            }
            PlayerInteractEntityEvent interactEvent = (PlayerInteractEntityEvent) event;
            if (protectionManager.can(INTERACT_ENTITY, interactEvent.getRightClicked().getLocation(), interactEvent.getPlayer())) {
                interactEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerInventoryOpenListener() {
        Bukkit.getPluginManager().registerEvent(InventoryOpenEvent.class, this, priority, (l, e) -> {
            if (!(e instanceof InventoryOpenEvent)) {
                return;
            }
            InventoryOpenEvent event = ((InventoryOpenEvent) e);
            InventoryHolder holder = event.getInventory().getHolder();
            Location location = getLocation(holder);
            if (holder instanceof Entity && holder == event.getPlayer()) return;
            if (location == null) return;

            if (protectionManager.can(CONTAINER, location, ((Player) event.getPlayer()))) {
                event.setCancelled(false);
            }
        }, plugin, false);
    }

    private void registerInteractListener() {
        Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, this, priority, (listener, event) -> {
            if (!(event instanceof PlayerInteractEvent)) {
                return;
            }
            PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
            if (interactEvent.getClickedBlock() == null) {
                return;
            }
            ProtectionManager.Action action = getInteractEventAction(interactEvent);
            if (protectionManager.can(action, interactEvent.getClickedBlock().getLocation(), interactEvent.getPlayer())) {
                interactEvent.setUseInteractedBlock(Event.Result.ALLOW);
                interactEvent.setUseItemInHand(Event.Result.ALLOW);
                interactEvent.setCancelled(false);
            }
        }, plugin, false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (settingsManager.getIgnoredList(BREAK).contains(block.getType().name())) {
            return;
        }
        if (protectionManager.can(BREAK, block.getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (settingsManager.getIgnoredList(PLACE).contains(block.getType().name())) {
            return;
        }
        if (protectionManager.can(PLACE, block.getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Player victim = event.getEntity() instanceof Player ? ((Player) event.getEntity()) : null;
        if (protectionManager.can(DAMAGE, event.getEntity().getLocation(), (Player) damager, victim)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryOpen(InventoryOpenEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        Location location = getLocation(holder);
        if (holder instanceof Entity && holder == event.getPlayer()) return;
        if (location == null) return;

        if (protectionManager.can(CONTAINER, location, ((Player) event.getPlayer()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (protectionManager.can(INTERACT_ENTITY, event.getRightClicked().getLocation(), event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) {
            return;
        }
        ProtectionManager.Action action = getInteractEventAction(event);
        if (protectionManager.can(action, event.getClickedBlock().getLocation(), event.getPlayer())) {
            event.setUseItemInHand(Event.Result.DENY);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setCancelled(true);
        }
    }

    private @Nullable Location getLocation(@Nullable InventoryHolder holder) {
        if (holder instanceof BlockState) {
            return ((BlockState) holder).getLocation();
        }
        return null;
    }

    @NotNull
    private ProtectionManager.Action getInteractEventAction(PlayerInteractEvent event) {
        ProtectionManager.Action action;
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getState() instanceof InventoryHolder) {
            action = CONTAINER;
        } else {
            action = INTERACT;
        }
        return action;
    }
}
