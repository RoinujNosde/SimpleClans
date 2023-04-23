package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerResetKdrEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.RED;

/**
 * @author phaed
 */
public final class ClanManager {

    private final SimpleClans plugin;
    private final ConcurrentHashMap<String, Clan> clans = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ClanPlayer> clanPlayers = new ConcurrentHashMap<>();
    private final HashMap<ClanPlayer, List<Kill>> kills = new HashMap<>();

    /**
     *
     */
    public ClanManager() {
        plugin = SimpleClans.getInstance();
    }

    /**
     * Deletes all clans and clan players in memory
     */
    public void cleanData() {
        clans.clear();
        clanPlayers.clear();
        kills.clear();
    }

    /**
     * Adds a kill to the memory
     */
    public void addKill(Kill kill) {
        if (kill == null) {
            return;
        }

        List<Kill> list = kills.computeIfAbsent(kill.getKiller(), k -> new ArrayList<>());

        Iterator<Kill> iterator = list.iterator();
        while (iterator.hasNext()) {
            Kill oldKill = iterator.next();
            if (oldKill.getVictim().equals(kill.getKiller())) {
                iterator.remove();
                continue;
            }

            //cleaning
            final int delay = plugin.getSettingsManager().getInt(KDR_DELAY_BETWEEN_KILLS);
            long timePassed = oldKill.getTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
            if (timePassed >= delay) {
                iterator.remove();
            }
        }

        list.add(kill);
    }

