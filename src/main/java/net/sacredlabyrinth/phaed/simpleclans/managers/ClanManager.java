package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol.ChatControlDisabled;
import net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol.ChatControlEnabled;
import net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol.ChatControlJoined;
import net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol.ChatControlLeft;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.MessageFormat;
import java.util.*;
import net.sacredlabyrinth.phaed.simpleclans.events.ChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public final class ClanManager {

    public final SimpleClans plugin;
    public final KillManager killManager = new KillManager(this);
    private final HashMap<String, Clan> clans = new HashMap<>();
    private final HashMap<String, ClanPlayer> clanPlayers = new HashMap<>();
    private final ChatControlEnabled chatControlEnabled = new ChatControlEnabled(this);
    private final ChatControlDisabled chatControlDisabled = new ChatControlDisabled(this);
    private final ChatControlJoined chatControlJoined = new ChatControlJoined(this);
    private final ChatControlLeft chatControlLeft = new ChatControlLeft(this);

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
        killManager.kills.clear();
    }

    /**
     * Adds a kill to the memory
     *
     * @param kill
     */
    public void addKill(Kill kill) {

        killManager.addKill(kill);
    }

    /**
     * Checks if this kill respects the delay
     *
     * @param kill
     * @return
     */
    public boolean isKillBeforeDelay(Kill kill) {

        return killManager.isKillBeforeDelay(kill);
    }

    /**
     * Import a clan into the in-memory store
     *
     * @param clan
     */
    public void importClan(Clan clan) {
        this.clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     *
     * @param clanPlayer
     */
    public void importClanPlayer(ClanPlayer clanPlayer) {
        if (clanPlayer.getUniqueId() != null) {
            this.clanPlayers.put(clanPlayer.getUniqueId().toString(), clanPlayer);
        }
    }

    /**
     * Create a new clan
     *
     * @param player
     * @param colorTag
     * @param name
     */
    public void createClan(Player player, String colorTag, String name) {
        ClanPlayer createClanPlayer = getCreateClanPlayer(player.getUniqueId());

        boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(colorTag, name, verified);
        clan.addPlayerToClan(createClanPlayer);
        createClanPlayer.setLeader(true);

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);
        plugin.getStorageManager().updateClanPlayer(createClanPlayer);

        SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new CreateClanEvent(clan));
    }

    /**
     * Reset a player's KDR
     *
     * @param clanPlayer
     */
    public void resetKdr(ClanPlayer clanPlayer) {
        killManager.resetKdr(clanPlayer);
    }

    /**
     * Delete a players data file
     *
     * @param clanPlayer
     */
    public void deleteClanPlayer(ClanPlayer clanPlayer) {
        Clan clan = clanPlayer.getClan();
        if (clan != null) {
            clan.removePlayerFromClan(clanPlayer.getUniqueId());
        }
        clanPlayers.remove(clanPlayer.getUniqueId().toString());
        plugin.getStorageManager().deleteClanPlayer(clanPlayer);
    }

    /**
     * Delete a player data from memory
     *
     * @param playerUniqueId
     */
    public void deleteClanPlayerFromMemory(UUID playerUniqueId) {
        clanPlayers.remove(playerUniqueId.toString());
    }

    /**
     * Remove a clan from memory
     *
     * @param tag
     */
    public void removeClan(String tag) {
        clans.remove(tag);
    }

    /**
     * Whether the tag belongs to a clan
     *
     * @param tag
     * @return
     */
    public boolean isClan(String tag) {
        return clans.containsKey(Helper.cleanTag(tag));

    }

    /**
     * Returns the clan the tag belongs to
     *
     * @param tag
     * @return
     */
    public Clan getClan(String tag) {
        return clans.get(Helper.cleanTag(tag));
    }

    /**
     * Get a player's clan
     *
     * @param playerUniqueId
     * @return null if not in a clan
     */
    public Clan getClanByPlayerUniqueId(UUID playerUniqueId) {
        ClanPlayer clanPlayer = getClanPlayer(playerUniqueId);

        if (clanPlayer != null) {
            return clanPlayer.getClan();
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
     *
     * @return
     */
    public List<ClanPlayer> getAllClanPlayers() {
        return new ArrayList<>(clanPlayers.values());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayerJoinEvent(Player player) {
        SimpleClans.getInstance().getStorageManager().importFromDatabaseOnePlayer(player);
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(OfflinePlayer player) {
        return getClanPlayer(player.getUniqueId());
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param player
     * @return
     */
    public ClanPlayer getClanPlayer(Player player) {
        return getClanPlayer((OfflinePlayer) player);
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getClanPlayer(UUID playerUniqueId) {
        ClanPlayer clanPlayer = clanPlayers.get(playerUniqueId.toString());

        if (clanPlayer == null) {
            return null;
        }

        if (clanPlayer.getClan() == null) {
            return null;
        }

        return clanPlayer;
    }

    /**
     * Gets the ClanPlayer data object if a player is currently in a clan, null
     * if he's not in a clan
     *
     * @param playerDisplayName
     * @return
     */
    public ClanPlayer getClanPlayerName(String playerDisplayName) {
        UUID uuid = UUIDMigration.getForcedPlayerUUID(playerDisplayName);

        if (uuid == null) {
            return null;
        }

        ClanPlayer clanPlayer = clanPlayers.get(uuid.toString());

        if (clanPlayer == null) {
            return null;
        }

        if (clanPlayer.getClan() == null) {
            return null;
        }

        return clanPlayer;
    }

    /**
     * Gets the ClanPlayer data object for the player, will retrieve disabled
     * clan players as well, these are players who used to be in a clan but are
     * not currently in one, their data file persists and can be accessed. their
     * clan will be null though.
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getAnyClanPlayer(UUID playerUniqueId) {
        return clanPlayers.get(playerUniqueId.toString());
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerDisplayName
     * @return
     */
    public ClanPlayer getCreateClanPlayerUUID(String playerDisplayName) {
        UUID playerUniqueId = UUIDMigration.getForcedPlayerUUID(playerDisplayName);
        if (playerUniqueId != null) {
            return getCreateClanPlayer(playerUniqueId);
        } else {
            return null;
        }
    }

    /**
     * Gets the ClanPlayer object for the player, creates one if not found
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer getCreateClanPlayer(UUID playerUniqueId) {
        if (clanPlayers.containsKey(playerUniqueId.toString())) {
            return clanPlayers.get(playerUniqueId.toString());
        }

        ClanPlayer clanPlayer = new ClanPlayer(playerUniqueId);

        plugin.getStorageManager().insertClanPlayer(clanPlayer);
        importClanPlayer(clanPlayer);

        return clanPlayer;
    }

    /**
     * Announce message to the server
     *
     * @param msg
     */
    public void serverAnnounce(String msg) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "* " + ChatColor.AQUA + msg);
        }

        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + plugin.getLang("server.announce") + "] " + ChatColor.WHITE + msg);
    }

    /**
     * Update the players display name with his clan's tag
     *
     * @param player
     */
    public void updateDisplayName(Player player) {
        // do not update displayname if in compat mode

        if (plugin.getSettingsManager().isCompatMode()) {
            return;
        }

        if (player == null) {
            return;
        }

        if (plugin.getSettingsManager().isChatTags()) {
            String prefix = plugin.getPermissionsManager().getPrefix(player);
            //String suffix = plugin.getPermissionsManager().getSuffix(player);
            String lastColor = plugin.getSettingsManager().isUseColorCodeFromPrefix() ? Helper.getLastColorCode(prefix) : ChatColor.WHITE + "";
            String fullName = player.getName();

            ClanPlayer anyClanPlayer = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

            if (anyClanPlayer == null) {
                return;
            }

            if (anyClanPlayer.isTagEnabled()) {
                Clan clan = anyClanPlayer.getClan();

                if (clan != null) {
                    fullName = clan.getTagLabel(anyClanPlayer.isLeader()) + lastColor + fullName + ChatColor.WHITE;
                }

                player.setDisplayName(fullName);
            } else {
                player.setDisplayName(lastColor + fullName + ChatColor.WHITE);
            }
        }
    }

    /**
     * Process a player and his clan's last seen date
     *
     * @param player
     */
    public void updateLastSeen(Player player) {
        ClanPlayer anyClanPlayer = getAnyClanPlayer(player.getUniqueId());

        if (anyClanPlayer != null) {
            anyClanPlayer.updateLastSeen();
            plugin.getStorageManager().updateClanPlayer(anyClanPlayer);

            Clan clan = anyClanPlayer.getClan();

            if (clan != null) {
                clan.updateLastUsed();
                plugin.getStorageManager().updateClan(clan);
            }
        }
    }

    /**
     * Bans a player from clan commands
     *
     * @param uuid the player's uuid
     */
    public void ban(UUID uuid) {
        ClanPlayer clanPlayer = getClanPlayer(uuid);
        Clan clan = null;
        if (clanPlayer != null) {
            clan = clanPlayer.getClan();
        }

        if (clan != null) {
            if (clan.getSize() == 1) {
                clan.disband();
            } else {
                clanPlayer.setClan(null);
                clanPlayer.addPastClan(clan.getColorTag() + (clanPlayer.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                clanPlayer.setLeader(false);
                clanPlayer.setJoinDate(0);
                clan.removeMember(uuid);

                plugin.getStorageManager().updateClanPlayer(clanPlayer);
                plugin.getStorageManager().updateClan(clan);
            }
        }

        plugin.getSettingsManager().addBanned(uuid);
    }

    /**
     * Get a count of rivable clans
     *
     * @return
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
     *
     * @param inv
     * @return
     */
    public String getArmorString(PlayerInventory inv) {
        String out = "";

        ItemStack h = inv.getHelmet();

        if (h != null) {
            if (h.getType().equals(Material.CHAINMAIL_HELMET)) {
                out += ChatColor.WHITE + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.DIAMOND_HELMET)) {
                out += ChatColor.AQUA + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.GOLDEN_HELMET)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.IRON_HELMET)) {
                out += ChatColor.GRAY + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.LEATHER_HELMET)) {
                out += ChatColor.GOLD + plugin.getLang("armor.h");
            } else if (h.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.h");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.h");
            }
        }
        ItemStack c = inv.getChestplate();

        if (c != null) {
            if (c.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
                out += ChatColor.WHITE + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                out += ChatColor.AQUA + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.GOLDEN_CHESTPLATE)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.IRON_CHESTPLATE)) {
                out += ChatColor.GRAY + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.LEATHER_CHESTPLATE)) {
                out += ChatColor.GOLD + plugin.getLang("armor.c");
            } else if (c.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.c");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.c");
            }
        }
        ItemStack l = inv.getLeggings();

        if (l != null) {
            if (l.getType().equals(Material.CHAINMAIL_LEGGINGS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.DIAMOND_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.GOLDEN_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.IRON_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.LEATHER_LEGGINGS)) {
                out += plugin.getLang("armor.l");
            } else if (l.getType().equals(Material.AIR)) {
                out += plugin.getLang("armor.l");
            } else {
                out += plugin.getLang("armor.l");
            }
        }
        ItemStack b = inv.getBoots();

        if (b != null) {
            if (b.getType().equals(Material.CHAINMAIL_BOOTS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.DIAMOND_BOOTS)) {
                out += ChatColor.AQUA + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.GOLDEN_BOOTS)) {
                out += ChatColor.YELLOW + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.IRON_BOOTS)) {
                out += ChatColor.WHITE + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.LEATHER_BOOTS)) {
                out += ChatColor.GOLD + plugin.getLang("armor.B");
            } else if (b.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + plugin.getLang("armor.B");
            } else {
                out += ChatColor.RED + plugin.getLang("armor.B");
            }
        }

        if (out.length() == 0) {
            out = ChatColor.BLACK + "None";
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players weapons
     *
     * @param inv
     * @return
     */
    public String getWeaponString(PlayerInventory inv) {
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();

        String out = "";

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.AQUA + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.GOLDEN_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.IRON_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.WHITE + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.STONE_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GRAY + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.WOODEN_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + plugin.getLang("weapon.S") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.BOW));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + plugin.getLang("weapon.B") + headColor + countString;
        }

        count = getItemCount(inv.all(Material.ARROW));
        count += getItemCount(inv.all(Material.SPECTRAL_ARROW));
        count += getItemCount(inv.all(Material.TIPPED_ARROW));

        if (count > 0) {
            out += ChatColor.WHITE + plugin.getLang("weapon.A") + headColor + count;
        }

        if (out.length() == 0) {
            out = ChatColor.BLACK + "None";
        }

        return out;
    }

    private int getItemCount(HashMap<Integer, ? extends ItemStack> all) {
        int count = 0;

        for (ItemStack is : all.values()) {
            count += is.getAmount();
        }

        return count;
    }

    private double getFoodPoints(PlayerInventory inv, Material material, int points, double saturation) {
        return getItemCount(inv.all(material)) * (points + saturation);
    }

    private double getFoodPoints(PlayerInventory inv, Material material, int type, int points, double saturation) {
        return getItemCount(inv.all(new ItemStack(material, 1, (short) type))) * (points + saturation);
    }

    /**
     * Returns a formatted string detailing the players food
     *
     * @param inv
     * @return
     */
    public String getFoodString(PlayerInventory inv) {

        double count = getFoodPoints(inv, Material.APPLE, 4, 2.4);
        count += getFoodPoints(inv, Material.BAKED_POTATO, 5, 6);
        count += getFoodPoints(inv, Material.BEETROOT, 1, 1.2);
        count += getFoodPoints(inv, Material.BEETROOT_SOUP, 6, 7.2);
        count += getFoodPoints(inv, Material.BREAD, 5, 6);
        count += getFoodPoints(inv, Material.CAKE, 14, 2.8);
        count += getFoodPoints(inv, Material.CARROT, 3, 3.6);
        count += getFoodPoints(inv, Material.CHORUS_FRUIT, 4, 2.4);
        count += getFoodPoints(inv, Material.COOKED_CHICKEN, 6, 7.2);
        count += getFoodPoints(inv, Material.COOKED_MUTTON, 6, 9.6);
        count += getFoodPoints(inv, Material.COOKED_PORKCHOP, 8, 12.8);
        count += getFoodPoints(inv, Material.COOKED_RABBIT, 5, 6);
        count += getFoodPoints(inv, Material.COOKED_SALMON, 1, 6, 9.6);
        count += getFoodPoints(inv, Material.COOKIE, 2, .4);
        count += getFoodPoints(inv, Material.GOLDEN_APPLE, 4, 9.6);
        count += getFoodPoints(inv, Material.GOLDEN_CARROT, 6, 14.4);
        count += getFoodPoints(inv, Material.MELON, 2, 1.2);
        count += getFoodPoints(inv, Material.MUSHROOM_STEW, 6, 7.2);
        count += getFoodPoints(inv, Material.POISONOUS_POTATO, 2, 1.2);
        count += getFoodPoints(inv, Material.POTATO, 1, 0.6);
        count += getFoodPoints(inv, Material.PUFFERFISH, 3, 1, 0.2);
        count += getFoodPoints(inv, Material.PUMPKIN_PIE, 8, 4.8);
        count += getFoodPoints(inv, Material.RABBIT_STEW, 10, 12);
        count += getFoodPoints(inv, Material.BEEF, 3, 1.8);
        count += getFoodPoints(inv, Material.CHICKEN, 2, 1.2);
        count += getFoodPoints(inv, Material.MUTTON, 2, 1.2);
        count += getFoodPoints(inv, Material.PORKCHOP, 3, 1.8);
        count += getFoodPoints(inv, Material.RABBIT, 3, 1.8);
        count += getFoodPoints(inv, Material.SALMON, 1, .4);
        count += getFoodPoints(inv, Material.COD, 2, .4);
        count += getFoodPoints(inv, Material.COOKED_COD, 5, 6);
        count += getFoodPoints(inv, Material.TROPICAL_FISH, 1, .2);
        count += getFoodPoints(inv, Material.ROTTEN_FLESH, 4, .8);
        count += getFoodPoints(inv, Material.SPIDER_EYE, 2, 3.2);
        count += getFoodPoints(inv, Material.COOKED_BEEF, 8, 12.8);

        if (count == 0) {
            return ChatColor.BLACK + plugin.getLang("none");
        } else {
            return ((int) count) + "" + ChatColor.GOLD + "p";
        }
    }

    /**
     * Returns a formatted string detailing the players health
     *
     * @param health
     * @return
     */
    public String getHealthString(double health) {
        String out = "";

        if (health >= 16) {
            out += ChatColor.GREEN;
        } else if (health >= 8) {
            out += ChatColor.GOLD;
        } else {
            out += ChatColor.RED;
        }

        for (int i = 0; i < health; i++) {
            out += '|';
        }

        return out;
    }

    /**
     * Returns a formatted string detailing the players hunger
     *
     * @param health
     * @return
     */
    public String getHungerString(int health) {
        String out = "";

        if (health >= 16) {
            out += ChatColor.GREEN;
        } else if (health >= 8) {
            out += ChatColor.GOLD;
        } else {
            out += ChatColor.RED;
        }

        for (int i = 0; i < health; i++) {
            out += '|';
        }

        return out;
    }

    /**
     * Sort clans by active
     *
     * @param clans
     * @param asc
     */
    public void sortClansByActive(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return ((Long) c1.getLastUsed()).compareTo(c2.getLastUsed()) * o;
        });
    }

    /**
     * Sort clans by founded date
     *
     * @param clans
     * @param asc
     */
    public void sortClansByFounded(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return ((Long) c1.getFounded()).compareTo(c2.getFounded()) * o;
        });
    }

    /**
     * Sort clans by size
     *
     * @param clans
     * @param asc
     */
    public void sortClansBySize(List<Clan> clans, boolean asc) {
        clans.sort((c1, c2) -> {
            int o = 1;
            if (!asc) {
                o = -1;
            }

            return ((Integer) c1.getSize()).compareTo(c2.getSize()) * o;
        });
    }

    /**
     * Sort clans by name
     *
     * @param clans
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
     *
     * @param clans
     * @return
     */
    public void sortClansByKDR(List<Clan> clans) {
        Collections.sort(clans, new Comparator<Clan>() {

            @Override
            public int compare(Clan c1, Clan c2) {
                Float o1 = c1.getTotalKDR();
                Float o2 = c2.getTotalKDR();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clans by KDR
     *
     * @param clans
     */
    public void sortClansBySize(List<Clan> clans) {
        Collections.sort(clans, new Comparator<Clan>() {

            @Override
            public int compare(Clan c1, Clan c2) {
                Integer o1 = c1.getAllMembers().size();
                Integer o2 = c2.getAllMembers().size();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clan players by KDR
     *
     * @param clanPlayerList
     * @return
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> clanPlayerList) {
        Collections.sort(clanPlayerList, new Comparator<ClanPlayer>() {

            @Override
            public int compare(ClanPlayer c1, ClanPlayer c2) {
                Float o1 = c1.getKDR();
                Float o2 = c2.getKDR();

                return o2.compareTo(o1);
            }
        });
    }

    /**
     * Sort clan players by last seen days
     *
     * @param clanPlayerList
     */
    public void sortClanPlayersByLastSeen(List<ClanPlayer> clanPlayerList) {
        Collections.sort(clanPlayerList, new Comparator<ClanPlayer>() {

            @Override
            public int compare(ClanPlayer c1, ClanPlayer c2) {
                Double o1 = c1.getLastSeenDays();
                Double o2 = c2.getLastSeenDays();

                return o1.compareTo(o2);
            }
        });
    }

    /**
     * Purchase member fee set
     *
     * @param player
     * @return
     */
    public boolean purchaseMemberFeeSet(Player player) {
        if (!plugin.getSettingsManager().isePurchaseMemberFeeSet()) {
            return true;
        }

        double price = plugin.getSettingsManager().geteMemberFeeSetPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase clan creation
     *
     * @param player
     * @return
     */
    public boolean purchaseCreation(Player player) {
        if (!plugin.getSettingsManager().isePurchaseCreation()) {
            return true;
        }

        double price = plugin.getSettingsManager().getCreationPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase invite
     *
     * @param player
     * @return
     */
    public boolean purchaseInvite(Player player) {
        if (!plugin.getSettingsManager().isePurchaseInvite()) {
            return true;
        }

        double price = plugin.getSettingsManager().getInvitePrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleport(Player player) {
        if (!plugin.getSettingsManager().isePurchaseHomeTeleport()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeTeleportPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Teleport Set
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeTeleportSet(Player player) {
        if (!plugin.getSettingsManager().isePurchaseHomeTeleportSet()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeTeleportPriceSet();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Reset Kdr
     *
     * @param player
     * @return
     */
    public boolean purchaseResetKdr(Player player) {
        if (!plugin.getSettingsManager().isePurchaseResetKdr()) {
            return true;
        }

        double price = plugin.getSettingsManager().geteResetKdr();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Purchase Home Regroup
     *
     * @param player
     * @return
     */
    public boolean purchaseHomeRegroup(Player player) {
        ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);

        if (!plugin.getSettingsManager().isePurchaseHomeRegroup()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeRegroupPrice();
        if (!plugin.getSettingsManager().iseUniqueTaxOnRegroup()) {
            price = price * clanPlayer.getClan().getOnlineMembers().size();
        }

        if (plugin.getSettingsManager().iseIssuerPaysRegroup()) {
            if (plugin.getPermissionsManager().hasEconomy()) {
                if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                    plugin.getPermissionsManager().playerChargeMoney(player, price);
                    player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
                } else {
                    player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                    return false;
                }
            }
        } else {
            Clan clan = clanPlayer.getClan();
            double balance = clan.getBalance();
            if (price > balance) {
                player.sendMessage(ChatColor.RED + plugin.getLang("clan.bank.not.enough.money"));
                return false;
            }
            clan.withdraw(price, player);
        }

        return true;
    }

    /**
     * Purchase clan verification
     *
     * @param player
     * @return
     */
    public boolean purchaseVerification(Player player) {
        if (!plugin.getSettingsManager().isePurchaseVerification()) {
            return true;
        }

        double price = plugin.getSettingsManager().getVerificationPrice();

        if (plugin.getPermissionsManager().hasEconomy()) {
            if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                plugin.getPermissionsManager().playerChargeMoney(player, price);
                player.sendMessage(ChatColor.RED + MessageFormat.format(plugin.getLang("account.has.been.debited"), price));
            } else {
                player.sendMessage(ChatColor.RED + plugin.getLang("not.sufficient.money"));
                return false;
            }
        }

        return true;
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param tag
     * @param msg
     */
    public void processClanChat(Player player, String tag, String msg) {
        Clan clan = plugin.getClanManager().getClan(tag);

        if (clan == null || !clan.isMember(player)) {
            return;
        }

        processClanChat(player, msg);
    }

    /**
     * Processes a clan chat command
     *
     * @param player
     * @param msg
     */
    public void processClanChat(Player player, final String msg) {
        final ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);

        if (clanPlayer == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            chatControlEnabled.chatcontrol(player, clanPlayer);
        } else if (command.equals(plugin.getLang("off"))) {
            chatControlDisabled.chatcontrol(player, clanPlayer);
        } else if (command.equals(plugin.getLang("join"))) {
            chatControlJoined.chatcontrol(player, clanPlayer);
        } else if (command.equals(plugin.getLang("leave"))) {
            chatControlLeft.chatcontrol(player, clanPlayer);
        } else if (command.equals(plugin.getLang("mute"))) {
            if (clanPlayer.isMuted()) {
                clanPlayer.setMuted(true);
                ChatBlock.sendMessage(player, lang("muted.clan.chat", player));
            } else {
                clanPlayer.setMuted(false);
                ChatBlock.sendMessage(player, lang("unmuted.clan.chat", player));
            }
        } else {
            final List<ClanPlayer> receivers = new LinkedList<>();
            for (ClanPlayer p : clanPlayer.getClan().getOnlineMembers()) {
                if (!p.isMuted()) {
                    receivers.add(p);
                }
            }





            new BukkitRunnable() {
                @Override
                public void run() {
                    ChatEvent ce = new ChatEvent(msg, clanPlayer, receivers, ChatEvent.Type.CLAN);
                    Bukkit.getServer().getPluginManager().callEvent(ce);

                    if (ce.isCancelled()) {
                        return;
                    }

                    String message = Helper.formatClanChat(clanPlayer, ce.getMessage(), ce.getPlaceholders());
                    String eyeMessage = Helper.formatSpyClanChat(clanPlayer, message);
                    plugin.getServer().getConsoleSender().sendMessage(eyeMessage);

                    for (ClanPlayer p : ce.getReceivers()) {
                        ChatBlock.sendMessage(p.toPlayer(), message);
                    }

                    sendToAllSeeing(eyeMessage, ce.getReceivers());
                }
            }.runTask(plugin);
        }
    }


    public void sendToAllSeeing(String msg, List<ClanPlayer> clanPlayerList) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")) {
                boolean alreadySent = false;

                ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);

                if (clanPlayer != null && clanPlayer.isMuted()) {
                    continue;
                }

                for (ClanPlayer clanPlayer1 : clanPlayerList) {
                    if (clanPlayer1.getName().equalsIgnoreCase(player.getName())) {
                        alreadySent = true;
                    }
                }

                if (!alreadySent) {
                    ChatBlock.sendMessage(player, msg);
                }
            }
        }
    }

    /**
     * Processes a ally chat command
     *
     * @param player
     * @param msg
     */
    public void processAllyChat(Player player, final String msg) {
        final ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);

        if (clanPlayer == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            clanPlayer.setAllyChat(true);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, lang("enabled.ally.chat", player));
        } else if (command.equals(plugin.getLang("off"))) {
            clanPlayer.setAllyChat(false);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, lang("disabled.ally.chat", player));
        } else if (command.equals(plugin.getLang("join"))) {
            clanPlayer.setChannel(ClanPlayer.Channel.ALLY);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, lang("joined.ally.chat", player));
        } else if (command.equals(plugin.getLang("leave"))) {
            clanPlayer.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, lang("left.ally.chat", player));
        } else if (command.equals(plugin.getLang("mute"))) {
            if (!clanPlayer.isMutedAlly()) {
                clanPlayer.setMutedAlly(true);
                ChatBlock.sendMessage(player, lang("muted.ally.chat", player));
            } else {
                clanPlayer.setMutedAlly(false);
                ChatBlock.sendMessage(player, lang("unmuted.ally.chat", player));
            }
        } else {
            final List<ClanPlayer> receivers = new LinkedList<>();
            Set<ClanPlayer> allies = clanPlayer.getClan().getAllAllyMembers();
            allies.addAll(clanPlayer.getClan().getMembers());
            for (ClanPlayer ally : allies) {
                if (ally.isMutedAlly()) {
                    continue;
                }
                if (player.getUniqueId().equals(ally.getUniqueId())) {
                    continue;
                }
                receivers.add(ally);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    ChatEvent ce = new ChatEvent(msg, clanPlayer, receivers, ChatEvent.Type.ALLY);
                    Bukkit.getServer().getPluginManager().callEvent(ce);

                    if (ce.isCancelled()) {
                        return;
                    }

                    String message = Helper.formatAllyChat(clanPlayer, ce.getMessage(), ce.getPlaceholders());
                    plugin.getLogger().info(message);

                    Player self = clanPlayer.toPlayer();
                    ChatBlock.sendMessage(self, message);

                    for (ClanPlayer p : ce.getReceivers()) {
                        ChatBlock.sendMessage(p.toPlayer(), message);
                    }
                }
            }.runTask(plugin);
        }
    }

    /**
     * Processes a global chat command
     *
     * @param player
     * @param msg
     * @return boolean
     */
    public boolean processGlobalChat(Player player, String msg) {
        ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player.getUniqueId());

        if (clanPlayer == null) {
            return false;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return false;
        }

        String command = split[0];

        if (command.equals(plugin.getLang("on"))) {
            clanPlayer.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        } else if (command.equals(plugin.getLang("off"))) {
            clanPlayer.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        } else {
            return true;
        }

        return false;
    }
}
