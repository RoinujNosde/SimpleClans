package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.google.common.base.Charsets;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.storage.DBCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.MySQLCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.SQLiteCore;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDFetcher;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;

/**
 * @author phaed
 */
public final class StorageManager {

    private SimpleClans plugin;
    private DBCore core;
    private HashMap<String, ChatBlock> chatBlocks = new HashMap<>();
    private Set<Clan> modifiedClans = new HashSet<>();
    private Set<ClanPlayer> modifiedClanPlayers = new HashSet<>();

    /**
     *
     */
    public StorageManager() {
        plugin = SimpleClans.getInstance();
        initiateDB();
        updateDatabase();
        importFromDatabase();
    }

    /**
     * Retrieve a player's pending chat lines
     *
     * @param player
     * @return
     */
    public ChatBlock getChatBlock(Player player) {
    	return chatBlocks.get(player.getUniqueId().toString());
    }

    /**
     * Store pending chat lines for a player
     *
     * @param player
     * @param cb
     */
    public void addChatBlock(CommandSender player, ChatBlock cb) {
		UUID uuid = UUIDMigration.getForcedPlayerUUID(player.getName());

		if (uuid == null) {
			return;
		}

		chatBlocks.put(uuid.toString(), cb);
    }

    /**
     * Initiates the db
     */
    public void initiateDB() {
        if (plugin.getSettingsManager().isUseMysql()) {
            core = new MySQLCore(plugin.getSettingsManager().getHost(), plugin.getSettingsManager().getDatabase(), plugin.getSettingsManager().getPort(), plugin.getSettingsManager().getUsername(), plugin.getSettingsManager().getPassword());

            if (core.checkConnection()) {
                plugin.getLogger().info(plugin.getLang("mysql.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                	plugin.getLogger().info("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ("
                    		+ " `id` bigint(20) NOT NULL auto_increment,"
                    		+ " `verified` tinyint(1) default '0',"
                    		+ " `tag` varchar(25) NOT NULL,"
                    		+ " `color_tag` varchar(25) NOT NULL,"
                    		+ " `name` varchar(100) NOT NULL,"
                    		+ " `description` varchar(255),"
                    		+ " `friendly_fire` tinyint(1) default '0',"
                    		+ " `founded` bigint NOT NULL,"
                    		+ " `last_used` bigint NOT NULL,"
                    		+ " `packed_allies` text NOT NULL,"
                    		+ " `packed_rivals` text NOT NULL,"
                    		+ " `packed_bb` mediumtext NOT NULL,"
                    		+ " `cape_url` varchar(255) NOT NULL,"
                    		+ " `flags` text NOT NULL,"
                    		+ " `balance` double(64,2),"
                    		+ " `fee_enabled` tinyint(1) default '0',"
                    		+ " `fee_value` double(64,2),"
                    		+ " `ranks` text NOT NULL,"
                    		+ " PRIMARY KEY  (`id`),"
                    		+ " UNIQUE KEY `uq_simpleclans_1` (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                	plugin.getLogger().info("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ("
                    		+ " `id` bigint(20) NOT NULL auto_increment,"
                    		+ " `name` varchar(16) NOT NULL,"
                    		+ " `leader` tinyint(1) default '0',"
                    		+ " `tag` varchar(25) NOT NULL,"
                    		+ " `friendly_fire` tinyint(1) default '0',"
                    		+ " `neutral_kills` int(11) default NULL,"
                    		+ " `rival_kills` int(11) default NULL,"
                    		+ " `civilian_kills` int(11) default NULL,"
                    		+ " `deaths` int(11) default NULL,"
                    		+ " `last_seen` bigint NOT NULL,"
                    		+ " `join_date` bigint NOT NULL,"
                    		+ " `trusted` tinyint(1) default '0',"
                    		+ " `flags` text NOT NULL,"
                    		+ " `packed_past_clans` text,"
                    		+ " `resign_times` text,"
                    		+ " PRIMARY KEY  (`id`),"
                    		+ " UNIQUE KEY `uq_sc_players_1` (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                	plugin.getLogger().info("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ("
                    		+ " `kill_id` bigint(20) NOT NULL auto_increment,"
                    		+ " `attacker` varchar(16) NOT NULL,"
                    		+ " `attacker_tag` varchar(16) NOT NULL,"
                    		+ " `victim` varchar(16) NOT NULL,"
                    		+ " `victim_tag` varchar(16) NOT NULL,"
                    		+ " `kill_type` varchar(1) NOT NULL,"
                    		+ " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }
            } else {
                SimpleClans.getInstance().getServer().getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + plugin.getLang("mysql.connection.failed"));
            }
        } else {
            core = new SQLiteCore(plugin.getDataFolder().getPath());

            if (core.checkConnection()) {

            	plugin.getLogger().info(plugin.getLang("sqlite.connection.successful"));

                if (!core.existsTable("sc_clans")) {
                	plugin.getLogger().info("Creating table: sc_clans");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_clans` ("
                    		+ " `id` bigint(20),"
                    		+ " `verified` tinyint(1) default '0',"
                    		+ " `tag` varchar(25) NOT NULL,"
                    		+ " `color_tag` varchar(25) NOT NULL,"
                    		+ " `name` varchar(100) NOT NULL,"
                    		+ " `description` varchar(255),"
                    		+ " `friendly_fire` tinyint(1) default '0',"
                    		+ " `founded` bigint NOT NULL,"
                    		+ " `last_used` bigint NOT NULL,"
                    		+ " `packed_allies` text NOT NULL,"
                    		+ " `packed_rivals` text NOT NULL,"
                    		+ " `packed_bb` mediumtext NOT NULL,"
                    		+ " `cape_url` varchar(255) NOT NULL,"
                    		+ " `flags` text NOT NULL,"
                    		+ " `balance` double(64,2) default 0.0,"
                    		+ " `fee_enabled` tinyint(1) default '0',"
                    		+ " `fee_value` double(64,2),"
                    		+ " `ranks` text NOT NULL,"
                    		+ "  PRIMARY KEY  (`id`),"
                    		+ " UNIQUE (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_players")) {
                	plugin.getLogger().info("Creating table: sc_players");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_players` ("
                    		+ " `id` bigint(20),"
                    		+ " `name` varchar(16) NOT NULL,"
                    		+ " `leader` tinyint(1) default '0',"
                    		+ " `tag` varchar(25) NOT NULL,"
                    		+ " `friendly_fire` tinyint(1) default '0',"
                    		+ " `neutral_kills` int(11) default NULL,"
                    		+ " `rival_kills` int(11) default NULL,"
                    		+ " `civilian_kills` int(11) default NULL,"
                    		+ " `deaths` int(11) default NULL,"
                    		+ " `last_seen` bigint NOT NULL,"
                    		+ " `join_date` bigint NOT NULL,"
                    		+ " `trusted` tinyint(1) default '0',"
                    		+ " `flags` text NOT NULL,"
                    		+ " `packed_past_clans` text,"
                    		+ " `resign_times` text,"
                    		+ " PRIMARY KEY  (`id`),"
                    		+ " UNIQUE (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable("sc_kills")) {
                	plugin.getLogger().info("Creating table: sc_kills");

                    String query = "CREATE TABLE IF NOT EXISTS `sc_kills` ("
                    		+ " `kill_id` bigint(20),"
                    		+ " `attacker` varchar(16) NOT NULL,"
                    		+ " `attacker_tag` varchar(16) NOT NULL,"
                    		+ " `victim` varchar(16) NOT NULL,"
                    		+ " `victim_tag` varchar(16) NOT NULL,"
                    		+ " `kill_type` varchar(1) NOT NULL,"
                    		+ " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }
            } else {
                SimpleClans.getInstance().getServer().getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + plugin.getLang("sqlite.connection.failed"));
            }
        }
    }

    /**
     * Closes DB connection
     */
    public void closeConnection() {
        core.close();
    }

    /**
     * Import all data from database to memory
     */
    public void importFromDatabase() {
        plugin.getClanManager().cleanData();

        List<Clan> clans = retrieveClans();
        purgeClans(clans);

        for (Clan clan : clans) {
            plugin.getClanManager().importClan(clan);
        }

        for (Clan clan : clans) {
            clan.validateWarring();
        }

        if (!clans.isEmpty()) {
        	plugin.getLogger().info(MessageFormat.format(plugin.getLang("clans"), clans.size()));
        }

        List<ClanPlayer> cps = retrieveClanPlayers();
        purgeClanPlayers(cps);

        for (ClanPlayer cp : cps) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            plugin.getClanManager().importClanPlayer(cp);
        }

        if (!cps.isEmpty()) {
        	plugin.getLogger().info(MessageFormat.format(plugin.getLang("clan.players"), cps.size()));
        }
    }

    /**
     * Import one ClanPlayer data from database to memory
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param player
     */
    public void importFromDatabaseOnePlayer(Player player) {
        plugin.getClanManager().deleteClanPlayerFromMemory(player.getUniqueId());

        ClanPlayer cp = retrieveOneClanPlayer(player.getUniqueId());

        if (cp != null) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            plugin.getClanManager().importClanPlayer(cp);
            
            plugin.getLogger().info("ClanPlayer Reloaded: " + player.getName() + ", UUID: " + player.getUniqueId().toString());
        }
    }

    private void purgeClans(List<Clan> clans) {
        List<Clan> purge = new ArrayList<>();

        for (Clan clan : clans) {
            if (clan.isVerified()) {
                if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeClan()) {
                    purge.add(clan);
                }
            } else {
                if (clan.getInactiveDays() > plugin.getSettingsManager().getPurgeUnverified()) {
                    purge.add(clan);
                }
            }
        }

        for (Clan clan : purge) {
        	plugin.getLogger().info(MessageFormat.format(plugin.getLang("purging.clan"), clan.getName()));
            deleteClan(clan);
            clans.remove(clan);
        }
    }

    private void purgeClanPlayers(List<ClanPlayer> cps) {
        List<ClanPlayer> purge = new ArrayList<>();

        for (ClanPlayer cp : cps) {
            if (cp.getInactiveDays() > plugin.getSettingsManager().getPurgePlayers() && !cp.isLeader()) {
                purge.add(cp);
            }
        }

        for (ClanPlayer cp : purge) {
        	plugin.getLogger().info(MessageFormat.format(plugin.getLang("purging.player.data"), cp.getName()));
            deleteClanPlayer(cp);
            cps.remove(cp);
        }
    }

    /**
     * Retrieves all simple clans from the database
     *
     * @return
     */
    public List<Clan> retrieveClans() {
        List<Clan> out = new ArrayList<>();

        String query = "SELECT * FROM  `sc_clans`;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        boolean verified = res.getBoolean("verified");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        String tag = res.getString("tag");
                        String color_tag = Helper.parseColors(res.getString("color_tag"));
                        String name = res.getString("name");
                        String description = res.getString("description");
                        String packed_allies = res.getString("packed_allies");
                        String packed_rivals = res.getString("packed_rivals");
                        String packed_bb = res.getString("packed_bb");
                        String cape_url = res.getString("cape_url");
                        String flags = res.getString("flags");
                        String ranks = res.getString("ranks");
                        long founded = res.getLong("founded");
                        long last_used = res.getLong("last_used");
                        double balance = res.getDouble("balance");
                        double feeValue = res.getDouble("fee_value");
                        boolean feeEnabled = res.getBoolean("fee_enabled");

                        if (founded == 0) {
                            founded = (new Date()).getTime();
                        }

                        if (last_used == 0) {
                            last_used = (new Date()).getTime();
                        }

                        Clan clan = new Clan();
                        clan.setFlags(flags);
                        clan.setVerified(verified);
                        clan.setFriendlyFire(friendly_fire);
                        clan.setTag(tag);
                        clan.setColorTag(color_tag);
                        clan.setName(name);
                        clan.setDescription(description);
                        clan.setPackedAllies(packed_allies);
                        clan.setPackedRivals(packed_rivals);
                        clan.setPackedBb(packed_bb);
                        clan.setCapeUrl(cape_url);
                        clan.setFounded(founded);
                        clan.setLastUsed(last_used);
                        clan.setBalance(balance);
                        clan.setMemberFee(feeValue);
                        clan.setMemberFeeEnabled(feeEnabled);
                        clan.setRanks(Helper.ranksFromJson(ranks));

                        out.add(clan);
                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print(el.toString());
                        }
                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Retrieves one Clan from the database
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param tagClan
     * @return
     */
    public Clan retrieveOneClan(String tagClan) {
        Clan out = null;

        String query = "SELECT * FROM  `sc_clans` WHERE `tag` = '" + tagClan + "';";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        boolean verified = res.getBoolean("verified");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        String tag = res.getString("tag");
                        String color_tag = Helper.parseColors(res.getString("color_tag"));
                        String name = res.getString("name");
                        String description = res.getString("description");
                        String packed_allies = res.getString("packed_allies");
                        String packed_rivals = res.getString("packed_rivals");
                        String packed_bb = res.getString("packed_bb");
                        String cape_url = res.getString("cape_url");
                        String flags = res.getString("flags");
                        String ranks = res.getString("ranks");
                        long founded = res.getLong("founded");
                        long last_used = res.getLong("last_used");
                        double balance = res.getDouble("balance");
                        double feeValue = res.getDouble("fee_value");
                        boolean feeEnabled = res.getBoolean("fee_enabled");

                        if (founded == 0) {
                            founded = (new Date()).getTime();
                        }

                        if (last_used == 0) {
                            last_used = (new Date()).getTime();
                        }

                        Clan clan = new Clan();
                        clan.setFlags(flags);
                        clan.setVerified(verified);
                        clan.setFriendlyFire(friendly_fire);
                        clan.setTag(tag);
                        clan.setColorTag(color_tag);
                        clan.setName(name);
                        clan.setDescription(description);
                        clan.setPackedAllies(packed_allies);
                        clan.setPackedRivals(packed_rivals);
                        clan.setPackedBb(packed_bb);
                        clan.setCapeUrl(cape_url);
                        clan.setFounded(founded);
                        clan.setLastUsed(last_used);
                        clan.setBalance(balance);
                        clan.setMemberFee(feeValue);
                        clan.setMemberFeeEnabled(feeEnabled);
                        clan.setRanks(Helper.ranksFromJson(ranks));

                        out = clan;
                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print(el.toString());
                        }
                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Retrieves all clan players from the database
     *
     * @return
     */
    public List<ClanPlayer> retrieveClanPlayers() {
        List<ClanPlayer> out = new ArrayList<>();

        String query = "SELECT * FROM  `sc_players`;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String uuid = res.getString("uuid");
                        String name = res.getString("name");
                        String tag = res.getString("tag");
                        boolean leader = res.getBoolean("leader");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        boolean trusted = res.getBoolean("trusted");
                        int neutral_kills = res.getInt("neutral_kills");
                        int rival_kills = res.getInt("rival_kills");
                        int civilian_kills = res.getInt("civilian_kills");
                        int deaths = res.getInt("deaths");
                        long last_seen = res.getLong("last_seen");
                        long join_date = res.getLong("join_date");
                        String flags = res.getString("flags");
                        String packed_past_clans = Helper.parseColors(res.getString("packed_past_clans"));
                        String resign_times = res.getString("resign_times");
                        
                        if (last_seen == 0) {
                            last_seen = (new Date()).getTime();
                        }

                        if (join_date == 0) {
                            join_date = (new Date()).getTime();
                        }

                        ClanPlayer cp = new ClanPlayer();
                        if (uuid != null) {
                            cp.setUniqueId(UUID.fromString(uuid));
                        }
                        cp.setFlags(flags);
                        cp.setName(name);
                        cp.setLeader(leader);
                        cp.setFriendlyFire(friendly_fire);
                        cp.setNeutralKills(neutral_kills);
                        cp.setRivalKills(rival_kills);
                        cp.setCivilianKills(civilian_kills);
                        cp.setDeaths(deaths);
                        cp.setLastSeen(last_seen);
                        cp.setJoinDate(join_date);
                        cp.setPackedPastClans(packed_past_clans);
                        cp.setTrusted(leader || trusted);
                        cp.setResignTimes(Helper.resignTimesFromJson(resign_times));

                        if (!tag.isEmpty()) {
                            Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);

                            if (clan != null) {
                                cp.setClan(clan);
                            }
                        }

                        out.add(cp);
                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print(el.toString());
                        }
                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Retrieves one clan player from the database
     * Used for BungeeCord Reload ClanPlayer and your Clan
     *
     * @param playerUniqueId
     * @return
     */
    public ClanPlayer retrieveOneClanPlayer(UUID playerUniqueId) {
        ClanPlayer out = null;

        String query = "SELECT * FROM `sc_players` WHERE `uuid` = '" + playerUniqueId.toString() + "';";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String uuid = res.getString("uuid");
                        String name = res.getString("name");
                        String tag = res.getString("tag");
                        boolean leader = res.getBoolean("leader");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        boolean trusted = res.getBoolean("trusted");
                        int neutral_kills = res.getInt("neutral_kills");
                        int rival_kills = res.getInt("rival_kills");
                        int civilian_kills = res.getInt("civilian_kills");
                        int deaths = res.getInt("deaths");
                        long last_seen = res.getLong("last_seen");
                        long join_date = res.getLong("join_date");
                        String flags = res.getString("flags");
                        String packed_past_clans = Helper.parseColors(res.getString("packed_past_clans"));
                        String resign_times = res.getString("resign_times");
                        
                        if (last_seen == 0) {
                            last_seen = (new Date()).getTime();
                        }

                        if (join_date == 0) {
                            join_date = (new Date()).getTime();
                        }

                        ClanPlayer cp = new ClanPlayer();
                        if (uuid != null) {
                            cp.setUniqueId(UUID.fromString(uuid));
                        }
                        cp.setFlags(flags);
                        cp.setName(name);
                        cp.setLeader(leader);
                        cp.setFriendlyFire(friendly_fire);
                        cp.setNeutralKills(neutral_kills);
                        cp.setRivalKills(rival_kills);
                        cp.setCivilianKills(civilian_kills);
                        cp.setDeaths(deaths);
                        cp.setLastSeen(last_seen);
                        cp.setJoinDate(join_date);
                        cp.setPackedPastClans(packed_past_clans);
                        cp.setTrusted(leader || trusted);
                        cp.setResignTimes(Helper.resignTimesFromJson(resign_times));
                        
                        if (!tag.isEmpty()) {
                            Clan clanDB = retrieveOneClan(tag);
                            Clan clan = SimpleClans.getInstance().getClanManager().getClan(tag);

                            if (clan != null) {
                                Clan clanReSync = SimpleClans.getInstance().getClanManager().getClan(tag);
                                clanReSync.setFlags(clanDB.getFlags());
                                clanReSync.setVerified(clanDB.isVerified());
                                clanReSync.setFriendlyFire(clanDB.isFriendlyFire());
                                clanReSync.setTag(clanDB.getTag());
                                clanReSync.setColorTag(clanDB.getColorTag());
                                clanReSync.setName(clanDB.getName());
                                clanReSync.setPackedAllies(clanDB.getPackedAllies());
                                clanReSync.setPackedRivals(clanDB.getPackedRivals());
                                clanReSync.setPackedBb(clanDB.getPackedBb());
                                clanReSync.setCapeUrl(clanDB.getCapeUrl());
                                clanReSync.setFounded(clanDB.getFounded());
                                clanReSync.setLastUsed(clanDB.getLastUsed());
                                clanReSync.setBalance(clanDB.getBalance());
                                cp.setClan(clanReSync);
                            } else {
                                plugin.getClanManager().importClan(clanDB);
                                clanDB.validateWarring();
                                Clan newclan = SimpleClans.getInstance().getClanManager().getClan(clanDB.getTag());
                                cp.setClan(newclan);
                            }
                        }

                        out = cp;
                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print(el.toString());
                        }
                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Insert a clan into the database
     *
     * @param clan
     */
    public void insertClan(Clan clan) {
        String query = "INSERT INTO `sc_clans` (`ranks`, `description`, `fee_enabled`, `fee_value`, `verified`, `tag`, `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, `packed_bb`, `cape_url`, `flags`, `balance`) ";
        String values = "VALUES ( '"
        							+ Helper.escapeQuotes(Helper.ranksToJson(clan.getRanks())) + "','"
        							+ Helper.escapeQuotes(clan.getDescription())+ "'," 
        							+ (clan.isMemberFeeEnabled() ? 1 : 0) +","
        							+ Helper.escapeQuotes(String.valueOf(clan.getMemberFee())) + "," 
        							+ (clan.isVerified() ? 1 : 0) + ",'" 
        							+ Helper.escapeQuotes(clan.getTag()) + "','" 
        							+ Helper.escapeQuotes(clan.getColorTag()) + "','" 
        							+ Helper.escapeQuotes(clan.getName()) + "'," 
        							+ (clan.isFriendlyFire() ? 1 : 0) + ",'" 
        							+ clan.getFounded() + "','" 
        							+ clan.getLastUsed() + "','" 
        							+ Helper.escapeQuotes(clan.getPackedAllies()) + "','" 
        							+ Helper.escapeQuotes(clan.getPackedRivals()) + "','" 
        							+ Helper.escapeQuotes(clan.getPackedBb()) + "','" 
        							+ Helper.escapeQuotes(clan.getCapeUrl()) + "','" 
        							+ Helper.escapeQuotes(clan.getFlags()) + "','" 
        							+ Helper.escapeQuotes(String.valueOf(clan.getBalance())) + "');";
        core.insert(query + values);
    }

    /**
     * Update a clan to the database asynchronously
     *
     * @param clan
     */
    @Deprecated
    public void updateClanAsync(final Clan clan) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updateClan(clan);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Change the name of a player in the database asynchronously
     *
     * @param p to update
     */
    public void updatePlayerNameAsync(final Player p) {
    	new BukkitRunnable() {
			@Override
			public void run() {
                updatePlayerName(p);
			}
		}.runTaskAsynchronously(plugin);
    }

    /**
     * Change the name of a player in the database
     *
     * @param p to update
     */
    public void updatePlayerName(final Player p) {
        String query = "UPDATE `sc_players` SET `name` = '" + p.getName() + "' WHERE uuid = '" + p.getUniqueId().toString() + "';";
        core.update(query);
    }

    /**
     * Update a clan to the database
     *
     * @param clan
     */
    public void updateClan(Clan clan) {
        updateClan(clan, true);
    }
    
    /**
     * Update a clan to the database
     *
     * @param clan clan to update
     *
     * @param updateLastUsed should the clan's last used time be updated as well?
     */
    public void updateClan(Clan clan, boolean updateLastUsed) {
        if (updateLastUsed) {
            clan.updateLastUsed();
        }
    	if (plugin.getSettingsManager().isSavePeriodically()) {
    		modifiedClans.add(clan);
    		return;
    	}
        core.update(getUpdateClanQuery(clan));
    }
    
    private String getUpdateClanQuery(Clan clan) {
        String query = "UPDATE `sc_clans` SET ranks = '"+ Helper.escapeQuotes(Helper.ranksToJson(clan.getRanks())) +"', description = '" + Helper.escapeQuotes(clan.getDescription())+ "', fee_enabled = "+ (clan.isMemberFeeEnabled() ? 1 : 0) +", fee_value = '" + clan.getMemberFee() + "', verified = " + (clan.isVerified() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(clan.getTag()) + "', color_tag = '" + Helper.escapeQuotes(clan.getColorTag()) + "', name = '" + Helper.escapeQuotes(clan.getName()) + "', friendly_fire = " + (clan.isFriendlyFire() ? 1 : 0) + ", founded = '" + clan.getFounded() + "', last_used = '" + clan.getLastUsed() + "', packed_allies = '" + Helper.escapeQuotes(clan.getPackedAllies()) + "', packed_rivals = '" + Helper.escapeQuotes(clan.getPackedRivals()) + "', packed_bb = '" + Helper.escapeQuotes(clan.getPackedBb()) + "', cape_url = '" + Helper.escapeQuotes(clan.getCapeUrl()) + "', cape_url = '" + Helper.escapeQuotes(String.valueOf(clan.getCapeUrl())) + "', balance = '" + clan.getBalance() + "', flags = '" + Helper.escapeQuotes(clan.getFlags()) + "' WHERE tag = '" + Helper.escapeQuotes(clan.getTag()) + "';";
        return query;
    }

    /**
     * Delete a clan from the database
     *
     * @param clan
     */
    public void deleteClan(Clan clan) {
        String query = "DELETE FROM `sc_clans` WHERE tag = '" + clan.getTag() + "';";
        core.delete(query);
    }

    /**
     * Insert a clan player into the database
     *
     * @param cp
     */
    public void insertClanPlayer(ClanPlayer cp) {
    	String query = "INSERT INTO `sc_players` (`resign_times`, `uuid`, `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, `rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
        String values = "VALUES ( '"+ Helper.escapeQuotes(Helper.resignTimesToJson(cp.getResignTimes()))+ "', '" + cp.getUniqueId().toString() + "', '" + cp.getName() + "'," + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "'," + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills() + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' " + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','" + Helper.escapeQuotes(cp.getFlags()) + "');";
        core.insert(query + values);
    }

    /**
     * Update a clan player to the database asynchronously
     *
     * @param cp
     */
    @Deprecated
    public void updateClanPlayerAsync(final ClanPlayer cp) {
    	new BukkitRunnable() {
			@Override
			public void run() {
                updateClanPlayer(cp);
			}
		}.runTaskAsynchronously(plugin);
    }

    /**
     * Update a clan player to the database
     *
     * @param cp
     */
    public void updateClanPlayer(ClanPlayer cp) {
        cp.updateLastSeen();
        if (plugin.getSettingsManager().isSavePeriodically()) {
        	modifiedClanPlayers.add(cp);
        	return;
        }
        core.update(getUpdateClanPlayerQuery(cp));
    }
    
    private String getUpdateClanPlayerQuery(ClanPlayer cp) {
        String query = "UPDATE `sc_players` SET resign_times = '"+ Helper.escapeQuotes(Helper.resignTimesToJson(cp.getResignTimes())) +"', leader = " + (cp.isLeader() ? 1 : 0) + ", tag = '" + Helper.escapeQuotes(cp.getTag()) + "' , friendly_fire = " + (cp.isFriendlyFire() ? 1 : 0) + ", neutral_kills = " + cp.getNeutralKills() + ", rival_kills = " + cp.getRivalKills() + ", civilian_kills = " + cp.getCivilianKills() + ", deaths = " + cp.getDeaths() + ", last_seen = '" + cp.getLastSeen() + "', packed_past_clans = '" + Helper.escapeQuotes(cp.getPackedPastClans()) + "', trusted = " + (cp.isTrusted() ? 1 : 0) + ", flags = '" + Helper.escapeQuotes(cp.getFlags()) + "', name = '" + cp.getName() + "' WHERE `uuid` = '" + cp.getUniqueId().toString() + "';";
        return query;
    }

    /**
     * Delete a clan player from the database
     *
     * @param cp
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        final Clan clan = cp.getClan();
        if (clan != null) {
            clan.addBbWithoutSaving(ChatColor.AQUA + MessageFormat.format(plugin.getLang("has.been.purged"), cp.getName()));
            updateClan(clan, false);
        }
		String query = "DELETE FROM `sc_players` WHERE uuid = '" + cp.getUniqueId() + "';";
		core.delete(query);
		deleteKills(cp.getUniqueId());
    }
    
    /**
     * Insert a kill into the database
     *
     * @param attacker
     * @param attackerTag
     * @param victim
     * @param victimTag
     * @param type
     */
    @Deprecated
    public void insertKill(Player attacker, String attackerTag, Player victim, String victimTag, String type) {
    	String query = "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, `victim`, `victim_tag`, `kill_type`) ";
    	String values = "VALUES ( '" + attacker.getUniqueId() + "','" + attacker.getName() + "','" + attackerTag + "','" + victim.getUniqueId() + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
    	core.insert(query + values);       
    }

    /**
     * Insert a kill into the database
     *
     * @param attacker the attacker
     * @param victim the victim
     * @param type the kill type
     */
    public void insertKill(@NotNull ClanPlayer attacker, @NotNull ClanPlayer victim, @NotNull String type) {
        String query = "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, " +
                "`victim`, `victim_tag`, `kill_type`) ";
        String values = "VALUES ( '" + attacker.getUniqueId() + "','" + attacker.getName() + "','" + attacker.getTag()
                + "','" + victim.getUniqueId() + "','" + victim.getName() + "','" + victim.getTag() + "','" + type + "');";
        core.insert(query + values);
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerName
     */
    @Deprecated
    public void deleteKills(String playerName) {
        String query = "DELETE FROM `sc_kills` WHERE `attacker` = '" + playerName + "'";
        core.delete(query);
    }

    /**
     * Delete a player's kill record form the database
     *
     * @param playerUniqueId
     */
    public void deleteKills(UUID playerUniqueId) {
        String query = "DELETE FROM `sc_kills` WHERE `attacker_uuid` = '" + playerUniqueId + "'";
        core.delete(query);
    }

    /**
     * Returns a map of victim->count of all kills that specific player did
     *
     * @param playerName
     * @return
     */
    public Map<String, Integer> getKillsPerPlayer(String playerName) {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT victim, count(victim) AS kills FROM `sc_kills` WHERE attacker = '" + playerName + "' GROUP BY victim ORDER BY count(victim) DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(victim, kills);
                    } catch (Exception ex) {
                        plugin.getLogger().info(ex.getMessage());


                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Returns a map of tag->count of all kills
     *
     * @return
     */
    public Map<String, Integer> getMostKilled() {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT attacker, victim, count(victim) AS kills FROM `sc_kills` GROUP BY attacker, victim ORDER BY 3 DESC;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        String attacker = res.getString("attacker");
                        String victim = res.getString("victim");
                        int kills = res.getInt("kills");
                        out.put(attacker + " " + victim, kills);
                    } catch (Exception ex) {
                        plugin.getLogger().info(ex.getMessage());


                    }
                }
            } catch (SQLException ex) {
                plugin.getLogger().severe(String.format("An Error occurred: %s", ex.getErrorCode()));
                plugin.getLogger().log(Level.SEVERE, null, ex);
            }
        }