    /**
     * Checks if this kill respects the delay
     */
    public boolean isKillBeforeDelay(Kill kill) {
        if (kill == null) {
            return false;
        }
        List<Kill> list = kills.get(kill.getKiller());
        if (list == null) {
            return false;
        }

        for (Kill oldKill : list) {
            if (oldKill.getVictim().equals(kill.getVictim())) {

                final int delay = plugin.getSettingsManager().getInt(KDR_DELAY_BETWEEN_KILLS);
                long timePassed = oldKill.getTime().until(kill.getTime(), ChronoUnit.MINUTES);
                if (timePassed < delay) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Import a clan into the in-memory store
     */
    public void importClan(Clan clan) {
        this.clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     */
    public void importClanPlayer(ClanPlayer cp) {
        if (cp.getUniqueId() != null) {
            this.clanPlayers.put(cp.getUniqueId(), cp);
        }
    }

    /**
     * Create a new clan
     */
    public void createClan(Player player, String colorTag, String name) {
        ClanPlayer cp = getCreateClanPlayer(player.getUniqueId());

        boolean verified = !plugin.getSettingsManager().is(REQUIRE_VERIFICATION)
                || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(colorTag, name, verified);
        clan.addPlayerToClan(cp);
        cp.setLeader(true);
        clan.getRanks().addAll(plugin.getSettingsManager().getStarterRanks());

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);
        plugin.getStorageManager().updateClanPlayer(cp);

        plugin.getRequestManager().deny(cp); //denies any previous invitation
        SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new CreateClanEvent(clan));
    }

    /**
     * Reset a player's KDR
     */
    public void resetKdr(ClanPlayer cp) {
        Player player = cp.toPlayer();
        boolean isEnabled = plugin.getSettingsManager().is(ECONOMY_PURCHASE_RESET_KDR);
        double price = plugin.getSettingsManager().getDouble(ECONOMY_RESET_KDR_PRICE);
        if (player != null) {
            PlayerResetKdrEvent event = new PlayerResetKdrEvent(player, isEnabled, price);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                cp.setCivilianKills(0);
                cp.setNeutralKills(0);
                cp.setRivalKills(0);
                cp.setAllyKills(0);
                cp.setDeaths(0);
                plugin.getStorageManager().updateClanPlayer(cp);
            }
        }
    }

    /**
     * Delete a players data file
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        Clan clan = cp.getClan();
        if (clan != null) {
            clan.removePlayerFromClan(cp.getUniqueId());
        }
        clanPlayers.remove(cp.getUniqueId());
        plugin.getStorageManager().deleteClanPlayer(cp);
    }

    /**
     * Delete a player data from memory
     */
    public void deleteClanPlayerFromMemory(UUID playerUniqueId) {
        clanPlayers.remove(playerUniqueId);
    }

    /**
     * Remove a clan from memory
     */
    public void removeClan(String tag) {
        clans.remove(tag);
    }

    /**
     * Whether the tag belongs to a clan
     */
    public boolean isClan(String tag) {
        return clans.containsKey(Helper.cleanTag(tag));
    }

    /**
     * Returns the clan the tag belongs to
     */
    public Clan getClan(String tag) {
        return clans.get(Helper.cleanTag(tag));
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public Clan getClanByPlayerName(String playerName) {
        return getClanByPlayerUniqueId(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Get a player's clan
     *
     * @return null if not in a clan
     */
    @Nullable
    public Clan getClanByPlayerUniqueId(UUID playerUniqueId) {
        ClanPlayer cp = getClanPlayer(playerUniqueId);

        if (cp != null) {
            return cp.getClan();
        }

        return null;
    }

    /**
     * @return the clans
     */
    public List<Clan> getClans() {
        return new ArrayList<>(clans.values());
    }

    /**
     * Returns the collection of all clan players, including the disabled ones
     */
    public List<ClanPlayer> getAllClanPlayers() {
        return new ArrayList<>(clanPlayers.values());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan Used for BungeeCord Reload ClanPlayer and your Clan
     */
    @Deprecated
    public @Nullable ClanPlayer getClanPlayerJoinEvent(Player player) {
        SimpleClans.getInstance().getStorageManager().importFromDatabaseOnePlayer(player);
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     */
    public @Nullable ClanPlayer getClanPlayer(@NotNull OfflinePlayer player) {
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     */
    public @Nullable ClanPlayer getClanPlayer(@NotNull Player player) {
        return getClanPlayer((OfflinePlayer) player);
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     */
    public @Nullable ClanPlayer getClanPlayer(UUID playerUniqueId) {
        ClanPlayer cp = clanPlayers.get(playerUniqueId);

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public ClanPlayer getClanPlayer(String playerName) {
        return getClanPlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     */
    @Deprecated
    public @Nullable ClanPlayer getClanPlayerName(String playerName) {
        UUID uuid = UUIDMigration.getForcedPlayerUUID(playerName);

        if (uuid == null) {
            return null;
        }

        ClanPlayer cp = clanPlayers.get(uuid);

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled
     * clan players as well, these are players who used to be in a clan but are
     * not currently in one, their data file persists and can be accessed. their
     * clan will be null though.
     */

    @Nullable
    public ClanPlayer getAnyClanPlayer(UUID uuid) {
        return clanPlayers.get(uuid);
    }

    @SuppressWarnings("deprecation")
    @Nullable
    public ClanPlayer getAnyClanPlayer(String playerName) {
        for (ClanPlayer cp : getAllClanPlayers()) {
            if (cp.getName().equals(playerName)) {
                return cp;
            }
        }
        return getAnyClanPlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     */
    public @Nullable ClanPlayer getCreateClanPlayerUUID(String playerName) {
        UUID playerUniqueId = UUIDMigration.getForcedPlayerUUID(playerName);
        if (playerUniqueId != null) {
            return getCreateClanPlayer(playerUniqueId);
        } else {
            return null;
        }
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     */
    public ClanPlayer getCreateClanPlayer(UUID uuid) {
        Objects.requireNonNull(uuid, "UUID must not be null");
        if (clanPlayers.containsKey(uuid)) {
            return clanPlayers.get(uuid);
        }

        ClanPlayer cp = new ClanPlayer(uuid);

        boolean save = true;
        for (ClanPlayer other : getAllClanPlayers()) {
            if (other.getName().equals(cp.getName())) {
                save = false;
                break;
            }
        }
        if (save) {
            plugin.getStorageManager().insertClanPlayer(cp);
            importClanPlayer(cp);
        } else if (plugin.getSettingsManager().is(DEBUG)) {
            plugin.getLogger().log(Level.WARNING, String.format("There already is a ClanPlayer with the name %s",
                    cp.getName()), new Exception());
        }

        return cp;
    }

    @SuppressWarnings("deprecation")
    @NotNull
    public ClanPlayer getCreateClanPlayer(String playerName) {
        return getCreateClanPlayer(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Announce message to the server
     *
     * @param msg the message
     */
    public void serverAnnounce(String msg) {
        if (plugin.getSettingsManager().is(DISABLE_MESSAGES)) {
            return;
        }

        plugin.getProxyManager().sendMessage("ALL", ChatColor.DARK_GRAY + "* " + ChatColor.AQUA + msg);
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[" + lang("server.announce") + "] " + ChatColor.WHITE + msg);
    }

    /**
     * Update the players display name with his clan's tag
     */
    public void updateDisplayName(@Nullable Player player) {
        // do not update displayname if in compat mode

        if (plugin.getSettingsManager().is(CHAT_COMPATIBILITY_MODE)) {
            return;
        }

        if (player == null) {
            return;
        }

        if (plugin.getSettingsManager().is(DISPLAY_CHAT_TAGS)) {
            String prefix = plugin.getPermissionsManager().getPrefix(player);
            //String suffix = plugin.getPermissionsManager().getSuffix(player);
            String lastColor = plugin.getSettingsManager().is(COLOR_CODE_FROM_PREFIX_FOR_NAME) ? ChatUtils.getLastColorCode(prefix) : ChatColor.WHITE + "";
            String fullName = player.getName();

            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

            if (cp == null) {
                return;
            }

            if (cp.isTagEnabled()) {
                Clan clan = cp.getClan();

                if (clan != null) {
                    fullName = clan.getTagLabel(cp.isLeader()) + lastColor + fullName + ChatColor.WHITE;
                }

                player.setDisplayName(fullName);
            } else {
                player.setDisplayName(lastColor + fullName + ChatColor.WHITE);
            }
        }
    }

    /**
     * Process a player and his clan's last seen date
     */
    public void updateLastSeen(Player player) {
        ClanPlayer cp = getAnyClanPlayer(player.getUniqueId());

        if (cp != null) {
            cp.updateLastSeen();
            plugin.getStorageManager().updateClanPlayer(cp);

            Clan clan = cp.getClan();

            if (clan != null) {
                clan.updateLastUsed();
                plugin.getStorageManager().updateClan(clan);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void ban(String playerName) {
        ban(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Bans a player from clan commands
     *
     * @param uuid the player's uuid
     */
    public void ban(UUID uuid) {
        ClanPlayer cp = getClanPlayer(uuid);
        Clan clan = null;
        if (cp != null) {
            clan = cp.getClan();
        }

        if (clan != null) {
            if (clan.getSize() == 1) {
                clan.disband(null, false, false);
            } else {
                cp.setClan(null);
                cp.addPastClan(clan.getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                cp.setLeader(false);
                cp.setJoinDate(0);
                clan.removeMember(uuid);

                plugin.getStorageManager().updateClanPlayer(cp);
                plugin.getStorageManager().updateClan(clan);
            }
        }

        plugin.getSettingsManager().addBanned(uuid);
    }

    /**
     * Get a count of rivable clans
     */
    public int getRivableClanCount() {
        int clanCount = 0;

        for (Clan tm : clans.values()) {
            if (!SimpleClans.getInstance().getSettingsManager().isUnrivable(tm.getTag())) {
                clanCount++;
            }
        }

        return clanCount;
    }

    /**
     * Returns a formatted string detailing the players armor
     */
    public String getArmorString(PlayerInventory inv) {
        String out = "";

        ItemStack h = inv.getHelmet();

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof Player) {
            player = (Player) holder;
        }

        if (h != null) {
            if (h.getType().equals(XMaterial.CHAINMAIL_HELMET.parseMaterial())) {
                out += ChatColor.WHITE + lang("armor.h", player);
            } else if (h.getType().equals(XMaterial.DIAMOND_HELMET.parseMaterial())) {
                out += ChatColor.AQUA + lang("armor.h", player);
            } else if (h.getType().equals(XMaterial.GOLDEN_HELMET.parseMaterial())) {
                out += ChatColor.YELLOW + lang("armor.h", player);
            } else if (h.getType().equals(XMaterial.IRON_HELMET.parseMaterial())) {
                out += ChatColor.GRAY + lang("armor.h", player);
            } else if (h.getType().equals(XMaterial.LEATHER_HELMET.parseMaterial())) {
                out += ChatColor.GOLD + lang("armor.h", player);
            } else if (h.getType().equals(XMaterial.AIR.parseMaterial())) {
                out += ChatColor.BLACK + lang("armor.h", player);
            } else {
                out += RED + lang("armor.h", player);
            }
        }
        ItemStack c = inv.getChestplate();

        if (c != null) {
            if (c.getType().equals(XMaterial.CHAINMAIL_CHESTPLATE.parseMaterial())) {
                out += ChatColor.WHITE + lang("armor.c", player);
            } else if (c.getType().equals(XMaterial.DIAMOND_CHESTPLATE.parseMaterial())) {
                out += ChatColor.AQUA + lang("armor.c", player);
            } else if (c.getType().equals(XMaterial.GOLDEN_CHESTPLATE.parseMaterial())) {
                out += ChatColor.YELLOW + lang("armor.c", player);
            } else if (c.getType().equals(XMaterial.IRON_CHESTPLATE.parseMaterial())) {
                out += ChatColor.GRAY + lang("armor.c", player);
            } else if (c.getType().equals(XMaterial.LEATHER_CHESTPLATE.parseMaterial())) {
                out += ChatColor.GOLD + lang("armor.c", player);
            } else if (c.getType().equals(XMaterial.AIR.parseMaterial())) {
                out += ChatColor.BLACK + lang("armor.c", player);
            } else {
                out += RED + lang("armor.c", player);
            }
        }
        ItemStack l = inv.getLeggings();

        if (l != null) {
            if (l.getType().equals(XMaterial.CHAINMAIL_LEGGINGS.parseMaterial())) {
                out += ChatColor.WHITE + lang("armor.l", player);
            } else if (l.getType().equals(XMaterial.DIAMOND_LEGGINGS.parseMaterial())) {
                out += lang("armor.l", player);
            } else if (l.getType().equals(XMaterial.GOLDEN_LEGGINGS.parseMaterial())) {
                out += lang("armor.l", player);
            } else if (l.getType().equals(XMaterial.IRON_LEGGINGS.parseMaterial())) {
                out += lang("armor.l", player);
            } else if (l.getType().equals(XMaterial.LEATHER_LEGGINGS.parseMaterial())) {
                out += lang("armor.l", player);
            } else if (l.getType().equals(XMaterial.AIR.parseMaterial())) {
                out += lang("armor.l", player);
            } else {
                out += lang("armor.l", player);
            }
        }
        ItemStack b = inv.getBoots();

        if (b != null) {
            if (b.getType().equals(XMaterial.CHAINMAIL_BOOTS.parseMaterial())) {
                out += ChatColor.WHITE + lang("armor.B", player);
            } else if (b.getType().equals(XMaterial.DIAMOND_BOOTS.parseMaterial())) {
                out += ChatColor.AQUA + lang("armor.B", player);
            } else if (b.getType().equals(XMaterial.GOLDEN_BOOTS.parseMaterial())) {
                out += ChatColor.YELLOW + lang("armor.B", player);
            } else if (b.getType().equals(XMaterial.IRON_BOOTS.parseMaterial())) {
                out += ChatColor.WHITE + lang("armor.B", player);
            } else if (b.getType().equals(XMaterial.LEATHER_BOOTS.parseMaterial())) {
                out += ChatColor.GOLD + lang("armor.B", player);
            } else if (b.getType().equals(XMaterial.AIR.parseMaterial())) {
                out += ChatColor.BLACK + lang("armor.B", player);
            } else {
                out += RED + lang("armor.B", player);
            }
        }

        if (out.length() == 0) {
            out = lang("none", player);
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players weapons
     */
    public String getWeaponString(PlayerInventory inv) {
        String headColor = plugin.getSettingsManager().getColored(PAGE_HEADINGS_COLOR);

        String out = "";

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof Player) {
            player = (Player) holder;
        }

        int count = getItemCount(inv, XMaterial.DIAMOND_SWORD);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.AQUA + lang("weapon.S", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.GOLDEN_SWORD);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + lang("weapon.S", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.IRON_SWORD);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.WHITE + lang("weapon.S", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.STONE_SWORD);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GRAY + lang("weapon.S", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.WOODEN_SWORD);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + lang("weapon.S", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.BOW);

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + lang("weapon.B", player) + headColor + countString;
        }

        count = getItemCount(inv, XMaterial.ARROW);
        count += getItemCount(inv, XMaterial.SPECTRAL_ARROW);
        count += getItemCount(inv, XMaterial.TIPPED_ARROW);

        if (count > 0) {
            out += ChatColor.WHITE + lang("weapon.A", player) + headColor + count;
        }

        if (out.length() == 0) {
            out = lang("none", player);
        }

        return out;
    }

    private int getItemCount(@NotNull PlayerInventory inv, @NotNull XMaterial material) {
        Material parsed = material.parseMaterial();
        if (parsed == null) {
            return 0;
        }

        return getItemCount(inv.all(parsed));
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all) {
        int count = 0;

        for (ItemStack is : all.values()) {
            count += is.getAmount();
        }

        return count;
    }

    private double getFoodPoints(PlayerInventory inv, XMaterial material, int points, double saturation) {
        Material parsed = material.parseMaterial();
        if (parsed == null) {
            return 0;
        }
        return getFoodPoints(inv, parsed, points, saturation);
    }

    private double getFoodPoints(PlayerInventory inv, Material material, int points, double saturation) {
        return getItemCount(inv.all(material)) * (points + saturation);
    }

    /**
     * Returns a formatted string detailing the players food
     *
     * @param inv the PlayerInventory
     * @return the food points string
     */
    public String getFoodString(PlayerInventory inv) {

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder instanceof Player) {
            player = (Player) holder;
        }

        double count = getFoodPoints(inv, XMaterial.APPLE, 4, 2.4);
        count += getFoodPoints(inv, XMaterial.BAKED_POTATO, 5, 6);
        count += getFoodPoints(inv, XMaterial.BEETROOT, 1, 1.2);
        count += getFoodPoints(inv, XMaterial.BEETROOT_SOUP, 6, 7.2);
        count += getFoodPoints(inv, XMaterial.BREAD, 5, 6);
        count += getFoodPoints(inv, XMaterial.CAKE, 14, 2.8);
        count += getFoodPoints(inv, XMaterial.CARROT, 3, 3.6);
        count += getFoodPoints(inv, XMaterial.CHORUS_FRUIT, 4, 2.4);
        count += getFoodPoints(inv, XMaterial.COOKED_CHICKEN, 6, 7.2);
        count += getFoodPoints(inv, XMaterial.COOKED_MUTTON, 6, 9.6);
        count += getFoodPoints(inv, XMaterial.COOKED_PORKCHOP, 8, 12.8);
        count += getFoodPoints(inv, XMaterial.COOKED_RABBIT, 5, 6);
        count += getFoodPoints(inv, XMaterial.COOKED_SALMON, 6, 9.6);
        count += getFoodPoints(inv, XMaterial.COOKIE, 2, .4);
        count += getFoodPoints(inv, XMaterial.GOLDEN_APPLE, 4, 9.6);
        count += getFoodPoints(inv, XMaterial.GOLDEN_CARROT, 6, 14.4);
        count += getFoodPoints(inv, XMaterial.MELON, 2, 1.2);
        count += getFoodPoints(inv, XMaterial.MUSHROOM_STEW, 6, 7.2);
        count += getFoodPoints(inv, XMaterial.POISONOUS_POTATO, 2, 1.2);
        count += getFoodPoints(inv, XMaterial.POTATO, 1, 0.6);
        count += getFoodPoints(inv, XMaterial.PUFFERFISH, 1, 0.2);
        count += getFoodPoints(inv, XMaterial.PUMPKIN_PIE, 8, 4.8);
        count += getFoodPoints(inv, XMaterial.RABBIT_STEW, 10, 12);
        count += getFoodPoints(inv, XMaterial.BEEF, 3, 1.8);
        count += getFoodPoints(inv, XMaterial.CHICKEN, 2, 1.2);
        count += getFoodPoints(inv, XMaterial.MUTTON, 2, 1.2);
        count += getFoodPoints(inv, XMaterial.PORKCHOP, 3, 1.8);
        count += getFoodPoints(inv, XMaterial.RABBIT, 3, 1.8);
        count += getFoodPoints(inv, XMaterial.SALMON, 1, .4);
        count += getFoodPoints(inv, XMaterial.COD, 2, .4);
        count += getFoodPoints(inv, XMaterial.COOKED_COD, 5, 6);
        count += getFoodPoints(inv, XMaterial.TROPICAL_FISH, 1, .2);
        count += getFoodPoints(inv, XMaterial.ROTTEN_FLESH, 4, .8);
        count += getFoodPoints(inv, XMaterial.SPIDER_EYE, 2, 3.2);
        count += getFoodPoints(inv, XMaterial.COOKED_BEEF, 8, 12.8);

        if (count == 0) {
            return lang("none", player);
        } else {
            return ((int) count) + "" + ChatColor.GOLD + "p";
        }
    }

    /**
     * Returns a colored bar based on the length
     */
    public String getBar(double length) {
        StringBuilder out = new StringBuilder();

        if (length >= 16) {
            out.append(ChatColor.GREEN);
        } else if (length >= 8) {
            out.append(ChatColor.GOLD);
        } else {
            out.append(RED);
        }

        for (int i = 0; i < length; i++) {
            out.append('|');
        }

        return out.toString();
    }

    /**
     * Sort clans by active
     */
    public void sortClansByActive(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return Long.compare(c1.getLastUsed(), c2.getLastUsed()) * o;
        });
    }

    /**
     * Sort clans by founded date
     */
    public void sortClansByFounded(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return Long.compare(c1.getFounded(), c2.getFounded()) * o;
        });
    }

    /**
     * Sort clans by kdr
     */
    public void sortClansByKDR(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return Float.compare(c1.getTotalKDR(), c2.getTotalKDR()) * o;
        });
    }

    /**
     * Sort clans by size
     */
    public void sortClansBySize(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return Integer.compare(c1.getSize(), c2.getSize()) * o;
        });
    }

    /**
     * Sort clans by name
     */
    public void sortClansByName(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return c1.getName().compareTo(c2.getName()) * o;
        });
    }

    /**
     * Sort clans by KDR
     */
    public void sortClansByKDR(List<Clan> clans) {
        clans.sort((c1, c2) -> {
            Float o1 = c1.getTotalKDR();
            Float o2 = c2.getTotalKDR();

            return o2.compareTo(o1);
        });
    }

    /**
     * Sort clans by KDR
     */
    public void sortClansBySize(List<Clan> clans) {
        clans.sort((c1, c2) -> {
            Integer o1 = c1.getMembers().size();
            Integer o2 = c2.getMembers().size();

            return o2.compareTo(o1);
        });
    }

    /**
     * Sort clan players by KDR
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> cps) {
        cps.sort((c1, c2) -> {
            Float o1 = c1.getKDR();
            Float o2 = c2.getKDR();

            return o2.compareTo(o1);
        });
    }

    /**
     * Sort clan players by last seen days
     */
    public void sortClanPlayersByLastSeen(List<ClanPlayer> cps) {
        cps.sort((c1, c2) -> {
            Double o1 = c1.getLastSeenDays();
            Double o2 = c2.getLastSeenDays();

            return o1.compareTo(o2);
        });
    }

    public long getMinutesBeforeRejoin(@NotNull ClanPlayer cp, @NotNull Clan clan) {
        SettingsManager settings = plugin.getSettingsManager();
        if (settings.is(ENABLE_REJOIN_COOLDOWN)) {
            Long resign = cp.getResignTime(clan.getTag());
            if (resign != null) {
                long timePassed = Instant.ofEpochMilli(resign).until(Instant.now(), ChronoUnit.MINUTES);
                int cooldown = settings.getInt(REJOIN_COOLDOWN);
                if (timePassed < cooldown) {
                    return cooldown - timePassed;
                }
            }
        }
        return 0;
    }

    /**
     * Purchase member fee set
     */
    public boolean purchaseMemberFeeSet(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_MEMBER_FEE_SET)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_MEMBER_FEE_SET_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase clan creation
     */
    public boolean purchaseCreation(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_CLAN_CREATE)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_CREATION_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase invite
     */
    public boolean purchaseInvite(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_CLAN_INVITE)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_INVITE_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport
     */
    public boolean purchaseHomeTeleport(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_HOME_TELEPORT)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_HOME_TELEPORT_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport Set
     */
    public boolean purchaseHomeTeleportSet(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_HOME_TELEPORT_SET)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_HOME_TELEPORT_SET_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Reset Kdr
     */
    public boolean purchaseResetKdr(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_RESET_KDR)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_RESET_KDR_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Regroup
     */
    public boolean purchaseHomeRegroup(Player player) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        if (cp == null) {
            return false;
        }

        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_HOME_REGROUP)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_REGROUP_PRICE);
        Clan clan = Objects.requireNonNull(cp.getClan(), "Clan cannot be null");
        if (!plugin.getSettingsManager().is(ECONOMY_UNIQUE_TAX_ON_REGROUP)) {
            price = price * VanishUtils.getNonVanished(player, clan).size();
        }

        if (plugin.getSettingsManager().is(ECONOMY_ISSUER_PAYS_REGROUP)) {
            if (plugin.getPermissionsManager().hasEconomy()) {
                if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                    plugin.getPermissionsManager().playerChargeMoney(player, price);
                    player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
                } else {
                    player.sendMessage(RED + lang("not.sufficient.money", player, price));
                    return false;
                }
            }
        } else {
            double money = plugin.getPermissionsManager().playerGetMoney(player);
            switch (clan.withdraw(new BankOperator(player, money), ClanBalanceUpdateEvent.Cause.COMMAND, price)) {
                case SUCCESS:
                    if (plugin.getPermissionsManager().playerGrantMoney(player, price)) {
                        player.sendMessage(ChatColor.AQUA + lang("player.clan.withdraw", player, price));
                        clan.addBb(player.getName(), lang("bb.clan.withdraw", price));
                        return true;
                    }
                    break;
                case NOT_ENOUGH_BALANCE:
                    player.sendMessage(lang("clan.bank.not.enough.money", player));
                    break;
            }
        }

        return false;
    }

    /**
     * Purchase clan verification
     */
    public boolean purchaseVerification(Player player) {
        if (!plugin.getSettingsManager().is(ECONOMY_PURCHASE_CLAN_VERIFY)) {
            return true;
        }

        double price = plugin.getSettingsManager().getDouble(ECONOMY_VERIFICATION_PRICE);

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(RED + MessageFormat.format(lang("account.has.been.debited", player), price));
            } else {
                player.sendMessage(RED + lang("not.sufficient.money", player, price));
                return false;
            }
        }

        return true;
    }

    /**
     * Processes a global chat command
     */
    @Deprecated
    public boolean processGlobalChat(Player player, String msg) {
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp == null) {
            return false;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return false;
        }

        String command = split[0];

        if (command.equals(lang("on", player))) {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        } else if (command.equals(lang("off", player))) {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        } else {
            return true;
        }

        return false;
    }
}
