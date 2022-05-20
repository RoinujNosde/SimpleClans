package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ENABLE_AUTO_GROUPS;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.PERMISSIONS_AUTO_GROUP_GROUPNAME;

/**
 * @author phaed
 */
public final class PermissionsManager {

    private final SimpleClans plugin;

    private @Nullable Permission permission;
    private @Nullable Economy economy;
    private @Nullable Chat chat;

    private final HashMap<String, List<String>> permissions = new HashMap<>();
    private final HashMap<Player, PermissionAttachment> permAttaches = new HashMap<>();

    public PermissionsManager() {
        plugin = SimpleClans.getInstance();

        try {
            Class.forName("net.milkbowl.vault.permission.Permission");

            chat = getProvider(Chat.class);
            economy = getProvider(Economy.class);
            permission = getProvider(Permission.class);
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
        permissions.clear();
        for (Clan clan : plugin.getClanManager().getClans()) {
            permissions.put(clan.getTag(), SimpleClans.getInstance().getSettingsManager().getConfig().
                    getStringList("permissions." + clan.getTag()));
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
                if (plugin.getSettingsManager().is(PERMISSIONS_AUTO_GROUP_GROUPNAME)) {
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
            removeClanPermissions(cp);
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
     * Removes permissions linked to a clan from the player
     */
    public void removeClanPermissions(ClanPlayer cp) {
        if (!plugin.getSettingsManager().is(ENABLE_AUTO_GROUPS)) {
            return;
        }

        if (permission != null && cp.toPlayer() != null) {
            permission.playerRemoveGroup(null, cp.toPlayer(), "clan_" + cp.getTag());
            permission.playerRemoveGroup(null, cp.toPlayer(), "sc_untrusted");
            permission.playerRemoveGroup(null, cp.toPlayer(), "sc_trusted");
            permission.playerRemoveGroup(null, cp.toPlayer(), "sc_leader");
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
     *
     * @deprecated use {@link PermissionsManager#has(Player, RankPermission, boolean)} or {@link PermissionsManager#has(Player, String)}
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
     * @param perm   the permission
     * @return whether he has the permission
     */
    public boolean has(@Nullable Player player, String perm) {
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
     *
     * @deprecated use {@link PermissionsManager#has(Player, RankPermission, boolean)} or {@link PermissionsManager#has(Player, String)}
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
        } else if (!rankName.isEmpty()) {
            clanPlayer.setRank(null);
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
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions", player));
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
        } else if (!rankName.isEmpty()) {
            clanPlayer.setRank(null);
        }

        if (notify && !hasLevel && !hasRankPermission) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("you.must.be.0.or.have.the.permission.1.to.use.this", player),
                    permission.getPermissionLevel() == PermissionLevel.LEADER ? lang("leader", player) : lang("trusted", player), permission.toString()));
        }

        return hasLevel || hasRankPermission;
    }

    /**
     * Gives the player permissions linked to a clan
     *
     */
    public void addClanPermissions(ClanPlayer cp) {
        if (!plugin.getSettingsManager().is(ENABLE_AUTO_GROUPS) || cp == null || permission == null) {
            return;
        }
        Player player = cp.toPlayer();
        if (player == null) {
            return;
        }

        permission.playerRemoveGroup(null, player, "sc_leader");
        permission.playerRemoveGroup(null, player, "sc_trusted");
        permission.playerRemoveGroup(null, player, "sc_untrusted");

        if (cp.getClan() != null) {
            permission.playerAddGroup(null, player, "clan_" + cp.getTag());
            if (cp.isLeader()) {
                permission.playerAddGroup(null, player, "sc_leader");
                return;
            }
            if (cp.isTrusted()) {
                permission.playerAddGroup(null, player, "sc_trusted");
                return;
            }
            permission.playerAddGroup(null, player, "sc_untrusted");
        }
    }

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

        return out;
    }

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

    private <T> @Nullable T getProvider(Class<T> clazz) {
        RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(clazz);
        if (registration != null) {
            return registration.getProvider();
        }
        return null;
    }
}
