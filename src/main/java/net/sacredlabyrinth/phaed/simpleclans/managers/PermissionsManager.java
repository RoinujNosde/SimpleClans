package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.*;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public final class PermissionsManager {

    /**
     *
     */
    private final SimpleClans plugin;

    private static Permission permission = null;
    private static Economy economy = null;
    private static Chat chat = null;

    private final HashMap<String, List<String>> permissions = new HashMap<>();
    private final HashMap<Player, PermissionAttachment> permAttaches = new HashMap<>();

    /**
     *
     */
    public PermissionsManager() {
        plugin = SimpleClans.getInstance();

        try {
            Class.forName("net.milkbowl.vault.permission.Permission");

            setupChat();
            setupEconomy();
            setupPermissions();
        } catch (ClassNotFoundException e) {
            SimpleClans.getInstance().getLogger().info("Vault not found. No economy or extended Permissions support.");
        }
    }

    /**
     * Whether economy plugin exists and is enabled
     *
     */
    public boolean hasEconomy() {
        return economy != null && economy.isEnabled();
    }

    /**
     * Loads the permissions for each clan from the config
     */
    public void loadPermissions() {
        SimpleClans.getInstance().getSettingsManager().load();
        permissions.clear();
        for (Clan clan : plugin.getClanManager().getClans()) {
            permissions.put(clan.getTag(), SimpleClans.getInstance().getConfig().getStringList("permissions." + clan.getTag()));
        }
    }

    /**
     * Saves the permissions for each clan from the config
     */
    public void savePermissions() {
        for (Clan clan : plugin.getClanManager().getClans()) {
            if (permissions.containsKey(clan.getTag())) {
                SimpleClans.getInstance().getSettingsManager().getConfig().set("permissions." + clan.getTag(), getPermissions(clan));
            }
        }
        SimpleClans.getInstance().getSettingsManager().load();
        SimpleClans.getInstance().getSettingsManager().save();
    }

    /**
     * Adds all permissions for a clan
     *
     */
    public void updateClanPermissions(Clan clan) {
        for (ClanPlayer cp : clan.getMembers()) {
            addPlayerPermissions(cp);
        }
    }

    /**
     * Adds permissions for a player
     *
     */
    public void addPlayerPermissions(@Nullable ClanPlayer cp) {
        if (cp == null) {
            return;
        }
        Clan clan = cp.getClan();
        if (clan == null) {
            return;
        }
        Player player = cp.toPlayer();
        if (player != null) {
            if (permissions.containsKey(clan.getTag())) {
                if (!permAttaches.containsKey(player)) {
                    permAttaches.put(player, player.addAttachment(SimpleClans.getInstance()));
                }
                //Adds all permissions from his clan
                for (String perm : getPermissions(clan)) {
                    permAttaches.get(player).setPermission(perm, true);
                }
                if (plugin.getSettingsManager().isAutoGroupGroupName()) {
                    permAttaches.get(player).setPermission("group." + clan.getTag(), true);
                }
                player.recalculatePermissions();
            }
        }
    }

    /**
     * Removes permissions for a clan (when it gets disbanded for example)
     *
     */
    public void removeClanPermissions(Clan clan) {
        for (ClanPlayer cp : clan.getMembers()) {
            removeClanPlayerPermissions(cp);
        }
    }

    /**
     * Removes permissions for a player (when he gets kicked for example)
     *
     */
    public void removeClanPlayerPermissions(@Nullable ClanPlayer cp) {
        if (cp != null && cp.getClan() != null && cp.toPlayer() != null) {
            Player player = cp.toPlayer();
            if (player != null && permissions.containsKey(cp.getClan().getTag()) && permAttaches.containsKey(player)) {
                permAttaches.get(player).remove();
                permAttaches.remove(player);
            }
        }
    }

    /**
     * @return the permissions for a clan
     */
    public List<String> getPermissions(Clan clan) {
        return permissions.get(clan.getTag());
    }

    /**
     * @return the PermissionsAttachments for every player
     */
    public Map<Player, PermissionAttachment> getPermAttaches() {
        return permAttaches;
    }
    
    /**
     * Charge a player some money
     *
     */    
    @Deprecated
    public boolean playerChargeMoney(String player, double money) {
        return economy.withdrawPlayer(player, money).transactionSuccess();
    }
    
    /**
     * Charge a player some money
     *
     */    
    public boolean playerChargeMoney(OfflinePlayer player, double money) {
        return economy.withdrawPlayer(player, money).transactionSuccess();
    }

    /**
     * Charge a player some money
     *
     */
    public boolean playerChargeMoney(Player player, double money) {
    	return playerChargeMoney((OfflinePlayer) player, money);
    }

    /**
     * Grants a player some money
     *
     */
    public boolean playerGrantMoney(Player player, double money) {
        return economy.depositPlayer(player, money).transactionSuccess();
    }

    /**
     * Grants a player some money
     *
     */
    @Deprecated
    public boolean playerGrantMoney(String player, double money) {
        return economy.depositPlayer(player, money).transactionSuccess();
    }

    /**
     * Grants a player some money
     *
     */
    public boolean playerGrantMoney(OfflinePlayer player, double money) {
        return economy.depositPlayer(player, money).transactionSuccess();
    }

    /**
     * Check if a user has the money
     *
     * @return whether he has the money
     */
    public boolean playerHasMoney(Player player, double money) {
        return economy.has(player, money);
    }

    /**
     * Returns the players money
     *
     * @return the players money
     */
    public double playerGetMoney(Player player) {
        return economy.getBalance(player);
    }

    /**
     * Check if a player has permissions
     *
     * @param world the world
     * @param player the player
     * @param perm the permission
     * @return whether he has the permission
     */
    public boolean has(@Nullable String world, OfflinePlayer player, String perm) {
    	if (player != null && permission != null) {
    		return permission.playerHas(world, player, perm);
    	}
    	
    	return false;
    }    
    
    /**
     * Check if a player has permissions
     *
     * @param player the player
     * @param perm the permission
     * @return whether he has the permission
     */
    public boolean has(Player player, String perm) {
        if (player == null) {
            SimpleClans.debug("null player");
            return false;
        }

        boolean hasPermission;
        if (permission != null) {
            hasPermission = permission.has(player, perm);
        } else {
            hasPermission = player.hasPermission(perm);
        }
        SimpleClans.debug(String.format("Permission %s is %s for %s", perm, hasPermission, player.getName()));
        return hasPermission;
    }
    
    /**
     * Checks if the player has the rank permission or the permission level, and the equivalent Bukkit permission
     * 
     * @param player the player
     * @param permission the rank permission
     * @param notify notify the player if they don't have permission
     */
    @Deprecated
    public boolean has(Player player, RankPermission permission, PermissionLevel level, boolean notify) {
    	if (player == null || permission == null) {
    		return false;
    	}
    	
    	ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);
    	if (clanPlayer == null) {
    		return false;
    	}
    	
    	boolean hasBukkitPermission = has(player, permission.getBukkitPermission());
    	if (!hasBukkitPermission) {
    		return false;
    	}
    	
    	boolean hasLevel = false;
    	if (level != null) {
    		switch (level) {
    			case LEADER:
    				hasLevel = clanPlayer.isLeader();
    				break;
    			case TRUSTED:
    				hasLevel = clanPlayer.isTrusted();
    				break;
    		}
    	}
    	
    	boolean hasRankPermission = false;
    	String rankName = clanPlayer.getRankId();
    	Clan clan = clanPlayer.getClan();
        //noinspection ConstantConditions
        if (clan.hasRank(rankName)) {
            //noinspection ConstantConditions
            hasRankPermission = clan.getRank(rankName).getPermissions().contains(permission.toString());
		} else {
			if (rankName != null && !rankName.isEmpty()) {
				clanPlayer.setRank(null);
			}
		}
		
		if (notify && !hasLevel && !hasRankPermission) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("you.must.be.0.or.have.the.permission.1.to.use.this", player),
            		level == PermissionLevel.LEADER ? lang("leader", player) : lang("trusted", player), permission.toString()));
		}
    	
    	return hasLevel || hasRankPermission;
    }
    
    /**
     * Checks if the player has the rank permission or the permission level, and the equivalent Bukkit permission
     * 
     * @param player the player
     * @param permission the rank permission
     * @param notify notify the player if they don't have permission
     */
    public boolean has(Player player, RankPermission permission, boolean notify) {
    	if (player == null || permission == null) {
    		return false;
    	}
    	
    	ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player);
    	if (clanPlayer == null) {
    	    if (notify) {
    	        player.sendMessage(ChatColor.RED + lang("not.a.member.of.any.clan", player));
            }
    		return false;
    	}
    	
    	boolean hasBukkitPermission = has(player, permission.getBukkitPermission());
    	if (!hasBukkitPermission) {
    	    if (notify) {
                ChatBlock.sendMessage(player,ChatColor.RED + lang("insufficient.permissions", player));
            }
    		return false;
    	}
    	
		boolean hasLevel = false;
		switch (permission.getPermissionLevel()) {
			case LEADER:
				hasLevel = clanPlayer.isLeader();
				break;
			case TRUSTED:
				hasLevel = clanPlayer.isTrusted();
				break;
		}
    	
    	boolean hasRankPermission = false;
    	String rankName = clanPlayer.getRankId();
    	Clan clan = clanPlayer.getClan();
        if (clan != null) {
            Rank rank = clan.getRank(rankName);
            if (rank != null) {
                hasRankPermission = rank.getPermissions().contains(permission.toString());
            }
		} else {
			if (rankName != null && !rankName.isEmpty()) {
				clanPlayer.setRank(null);
			}
		}
		
		if (notify && !hasLevel && !hasRankPermission) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("you.must.be.0.or.have.the.permission.1.to.use.this",player),
            		permission.getPermissionLevel() == PermissionLevel.LEADER ? lang("leader",player) : lang("trusted",player), permission.toString()));
		}
    	
    	return hasLevel || hasRankPermission;
    }

    /**
     * Gives the player permissions linked to a clan
     *
     */
    public void addClanPermissions(ClanPlayer cp) {
        if (!plugin.getSettingsManager().isEnableAutoGroups()) {
            return;
        }

        if (permission != null) {
            if (cp != null && cp.toPlayer() != null) {
                if (cp.getClan() != null) {
                    if (!permission.playerInGroup(cp.toPlayer(), "clan_" + cp.getTag())) {
                        permission.playerAddGroup(cp.toPlayer(), "clan_" + cp.getTag());
                    }

                    if (cp.isLeader()) {
                        if (!permission.playerInGroup(cp.toPlayer(), "sc_leader")) {
                            permission.playerAddGroup(cp.toPlayer(), "sc_leader");
                        }
                        permission.playerRemoveGroup(cp.toPlayer(), "sc_untrusted");
                        permission.playerRemoveGroup(cp.toPlayer(), "sc_trusted");
                        return;
                    }

                    if (cp.isTrusted()) {
                        if (!permission.playerInGroup(cp.toPlayer(), "sc_trusted")) {
                            permission.playerAddGroup(cp.toPlayer(), "sc_trusted");
                        }
                        permission.playerRemoveGroup(cp.toPlayer(), "sc_untrusted");
                        permission.playerRemoveGroup(cp.toPlayer(), "sc_leader");
                        return;
                    }

                    if (!permission.playerInGroup(cp.toPlayer(), "sc_untrusted")) {
                        permission.playerAddGroup(cp.toPlayer(), "sc_untrusted");
                    }
                } else {
                    permission.playerRemoveGroup(cp.toPlayer(), "sc_untrusted");
                }
                permission.playerRemoveGroup(cp.toPlayer(), "sc_trusted");
                permission.playerRemoveGroup(cp.toPlayer(), "sc_leader");
            }
        }
    }

    /**
     * Removes permissions linked to a clan from the player
     *
     */
    public void removeClanPermissions(ClanPlayer cp) {
        if (!plugin.getSettingsManager().isEnableAutoGroups()) {
            return;
        }

        if (permission != null && cp.toPlayer() != null) {
            permission.playerRemoveGroup(cp.toPlayer(), "clan_" + cp.getTag());
            permission.playerRemoveGroup(cp.toPlayer(), "sc_untrusted");
            permission.playerRemoveGroup(cp.toPlayer(), "sc_trusted");
            permission.playerRemoveGroup(cp.toPlayer(), "sc_leader");
        }
    }

    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }

    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

    }

    /**
     */
    public String getPrefix(Player p) {
        String out = "";

        try {
            if (chat != null) {
                out = chat.getPlayerPrefix(p);
            }
        } catch (Exception ex) {
            // yea vault kinda sucks like that
        }

        if (permission != null && chat != null) {
            try {
                String world = p.getWorld().getName();
                String prefix = chat.getPlayerPrefix(world, p);
                if (prefix == null || prefix.isEmpty()) {
                    String group = permission.getPrimaryGroup(world, p);
                    prefix = chat.getGroupPrefix(world, group);
                    if (prefix == null) {
                        prefix = "";
                    }
                }

                out = prefix.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        // add in colorMe color

        /*
        Plugin colorMe = plugin.getServer().getPluginManager().getPlugin("ColorMe");

        if (colorMe != null)
        {
            out += ((ColorMe) colorMe).getColor(p.getName());
        }
         */
        return out;
    }

    /**
     */
    public String getSuffix(Player p) {
        try {
            if (chat != null) {
                return chat.getPlayerSuffix(p);
            }
        } catch (Exception ex) {
            // yea vault kinda sucks like that
        }

        if (permission != null && chat != null) {
            try {
                String world = p.getWorld().getName();
                String suffix = chat.getPlayerSuffix(world, p);
                if (suffix == null || suffix.isEmpty()) {
                    String group = permission.getPrimaryGroup(world, p);
                    suffix = chat.getGroupSuffix(world, group);
                    if (suffix == null) {
                        suffix = "";
                    }
                }
                return suffix.replace("&", "\u00a7").replace(String.valueOf((char) 194), "");
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return "";
            }
        }
        return "";
    }
}
