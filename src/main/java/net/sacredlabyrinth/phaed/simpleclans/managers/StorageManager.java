package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.google.common.base.Charsets;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.chest.ClanChest;
import net.sacredlabyrinth.phaed.simpleclans.chest.LockResult;
import net.sacredlabyrinth.phaed.simpleclans.chest.LockStatus;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.storage.DBCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.MySQLCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.SQLiteCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.TransactionRunnable;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.YAMLSerializer;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDFetcher;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Date;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * @author phaed
 */
public final class StorageManager {

    private final SimpleClans plugin;
    private DBCore core;
    private final HashMap<String, ChatBlock> chatBlocks = new HashMap<>();
    private final Set<Clan> modifiedClans = new HashSet<>();
    private final Set<ClanPlayer> modifiedClanPlayers = new HashSet<>();

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
     * @param player the Player
     * @return the ChatBlock
     */
    public ChatBlock getChatBlock(Player player) {
        return chatBlocks.get(player.getName());
    }

    /**
     * Store pending chat lines for a player
     */
    public void addChatBlock(CommandSender player, ChatBlock cb) {
        chatBlocks.put(player.getName(), cb);
    }

    /**
     * Initiates the db
     */
    public void initiateDB() {
        SettingsManager settings = plugin.getSettingsManager();
        if (settings.is(MYSQL_ENABLE)) {
            core = new MySQLCore(settings.getString(MYSQL_HOST), settings.getString(MYSQL_DATABASE), settings.getInt(MYSQL_PORT), settings.getString(MYSQL_USERNAME), settings.getString(MYSQL_PASSWORD));

            if (core.checkConnection()) {
                plugin.getLogger().info(lang("mysql.connection.successful"));

                if (!core.existsTable(getPrefixedTable("clans"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("clans"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("clans") + "` ("
                            + " `id` bigint(20) NOT NULL auto_increment,"
                            + " `verified` tinyint(1) default '0',"
                            + " `tag` varchar(25) NOT NULL,"
                            + " `color_tag` varchar(255) NOT NULL,"
                            + " `name` varchar(100) NOT NULL,"
                            + " `description` varchar(255),"
                            + " `friendly_fire` tinyint(1) default '0',"
                            + " `founded` bigint NOT NULL,"
                            + " `last_used` bigint NOT NULL,"
                            + " `packed_allies` text NOT NULL,"
                            + " `packed_rivals` text NOT NULL,"
                            + " `packed_bb` mediumtext NOT NULL,"
                            + " `cape_url` varchar(255) NOT NULL,"
                            + " `flags` text NOT NULL," + " `balance` double(64,2),"
                            + " `fee_enabled` tinyint(1) default '0',"
                            + " `fee_value` double(64,2),"
                            + " `ranks` text NOT NULL,"
                            + " `banner` text,"
                            + " `chest_content` BLOB,"
                            + " PRIMARY KEY  (`id`),"
                            + " UNIQUE KEY `uq_simpleclans_1` (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("players"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("players"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("players") + "` ("
                            + " `id` bigint(20) NOT NULL auto_increment,"
                            + " `name` varchar(16) NOT NULL,"
                            + " `leader` tinyint(1) default '0',"
                            + " `tag` varchar(25) NOT NULL,"
                            + " `friendly_fire` tinyint(1) default '0',"
                            + " `neutral_kills` int(11) default NULL,"
                            + " `rival_kills` int(11) default NULL,"
                            + " `civilian_kills` int(11) default NULL,"
                            + " `ally_kills` int(11) default NULL,"
                            + " `deaths` int(11) default NULL,"
                            + " `last_seen` bigint NOT NULL,"
                            + " `join_date` bigint NOT NULL,"
                            + " `trusted` tinyint(1) default '0',"
                            + " `flags` text NOT NULL,"
                            + " `packed_past_clans` text,"
                            + " `resign_times` text,"
                            + " `locale` varchar(10),"
                            + " PRIMARY KEY  (`id`),"
                            + " UNIQUE KEY `uq_sc_players_1` (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("kills"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("kills"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("kills") + "` ("
                            + " `kill_id` bigint(20) NOT NULL auto_increment,"
                            + " `attacker` varchar(16) NOT NULL,"
                            + " `attacker_tag` varchar(16) NOT NULL,"
                            + " `victim` varchar(16) NOT NULL,"
                            + " `victim_tag` varchar(16) NOT NULL,"
                            + " `kill_type` varchar(1) NOT NULL,"
                            + " `created_at` datetime NULL,"
                            + " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("chest_locks"))) {
                    String sql = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("chest_locks") + "` ("
                            + " `id` bigint(20) NOT NULL AUTO_INCREMENT,"
                            + " `clan_tag` varchar(64) NOT NULL,"
                            + " `server_name` varchar(64) NOT NULL,"
                            + " `locked_by` varchar(36) NOT NULL,"
                            + " `lock_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                            + " PRIMARY KEY (`id`),"
                            + " INDEX idx_server_name (`server_name`)"
                            + ");";

                    core.execute(sql);
                }
            } else {
                plugin.getServer().getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + lang("mysql.connection.failed"));
            }
        } else {
            core = new SQLiteCore(plugin.getDataFolder().getPath());

            if (core.checkConnection()) {

                plugin.getLogger().info(lang("sqlite.connection.successful"));

                if (!core.existsTable(getPrefixedTable("clans"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("clans"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("clans") + "` ("
                            + " `id` bigint(20),"
                            + " `verified` tinyint(1) default '0',"
                            + " `tag` varchar(25) NOT NULL,"
                            + " `color_tag` varchar(255) NOT NULL,"
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
                            + " `banner` text,"
                            + " `chest_content` BLOB,"
                            + "  PRIMARY KEY  (`id`),"
                            + " UNIQUE (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("players"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("players"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("players") + "` ("
                            + " `id` bigint(20)," + " `name` varchar(16) NOT NULL,"
                            + " `leader` tinyint(1) default '0'," + " `tag` varchar(25) NOT NULL,"
                            + " `friendly_fire` tinyint(1) default '0',"
                            + " `neutral_kills` int(11) default NULL,"
                            + " `rival_kills` int(11) default NULL,"
                            + " `civilian_kills` int(11) default NULL,"
                            + " `ally_kills` int(11) default NULL,"
                            + " `deaths` int(11) default NULL,"
                            + " `last_seen` bigint NOT NULL,"
                            + " `join_date` bigint NOT NULL,"
                            + " `trusted` tinyint(1) default '0',"
                            + " `flags` text NOT NULL,"
                            + " `packed_past_clans` text,"
                            + " `resign_times` text,"
                            + " `locale` varchar(10),"
                            + " PRIMARY KEY  (`id`)," + " UNIQUE (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("kills"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("kills"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("kills") + "` ("
                            + " `kill_id` bigint(20),"
                            + " `attacker` varchar(16) NOT NULL,"
                            + " `attacker_tag` varchar(16) NOT NULL,"
                            + " `victim` varchar(16) NOT NULL,"
                            + " `victim_tag` varchar(16) NOT NULL,"
                            + " `kill_type` varchar(1) NOT NULL,"
                            + " `created_at` datetime NULL,"
                            + " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("chest_locks"))) {
                    String sql = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("chest_locks") + "` ("
                            + " `id` integer primary key autoincrement,"
                            + " `clan_tag` text not null,"
                            + " `server_name` text not null,"
                            + " `locked_by` text not null,"
                            + " `lock_time` datetime default current_timestamp"
                            + ");";

                    core.execute(sql);

                    String indexSql = "CREATE INDEX IF NOT EXISTS idx_server_name ON `" + getPrefixedTable("chest_locks") + "` (`server_name`);";
                    core.execute(indexSql);
                }
            } else {
                plugin.getServer().getConsoleSender().sendMessage("[SimpleClans] " + ChatColor.RED + lang("sqlite.connection.failed"));
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
            plugin.getLogger().info(MessageFormat.format(lang("clans"), clans.size()));
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
            plugin.getLogger().info(MessageFormat.format(lang("clan.players"), cps.size()));
        }
    }

    /**
     * Import one ClanPlayer data from database to memory
     * Used for BungeeCord Reload ClanPlayer and your Clan
     */
    @Deprecated
    public void importFromDatabaseOnePlayer(Player player) {
        plugin.getClanManager().deleteClanPlayerFromMemory(player.getUniqueId());

        ClanPlayer cp = retrieveOneClanPlayer(player.getUniqueId());

        if (cp != null) {
            Clan tm = cp.getClan();

            if (tm != null) {
                tm.importMember(cp);
            }
            plugin.getClanManager().importClanPlayer(cp);

            plugin.getLogger().info("ClanPlayer Reloaded: " + player.getName() + ", UUID: " + player.getUniqueId());
        }
    }

    private void purgeClans(List<Clan> clans) {
        List<Clan> purge = new ArrayList<>();

        for (Clan clan : clans) {
            if (clan.isPermanent()) {
                continue;
            }
            if (clan.isVerified()) {
                int purgeClan = plugin.getSettingsManager().getInt(PURGE_INACTIVE_CLAN_DAYS);
                if (clan.getInactiveDays() > purgeClan && purgeClan > 0) {
                    purge.add(clan);
                }
            } else {
                int purgeUnverified = plugin.getSettingsManager().getInt(PURGE_UNVERIFIED_CLAN_DAYS);
                if (clan.getInactiveDays() > purgeUnverified && purgeUnverified > 0) {
                    purge.add(clan);
                }
            }
        }

        for (Clan clan : purge) {
            plugin.getLogger().info(lang("purging.clan", clan.getName()));
            for (ClanPlayer member : clan.getMembers()) {
                clan.removePlayerFromClan(member.getUniqueId());
            }
            deleteClan(clan);
            clans.remove(clan);
        }
    }

    private void purgeClanPlayers(List<ClanPlayer> cps) {
        int purgePlayers = plugin.getSettingsManager().getInt(PURGE_INACTIVE_PLAYER_DAYS);
        if (purgePlayers < 1) {
            return;
        }
        List<ClanPlayer> purge = new ArrayList<>();

        for (ClanPlayer cp : cps) {
            //let the clan be purged first
            if (cp.isLeader() && cp.getClan() != null) {
                continue;
            }
            if (cp.getInactiveDays() > purgePlayers) {
                purge.add(cp);
            }
        }

        for (ClanPlayer cp : purge) {
            plugin.getLogger().info(lang("purging.player.data", cp.getName()));
            deleteClanPlayer(cp);
            cps.remove(cp);
        }
    }

    /**
     * Retrieves all simple clans from the database
     */
    public List<Clan> retrieveClans() {
        List<Clan> out = new ArrayList<>();

        String query = "SELECT * FROM `" + getPrefixedTable("clans") + "`;";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        boolean verified = res.getBoolean("verified");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        String tag = res.getString("tag");
                        String color_tag = ChatUtils.parseColors(res.getString("color_tag"));
                        String name = res.getString("name");
                        String description = res.getString("description");
                        String packed_allies = res.getString("packed_allies");
                        String packed_rivals = res.getString("packed_rivals");
                        String packed_bb = res.getString("packed_bb");
                        String flags = res.getString("flags");
                        String ranksJson = res.getString("ranks");
                        long founded = res.getLong("founded");
                        long last_used = res.getLong("last_used");
                        double balance = res.getDouble("balance");
                        double feeValue = res.getDouble("fee_value");
                        boolean feeEnabled = res.getBoolean("fee_enabled");
                        ItemStack banner = YAMLSerializer.deserialize(res.getString("banner"), ItemStack.class);
                        ClanChest cc = ClanChest.deserialize(res.getBytes("chest_content"));

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
                        clan.setFounded(founded);
                        clan.setLastUsed(last_used);
                        clan.setBalance(BankOperator.INTERNAL, ClanBalanceUpdateEvent.Cause.LOADING, BankLogger.Operation.SET, balance);
                        clan.setMemberFee(feeValue);
                        clan.setMemberFeeEnabled(feeEnabled);
                        clan.setRanks(Helper.ranksFromJson(ranksJson));
                        clan.setDefaultRank(Helper.defaultRankFromJson(ranksJson));
                        clan.setBanner(banner);
                        clan.setClanChest(cc);

                        out.add(clan);
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
     */
    public @Nullable Clan retrieveOneClan(String tagClan) {
        Clan out = null;

        String query = "SELECT * FROM  `" + getPrefixedTable("clans") + "` WHERE `tag` = '" + tagClan + "';";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                while (res.next()) {
                    try {
                        boolean verified = res.getBoolean("verified");
                        boolean friendly_fire = res.getBoolean("friendly_fire");
                        String tag = res.getString("tag");
                        String color_tag = ChatUtils.parseColors(res.getString("color_tag"));
                        String name = res.getString("name");
                        String description = res.getString("description");
                        String packed_allies = res.getString("packed_allies");
                        String packed_rivals = res.getString("packed_rivals");
                        String packed_bb = res.getString("packed_bb");
                        String flags = res.getString("flags");
                        String ranksJson = res.getString("ranks");
                        long founded = res.getLong("founded");
                        long last_used = res.getLong("last_used");
                        double balance = res.getDouble("balance");
                        double feeValue = res.getDouble("fee_value");
                        boolean feeEnabled = res.getBoolean("fee_enabled");
                        ItemStack banner = YAMLSerializer.deserialize(res.getString("banner"), ItemStack.class);
                        ClanChest cc = ClanChest.deserialize(res.getBytes("chest_content"));

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
                        clan.setFounded(founded);
                        clan.setLastUsed(last_used);
                        clan.setBalance(BankOperator.INTERNAL, ClanBalanceUpdateEvent.Cause.LOADING, BankLogger.Operation.SET, balance);
                        clan.setMemberFee(feeValue);
                        clan.setMemberFeeEnabled(feeEnabled);
                        clan.setRanks(Helper.ranksFromJson(ranksJson));
                        clan.setDefaultRank(Helper.defaultRankFromJson(ranksJson));
                        clan.setBanner(banner);
                        clan.setClanChest(cc);

                        out = clan;
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
     */
    public List<ClanPlayer> retrieveClanPlayers() {
        List<ClanPlayer> out = new ArrayList<>();

        String query = "SELECT * FROM  `" + getPrefixedTable("players") + "`;";
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
                        int ally_kills = res.getInt("ally_kills");
                        int deaths = res.getInt("deaths");
                        long last_seen = res.getLong("last_seen");
                        long join_date = res.getLong("join_date");
                        String flags = res.getString("flags");
                        String packed_past_clans = ChatUtils.parseColors(res.getString("packed_past_clans"));
                        String resign_times = res.getString("resign_times");
                        Locale locale = Helper.forLanguageTag(res.getString("locale"));

                        if (last_seen == 0) {
                            last_seen = (new Date()).getTime();
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
                        cp.setAllyKills(ally_kills);
                        cp.setDeaths(deaths);
                        cp.setLastSeen(last_seen);
                        cp.setJoinDate(join_date);
                        cp.setPackedPastClans(packed_past_clans);
                        cp.setTrusted(leader || trusted);
                        cp.setResignTimes(Helper.resignTimesFromJson(resign_times));
                        cp.setLocale(locale);

                        if (!tag.isEmpty()) {
                            Clan clan = plugin.getClanManager().getClan(tag);

                            if (clan != null) {
                                cp.setClan(clan);
                            }
                        }

                        out.add(cp);
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
     */
    public @Nullable ClanPlayer retrieveOneClanPlayer(UUID playerUniqueId) {
        ClanPlayer out = null;

        String query = "SELECT * FROM `" + getPrefixedTable("players") + "` WHERE `uuid` = '" + playerUniqueId.toString() + "';";
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
                        int ally_kills = res.getInt("ally_kills");
                        int deaths = res.getInt("deaths");
                        long last_seen = res.getLong("last_seen");
                        long join_date = res.getLong("join_date");
                        String flags = res.getString("flags");
                        String packed_past_clans = ChatUtils.parseColors(res.getString("packed_past_clans"));
                        String resign_times = res.getString("resign_times");
                        Locale locale = Helper.forLanguageTag(res.getString("locale"));

                        if (last_seen == 0) {
                            last_seen = (new Date()).getTime();
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
                        cp.setAllyKills(ally_kills);
                        cp.setDeaths(deaths);
                        cp.setLastSeen(last_seen);
                        cp.setJoinDate(join_date);
                        cp.setPackedPastClans(packed_past_clans);
                        cp.setTrusted(leader || trusted);
                        cp.setResignTimes(Helper.resignTimesFromJson(resign_times));
                        cp.setLocale(locale);

                        if (!tag.isEmpty()) {
                            Clan clanDB = retrieveOneClan(tag);
                            Clan clan = plugin.getClanManager().getClan(tag);

                            if (clan != null && clanDB != null) {
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
                                clanReSync.setFounded(clanDB.getFounded());
                                clanReSync.setLastUsed(clanDB.getLastUsed());
                                clanReSync.setBalance(BankOperator.INTERNAL, ClanBalanceUpdateEvent.Cause.LOADING, BankLogger.Operation.SET, clanDB.getBalance());
                                cp.setClan(clanReSync);
                            } else {
                                plugin.getClanManager().importClan(clanDB);
                                clanDB.validateWarring();
                                Clan newClan = plugin.getClanManager().getClan(clanDB.getTag());
                                cp.setClan(newClan);
                            }
                        }

                        out = cp;
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
     */
    public void insertClan(Clan clan) {
        plugin.getProxyManager().sendUpdate(clan);

        try (PreparedStatement st = prepareUpsertClanStatement(core.getConnection())) {
            setValues(st, clan);
            st.executeUpdate();
        } catch (SQLException | IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error inserting Clan %s", clan.getTag()), ex);
        }
    }

    /**
     * Update a clan to the database asynchronously
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
     * @param cp to update
     */
    public void updatePlayerNameAsync(final @NotNull ClanPlayer cp) {
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayerName(cp);
            }
        }.runTaskAsynchronously(plugin);
    }

    /**
     * Change the name of a player in the database
     *
     * @param cp to update
     */
    public void updatePlayerName(final @NotNull ClanPlayer cp) {
        String query = "UPDATE `" + getPrefixedTable("players")
                + "` SET `name` = '" + cp.getName() + "' WHERE uuid = '" + cp.getUniqueId() + "';";
        core.executeUpdate(query);
    }

    /**
     * Update a clan to the database
     */
    public void updateClan(Clan clan) {
        updateClan(clan, true);
    }

    /**
     * Update a clan to the database
     *
     * @param clan           clan to update
     * @param updateLastUsed should the clan's last used time be updated as well?
     */
    public void updateClan(Clan clan, boolean updateLastUsed) {
        if (updateLastUsed) {
            clan.updateLastUsed();
        }
        plugin.getProxyManager().sendUpdate(clan);
        if (plugin.getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            modifiedClans.add(clan);
            return;
        }
        try (PreparedStatement st = prepareUpsertClanStatement(core.getConnection())) {
            setValues(st, clan);
            st.executeUpdate();
        } catch (SQLException | IOException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error updating Clan %s", clan.getTag()), ex);
        }
    }

    private PreparedStatement prepareUpsertClanStatement(Connection connection) throws SQLException {
        String sql;
        if (plugin.getSettingsManager().is(MYSQL_ENABLE)) {
            sql = "INSERT INTO " + getPrefixedTable("clans")
                    + " (`banner`, `ranks`, `description`, `fee_enabled`, `fee_value`, `verified`, `tag`, "
                    + "`color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, "
                    + "`packed_bb`, `cape_url`, `flags`, `chest_content`, `balance`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                    + "ON DUPLICATE KEY UPDATE "
                    + "ranks = VALUES(ranks), banner = VALUES(banner), description = VALUES(description), "
                    + "fee_enabled = VALUES(fee_enabled), fee_value = VALUES(fee_value), verified = VALUES(verified), "
                    + "color_tag = VALUES(color_tag), name = VALUES(name), friendly_fire = VALUES(friendly_fire), "
                    + "founded = VALUES(founded), last_used = VALUES(last_used), packed_allies = VALUES(packed_allies), "
                    + "packed_rivals = VALUES(packed_rivals), packed_bb = VALUES(packed_bb), cape_url = VALUES(cape_url), "
                    + "balance = VALUES(balance), flags = VALUES(flags), chest_content = VALUES(chest_content)";
        } else {
            sql = "INSERT OR REPLACE INTO " + getPrefixedTable("clans")
                    + " (`banner`, `ranks`, `description`, `fee_enabled`, `fee_value`, `verified`, `tag`, "
                    + "`color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, "
                    + "`packed_bb`, `cape_url`, `flags`, `chest_content`, `balance`) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        return connection.prepareStatement(sql);
    }

    private void setValues(PreparedStatement statement, Clan clan) throws SQLException, IOException {
        statement.setString(1, Helper.escapeQuotes(YAMLSerializer.serialize(clan.getBanner())));
        statement.setString(2, Helper.escapeQuotes(Helper.ranksToJson(clan.getRanks(), clan.getDefaultRank())));
        statement.setString(3, Helper.escapeQuotes(clan.getDescription()));
        statement.setInt(4, clan.isMemberFeeEnabled() ? 1 : 0);
        statement.setDouble(5, clan.getMemberFee());
        statement.setInt(6, clan.isVerified() ? 1 : 0);
        statement.setString(7, Helper.escapeQuotes(clan.getTag()));
        statement.setString(8, Helper.escapeQuotes(clan.getColorTag()));
        statement.setString(9, Helper.escapeQuotes(clan.getName()));
        statement.setInt(10, clan.isFriendlyFire() ? 1 : 0);
        statement.setLong(11, clan.getFounded());
        statement.setLong(12, clan.getLastUsed());
        statement.setString(13, Helper.escapeQuotes(clan.getPackedAllies()));
        statement.setString(14, Helper.escapeQuotes(clan.getPackedRivals()));
        statement.setString(15, Helper.escapeQuotes(clan.getPackedBb()));
        statement.setString(16, Helper.escapeQuotes(clan.getCapeUrl()));
        statement.setString(17, Helper.escapeQuotes(clan.getFlags()));
        statement.setBytes(18, clan.getClanChest().serialize());
        statement.setDouble(19, clan.getBalance());
    }

    /**
     * Delete a clan from the database
     */
    public void deleteClan(Clan clan) {
        plugin.getProxyManager().sendDelete(clan);
        String query = "DELETE FROM `" + getPrefixedTable("clans") + "` WHERE tag = '" + clan.getTag() + "';";
        core.executeUpdate(query);
    }

    /**
     * Insert a clan player into the database
     */
    public void insertClanPlayer(ClanPlayer cp) {
        plugin.getProxyManager().sendUpdate(cp);

        String query = "INSERT INTO `" + getPrefixedTable("players")
                + "` (`uuid`, `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, " +
                "`rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
        String values = "VALUES ('" + cp.getUniqueId().toString() + "', '" + cp.getName() + "',"
                + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "',"
                + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills()
                + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' "
                + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','"
                + Helper.escapeQuotes(cp.getFlags()) + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Update a clan player to the database asynchronously
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
     */
    public void updateClanPlayer(ClanPlayer cp) {
        cp.updateLastSeen();
        plugin.getProxyManager().sendUpdate(cp);
        if (plugin.getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            modifiedClanPlayers.add(cp);
            return;
        }
        try (PreparedStatement st = prepareUpdateClanPlayerStatement(core.getConnection())) {
            setValues(st, cp);
            st.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error updating ClanPlayer %s", cp.getName()), ex);
        }
    }

    private PreparedStatement prepareUpdateClanPlayerStatement(Connection connection) throws SQLException {
        String sql = "UPDATE `" + getPrefixedTable("players") + "` SET locale = ?, resign_times = ?, leader = ?, tag = ?, friendly_fire = ?," +
                " neutral_kills = ?, ally_kills = ?, rival_kills = ?, civilian_kills = ?, deaths = ?, last_seen = ?," +
                " packed_past_clans = ?, trusted = ?, flags = ?, `name` = ? WHERE `uuid` = ?;";
        return connection.prepareStatement(sql);
    }

    private void setValues(PreparedStatement statement, ClanPlayer cp) throws SQLException {
        statement.setString(1, Helper.toLanguageTag(cp.getLocale()));
        statement.setString(2, Helper.resignTimesToJson(cp.getResignTimes()));
        statement.setInt(3, cp.isLeader() ? 1 : 0);
        statement.setString(4, cp.getTag());
        statement.setInt(5, cp.isFriendlyFire() ? 1 : 0);
        statement.setInt(6, cp.getNeutralKills());
        statement.setInt(7, cp.getAllyKills());
        statement.setInt(8, cp.getRivalKills());
        statement.setInt(9, cp.getCivilianKills());
        statement.setInt(10, cp.getDeaths());
        statement.setLong(11, cp.getLastSeen());
        statement.setString(12, cp.getPackedPastClans());
        statement.setInt(13, cp.isTrusted() ? 1 : 0);
        statement.setString(14, cp.getFlags());
        statement.setString(15, cp.getName());
        statement.setString(16, cp.getUniqueId().toString());
    }

    /**
     * Delete a clan player from the database
     */
    public void deleteClanPlayer(ClanPlayer cp) {
        final Clan clan = cp.getClan();
        if (clan != null) {
            clan.addBbWithoutSaving(MessageFormat.format(lang("has.been.purged"), cp.getName()));
            updateClan(clan, false);
        }
        plugin.getProxyManager().sendDelete(cp);
        String query = "DELETE FROM `" + getPrefixedTable("players") + "` WHERE uuid = '" + cp.getUniqueId() + "';";
        core.executeUpdate(query);
        deleteKills(cp.getUniqueId());
    }

    /**
     * Insert a kill into the database
     */
    @Deprecated
    public void insertKill(Player attacker, String attackerTag, Player victim, String victimTag, String type) {
        String query = "INSERT INTO `" + getPrefixedTable("kills")
                + "` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, `victim`, `victim_tag`, `kill_type`) ";
        String values = "VALUES ( '"
                + attacker.getUniqueId() + "','"
                + attacker.getName() + "','"
                + attackerTag + "','"
                + victim.getUniqueId() + "','"
                + victim.getName() + "','"
                + victimTag + "','"
                + type + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Insert a kill into the database
     *
     * @param attacker the attacker
     * @param victim   the victim
     * @param type     the kill type
     */
    public void insertKill(@NotNull ClanPlayer attacker, @NotNull ClanPlayer victim, @NotNull String type, @NotNull LocalDateTime time) {
        String query = "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, " +
                "`victim`, `victim_tag`, `kill_type`, `created_at`) ";
        String values = "VALUES ( '"
                + attacker.getUniqueId() + "','"
                + attacker.getName() + "','"
                + attacker.getTag() + "','"
                + victim.getUniqueId() + "','"
                + victim.getName() + "','"
                + victim.getTag() + "','"
                + type + "','"
                + time + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Delete a player's kill record form the database
     */
    @Deprecated
    public void deleteKills(String playerName) {
        String query = "DELETE FROM `" + getPrefixedTable("kills") + "` WHERE `attacker` = '" + playerName + "'";
        core.executeUpdate(query);
    }

    /**
     * Delete a player's kill record form the database
     */
    public void deleteKills(UUID playerUniqueId) {
        String query = "DELETE FROM `" + getPrefixedTable("kills") + "` WHERE `attacker_uuid` = '" + playerUniqueId + "'";
        core.executeUpdate(query);
    }

    /**
     * Returns a map of victim-{@literal >}count of all kills that specific player did
     *
     * @param playerName the attacker name
     * @return a map of kills per victim
     */
    public Map<String, Integer> getKillsPerPlayer(String playerName) {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT victim, count(victim) AS kills FROM `" + getPrefixedTable("kills")
                + "` WHERE attacker = '" + playerName + "' GROUP BY victim ORDER BY count(victim) DESC;";
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
     * Returns a map of tag-{@literal >}count of all kills
     *
     * @return a map of kills per attacker+victim
     */
    public Map<String, Integer> getMostKilled() {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT attacker, victim, count(victim) AS kills FROM `" + getPrefixedTable("kills")
                + "` GROUP BY attacker, victim ORDER BY 3 DESC;";
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
     * Gets, asynchronously, a map of tag-{@literal >}count of all kills and notifies via callback when it's ready
     *
     * @param callback the callback
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
     * Gets, asynchronously, a map of victim-{@literal >}count of all kills
     * that specific player did and notifies via callback when it's ready
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
     */
    public interface DataCallback<T> {
        /**
         * Notifies when the result is ready
         */
        void onResultReady(T data);
    }

    /**
     * Updates the database to the latest version
     */
    private void updateDatabase() {
        String query;

        /*
         * From 2.2.6.3 to 2.3
         */
        if (!core.existsColumn(getPrefixedTable("clans"), "balance")) {
            query = "ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `balance` double(64,2);";
            core.execute(query);
        }

        /*
         * From 2.7.16 to 2.7.17
         */
        if (!core.existsColumn(getPrefixedTable("clans"), "fee_enabled")) {
            query = "ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `fee_enabled` tinyint(1) default '0';";
            core.execute(query);
        }
        if (!core.existsColumn(getPrefixedTable("clans"), "fee_value")) {
            query = "ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `fee_value` double(64,2);";
            core.execute(query);
        }

        /*
         * From 2.7.21 to 2.7.22
         */
        if (!core.existsColumn(getPrefixedTable("clans"), "description")) {
            query = "ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `description` varchar(255);";
            core.execute(query);
        }

        /*
         * From 2.7.22 to 2.7.23
         */
        if (!core.existsColumn(getPrefixedTable("players"), "resign_times")) {
            query = "ALTER TABLE `" + getPrefixedTable("players") + "` ADD COLUMN `resign_times` text;";
            core.execute(query);
        }

        /*
         * From 2.8.2 to 2.9
         */
        if (!core.existsColumn(getPrefixedTable("clans"), "ranks")) {
            query = "ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `ranks` text;";
            core.execute(query);
        }

        // From 2.12.1 to 2.13.0
        if (!core.existsColumn(getPrefixedTable("players"), "locale")) {
            query = "ALTER TABLE `" + getPrefixedTable("players") + "` ADD COLUMN `locale` varchar(10);";
            core.execute(query);
        }
        if (!core.existsColumn(getPrefixedTable("clans"), "banner")) {
            core.execute("ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `banner` text;");
        }

        // From 2.15.1 to 2.15.2
        if (!core.existsColumn(getPrefixedTable("players"), "ally_kills")) {
            core.execute("ALTER TABLE `" + getPrefixedTable("players") + "` ADD COLUMN `ally_kills` int(11) DEFAULT NULL;");
        }

        if (plugin.getSettingsManager().is(MYSQL_ENABLE)) {
            core.execute("ALTER TABLE `" + getPrefixedTable("clans") + "` MODIFY color_tag VARCHAR(255);");
        }

        /*
         * Bukkit 1.7.5+ UUID Migration
         */
        if (!core.existsColumn(getPrefixedTable("kills"), "attacker_uuid")) {
            query = "ALTER TABLE `" + getPrefixedTable("kills") + "` ADD attacker_uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);
        }
        if (!core.existsColumn(getPrefixedTable("kills"), "victim_uuid")) {
            query = "ALTER TABLE `" + getPrefixedTable("kills") + "` ADD victim_uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);
        }
        boolean useMysql = plugin.getSettingsManager().is(MYSQL_ENABLE);
        if (!core.existsColumn(getPrefixedTable("players"), "uuid")) {
            query = "ALTER TABLE `" + getPrefixedTable("players") + "` ADD uuid VARCHAR( 255 ) DEFAULT NULL;";
            core.execute(query);

            if (useMysql) {
                query = "ALTER TABLE `" + getPrefixedTable("players") + "` ADD UNIQUE `uq_player_uuid` (`uuid`);";
                core.execute(query);
            }

            updatePlayersToUUID();

            if (useMysql) {
                query = "ALTER TABLE `" + getPrefixedTable("players") + "` DROP INDEX uq_sc_players_1;";
            } else {
                query = "DROP INDEX IF EXISTS uq_sc_players_1;";
            }
            core.execute(query);
        }

        if (core.existsColumn(getPrefixedTable("players"), "uuid") && !useMysql) {
            query = "CREATE UNIQUE INDEX IF NOT EXISTS `uq_player_uuid` ON `" + getPrefixedTable("players") + "` (`uuid`);";
            core.execute(query);
        }

        // From 2.19.3 to 2.20.0
        if (!core.existsColumn("sc_kills", "created_at")) {
            query = "ALTER TABLE sc_kills ADD `created_at` datetime NULL;";
            core.execute(query);
        }
        if (!core.existsColumn(getPrefixedTable("clans"), "chest_content")) {
            core.execute("ALTER TABLE `" + getPrefixedTable("clans") + "` ADD COLUMN `chest_content` BLOB DEFAULT NULL;");
        }
    }

    /**
     * Updates the database to the latest version
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
                String query = "UPDATE `" + getPrefixedTable("players") + "` SET uuid = '" + uuidPlayer.toString() + "' WHERE name = '" + cp.getName() + "';";
                core.executeUpdate(query);

                String query2 = "UPDATE `" + getPrefixedTable("kills") + "` SET attacker_uuid = '" + uuidPlayer + "' WHERE attacker = '" + cp.getName() + "';";
                core.executeUpdate(query2);

                String query3 = "UPDATE `" + getPrefixedTable("kills") + "` SET victim_uuid = '" + uuidPlayer + "' WHERE victim = '" + cp.getName() + "';";
                core.executeUpdate(query3);
                plugin.getLogger().info("[" + i + " / " + cps.size() + "] Success: " + cp.getName() + "; UUID: " + uuidPlayer);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.WARNING, "[" + i + " / " + cps.size() + "] Failed [ERRO]: " + cp.getName() + "; UUID: ???");
            }
            i++;
        }
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");


        if (!cps.isEmpty()) {
            plugin.getLogger().info(MessageFormat.format(lang("clan.players"), cps.size()));
        }
        SimpleClans.getInstance().setUUID(true);
    }

    private String getPrefixedTable(String name) {
        return plugin.getSettingsManager().getString(MYSQL_TABLE_PREFIX) + name;
    }

    /**
     * Saves modified Clans, ClanPlayers and ClanChests to the database
     *
     * @since 2.10.2
     *
     * <p>
     * author: RoinujNosde
     * </p>
     */
    public void saveModified() {
        try (PreparedStatement pst = prepareUpdateClanPlayerStatement(core.getConnection())) {
            //removing purged players
            modifiedClanPlayers.retainAll(plugin.getClanManager().getAllClanPlayers());
            for (ClanPlayer cp : modifiedClanPlayers) {
                setValues(pst, cp);
                pst.addBatch();
            }
            pst.executeBatch();

            modifiedClanPlayers.clear();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error saving modified ClanPlayers:", ex);
        }
        try (PreparedStatement pst = prepareUpsertClanStatement(core.getConnection())) {
            //removing disbanded clans
            modifiedClans.retainAll(plugin.getClanManager().getClans());
            for (Clan clan : modifiedClans) {
                setValues(pst, clan);
                pst.addBatch();
            }
            pst.executeBatch();

            modifiedClans.clear();
        } catch (SQLException | IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error saving modified Clans:", ex);
        }
    }

    public LockResult checkChestLock(String serverName, String clanTag) throws SQLException {
        PreparedStatement pst = prepareSelectChestLockStatement(core.getConnection());
        pst.setString(1, clanTag);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            String lockedServer = rs.getString("server_name");
            UUID lockedBy = UUID.fromString(rs.getString("locked_by"));

            if (!lockedServer.equals(serverName)) {
                return new LockResult(LockStatus.LOCKED_BY_OTHER_SERVER, lockedBy, lockedServer);
            }
        }

        return new LockResult(LockStatus.NOT_LOCKED);
    }

    public boolean lockChest(String serverName, String clanTag, UUID playerUuid) throws SQLException {
        PreparedStatement pst = prepareInsertChestLockStatement(core.getConnection());
        pst.setString(1, clanTag);
        pst.setString(2, serverName);
        pst.setString(3, playerUuid.toString());
        return pst.executeUpdate() > 0;
    }

    public void unlockChest(String clanTag, UUID playerUuid) throws SQLException {
        PreparedStatement pst = prepareDeleteChestLockStatement(core.getConnection());
        pst.setString(1, clanTag);
        pst.setString(2, playerUuid.toString());
        pst.executeUpdate();
    }

    public boolean runWithTransaction(TransactionRunnable action) {
        Connection conn;
        try {
            conn = core.getConnection();
            conn.setAutoCommit(false);
            try {
                action.run();
                conn.commit();
                conn.setAutoCommit(true);
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error while executing the transaction", ex);
            return false;
        }
    }

    public void clearChestLocks(@NotNull String serverName) {
        String query = "DELETE FROM " + getPrefixedTable("chest_locks") + " WHERE `server_name` = ?";
        try (PreparedStatement stmt = core.getConnection().prepareStatement(query)) {
            stmt.setString(1, serverName);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error clearing chest locks", ex);
        }
    }

    // used only when MySQL enabled
    private PreparedStatement prepareSelectChestLockStatement(Connection connection) throws SQLException {
        if (!plugin.getSettingsManager().is(MYSQL_ENABLE)) {
            throw new IllegalStateException("MYSQL should be enabled to perform this operation.");
        }

        String sql = "SELECT `server_name`, `locked_by` FROM `" + getPrefixedTable("chest_locks") + "` WHERE `clan_tag` = ? FOR UPDATE";
        return connection.prepareStatement(sql);
    }

    private PreparedStatement prepareDeleteChestLockStatement(Connection connection) throws SQLException {
        String sql = "DELETE FROM `" + getPrefixedTable("chest_locks") + "` WHERE `clan_tag` = ? AND `locked_by` = ?";
        return connection.prepareStatement(sql);
    }

    private PreparedStatement prepareInsertChestLockStatement(Connection connection) throws SQLException {
        String sql = "INSERT INTO `" + getPrefixedTable("chest_locks") + "` (`clan_tag`, `server_name`, `locked_by`) VALUES (?, ?, ?)";
        return connection.prepareStatement(sql);
    }
}