        return out;
    }

    /**
     * Gets, asynchronously, a map of tag->count of all kills and notifies via callback when it's ready
     * 
     * @param callback
     */
    public void getMostKilled(DataCallback<Map<String, Integer>> callback) {
    	new BukkitRunnable() {
			
			@Override
			public void run() {
				callback.onResultReady(getMostKilled());
			}
		}.runTaskAsynchronously(plugin);
    }
    
    /**
     * Gets, asynchronously, a map of victim->count of all kills that specific player did and notifies via callback when it's ready
     * 
     * @param playerName
     * @param callback
     */
    public void getKillsPerPlayer(final String playerName, final DataCallback<Map<String, Integer>> callback) {
    	new BukkitRunnable() {
			@Override
			public void run() {
				callback.onResultReady(getKillsPerPlayer(playerName));
			}
		}.runTaskAsynchronously(plugin);
    }
    
    /**
     * Callback that returns some data
     * 
     * @author roinujnosde
     *
     */
    public interface DataCallback<T> {
    	/**
    	 * Notifies when the result is ready
    	 * 
    	 * @param data
    	 */
    	void onResultReady(T data);
    }
    
    /**
     * Updates the database to the latest version
     *
     * @param
     */
    private void updateDatabase() {
        String query = null;

        /**
         * From 2.2.6.3 to 2.3
         */
        if (!core.existsColumn("sc_clans", "balance")) {
            query = "ALTER TABLE sc_clans ADD COLUMN `balance` double(64,2);";
            core.execute(query);
        }
        
        /**
         * From 2.7.16 to 2.7.17
         */
        if (!core.existsColumn("sc_clans", "fee_enabled")) {
            query = "ALTER TABLE sc_clans ADD COLUMN `fee_enabled` tinyint(1) default '0';";
            core.execute(query);
        }
        if (!core.existsColumn("sc_clans", "fee_value")) {
            query = "ALTER TABLE sc_clans ADD COLUMN `fee_value` double(64,2);";
            core.execute(query);
        }
        
        /**
         * From 2.7.21 to 2.7.22
         */
        if (!core.existsColumn("sc_clans", "description")) {
        	query = "ALTER TABLE sc_clans ADD COLUMN `description` varchar(255);";
        	core.execute(query);
        }
        
        /**
         * From 2.7.22 to 2.7.23
         */
        if (!core.existsColumn("sc_players", "resign_times")) {
        	query = "ALTER TABLE sc_players ADD COLUMN `resign_times` text;";
        	core.execute(query);
        }
        
        /**
         * From 2.8.2 to 2.9
         */
        if (!core.existsColumn("sc_clans", "ranks")) {
        	query = "ALTER TABLE sc_clans ADD COLUMN `ranks` text;";
        	core.execute(query);
        }

        /**
         * Bukkit 1.7.5+ UUID Migration
         */
        if (!core.existsColumn("sc_kills", "attacker_uuid")) {
            query = "ALTER TABLE sc_kills ADD attacker_uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);
        }
        if (!core.existsColumn("sc_kills", "victim_uuid")) {
            query = "ALTER TABLE sc_kills ADD victim_uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);
        }
        if (!core.existsColumn("sc_players", "uuid")) {
            query = "ALTER TABLE sc_players ADD uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);

            if (plugin.getSettingsManager().isUseMysql()) {
                query = "ALTER TABLE `sc_players` ADD UNIQUE `uq_player_uuid` (`uuid`);";
                core.execute(query);
            }

            updatePlayersToUUID();

            query = "ALTER TABLE sc_players DROP INDEX uq_sc_players_1;";
            core.execute(query);
        }

        if (core.existsColumn("sc_players", "uuid") && !plugin.getSettingsManager().isUseMysql()) {
            query = "CREATE UNIQUE INDEX IF NOT EXISTS `uq_player_uuid` ON `sc_players` (`uuid`);";
            core.execute(query);
        }
    }

    /**
     * Updates the database to the latest version
     *
     * @param
     */
    
	private void updatePlayersToUUID() {
		plugin.getLogger().log(Level.WARNING, "Starting Migration to UUID Players !");
		plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DONT STOP BUKKIT ! ==================== ");
		plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DONT STOP BUKKIT ! ==================== ");
		plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DONT STOP BUKKIT ! ==================== ");
        SimpleClans.getInstance().setUUID(false);
        List<ClanPlayer> cps = retrieveClanPlayers();

        int i = 1;
        for (ClanPlayer cp : cps) {
            try {
                UUID uuidPlayer;
                if (SimpleClans.getInstance().getServer().getOnlineMode()) {
                    uuidPlayer = UUIDFetcher.getUUIDOfThrottled(cp.getName());
                } else {
                    uuidPlayer = UUID.nameUUIDFromBytes(("OfflinePlayer:" + cp.getName()).getBytes(Charsets.UTF_8));
                }
                String query = "UPDATE `sc_players` SET uuid = '" + uuidPlayer.toString() + "' WHERE name = '" + cp.getName() + "';";
                core.update(query);

                String query2 = "UPDATE `sc_kills` SET attacker_uuid = '" + uuidPlayer.toString() + "' WHERE attacker = '" + cp.getName() + "';";
                core.update(query2);

                String query3 = "UPDATE `sc_kills` SET victim_uuid = '" + uuidPlayer.toString() + "' WHERE victim = '" + cp.getName() + "';";
                core.update(query3);
                plugin.getLogger().info("[" + i + " / " + cps.size() + "] Success: " + cp.getName() + "; UUID: " + uuidPlayer.toString());
            } catch (Exception ex) {
            	plugin.getLogger().log(Level.WARNING, "[" + i + " / " + cps.size() + "] Failed [ERRO]: " + cp.getName() + "; UUID: ???");
            }
            i++;
        }
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");


        if (!cps.isEmpty()) {
        	plugin.getLogger().info(MessageFormat.format(plugin.getLang("clan.players"), cps.size()));
        }
        SimpleClans.getInstance().setUUID(true);
    }

	
	/**
	 * Saves modified Clans and ClanPlayers to the database
	 * 
	 * @author RoinujNosde
	 * @since 2.10.2
	 */
	public void saveModified() {
		try (Statement statement = core.getConnection().createStatement()) {
			List<Clan> allClans = plugin.getClanManager().getClans();
			for (Clan clan : modifiedClans) {
				if (!allClans.contains(clan)) {
					continue;
				}
				statement.addBatch(getUpdateClanQuery(clan));
			}
			List<ClanPlayer> allClanPlayers = plugin.getClanManager().getAllClanPlayers();
			for (ClanPlayer cp : modifiedClanPlayers) {
				if (!allClanPlayers.contains(cp)) {
					continue;
				}
				statement.addBatch(getUpdateClanPlayerQuery(cp));
			}
			statement.executeBatch();
			
			modifiedClans.clear();
			modifiedClanPlayers.clear();
		} catch (SQLException e) {
            plugin.getLogger().severe("Error saving modified Clans and ClanPlayers:");
			e.printStackTrace();
		}
	}
}
