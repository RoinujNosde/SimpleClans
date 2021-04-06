package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.War;
import net.sacredlabyrinth.phaed.simpleclans.events.WarEndEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.WarStartEvent;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.ProtectionProvider;
import net.sacredlabyrinth.phaed.simpleclans.listeners.LandProtection;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ProtectionManager {

    private final SettingsManager settingsManager;
    private final ClanManager clanManager;
    private final Logger logger;
    private final Map<War, BukkitTask> wars = new HashMap<>();
    private final List<ProtectionProvider> providers = new ArrayList<>();
    private LandProtection landProtection;
    private final SimpleClans plugin;

    public ProtectionManager() {
        plugin = SimpleClans.getInstance();
        settingsManager = plugin.getSettingsManager();
        clanManager = plugin.getClanManager();
        logger = plugin.getLogger();
        if (!settingsManager.isWarEnabled() && !settingsManager.isLandSharing()) {
            return;
        }
        //running on next tick, so all plugins are already loaded
        Bukkit.getScheduler().runTask(plugin, this::registerProviders);
        clearWars();
    }

    public void registerListeners() {
        landProtection = new LandProtection(plugin);
        if (!settingsManager.isWarEnabled() && !settingsManager.isLandSharing()) {
            return;
        }
        landProtection.registerListeners();
    }

    /**
     * Gets the Player's lands at the given Location, if not found, returns all Player's lands in the World
     *
     * @param player   the Player
     * @param location the Location
     * @return the lands
     */
    public Set<Land> getLands(@NotNull Player player, @NotNull Location location) {
        Set<Land> lands = getLandsAt(location);
        lands.removeIf(land -> !land.getOwners().contains(player.getUniqueId()));
        if (lands.isEmpty()) {
            lands = getLandsOf(player, player.getWorld());
        }
        return lands;
    }

    @NotNull
    public Set<Land> getLandsAt(@NotNull Location location) {
        Set<Land> lands = new HashSet<>();
        for (ProtectionProvider provider : providers) {
            lands.addAll(provider.getLandsAt(location));
        }
        return lands;
    }

    public boolean isOwner(@NotNull OfflinePlayer player, @NotNull Location location) {
        for (Land land : getLandsAt(location)) {
            if (land.getOwners().contains(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    public Set<Land> getLandsOf(@NotNull OfflinePlayer player, @NotNull World world) {
        Set<Land> lands = new HashSet<>();
        for (ProtectionProvider provider : providers) {
            lands.addAll(provider.getLandsOf(player, world));
        }
        return lands;
    }

    public boolean can(@NotNull Action action, @NotNull Location location, @NotNull Player player) {
        return can(action, location, player, null);
    }

    public boolean can(@NotNull Action action, @NotNull Location location, @NotNull Player player, @Nullable Player other) {
        for (Land land : getLandsAt(location)) {
            for (UUID owner : land.getOwners()) {
                if (owner == null) {
                    continue;
                }
                Player involved;
                if (other != null && player.getUniqueId().equals(owner)) {
                    involved = other;
                } else {
                    involved = player;
                }
                if (isWarringAndAllowed(action, owner, involved) ||
                        isSameClanAndAllowed(action, owner, involved, land.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean addWar(@NotNull ClanPlayer requester, Clan requestClan, Clan targetClan) {
        War war = new War(requestClan, targetClan);

        if (wars.containsKey(war)) {
            return false;
        }

        WarStartEvent event = new WarStartEvent(war);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        requestClan.addWarringClan(requester, targetClan);
        targetClan.addWarringClan(requester, requestClan);

        wars.put(war, scheduleTask(war, settingsManager.getWarNormalExpirationTime()));
        return true;
    }

    @Nullable
    private BukkitTask scheduleTask(@NotNull War war, int expirationTime) {
        BukkitTask timeoutTask = null;
        if (expirationTime > 0) {
            timeoutTask = Bukkit.getScheduler().runTaskLater(plugin, new WarTimeoutTask(war), expirationTime);
        }
        return timeoutTask;
    }

    public void setWarExpirationTime(@NotNull Clan clan, int expirationTime) {
        if (expirationTime < 1) {
            return;
        }
        for (Map.Entry<War, BukkitTask> entry : wars.entrySet()) {
            War war = entry.getKey();
            if (!war.getClans().contains(clan)) {
                continue;
            }
            BukkitTask task = entry.getValue();
            if (task != null && !task.isCancelled()) {
                task.cancel();
            }
            entry.setValue(scheduleTask(war, expirationTime));
        }
    }

    public void removeWar(@Nullable War war, @NotNull WarEndEvent.Reason reason) {
        if (war == null) {
            return;
        }
        wars.remove(war);
        Clan clan1 = war.getClans().get(0);
        Clan clan2 = war.getClans().get(1);
        clan1.removeWarringClan(clan2);
        clan2.removeWarringClan(clan1);

        WarEndEvent event = new WarEndEvent(war, reason);
        Bukkit.getPluginManager().callEvent(event);
    }

    public @Nullable War getWar(@Nullable Clan clan1, @Nullable Clan clan2) {
        if (clan1 == null || clan2 == null) {
            return null;
        }
        for (War war : wars.keySet()) {
            List<Clan> clans = war.getClans();
            if (clans.contains(clan1) && clans.contains(clan2)) {
                return war;
            }
        }
        return null;
    }

    private void clearWars() {
        if (!settingsManager.isWarEnabled()) {
            return;
        }
        for (Clan clan : clanManager.getClans()) {
            for (Clan warringClan : clan.getWarringClans()) {
                clan.removeWarringClan(warringClan);
            }
        }
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

    private void registerProviders() {
        for (String className : settingsManager.getProtectionProviders()) {
            Object instance = null;
            try {
                Class<?> clazz = getProviderClass(className);
                instance = clazz.getConstructor().newInstance();
            } catch (ClassNotFoundException ex) {
                logger.log(Level.WARNING, String.format("Provider %s not found!", className), ex);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                    IllegalAccessException ex) {
                logger.log(Level.WARNING, String.format("Error instantiating provider %s", className), ex);
            }
            if (instance instanceof ProtectionProvider) {
                registerProvider((ProtectionProvider) instance);
            } else {
                logger.warning(String.format("%s is not an instance of ProtectionProvider", className));
            }
        }
    }

    private void registerProvider(ProtectionProvider provider) {
        String requiredPlugin = provider.getRequiredPluginName();
        String providerName = provider.getClass().getSimpleName();
        if (requiredPlugin != null && Bukkit.getPluginManager().getPlugin(requiredPlugin) == null) {
            logger.warning(String.format("Required plugin %s for the provider %s was not found!",
                    requiredPlugin, providerName));
            return;
        }
        try {
            provider.setup();
        } catch (LinkageError | Exception throwable) {
            logger.log(Level.WARNING, String.format("Error registering provider %s", providerName));
            if (settingsManager.isDebugging()) {
                throwable.printStackTrace();
            }
            return;
        }
        providers.add(provider);
        landProtection.registerCreateLandEvent(provider, provider.getCreateLandEvent());
    }

    @NotNull
    private Class<?> getProviderClass(String className) throws ClassNotFoundException {
        try {
            return Class.forName("net.sacredlabyrinth.phaed.simpleclans.hooks.protection.providers." + className);
        } catch (ClassNotFoundException ignored) {
        }
        return Class.forName(className);
    }

    public enum Action {
        BREAK, INTERACT, INTERACT_ENTITY, PLACE, DAMAGE, CONTAINER
    }

    private class WarTimeoutTask implements Runnable {

        private final War war;

        private WarTimeoutTask(War war) {
            this.war = war;
        }

        @Override
        public void run() {
            removeWar(war, WarEndEvent.Reason.EXPIRATION);
            Clan clan1 = war.getClans().get(0);
            Clan clan2 = war.getClans().get(1);
            clan1.addBb(clan1.getColorTag(), lang("war.expired", clan2.getTag()));
            clan2.addBb(clan2.getColorTag(), lang("war.expired", clan1.getTag()));
        }
    }
}
