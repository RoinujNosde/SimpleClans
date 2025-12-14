package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankOperator;
import net.sacredlabyrinth.phaed.simpleclans.storage.DBCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.MySQLCore;
import net.sacredlabyrinth.phaed.simpleclans.storage.SQLiteCore;
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

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    public ChatBlock getChatBlock(@NotNull Player player) {
        return chatBlocks.get(player.getName());
    }

    /**
     * Store pending chat lines for a player
     *
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

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("clans") + "` (" + " `id` bigint(20) NOT NULL auto_increment," + " `verified` tinyint(1) default '0'," + " `tag` varchar(25) NOT NULL," + " `color_tag` varchar(255) NOT NULL," + " `name` varchar(100) NOT NULL," + " `description` varchar(255)," + " `friendly_fire` tinyint(1) default '0'," + " `founded` bigint NOT NULL," + " `last_used` bigint NOT NULL," + " `packed_allies` text NOT NULL," + " `packed_rivals` text NOT NULL," + " `packed_bb` mediumtext NOT NULL," + " `cape_url` varchar(255) NOT NULL," + " `flags` text NOT NULL," + " `balance` double(64,2)," + " `fee_enabled` tinyint(1) default '0'," + " `fee_value` double(64,2)," + " `ranks` text NOT NULL," + " `banner` text," + " PRIMARY KEY  (`id`)," + " UNIQUE KEY `uq_simpleclans_1` (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("players"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("players"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("players") + "` (" + " `id` bigint(20) NOT NULL auto_increment," + " `name` varchar(16) NOT NULL," + " `leader` tinyint(1) default '0'," + " `tag` varchar(25) NOT NULL," + " `friendly_fire` tinyint(1) default '0'," + " `neutral_kills` int(11) default NULL," + " `rival_kills` int(11) default NULL," + " `civilian_kills` int(11) default NULL," + " `ally_kills` int(11) default NULL," + " `deaths` int(11) default NULL," + " `last_seen` bigint NOT NULL," + " `join_date` bigint NOT NULL," + " `trusted` tinyint(1) default '0'," + " `flags` text NOT NULL," + " `packed_past_clans` text," + " `resign_times` text," + " `locale` varchar(10)," + " PRIMARY KEY  (`id`)," + " UNIQUE KEY `uq_sc_players_1` (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("kills"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("kills"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("kills") + "` (" + " `kill_id` bigint(20) NOT NULL auto_increment," + " `attacker` varchar(16) NOT NULL," + " `attacker_tag` varchar(16) NOT NULL," + " `victim` varchar(16) NOT NULL," + " `victim_tag` varchar(16) NOT NULL," + " `kill_type` varchar(1) NOT NULL," + " `created_at` datetime NULL," + " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
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

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("clans") + "` (" + " `id` bigint(20)," + " `verified` tinyint(1) default '0'," + " `tag` varchar(25) NOT NULL," + " `color_tag` varchar(255) NOT NULL," + " `name` varchar(100) NOT NULL," + " `description` varchar(255)," + " `friendly_fire` tinyint(1) default '0'," + " `founded` bigint NOT NULL," + " `last_used` bigint NOT NULL," + " `packed_allies` text NOT NULL," + " `packed_rivals` text NOT NULL," + " `packed_bb` mediumtext NOT NULL," + " `cape_url` varchar(255) NOT NULL," + " `flags` text NOT NULL," + " `balance` double(64,2) default 0.0," + " `fee_enabled` tinyint(1) default '0'," + " `fee_value` double(64,2)," + " `ranks` text NOT NULL," + " `banner` text," + "  PRIMARY KEY  (`id`)," + " UNIQUE (`tag`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("players"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("players"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("players") + "` (" + " `id` bigint(20)," + " `name` varchar(16) NOT NULL," + " `leader` tinyint(1) default '0'," + " `tag` varchar(25) NOT NULL," + " `friendly_fire` tinyint(1) default '0'," + " `neutral_kills` int(11) default NULL," + " `rival_kills` int(11) default NULL," + " `civilian_kills` int(11) default NULL," + " `ally_kills` int(11) default NULL," + " `deaths` int(11) default NULL," + " `last_seen` bigint NOT NULL," + " `join_date` bigint NOT NULL," + " `trusted` tinyint(1) default '0'," + " `flags` text NOT NULL," + " `packed_past_clans` text," + " `resign_times` text," + " `locale` varchar(10)," + " PRIMARY KEY  (`id`)," + " UNIQUE (`name`));";
                    core.execute(query);
                }

                if (!core.existsTable(getPrefixedTable("kills"))) {
                    plugin.getLogger().info("Creating table: " + getPrefixedTable("kills"));

                    String query = "CREATE TABLE IF NOT EXISTS `" + getPrefixedTable("kills") + "` (" + " `kill_id` bigint(20)," + " `attacker` varchar(16) NOT NULL," + " `attacker_tag` varchar(16) NOT NULL," + " `victim` varchar(16) NOT NULL," + " `victim_tag` varchar(16) NOT NULL," + " `kill_type` varchar(1) NOT NULL," + " `created_at` datetime NULL," + " PRIMARY KEY  (`kill_id`));";
                    core.execute(query);
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
     *
     */
    @Deprecated
    public void importFromDatabaseOnePlayer(@NotNull Player player) {
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
     *
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
     *
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
     *
     */
    public void insertClan(Clan clan) {
        plugin.getProxyManager().sendUpdate(clan);

        String query = "INSERT INTO `" + getPrefixedTable("clans") + "` (`banner`, `ranks`, `description`, `fee_enabled`, `fee_value`, `verified`, `tag`," + " `color_tag`, `name`, `friendly_fire`, `founded`, `last_used`, `packed_allies`, `packed_rivals`, " + "`packed_bb`, `cape_url`, `flags`, `balance`) ";
        String values = "VALUES ( '" + Helper.escapeQuotes(YAMLSerializer.serialize(clan.getBanner())) + "','" + Helper.escapeQuotes(Helper.ranksToJson(clan.getRanks(), clan.getDefaultRank())) + "','" + Helper.escapeQuotes(clan.getDescription()) + "'," + (clan.isMemberFeeEnabled() ? 1 : 0) + "," + Helper.escapeQuotes(String.valueOf(clan.getMemberFee())) + "," + (clan.isVerified() ? 1 : 0) + ",'" + Helper.escapeQuotes(clan.getTag()) + "','" + Helper.escapeQuotes(clan.getColorTag()) + "','" + Helper.escapeQuotes(clan.getName()) + "'," + (clan.isFriendlyFire() ? 1 : 0) + ",'" + clan.getFounded() + "','" + clan.getLastUsed() + "','" + Helper.escapeQuotes(clan.getPackedAllies()) + "','" + Helper.escapeQuotes(clan.getPackedRivals()) + "','" + Helper.escapeQuotes(clan.getPackedBb()) + "','" + Helper.escapeQuotes(clan.getCapeUrl()) + "','" + Helper.escapeQuotes(clan.getFlags()) + "','" + Helper.escapeQuotes(String.valueOf(clan.getBalance())) + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Update a clan to the database asynchronously
     *
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
     * Retrieves a clan player from the database by name
     *
     * @param name the player name to search for
     * @return the ClanPlayer if found, null otherwise
     */
    public @Nullable ClanPlayer retrieveClanPlayerByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        String query = "SELECT * FROM `" + getPrefixedTable("players") + "` WHERE `name` = '" + name + "';";
        ResultSet res = core.select(query);

        if (res != null) {
            try {
                if (res.next()) {
                    return buildClanPlayerFromResultSet(res);
                }
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Error retrieving ClanPlayer by name: " + name, ex);
            } finally {
                try {
                    res.close();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error closing ResultSet", e);
                }
            }
        }
        return null;
    }

    /**
     * Helper method to build a ClanPlayer from a ResultSet
     */
    private ClanPlayer buildClanPlayerFromResultSet(ResultSet res) throws SQLException {
        String uuid = res.getString("uuid");
        String name = res.getString("name");
        String tagStr = res.getString("tag");
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
        String packed_past_clans = res.getString("packed_past_clans");
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

        // Set clan relationship if tag exists
        if (tagStr != null && !tagStr.isEmpty()) {
            Clan clan = plugin.getClanManager().getClan(tagStr);
            if (clan != null) {
                cp.setClan(clan);
            }
        }

        return cp;
    }

    /**
     * Synchronizes player data in the database, handling duplicates
     * - If name differs but UUID matches: update name
     * - If names match but UUIDs differ: update UUID
     * - If both name and UUID exist in different records: merge and delete duplicates
     *
     * @param player the player to sync
     */
    public void syncPlayerData(@NotNull Player player) {
        try {
            String currentName = player.getName();
            UUID currentUuid = player.getUniqueId();

            ClanPlayer byName = retrieveClanPlayerByName(currentName);
            ClanPlayer byUuid = retrieveOneClanPlayer(currentUuid);

            // Case 1: No records exist - create new
            if (byName == null && byUuid == null) {
                plugin.getLogger().info(String.format("No existing records for %s (%s)", currentName, currentUuid));
                return;
            }

            // Case 2: Found by name only, UUID differs - update UUID
            if (byName != null && byUuid == null) {
                plugin.getLogger().warning(String.format("Correcting UUID for %s: %s → %s", byName.getName(), byName.getUniqueId(), currentUuid));

                UUID oldUuid = byName.getUniqueId();

                // Update UUID in database directly
                String updateQuery = "UPDATE `" + getPrefixedTable("players") + "` SET `uuid` = '" + currentUuid + "', `name` = '" + currentName + "', `last_seen` = " + System.currentTimeMillis() + " WHERE uuid = '" + oldUuid + "';";
                core.executeUpdate(updateQuery);

                // Update in-memory object
                byName.setUniqueId(currentUuid);
                byName.setName(currentName);
                byName.setLastSeen(System.currentTimeMillis());

                // Update in-memory reference
                plugin.getClanManager().deleteClanPlayerFromMemory(oldUuid);
                plugin.getClanManager().importClanPlayer(byName);

                plugin.getLogger().info(String.format("UUID corrected in database: %s", currentUuid));
                return;
            }

            // Case 3: Found by UUID only, name differs - update name
            if (byName == null && !byUuid.getName().equals(currentName)) {
                plugin.getLogger().info(String.format("Correcting name for %s to %s (%s)", byUuid.getName(), currentName, currentUuid));

                byUuid.setName(currentName);
                byUuid.setLastSeen(System.currentTimeMillis());
                updateClanPlayer(byUuid, true); // Force immediate update
                plugin.getLogger().info(String.format("Player name updated in database: %s", currentName));
                return;
            }

            // Case 4: Both found and they're the same record - just update
            if (byName != null && byName.getUniqueId() != null && byName.getUniqueId().equals(byUuid.getUniqueId())) {
                byUuid.setName(currentName);
                byUuid.setLastSeen(System.currentTimeMillis());
                updateClanPlayer(byUuid, true); // Force immediate update
                plugin.getLogger().info(String.format("Player data synchronized: %s", currentName));
                return;
            }

            // Case 5: Both found but different records - merge duplicates
            plugin.getLogger().warning(String.format("Duplicate detection!\n" + " - Record A: %s/%s\n" + " - Record B: %s/%s\n" + "➜ Merging records.", byName.getName(), byName.getUniqueId(), byUuid.getName(), byUuid.getUniqueId()));

            UUID oldByNameUuid = byName.getUniqueId();

            // Merge data (keep the byUuid record as base, merge stats from byName)
            ClanPlayer merged = mergeClanPlayers(byUuid, byName);
            merged.setUniqueId(currentUuid);
            merged.setName(currentName);

            // Delete the record with wrong UUID only
            String deleteByName = "DELETE FROM `" + getPrefixedTable("players") + "` WHERE uuid = '" + oldByNameUuid + "';";
            core.executeUpdate(deleteByName);

            // Update the kept record with merged data
            updateClanPlayer(merged, true);

            // Update in-memory
            plugin.getClanManager().deleteClanPlayerFromMemory(oldByNameUuid);
            plugin.getClanManager().importClanPlayer(merged);

            plugin.getLogger().info(String.format("Duplicate records merged for %s (%s)", currentName, currentUuid));
        } catch (Exception e) {
            plugin.getServer().getLogger().log(Level.SEVERE, "[SimpleClans] Error synchronizing player data for " + player.getName(), e);
        }
    }

    /**
     * Merges two ClanPlayer records, combining their stats
     */
    private ClanPlayer mergeClanPlayers(ClanPlayer primary, ClanPlayer secondary) {
        ClanPlayer merged = new ClanPlayer();

        // Copy identity from primary
        merged.setUniqueId(primary.getUniqueId());
        merged.setName(primary.getName());

        // Keep clan membership from primary (or secondary if primary has none)
        Clan primaryClan = primary.getClan();
        Clan secondaryClan = secondary.getClan();
        if (primaryClan != null) {
            merged.setClan(primaryClan);
        } else if (secondaryClan != null) {
            merged.setClan(secondaryClan);
        }

        merged.setLeader(primary.isLeader() || secondary.isLeader());
        merged.setTrusted(primary.isTrusted() || secondary.isTrusted());

        // Merge stats (sum kills/deaths)
        merged.setNeutralKills(primary.getNeutralKills() + secondary.getNeutralKills());
        merged.setRivalKills(primary.getRivalKills() + secondary.getRivalKills());
        merged.setCivilianKills(primary.getCivilianKills() + secondary.getCivilianKills());
        merged.setAllyKills(primary.getAllyKills() + secondary.getAllyKills());
        merged.setDeaths(primary.getDeaths() + secondary.getDeaths());

        // Keep earliest join date and latest last seen
        merged.setJoinDate(Math.min(primary.getJoinDate(), secondary.getJoinDate()));
        merged.setLastSeen(Math.max(primary.getLastSeen(), secondary.getLastSeen()));

        // Merge other properties from primary
        merged.setFriendlyFire(primary.isFriendlyFire());
        merged.setFlags(primary.getFlags());
        merged.setPackedPastClans(primary.getPackedPastClans());
        merged.setResignTimes(primary.getResignTimes());
        merged.setLocale(primary.getLocale());

        return merged;
    }

    /**
     * Change the name of a player in the database
     *
     * @param cp to update
     * @deprecated Use syncPlayerData instead for proper duplicate handling
     */
    @Deprecated
    public void updatePlayerName(final @NotNull ClanPlayer cp) {
        String query = "UPDATE `" + getPrefixedTable("players") + "` SET `name` = '" + cp.getName() + "' WHERE uuid = '" + cp.getUniqueId() + "';";
        core.executeUpdate(query);
    }

    /**
     * Update a clan to the database
     *
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
        try (PreparedStatement st = prepareUpdateClanStatement(core.getConnection())) {
            setValues(st, clan);
            st.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, String.format("Error updating Clan %s", clan.getTag()), ex);
        }
    }

    private PreparedStatement prepareUpdateClanStatement(Connection connection) throws SQLException {
        String sql = "UPDATE `" + getPrefixedTable("clans") + "` SET ranks = ?, banner = ?, description = ?, fee_enabled = ?, fee_value = ?, " + "verified = ?, tag = ?, color_tag = ?, `name` = ?, friendly_fire = ?, founded = ?, last_used = ?, " + "packed_allies = ?, packed_rivals = ?, packed_bb = ?, balance = ?, flags = ? WHERE tag = ?;";
        return connection.prepareStatement(sql);
    }

    private void setValues(PreparedStatement statement, Clan clan) throws SQLException {
        statement.setString(1, Helper.ranksToJson(clan.getRanks(), clan.getDefaultRank()));
        statement.setString(2, YAMLSerializer.serialize(clan.getBanner()));
        statement.setString(3, clan.getDescription());
        statement.setInt(4, clan.isMemberFeeEnabled() ? 1 : 0);
        statement.setDouble(5, clan.getMemberFee());
        statement.setInt(6, clan.isVerified() ? 1 : 0);
        statement.setString(7, clan.getTag());
        statement.setString(8, clan.getColorTag());
        statement.setString(9, clan.getName());
        statement.setInt(10, clan.isFriendlyFire() ? 1 : 0);
        statement.setLong(11, clan.getFounded());
        statement.setLong(12, clan.getLastUsed());
        statement.setString(13, clan.getPackedAllies());
        statement.setString(14, clan.getPackedRivals());
        statement.setString(15, clan.getPackedBb());
        statement.setDouble(16, clan.getBalance());
        statement.setString(17, clan.getFlags());
        statement.setString(18, clan.getTag());
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
     *
     */
    public void insertClanPlayer(ClanPlayer cp) {
        plugin.getProxyManager().sendUpdate(cp);

        String query = "INSERT INTO `" + getPrefixedTable("players") + "` (`uuid`, `name`, `leader`, `tag`, `friendly_fire`, `neutral_kills`, " + "`rival_kills`, `civilian_kills`, `deaths`, `last_seen`, `join_date`, `packed_past_clans`, `flags`) ";
        String values = "VALUES ('" + cp.getUniqueId().toString() + "', '" + cp.getName() + "'," + (cp.isLeader() ? 1 : 0) + ",'" + Helper.escapeQuotes(cp.getTag()) + "'," + (cp.isFriendlyFire() ? 1 : 0) + "," + cp.getNeutralKills() + "," + cp.getRivalKills() + "," + cp.getCivilianKills() + "," + cp.getDeaths() + ",'" + cp.getLastSeen() + "',' " + cp.getJoinDate() + "','" + Helper.escapeQuotes(cp.getPackedPastClans()) + "','" + Helper.escapeQuotes(cp.getFlags()) + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Update a clan player to the database asynchronously
     *
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
     */
    public void updateClanPlayer(ClanPlayer cp) {
        updateClanPlayer(cp, false);
    }

    /**
     * Update a clan player to the database
     *
     * @param cp             the clan player to update
     * @param forceImmediate if true, bypasses periodic save setting and updates immediately
     */
    public void updateClanPlayer(ClanPlayer cp, boolean forceImmediate) {
        cp.updateLastSeen();
        plugin.getProxyManager().sendUpdate(cp);
        if (!forceImmediate && plugin.getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
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
        String sql = "UPDATE `" + getPrefixedTable("players") + "` SET locale = ?, resign_times = ?, leader = ?, tag = ?, friendly_fire = ?," + " neutral_kills = ?, ally_kills = ?, rival_kills = ?, civilian_kills = ?, deaths = ?, last_seen = ?," + " packed_past_clans = ?, trusted = ?, flags = ?, `name` = ? WHERE `uuid` = ?;";
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
     *
     */
    @Deprecated
    public void insertKill(@NotNull Player attacker, @NotNull String attackerTag, @NotNull Player victim, @NotNull String victimTag, @NotNull String type) {
        String query = "INSERT INTO `" + getPrefixedTable("kills") + "` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, `victim`, `victim_tag`, `kill_type`) ";
        String values = "VALUES ( '" + attacker.getUniqueId() + "','" + attacker.getName() + "','" + attackerTag + "','" + victim.getUniqueId() + "','" + victim.getName() + "','" + victimTag + "','" + type + "');";
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
        String query = "INSERT INTO `sc_kills` (  `attacker_uuid`, `attacker`, `attacker_tag`, `victim_uuid`, " + "`victim`, `victim_tag`, `kill_type`, `created_at`) ";
        String values = "VALUES ( '" + attacker.getUniqueId() + "','" + attacker.getName() + "','" + attacker.getTag() + "','" + victim.getUniqueId() + "','" + victim.getName() + "','" + victim.getTag() + "','" + type + "','" + time + "');";
        core.executeUpdate(query + values);
    }

    /**
     * Delete a player's kill record form the database
     *
     */
    @Deprecated
    public void deleteKills(String playerName) {
        String query = "DELETE FROM `" + getPrefixedTable("kills") + "` WHERE `attacker` = '" + playerName + "'";
        core.executeUpdate(query);
    }

    /**
     * Delete a player's kill record form the database
     *
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
     *
     */
    public Map<String, Integer> getKillsPerPlayer(String playerName) {
        HashMap<String, Integer> out = new HashMap<>();

        String query = "SELECT victim, count(victim) AS kills FROM `" + getPrefixedTable("kills") + "` WHERE attacker = '" + playerName + "' GROUP BY victim ORDER BY count(victim) DESC;";
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

        String query = "SELECT attacker, victim, count(victim) AS kills FROM `" + getPrefixedTable("kills") + "` GROUP BY attacker, victim ORDER BY 3 DESC;";
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
     * Gets, asynchronously, a map of victim-{@literal >}count of all kills that specific player did and notifies via callback when it's ready
     *
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
         */
        void onResultReady(T data);
    }

    /**
     * Updates the database to the latest version
     *
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
        if (!core.existsColumn(getPrefixedTable("kills"), "created_at")) {
            query = "ALTER TABLE sc_kills ADD `created_at` datetime NULL;";
            core.execute(query);
        }
    }

    /**
     * Updates the database to the latest version
     *
     */
    private void updatePlayersToUUID() {
        logMigrationStart();

        List<ClanPlayer> cps = retrieveClanPlayers();
        Map<String, UUID> uuidMap = fetchUUIDs(cps);

        int totalPlayers = cps.size();
        for (int i = 0; i < totalPlayers; i++) {
            ClanPlayer cp = cps.get(i);
            try {
                UUID uuid = uuidMap.get(cp.getName());
                if (uuid != null) {
                    updatePlayerInDatabase(cp.getName(), uuid);
                    logSuccess(i + 1, totalPlayers, cp.getName(), uuid);
                }
            } catch (Exception ex) {
                logFailure(i + 1, totalPlayers, cp.getName(), ex);
            }
        }

        logMigrationEnd(totalPlayers);
    }

    private void updatePlayerInDatabase(String playerName, UUID uuid) {
        String[] tables = {"players", "kills", "kills"};
        String[] columns = {"uuid", "attacker_uuid", "victim_uuid"};
        String[] conditions = {"name", "attacker", "victim"};

        for (int i = 0; i < tables.length; i++) {
            String query = String.format("UPDATE `%s` SET %s = '%s' WHERE %s = '%s';", getPrefixedTable(tables[i]), columns[i], uuid.toString(), conditions[i], playerName);
            core.executeUpdate(query);
        }
    }

    private Map<String, UUID> fetchUUIDs(List<ClanPlayer> clanPlayers) {
        Map<String, UUID> uuidMap = new HashMap<>();

        try {
            if (SimpleClans.getInstance().getServer().getOnlineMode()) {
                uuidMap = UUIDFetcher.fetchUUIDsForClanPlayers(clanPlayers);
            } else {
                uuidMap = clanPlayers.stream().collect(Collectors.toMap(ClanPlayer::getName, player -> UUID.nameUUIDFromBytes(("OfflinePlayer:" + player.getName()).getBytes(StandardCharsets.UTF_8))));
            }
        } catch (InterruptedException | ExecutionException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error fetching UUIDs in bulk: " + ex.getMessage(), ex);
        }

        return uuidMap;
    }

    private void logSuccess(int current, int total, String playerName, UUID uuid) {
        plugin.getLogger().info(String.format("[%d / %d] Success: %s; UUID: %s", current, total, playerName, uuid));
    }

    private void logFailure(int current, int total, String playerName, Exception ex) {
        plugin.getLogger().log(Level.WARNING, String.format("[%d / %d] Failed [ERROR]: %s; UUID: ???", current, total, playerName), ex);
    }

    private void logMigrationStart() {
        plugin.getLogger().log(Level.WARNING, "Starting Migration to UUID Players!");
        plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DON'T STOP BUKKIT! ====================");
        plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DON'T STOP BUKKIT! ====================");
        plugin.getLogger().log(Level.WARNING, "==================== ATTENTION DON'T STOP BUKKIT! ====================");
    }

    private void logMigrationEnd(int totalPlayers) {
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");
        plugin.getLogger().log(Level.WARNING, "==================== END OF MIGRATION ====================");

        if (totalPlayers > 0) {
            plugin.getLogger().info(MessageFormat.format(lang("clan.players"), totalPlayers));
        }
    }

    private String getPrefixedTable(String name) {
        return plugin.getSettingsManager().getString(MYSQL_TABLE_PREFIX) + name;
    }

    /**
     * Saves modified Clans and ClanPlayers to the database
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
        try (PreparedStatement pst = prepareUpdateClanStatement(core.getConnection())) {
            //removing disbanded clans
            modifiedClans.retainAll(plugin.getClanManager().getClans());
            for (Clan clan : modifiedClans) {
                setValues(pst, clan);
                pst.addBatch();
            }
            pst.executeBatch();

            modifiedClans.clear();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Error saving modified Clans:", ex);
        }
    }
}