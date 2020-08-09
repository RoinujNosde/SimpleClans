package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    private final SimpleClans plugin;
    private final HashMap<String, Clan> clans = new HashMap<>();
    private final HashMap<String, ClanPlayer> clanPlayers = new HashMap<>();
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
     * 
     * @param kill
     */
    public void addKill(Kill kill) {
    	if (kill == null) {
    		return;
    	}

    	List<Kill> list = kills.get(kill.getKiller());
    	if (list == null) {
    		list = new ArrayList<>();
    		kills.put(kill.getKiller(), list);
    	}
    	
    	Iterator<Kill> iterator = list.iterator();
    	while (iterator.hasNext()) {
    		Kill oldKill = iterator.next();
    		if (oldKill.getVictim().equals(kill.getKiller())) {
    			iterator.remove();
    			continue;
    		}
    		
    		//cleaning
    		final int delay = plugin.getSettingsManager().getDelayBetweenKills();
			long timePassed = oldKill.getTime().until(LocalDateTime.now(), ChronoUnit.MINUTES);
			if (timePassed >= delay) {
				iterator.remove();
			}
    	}
    	
    	list.add(kill);
    }
    
    /**
     * Checks if this kill respects the delay
     * 
     * @param kill
     * @return
     */
	public boolean isKillBeforeDelay(Kill kill) {
		if (kill == null) {
			return false;
		}
		List<Kill> list = kills.get(kill.getKiller());
		if (list == null) {
			return false;
		}

		Iterator<Kill> iterator = list.iterator();
		while (iterator.hasNext()) {

			Kill oldKill = iterator.next();
			if (oldKill.getVictim().equals(kill.getVictim())) {

				final int delay = plugin.getSettingsManager().getDelayBetweenKills();
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
     *
     * @param clan
     */
    public void importClan(Clan clan) {
        this.clans.put(clan.getTag(), clan);
    }

    /**
     * Import a clan player into the in-memory store
     *
     * @param cp
     */
    public void importClanPlayer(ClanPlayer cp) {
		if (cp.getUniqueId() != null) {
			this.clanPlayers.put(cp.getUniqueId().toString(), cp);
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
        ClanPlayer cp = getCreateClanPlayer(player.getUniqueId());

        boolean verified = !plugin.getSettingsManager().isRequireVerification() || plugin.getPermissionsManager().has(player, "simpleclans.mod.verify");

        Clan clan = new Clan(colorTag, name, verified);
        clan.addPlayerToClan(cp);
        cp.setLeader(true);

        plugin.getStorageManager().insertClan(clan);
        importClan(clan);
        plugin.getStorageManager().updateClanPlayer(cp);

        SimpleClans.getInstance().getPermissionsManager().updateClanPermissions(clan);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new CreateClanEvent(clan));
    }

    /**
     * Reset a player's KDR
     *
     * @param cp
     */
    public void resetKdr(ClanPlayer cp) {
        cp.setCivilianKills(0);
        cp.setNeutralKills(0);
        cp.setRivalKills(0);
        cp.setDeaths(0);
        plugin.getStorageManager().updateClanPlayer(cp);
    }

    /**
     * Delete a players data file
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        Clan clan = cp.getClan();
		if (clan != null) {
			clan.removePlayerFromClan(cp.getUniqueId());
		}
        clanPlayers.remove(cp.getUniqueId().toString());
        plugin.getStorageManager().deleteClanPlayer(cp);
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
        ClanPlayer cp = clanPlayers.get(playerUniqueId.toString());

        if (cp == null) {
            return null;
        }

        if (cp.getClan() == null) {
            return null;
        }

        return cp;
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

        ClanPlayer cp = clanPlayers.get(uuid.toString());

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

        ClanPlayer cp = new ClanPlayer(playerUniqueId);

        plugin.getStorageManager().insertClanPlayer(cp);
        importClanPlayer(cp);

        return cp;
    }

    /**
     * Announce message to the server
     *
     * @param msg the message
     */
    public void serverAnnounce(String msg) {
        if (plugin.getSettingsManager().isDisableMessages()) {
            return;
        }
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            ChatBlock.sendMessage(player, ChatColor.DARK_GRAY + "* " + ChatColor.AQUA + msg);
        }

        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + lang("server.announce") + "] " + ChatColor.WHITE + msg);
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
     *
     * @param player
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
                clan.disband();
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

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder != null && holder instanceof Player) {
        	player = (Player) holder;
        }

        if (h != null) {
            if (h.getType().equals(Material.CHAINMAIL_HELMET)) {
                out += ChatColor.WHITE + lang("armor.h",player);
            } else if (h.getType().equals(Material.DIAMOND_HELMET)) {
                out += ChatColor.AQUA + lang("armor.h",player);
            } else if (h.getType().equals(Material.GOLDEN_HELMET)) {
                out += ChatColor.YELLOW + lang("armor.h",player);
            } else if (h.getType().equals(Material.IRON_HELMET)) {
                out += ChatColor.GRAY + lang("armor.h",player);
            } else if (h.getType().equals(Material.LEATHER_HELMET)) {
                out += ChatColor.GOLD + lang("armor.h",player);
            } else if (h.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + lang("armor.h",player);
            } else {
                out += ChatColor.RED + lang("armor.h",player);
            }
        }
        ItemStack c = inv.getChestplate();

        if (c != null) {
            if (c.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
                out += ChatColor.WHITE + lang("armor.c",player);
            } else if (c.getType().equals(Material.DIAMOND_CHESTPLATE)) {
                out += ChatColor.AQUA + lang("armor.c",player);
            } else if (c.getType().equals(Material.GOLDEN_CHESTPLATE)) {
                out += ChatColor.YELLOW + lang("armor.c",player);
            } else if (c.getType().equals(Material.IRON_CHESTPLATE)) {
                out += ChatColor.GRAY + lang("armor.c",player);
            } else if (c.getType().equals(Material.LEATHER_CHESTPLATE)) {
                out += ChatColor.GOLD + lang("armor.c",player);
            } else if (c.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + lang("armor.c",player);
            } else {
                out += ChatColor.RED + lang("armor.c",player);
            }
        }
        ItemStack l = inv.getLeggings();

        if (l != null) {
            if (l.getType().equals(Material.CHAINMAIL_LEGGINGS)) {
                out += ChatColor.WHITE + lang("armor.l",player);
            } else if (l.getType().equals(Material.DIAMOND_LEGGINGS)) {
                out += lang("armor.l",player);
            } else if (l.getType().equals(Material.GOLDEN_LEGGINGS)) {
                out += lang("armor.l",player);
            } else if (l.getType().equals(Material.IRON_LEGGINGS)) {
                out += lang("armor.l",player);
            } else if (l.getType().equals(Material.LEATHER_LEGGINGS)) {
                out += lang("armor.l",player);
            } else if (l.getType().equals(Material.AIR)) {
                out += lang("armor.l",player);
            } else {
                out += lang("armor.l",player);
            }
        }
        ItemStack b = inv.getBoots();

        if (b != null) {
            if (b.getType().equals(Material.CHAINMAIL_BOOTS)) {
                out += ChatColor.WHITE + lang("armor.B",player);
            } else if (b.getType().equals(Material.DIAMOND_BOOTS)) {
                out += ChatColor.AQUA + lang("armor.B",player);
            } else if (b.getType().equals(Material.GOLDEN_BOOTS)) {
                out += ChatColor.YELLOW + lang("armor.B",player);
            } else if (b.getType().equals(Material.IRON_BOOTS)) {
                out += ChatColor.WHITE + lang("armor.B",player);
            } else if (b.getType().equals(Material.LEATHER_BOOTS)) {
                out += ChatColor.GOLD + lang("armor.B",player);
            } else if (b.getType().equals(Material.AIR)) {
                out += ChatColor.BLACK + lang("armor.B",player);
            } else {
                out += ChatColor.RED + lang("armor.B",player);
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

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder != null && holder instanceof Player) {
        	player = (Player) holder;
        }

        int count = getItemCount(inv.all(Material.DIAMOND_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.AQUA + lang("weapon.S",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.GOLDEN_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + lang("weapon.S",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.IRON_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.WHITE + lang("weapon.S",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.STONE_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GRAY + lang("weapon.S",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.WOODEN_SWORD));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + lang("weapon.S",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.BOW));

        if (count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.GOLD + lang("weapon.B",player) + headColor + countString;
        }

        count = getItemCount(inv.all(Material.ARROW));
        count += getItemCount(inv.all(Material.SPECTRAL_ARROW));
        count += getItemCount(inv.all(Material.TIPPED_ARROW));

        if (count > 0) {
            out += ChatColor.WHITE + lang("weapon.A",player) + headColor + count;
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

        Player player = null;
        InventoryHolder holder = inv.getHolder();
        if (holder != null && holder instanceof Player) {
        	player = (Player) holder;
        }

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
            return ChatColor.BLACK + lang("none",player);
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
     * Sort clans by kdr
     * 
     * @param clans
     * @param asc
     */
    public void sortClansByKDR(List<Clan> clans, boolean asc) {
    	clans.sort((c1, c2) -> {
    	  	int o = 1;
        	if (!asc) {
        		o = -1;
        	}
        	
    		return ((Float) c1.getTotalKDR()).compareTo(c2.getTotalKDR()) * o;
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
     * @param cps
     * @return
     */
    public void sortClanPlayersByKDR(List<ClanPlayer> cps) {
        Collections.sort(cps, new Comparator<ClanPlayer>() {

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
     * @param cps
     */
    public void sortClanPlayersByLastSeen(List<ClanPlayer> cps) {
        Collections.sort(cps, new Comparator<ClanPlayer>() {

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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (!plugin.getSettingsManager().isePurchaseHomeRegroup()) {
            return true;
        }

        double price = plugin.getSettingsManager().getHomeRegroupPrice();
        if (!plugin.getSettingsManager().iseUniqueTaxOnRegroup()) {
            price = price * cp.getClan().getOnlineMembers().size();
        }

        if (plugin.getSettingsManager().iseIssuerPaysRegroup()) {
            if (plugin.getPermissionsManager().hasEconomy()) {
                if (plugin.getPermissionsManager().playerHasMoney(player, price)) {
                    plugin.getPermissionsManager().playerChargeMoney(player, price);
                    player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
                } else {
                    player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
                    return false;
                }
            }
        } else {
            Clan clan = cp.getClan();
            double balance = clan.getBalance();
            if (price > balance) {
                player.sendMessage(ChatColor.RED + lang("clan.bank.not.enough.money",player));
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
                player.sendMessage(ChatColor.RED + MessageFormat.format(lang("account.has.been.debited",player), price));
            } else {
                player.sendMessage(ChatColor.RED + lang("not.sufficient.money",player));
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
        final ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(lang("on",player))) {
            cp.setClanChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("enabled.clan.chat", player));
        } else if (command.equals(lang("off",player))) {
            cp.setClanChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("disabled.clan.chat", player));
        } else if (command.equals(lang("join",player))) {
            cp.setChannel(ClanPlayer.Channel.CLAN);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("joined.clan.chat", player));
        } else if (command.equals(lang("leave",player))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("left.clan.chat", player));
        } else if (command.equals(lang("mute",player))) {
            if (cp.isMuted()) {
                cp.setMuted(true);
                ChatBlock.sendMessage(player, lang("muted.clan.chat", player));
            } else {
                cp.setMuted(false);
                ChatBlock.sendMessage(player, lang("unmuted.clan.chat", player));
            }
        } else {
            final List<ClanPlayer> receivers = new LinkedList<>();
            for (ClanPlayer p : cp.getClan().getOnlineMembers()) {
                if (!p.isMuted()) {
                    receivers.add(p);
                }
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    ChatEvent ce = new ChatEvent(msg, cp, receivers, ChatEvent.Type.CLAN);
                    Bukkit.getServer().getPluginManager().callEvent(ce);

                    if (ce.isCancelled()) {
                        return;
                    }

                    String message = Helper.formatClanChat(cp, ce.getMessage(), ce.getPlaceholders());
                    String eyeMessage = Helper.formatSpyClanChat(cp, message);
                    plugin.getServer().getConsoleSender().sendMessage(eyeMessage);

                    for (ClanPlayer p : ce.getReceivers()) {
                        ChatBlock.sendMessage(p.toPlayer(), message);
                    }

                    sendToAllSeeing(eyeMessage, ce.getReceivers());
                }
            }.runTask(plugin);
        }
    }

    public void sendToAllSeeing(String msg, List<ClanPlayer> cps) {
        Collection<Player> players = Helper.getOnlinePlayers();

        for (Player player : players) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.admin.all-seeing-eye")) {
                boolean alreadySent = false;

                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null && cp.isMuted()) {
                    continue;
                }

                for (ClanPlayer cpp : cps) {
                    if (cpp.getName().equalsIgnoreCase(player.getName())) {
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
        final ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            return;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return;
        }

        String command = split[0];

        if (command.equals(lang("on",player))) {
            cp.setAllyChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("enabled.ally.chat", player));
        } else if (command.equals(lang("off",player))) {
            cp.setAllyChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("disabled.ally.chat", player));
        } else if (command.equals(lang("join",player))) {
            cp.setChannel(ClanPlayer.Channel.ALLY);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("joined.ally.chat", player));
        } else if (command.equals(lang("leave",player))) {
            cp.setChannel(ClanPlayer.Channel.NONE);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, lang("left.ally.chat", player));
        } else if (command.equals(lang("mute",player))) {
            if (!cp.isMutedAlly()) {
                cp.setMutedAlly(true);
                ChatBlock.sendMessage(player, lang("muted.ally.chat", player));
            } else {
                cp.setMutedAlly(false);
                ChatBlock.sendMessage(player, lang("unmuted.ally.chat", player));
            }
        } else {
            final List<ClanPlayer> receivers = new LinkedList<>();
            Set<ClanPlayer> allies = cp.getClan().getAllAllyMembers();
            allies.addAll(cp.getClan().getMembers());
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
                    ChatEvent ce = new ChatEvent(msg, cp, receivers, ChatEvent.Type.ALLY);
                    Bukkit.getServer().getPluginManager().callEvent(ce);

                    if (ce.isCancelled()) {
                        return;
                    }

                    String message = Helper.formatAllyChat(cp, ce.getMessage(), ce.getPlaceholders());
                    plugin.getLogger().info(message);

                    Player self = cp.toPlayer();
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
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp == null) {
            return false;
        }

        String[] split = msg.split(" ");

        if (split.length == 0) {
            return false;
        }

        String command = split[0];

        if (command.equals(lang("on",player))) {
            cp.setGlobalChat(true);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have enabled global chat");
        } else if (command.equals(lang("off",player))) {
            cp.setGlobalChat(false);
            plugin.getStorageManager().updateClanPlayer(cp);
            ChatBlock.sendMessage(player, ChatColor.AQUA + "You have disabled global chat");
        } else {
            return true;
        }

        return false;
    }
}
