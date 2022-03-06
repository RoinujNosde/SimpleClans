package net.sacredlabyrinth.phaed.simpleclans;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.Placeholder;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLog;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.EconomyResponse.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.events.ClanBalanceUpdateEvent.Cause;
import static net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger.Operation.*;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.*;

/**
 * @author phaed
 */
public class Clan implements Serializable, Comparable<Clan> {

    private static final long serialVersionUID = 1L;
    private static final String WARRING_KEY = "warring";
    private boolean verified;
    private String tag;
    private String colorTag;
    private String name;
    private String description;
    private double balance;
    private double fee;
    private boolean friendlyFire;
    private long founded;
    private long lastUsed;
    private String capeUrl;
    private List<String> allies = new ArrayList<>();
    private List<String> rivals = new ArrayList<>();
    private List<String> bb = new ArrayList<>();
    private final List<String> members = new ArrayList<>();
    private Flags flags = new Flags(null);
    private boolean feeEnabled;
    private List<Rank> ranks = new ArrayList<>();
    private @Nullable String defaultRank = null;
    private @Nullable ItemStack banner;

    /**
     *
     */
    public Clan() {
        this.capeUrl = "";
        this.tag = "";
    }

    public Clan(String tag, String name, boolean verified) {
        this.tag = Helper.cleanTag(tag);
        this.colorTag = ChatUtils.parseColors(tag);
        this.name = name;
        this.founded = (new Date()).getTime();
        this.lastUsed = (new Date()).getTime();
        this.verified = verified;
        this.capeUrl = "";
        if (SimpleClans.getInstance().getSettingsManager().is(CLAN_FF_ON_BY_DEFAULT)) {
            friendlyFire = true;
        }
    }

    @Override
    public int hashCode() {
        return getTag().hashCode() >> 13;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Clan)) {
            return false;
        }

        Clan other = (Clan) obj;
        return other.getTag().equals(this.getTag());
    }

    @Override
    public int compareTo(Clan other) {
        return this.getTag().compareToIgnoreCase(other.getTag());
    }

    @Override
    public String toString() {
        return tag;
    }

    /**
     * Deposits money to the clan
     */
    @Deprecated
    public void deposit(double amount, Player player) {
        if (SimpleClans.getInstance().getPermissionsManager().playerHasMoney(player, amount)) {
            if (SimpleClans.getInstance().getPermissionsManager().playerChargeMoney(player, amount)) {
                player.sendMessage(AQUA + lang("player.clan.deposit", player, amount));
                addBb(player.getName(), AQUA + lang("bb.clan.deposit", amount));
                setBalance(getBalance() + amount);
                SimpleClans.getInstance().getStorageManager().updateClan(this);
            } else {
                player.sendMessage(AQUA + lang("not.sufficient.money", player, amount));
            }
        } else {
            player.sendMessage(AQUA + lang("not.sufficient.money", player, amount));
        }
    }

    /**
     * Deposits money to the clan
     */
    public EconomyResponse deposit(@Nullable CommandSender sender, @NotNull Cause cause, double amount) {
        EconomyResponse response = null;

        if (amount < 0) {
            response = NEGATIVE_VALUE;
        }

        if (response == null) {
            response = setBalance(sender, cause, DEPOSIT, getBalance() + amount);
        }

        SimpleClans.getInstance().getBankLogger().log(new BankLog(sender, this, response, DEPOSIT, cause, amount));
        return response;
    }

    /**
     * Withdraws money from the clan
     */
    @Deprecated
    public void withdraw(double amount, Player player) {
        if (getBalance() >= amount) {
            if (SimpleClans.getInstance().getPermissionsManager().playerGrantMoney(player, amount)) {
                player.sendMessage(AQUA + lang("player.clan.withdraw", player, amount));
                addBb(player.getName(), AQUA + lang("bb.clan.withdraw", amount));
                setBalance(getBalance() - amount);
            }
        } else {
            player.sendMessage(AQUA + lang("clan.bank.not.enough.money", player));
        }
    }

    /**
     * Withdraws money from the clan
     */
    public EconomyResponse withdraw(@Nullable CommandSender sender, @NotNull Cause cause, double amount) {
        EconomyResponse response = null;

        if (amount < 0) {
            response = NEGATIVE_VALUE;
        }

        if (getBalance() < amount) {
            response = NOT_ENOUGH_BALANCE;
        }

        if (response == null) {
            response = setBalance(sender, cause, WITHDRAW, getBalance() - amount);
        }

        SimpleClans.getInstance().getBankLogger().log(new BankLog(sender, this, response, WITHDRAW, cause, amount));
        return response;
    }

    /**
     * Returns the clan's name
     *
     * @return the name
     */
    @Placeholder("name")
    public String getName() {
        return name;
    }

    /**
     * (used internally)
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the clan's description
     *
     * @return the description or null if it doesn't have one
     */
    public String getDescription() {
        return description;
    }

    /**
     * (used internally)
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the clan's fee
     */
    public void setMemberFee(double fee) {
        if (fee < 0) {
            fee = 0;
        }
        this.fee = fee;
    }

    /**
     * Returns the clan's fee
     *
     * @return the fee
     */
    public double getMemberFee() {
        return fee;
    }

    /**
     * Returns the clan's balance
     *
     * @return the balance
     */
    @Placeholder("balance")
    public double getBalance() {
        return balance;
    }

    /**
     * (used internally)
     *
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        setBalance(null, Cause.API, SET, balance);
    }

    public EconomyResponse setBalance(@Nullable CommandSender updater, @NotNull Cause cause, @NotNull BankLogger.Operation operation, double balance) {
        EconomyResponse response = SUCCESS;

        ClanBalanceUpdateEvent event = new ClanBalanceUpdateEvent(updater, this, getBalance(), balance, cause);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            response = CANCELLED;
        }

        this.balance = event.getNewBalance();
        if (cause != Cause.LOADING) {
            if (operation == SET) {
                SimpleClans.getInstance().getBankLogger().log(new BankLog(updater, this, response, SET, cause, balance));
            }
            SimpleClans.getInstance().getStorageManager().updateClan(this);
        }
        return response;
    }

    /**
     * Returns the clan's tag clean (no colors)
     *
     * @return the tag
     */
    @Placeholder("tag")
    public String getTag() {
        return tag;
    }

    /**
     * (used internally)
     *
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the first color in the clan's tag
     *
     * @return the color code or an empty string if there is no color
     */
    @Placeholder("color")
    public String getColor() {
        if (colorTag.startsWith("\u00a7x")) { // Hexadecimal Code
            return colorTag.substring(0, 14);
        } else if (colorTag.charAt(0) == '\u00a7') { // Regular Code
            return colorTag.substring(0, 2);
        } else { // No Code
            return "";
        }
    }

    /**
     * Returns the last used date in milliseconds
     *
     * @return the lastUsed
     */
    public long getLastUsed() {
        return lastUsed;
    }

    /**
     * Updates last used date to today (does not update clan on db)
     */
    public void updateLastUsed() {
        setLastUsed((new Date()).getTime());
    }

    /**
     * Returns the number of days the clan has been inactive
     */
    @Placeholder("inactivedays")
    public int getInactiveDays() {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastUsed()), now));
    }

    /**
     * Returns the max number of days the clan can be inactive
     * A {@literal <=} 0 means it won't be purged
     */
    public int getMaxInactiveDays() {
        if (isPermanent()) {
            return -1;
        }

        int verifiedClanInactiveDays = SimpleClans.getInstance().getSettingsManager().getInt(PURGE_INACTIVE_CLAN_DAYS);
        int unverifiedClanInactiveDays = SimpleClans.getInstance().getSettingsManager().getInt(PURGE_UNVERIFIED_CLAN_DAYS);

        return isVerified() ? verifiedClanInactiveDays : unverifiedClanInactiveDays;
    }

    /**
     * (used internally)
     *
     * @param lastUsed the lastUsed to set
     */
    public void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * Check whether this clan allows friendly fire
     *
     * @return the friendlyFire
     */
    @Placeholder("friendly_fire")
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    /**
     * Sets the friendly fire status of this clan (does not update clan on db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    /**
     * Check if the player is a member of this clan
     *
     * @param player the Player
     * @return confirmation
     */
    public boolean isMember(Player player) {
        return members.contains(player.getUniqueId().toString());
    }

    /**
     * Check if the player is a member of this clan
     *
     * @param playerUniqueId the Player's UUID
     * @return confirmation
     */
    public boolean isMember(UUID playerUniqueId) {
        return members.contains(playerUniqueId.toString());
    }

    @SuppressWarnings("deprecation")
    public boolean isMember(String playerName) {
        return isMember(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Returns a list with the contents of the bulletin board
     *
     * @return the bb
     */
    public List<String> getBb() {
        return Collections.unmodifiableList(bb);
    }

    /**
     * Return a list of all the allies' tags clean (no colors)
     *
     * @return the allies
     */
    @Placeholder(value = "allies_count", resolver = "list_size")
    public List<String> getAllies() {
        return Collections.unmodifiableList(allies);
    }

    private void addAlly(String tag) {
        allies.add(tag);
    }

    private boolean removeAlly(String ally) {
        if (!allies.contains(ally)) {
            return false;
        }

        allies.remove(ally);
        return true;
    }

    /**
     * The founded date in milliseconds
     *
     * @return the founded
     */
    public long getFounded() {
        return founded;
    }

    /**
     * The string representation of the founded date
     */
    @Placeholder("founded")
    public String getFoundedString() {
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.founded));
    }

    /**
     * (used internally)
     *
     * @param founded the founded to set
     */
    public void setFounded(long founded) {
        this.founded = founded;
    }

    /**
     * Returns the color tag for this clan
     *
     * @return the colorTag
     */
    @Placeholder("color_tag")
    public String getColorTag() {
        return colorTag;
    }

    /**
     * (used internally)
     *
     * @param colorTag the colorTag to set
     */
    public void setColorTag(String colorTag) {
        this.colorTag = ChatUtils.parseColors(colorTag);
    }

    /**
     * Adds a bulletin board message without announcer
     */
    public void addBb(String msg) {
        addBbWithoutSaving(msg);
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }


    /**
     * Adds a bulletin board message without saving it to the database
     */
    public void addBbWithoutSaving(String msg) {
        while (bb.size() > SimpleClans.getInstance().getSettingsManager().getInt(BB_SIZE)) {
            bb.remove(0);
        }

        bb.add(System.currentTimeMillis() + "_" + msg);
    }

    /**
     * Adds a bulletin board message without announcer and saves it to the database
     *
     * @param updateLastUsed should the clan's last used time be updated as well?
     */
    public void addBb(String msg, boolean updateLastUsed) {
        addBbWithoutSaving(msg);
        SimpleClans.getInstance().getStorageManager().updateClan(this, updateLastUsed);
    }

    /**
     * Clears the bulletin board
     */
    public void clearBb() {
        bb.clear();
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    /**
     * (used internally)
     */
    public void importMember(ClanPlayer cp) {
        String uuid;
        if (cp.getUniqueId() != null) {
            uuid = cp.getUniqueId().toString();
        } else {
            return;
        }

        if (!members.contains(uuid)) {
            members.add(uuid);
        }
    }

    /**
     * (used internally)
     */
    public void removeMember(UUID playerUniqueId) {
        members.remove(playerUniqueId.toString());
    }

    /**
     * Get total clan size
     */
    @Placeholder("size")
    public int getSize() {
        return members.size();
    }

    /**
     * Returns a list of all rival tags clean (no colors)
     *
     * @return the rivals
     */
    @Placeholder(value = "rivals_count", resolver = "list_size")
    public List<String> getRivals() {
        return Collections.unmodifiableList(rivals);
    }

    private void addRival(String tag) {
        rivals.add(tag);
    }

    private boolean removeRival(String rival) {
        return rivals.remove(rival);
    }

    /**
     * Check if the tag is a rival
     */
    public boolean isRival(String tag) {
        return rivals.contains(tag);
    }

    /**
     * Check if the tag is an ally
     */
    public boolean isAlly(String tag) {
        return allies.contains(tag);
    }

    /**
     * Tells you if the clan is verified, always returns true if no verification
     * is required
     */
    @Placeholder("is_verified")
    public boolean isVerified() {
        return !SimpleClans.getInstance().getSettingsManager().is(REQUIRE_VERIFICATION) || verified;

    }

    /**
     * (used internally)
     *
     * @param verified the verified to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Placeholder("is_permanent")
    public boolean isPermanent() {
        return flags.getBoolean("permanent", false);
    }

    public void setPermanent(boolean permanent) {
        flags.put("permanent", permanent);
    }

    /**
     * Returns the cape url for this clan
     *
     * @return the capeUrl
     */
    @Deprecated
    public String getCapeUrl() {
        return capeUrl;
    }

    /**
     * (used internally)
     *
     * @param capeUrl the capeUrl to set
     */
    @Deprecated
    public void setCapeUrl(String capeUrl) {
        this.capeUrl = capeUrl;
    }

    /**
     * (used internally)
     *
     * @return the packedBb
     */
    public String getPackedBb() {
        return String.join("|", bb);
    }

    /**
     * (used internally)
     *
     * @param packedBb the packedBb to set
     */
    public void setPackedBb(String packedBb) {
        bb = Helper.fromArrayToList(packedBb.split("[|]"));
    }

    /**
     * (used internally)
     *
     * @return the packedAllies
     */
    public String getPackedAllies() {
        return String.join("|", allies);
    }

    /**
     * (used internally)
     *
     * @param packedAllies the packedAllies to set
     */
    public void setPackedAllies(String packedAllies) {
        allies = Helper.fromArrayToList(packedAllies.split("[|]"));
    }

    /**
     * (used internally)
     *
     * @return the packedRivals
     */
    public String getPackedRivals() {
        return String.join("|", rivals);
    }

    /**
     * (used internally)
     *
     * @param packedRivals the packedRivals to set
     */
    public void setPackedRivals(String packedRivals) {
        rivals = Helper.fromArrayToList(packedRivals.split("[|]"));
    }

    /**
     * Returns a separator delimited string with all the ally clan's colored
     * tags
     */
    public String getAllyString(String sep, @Nullable CommandSender viewer) {
        String coloredAllies = getAllies().stream().
                map(allyTag -> SimpleClans.getInstance().getClanManager().getClan(allyTag)).
                filter(Objects::nonNull).
                map(Clan::getColorTag).
                collect(Collectors.joining(sep));

        return coloredAllies.isEmpty() ? lang("none", viewer) : coloredAllies;
    }

    /**
     * @deprecated use {@link Clan#getAllyString(String, CommandSender)}
     */
    @Deprecated
    public String getAllyString(String sep) {
        return getAllyString(sep, null);
    }

    /**
     * Returns a separator delimited string with all the rival clan's colored
     * tags
     */
    public String getRivalString(String sep, @Nullable CommandSender viewer) {
        String coloredRivals = getRivals().stream().
                map(rivalTag -> SimpleClans.getInstance().getClanManager().getClan(rivalTag)).
                filter(Objects::nonNull).
                map(rival -> isWarring(rival) ? DARK_RED + "[" + rival.getTag() + "]" : rival.getColorTag()).
                map(ChatUtils::parseColors).
                collect(Collectors.joining(sep));

        return coloredRivals.isEmpty() ? lang("none", viewer) : coloredRivals;
    }

    /**
     * @deprecated use {@link Clan#getRivalString(String, CommandSender)}
     */
    @Deprecated
    public String getRivalString(String sep) {
        return getRivalString(sep, null);
    }

    /**
     * Returns a separator delimited string with all the leaders
     *
     * @return the formatted leaders string
     */
    public String getLeadersString(String prefix, String sep) {
        return members.stream().
                map(member -> SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member))).
                filter(Objects::nonNull).
                filter(ClanPlayer::isLeader).
                map(clanPlayer -> prefix.concat(clanPlayer.getName())).
                collect(Collectors.joining(sep));
    }

    /**
     * Check if a player is a leader of a clan
     *
     * @return the leaders
     */
    public boolean isLeader(Player player) {
        return isLeader(player.getUniqueId());
    }


    /**
     * Check if a player is a leader of a clan
     *
     * @return the leaders
     */
    public boolean isLeader(UUID playerUniqueId) {
        if (isMember(playerUniqueId)) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);

            return cp != null && cp.isLeader();
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isLeader(String playerName) {
        return isLeader(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Get all members that must pay the fee (that excludes leaders and players with the permission to bypass it)
     *
     * @return the fee payers
     */
    public Set<ClanPlayer> getFeePayers() {
        PermissionsManager permissions = SimpleClans.getInstance().getPermissionsManager();
        return getNonLeaders().stream().filter(cp -> !permissions.has(cp.toPlayer(), "simpleclans.member.bypass-fee")).collect(Collectors.toSet());
    }

    /**
     * Get all members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getMembers() {
        List<ClanPlayer> out = new ArrayList<>();

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }
            out.add(cp);
        }

        Collections.sort(out);

        return out;
    }

    /**
     * Get all online members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    @Placeholder(value = "onlinemembers_count", resolver = "list_size", config = "filter_vanished")
    public List<ClanPlayer> getOnlineMembers() {
        List<ClanPlayer> out = new ArrayList<>();

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }
            if (cp.toPlayer() != null) {
                out.add(cp);
            }
        }

        return out;
    }


    /**
     * Get all leaders in the clan
     *
     * @return the leaders
     */
    @Placeholder(value = "leader_size", resolver = "list_size")
    public List<ClanPlayer> getLeaders() {
        List<ClanPlayer> out = new ArrayList<>();

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            if (cp.isLeader()) {
                out.add(cp);
            }
        }

        return out;
    }

    /**
     * Get all non-leader players in the clan
     *
     * @return non leaders
     */
    public List<ClanPlayer> getNonLeaders() {
        List<ClanPlayer> out = new ArrayList<>();

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            if (!cp.isLeader()) {
                out.add(cp);
            }
        }

        Collections.sort(out);

        return out;
    }

    /**
     * Get all clan's members
     *
     * @deprecated use {@link Clan#getMembers()}
     */
    @Deprecated
    public List<ClanPlayer> getAllMembers() {
        return getMembers();
    }

    /**
     * Get all the ally clan's members
     */
    public Set<ClanPlayer> getAllAllyMembers() {
        Set<ClanPlayer> out = new HashSet<>();

        for (String tag : allies) {
            Clan ally = SimpleClans.getInstance().getClanManager().getClan(tag);

            if (ally != null) {
                out.addAll(ally.getMembers());
            }
        }

        return out;
    }

    /**
     * Gets the clan's total KDR
     */
    @Placeholder(value = "total_kdr", resolver = "kdr")
    @Placeholder(value = "topclans_position", resolver = "ranking_position")
    public float getTotalKDR() {
        if (members.isEmpty()) {
            return 0;
        }

        double totalWeightedKills = 0;
        int totalDeaths = 0;

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            totalWeightedKills += cp.getWeightedKills();
            totalDeaths += cp.getDeaths();
        }

        if (totalDeaths == 0) {
            totalDeaths = 1;
        }

        return ((float) totalWeightedKills) / ((float) totalDeaths);
    }

    /**
     * Gets the clan's total KDR
     */
    @Placeholder("total_deaths")
    public int getTotalDeaths() {
        int totalDeaths = 0;

        if (members.isEmpty()) {
            return totalDeaths;
        }

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            totalDeaths += cp.getDeaths();
        }

        return totalDeaths;
    }

    /**
     * Gets average weighted kills for the clan
     */
    @Placeholder("average_wk")
    public int getAverageWK() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            total += cp.getWeightedKills();
        }

        return total / getSize();
    }

    @Placeholder("total_kills")
    public int getTotalKills() {
        return getTotalCivilian() + getTotalNeutral() + getTotalRival() + getTotalAlly();
    }

    /**
     * Gets total rival kills for the clan
     */
    @Placeholder("total_rival")
    public int getTotalRival() {
        int total = 0;

        for (ClanPlayer cp : getMembers()) {
            total += cp.getRivalKills();
        }

        return total;
    }

    /**
     * Gets total neutral kills for the clan
     */
    @Placeholder("total_neutral")
    public int getTotalNeutral() {
        int total = 0;

        for (ClanPlayer cp : getMembers()) {
            total += cp.getNeutralKills();
        }

        return total;
    }

    /**
     * Gets total civilian kills for the clan
     */
    @Placeholder("total_civilian")
    public int getTotalCivilian() {
        int total = 0;

        for (ClanPlayer cp : getMembers()) {
            total += cp.getCivilianKills();
        }

        return total;
    }

    @Placeholder("total_ally")
    public int getTotalAlly() {
        int total = 0;

        for (ClanPlayer cp : getMembers()) {
            total += cp.getAllyKills();
        }

        return total;
    }

    /**
     * Check whether the clan has crossed the rival limit
     */
    public boolean reachedRivalLimit() {
        int rivalCount = rivals.size();
        int clanCount = SimpleClans.getInstance().getClanManager().getRivableClanCount() - 1;
        double rivalPercent = SimpleClans.getInstance().getSettingsManager().getPercent(RIVAL_LIMIT_PERCENT);

        double limit = ((double) clanCount) * (rivalPercent / ((double) 100));

        return rivalCount > limit;
    }

    /**
     * Add a new player to the clan
     */
    public void addPlayerToClan(ClanPlayer cp) {
        cp.removePastClan(getColorTag());
        cp.setClan(this);
        cp.setLeader(false);
        cp.setTrusted(SimpleClans.getInstance().getSettingsManager().is(CLAN_TRUST_MEMBERS_BY_DEFAULT));
        if (defaultRank != null) {
            cp.setRank(defaultRank);
        }

        importMember(cp);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        // add clan permission
        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
        SimpleClans.getInstance().getPermissionsManager().addPlayerPermissions(cp);

        Player player = SimpleClans.getInstance().getServer().getPlayer(cp.getUniqueId());

        if (player != null) {
            SimpleClans.getInstance().getClanManager().updateDisplayName(player);
        }
        Bukkit.getPluginManager().callEvent(new PlayerJoinedClanEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void removePlayerFromClan(String playerName) {
        removePlayerFromClan(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Remove a player from a clan
     */
    public void removePlayerFromClan(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);
        if (cp == null || !isMember(playerUniqueId)) {
            return;
        }

        // remove clan group-permission
        SimpleClans.getInstance().getPermissionsManager().removeClanPermissions(cp);

        // remove permissions
        SimpleClans.getInstance().getPermissionsManager().removeClanPlayerPermissions(cp);

        cp.setClan(null);
        cp.addPastClan(getColorTag() + (cp.isLeader() ? DARK_RED + "*" : ""));
        cp.setLeader(false);
        cp.setTrusted(false);
        cp.setJoinDate(0);
        cp.setRank(null);
        removeMember(playerUniqueId);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        Player matched = SimpleClans.getInstance().getServer().getPlayer(playerUniqueId);

        if (matched != null) {
            SimpleClans.getInstance().getClanManager().updateDisplayName(matched);
        }
        Bukkit.getPluginManager().callEvent(new PlayerKickedClanEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void promote(String playerName) {
        promote(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Promote a member to a leader of a clan
     */
    public void promote(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(playerUniqueId);

        cp.setLeader(true);
        cp.setTrusted(true);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        // add clan permission
        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
        Bukkit.getPluginManager().callEvent(new PlayerPromoteEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void demote(String playerName) {
        demote(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Demote a leader back to a member of a clan
     */
    public void demote(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(playerUniqueId);

        cp.setLeader(false);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        // add clan permission
        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
        Bukkit.getPluginManager().callEvent(new PlayerDemoteEvent(this, cp));
    }

    /**
     * Add an ally to a clan, and the clan to the ally
     */
    public void addAlly(Clan ally) {
        removeRival(ally.getTag());
        addAlly(ally.getTag());

        ally.removeRival(getTag());
        ally.addAlly(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(ally);
        Bukkit.getPluginManager().callEvent(new AllyClanAddEvent(this, ally));
    }

    /**
     * Remove an ally form the clan, and the clan from the ally
     */
    public void removeAlly(Clan ally) {
        removeAlly(ally.getTag());
        ally.removeAlly(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(ally);
        Bukkit.getPluginManager().callEvent(new AllyClanRemoveEvent(this, ally));
    }

    /**
     * Add a rival to the clan, and the clan to the rival
     */
    public void addRival(Clan rival) {
        removeAlly(rival.getTag());
        addRival(rival.getTag());

        rival.removeAlly(getTag());
        rival.addRival(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(rival);
        Bukkit.getPluginManager().callEvent(new RivalClanAddEvent(this, rival));
    }

    /**
     * Removes a rival from the clan, the clan from the rival
     */
    public void removeRival(Clan rival) {
        removeRival(rival.getTag());
        rival.removeRival(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(rival);
        Bukkit.getPluginManager().callEvent(new RivalClanRemoveEvent(this, rival));
    }

    /**
     * Verify a clan
     */
    public void verifyClan() {
        setVerified(true);
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    /**
     * Check whether any clan member is online
     */
    @Placeholder("is_anyonline")
    public boolean isAnyOnline() {
        return members.stream().map(UUID::fromString).map(Bukkit::getPlayer).anyMatch(Objects::nonNull);
    }

    /**
     * Checks if there are enough leaders online to vote
     *
     * @param cp the one to demote
     * @return true if there are
     */
    public boolean enoughLeadersOnlineToDemote(ClanPlayer cp) {
        List<ClanPlayer> online = getOnlineLeaders();
        online.remove(cp);

        double minimum = SimpleClans.getInstance().getSettingsManager().getPercent(CLAN_PERCENTAGE_ONLINE_TO_DEMOTE);
        // all leaders minus the one being demoted
        double totalLeaders = getLeaders().size() - 1;
        double onlineLeaders = online.size();


        return ((onlineLeaders / totalLeaders) * 100) >= minimum;
    }

    /**
     * Gets the online leaders
     *
     * @return the online leaders
     */
    public List<ClanPlayer> getOnlineLeaders() {
        return getOnlineMembers().stream().filter(ClanPlayer::isLeader).collect(Collectors.toList());
    }

    /**
     * Check whether all leaders of a clan are online
     */
    public boolean allLeadersOnline() {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.toPlayer() == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     */
    @Deprecated
    public boolean allOtherLeadersOnline(String playerName) {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.getName().equalsIgnoreCase(playerName)) {
                continue;
            }

            if (leader.toPlayer() == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     */
    public boolean allOtherLeadersOnline(UUID playerUniqueId) {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.getUniqueId().equals(playerUniqueId)) {
                continue;
            }

            if (leader.toPlayer() == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Change a clan's tag
     */
    public void changeClanTag(String tag) {
        setColorTag(tag);
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    /**
     * Announce message to a whole clan
     */
    public void clanAnnounce(String playerName, String msg) {
        String message = SimpleClans.getInstance().getSettingsManager().getColored(CLANCHAT_ANNOUNCEMENT_COLOR) + msg;

        for (ClanPlayer cp : getMembers()) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }

        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(AQUA + "[" + lang("clan.announce") + AQUA + "] " + AQUA + "[" + Helper.getColorName(playerName) + WHITE + "] " + message);
    }

    /**
     * Announce message to a all the leaders of a clan
     */
    public void leaderAnnounce(String msg) {
        String message = SimpleClans.getInstance().getSettingsManager().getColored(CLANCHAT_ANNOUNCEMENT_COLOR) + msg;

        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer cp : leaders) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }
        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(AQUA + "[" + lang("leader.announce") + AQUA + "] " + WHITE + message);
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     */
    public void addBb(String announcerName, String msg) {
        if (isVerified()) {
            addBb(SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR) + msg);
            clanAnnounce(announcerName, SimpleClans.getInstance().getSettingsManager().getColored(BB_ACCENT_COLOR) + "* " + SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR) + ChatUtils.parseColors(msg));
        }
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     */
    public void addBb(String announcerName, String msg, boolean updateLastUsed) {
        if (isVerified()) {
            addBb(SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR) + msg, updateLastUsed);
            clanAnnounce(announcerName, SimpleClans.getInstance().getSettingsManager().getColored(BB_ACCENT_COLOR) + "* " + SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR) + ChatUtils.parseColors(msg));
        }
    }

    /**
     * Displays bb to a player
     */
    public void displayBb(Player player) {
        displayBb(player, -1);
    }

    /**
     * Displays bb to a player
     *
     * @param maxSize amount of lines to display
     */
    public void displayBb(Player player, int maxSize) {
        if (isVerified()) {
            ChatBlock.sendBlank(player);
            String bbAccentColor = SimpleClans.getInstance().getSettingsManager().getColored(BB_ACCENT_COLOR);
            String pageHeadingsColor = SimpleClans.getInstance().getSettingsManager().getColored(PAGE_HEADINGS_COLOR);
            ChatBlock.saySingle(player, lang("bulletin.board.header", bbAccentColor, pageHeadingsColor, getName()));

            List<String> localBb;
            if (maxSize == -1) {
                localBb = bb;
                maxSize = SimpleClans.getInstance().getSettingsManager().getInt(BB_SIZE);
            } else {
                localBb = new ArrayList<>(bb);
            }
            while (localBb.size() > maxSize) {
                localBb.remove(0);
            }

            for (String msg : localBb) {
                if (!sendBbTime(player, msg)) {
                    String bbColor = SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR);
                    ChatBlock.sendMessage(player, bbAccentColor + "* " + bbColor + ChatUtils.parseColors(msg));
                }
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Sends a bb message with the timestamp in a hover message, if the bb message is timestamped
     *
     * @param msg the bb message
     * @return true if sent
     */
    @SuppressWarnings("deprecation")
    private boolean sendBbTime(Player player, String msg) {
        try {
            int index = msg.indexOf("_");
            if (index < 1) {
                return false;
            }
            long time = (System.currentTimeMillis() - Long.parseLong(msg.substring(0, index))) / 1000L;
            String bbAccentColor = SimpleClans.getInstance().getSettingsManager().getColored(BB_ACCENT_COLOR);
            String bbColor = SimpleClans.getInstance().getSettingsManager().getColored(BB_COLOR);
            msg = ChatUtils.parseColors(bbAccentColor + "* " + bbColor + msg.substring(++index));
            TextComponent textComponent = new TextComponent(msg);
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                    Dates.formatTime(time, 1) + lang("bb.ago"))));
            player.spigot().sendMessage(textComponent);
            return true;
        } catch (Throwable rock) {
            return false;
        }
    }

    /**
     * Disbands the clan
     *
     * @param sender   who is trying to disband
     * @param announce should it be announced?
     * @param force    should it be force disbanded?
     */
    public void disband(@Nullable CommandSender sender, boolean announce, boolean force) {
        Collection<ClanPlayer> clanPlayers = SimpleClans.getInstance().getClanManager().getAllClanPlayers();
        List<Clan> clans = SimpleClans.getInstance().getClanManager().getClans();

        if (isPermanent() && !force) {
            ChatBlock.sendMessage(sender, RED + lang("cannot.disband.permanent"));
            return;
        }

        if (announce) {
            if (SimpleClans.getInstance().getSettingsManager().is(DISABLE_MESSAGES) && sender != null) {
                clanAnnounce(sender.getName(), AQUA + lang("clan.has.been.disbanded", getName()));
            } else {
                SimpleClans.getInstance().getClanManager().serverAnnounce(AQUA + lang("clan.has.been.disbanded", getName()));
            }
        }

        SimpleClans.getInstance().getPermissionsManager().removeClanPermissions(this);
        for (ClanPlayer cp : clanPlayers) {
            if (cp.getTag().equals(getTag())) {
                cp.setClan(null);

                if (isVerified()) {
                    cp.addPastClan(getColorTag() + (cp.isLeader() ? DARK_RED + "*" : ""));
                }

                cp.setLeader(false);
            }
        }

        Bukkit.getPluginManager().callEvent(new DisbandClanEvent(sender, this));
        clans.remove(this);

        for (Clan c : clans) {
            String disbanded = lang("clan.disbanded");

            if (c.removeWarringClan(this)) {
                c.addBb(disbanded, AQUA + lang("you.are.no.longer.at.war", c.getName(), getColorTag()));
            }

            if (c.removeRival(getTag())) {
                c.addBb(disbanded, AQUA + lang("has.been.disbanded.rivalry.ended", getName()));
            }

            if (c.removeAlly(getTag())) {
                c.addBb(disbanded, AQUA + lang("has.been.disbanded.alliance.ended", getName()));
            }
        }
        SimpleClans.getInstance().getRequestManager().removeRequest(getTag());

        SimpleClans.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(SimpleClans.getInstance(), () -> {
            SimpleClans.getInstance().getClanManager().removeClan(getTag());
            SimpleClans.getInstance().getStorageManager().deleteClan(this);
        }, 1);
    }

    public void disband() {
        disband(null, true, false);
    }

    /**
     * Whether this clan can be rivaled
     */
    @Placeholder("is_unrivable")
    public boolean isUnrivable() {
        return SimpleClans.getInstance().getSettingsManager().isUnrivable(getTag());
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param tag the tag of the clan we are at war with
     */
    public boolean isWarring(String tag) {
        return flags.getStringList(WARRING_KEY).contains(tag);
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param clan the clan we are testing against
     */
    public boolean isWarring(Clan clan) {
        return isWarring(clan.getTag());
    }

    /**
     * Add a clan to be at war with
     */
    public void addWarringClan(@Nullable ClanPlayer requestPlayer, Clan targetClan) {
        List<String> warring = flags.getStringList(WARRING_KEY);
        if (!warring.contains(targetClan.getTag())) {
            warring.add(targetClan.getTag());
            flags.put(WARRING_KEY, warring);
            if (requestPlayer != null) {
                addBb(requestPlayer.getName(), AQUA + lang("you.are.at.war",
                        getName(), targetClan.getColorTag()));
            }
            SimpleClans.getInstance().getStorageManager().updateClan(this);
        }
    }

    public void addWarringClan(Clan targetClan) {
        addWarringClan(null, targetClan);
    }

    /**
     * Remove a warring clan
     */
    public boolean removeWarringClan(Clan clan) {
        List<String> warring = flags.getStringList(WARRING_KEY);
        if (warring.remove(clan.getTag())) {
            flags.put(WARRING_KEY, warring);
            SimpleClans.getInstance().getStorageManager().updateClan(this);
            return true;
        }

        return false;
    }

    /**
     * Return a collection of all the warring clans
     *
     * @return the clan list
     */
    public List<Clan> getWarringClans() {
        return flags.getStringList(WARRING_KEY).stream().map(tag -> SimpleClans.getInstance().getClanManager()
                .getClan(tag)).collect(Collectors.toList());
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    public String getFlags() {
        return flags.toJSONString();
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString) {
        flags = new Flags(flagString);
    }

    public void validateWarring() {
        List<String> warring = flags.getStringList(WARRING_KEY);
        Iterator<String> iterator = warring.iterator();
        while (iterator.hasNext()) {
            String clanTag = iterator.next();
            Clan clan = SimpleClans.getInstance().getClanManager().getClan(clanTag);
            if (clan == null) {
                iterator.remove();
            }
        }
        flags.put(WARRING_KEY, warring);
    }

    public void setHomeLocation(@Nullable Location home) {
        flags.put("homeX", home != null ? home.getX() : 0);
        flags.put("homeY", home != null ? home.getY() : 0);
        flags.put("homeZ", home != null ? home.getZ() : 0);
        flags.put("homePitch", home != null ? home.getPitch() : 0);
        flags.put("homeYaw", home != null ? home.getYaw() : 0);
        String world = home != null && home.getWorld() != null ? home.getWorld().getName() : "";
        flags.put("homeWorld", world);

        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    public @Nullable Location getHomeLocation() {
        String homeWorld = flags.getString("homeWorld");
        if (homeWorld == null) {
            return null;
        }
        World world = Bukkit.getWorld(homeWorld);
        if (world == null) {
            return null;
        }
        double x = flags.getNumber("homeX").doubleValue();
        double y = flags.getNumber("homeY").doubleValue();
        double z = flags.getNumber("homeZ").doubleValue();
        float yaw = flags.getNumber("homeYaw").floatValue();
        float pitch = flags.getNumber("homePitch").floatValue();

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String getTagLabel(boolean isLeader) {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();
        String bracketColor = isLeader ? sm.getColored(TAG_BRACKET_LEADER_COLOR) : sm.getColored(TAG_BRACKET_COLOR);
        String bracketDefaultColor = sm.getColored(TAG_DEFAULT_COLOR);
        String bracketLeft = sm.getColored(TAG_BRACKET_LEFT);
        String bracketRight = sm.getColored(TAG_BRACKET_RIGHT);
        String tagSeparatorColor = isLeader ? sm.getColored(TAG_SEPARATOR_LEADER_COLOR) : sm.getColored(TAG_SEPARATOR_COLOR);
        String tagSeparator = sm.getString(TAG_SEPARATOR_CHAR);

        return bracketColor + bracketLeft + bracketDefaultColor + getColorTag() + bracketColor + bracketRight + tagSeparatorColor + tagSeparator;
    }

    /**
     * Checks if the fee is enabled
     *
     * @return true if enabled
     */
    public boolean isMemberFeeEnabled() {
        return feeEnabled;
    }

    /**
     * Enables or disables the fee
     */
    public void setMemberFeeEnabled(boolean enable) {
        feeEnabled = enable;
    }

    /**
     * @return the allowWithdraw
     */
    @Placeholder("allow_withdraw")
    public boolean isAllowWithdraw() {
        return flags.getBoolean("allowWithdraw", false);
    }

    /**
     * @param allowWithdraw the allowWithdraw to set
     */
    public void setAllowWithdraw(boolean allowWithdraw) {
        flags.put("allowWithdraw", allowWithdraw);
    }

    /**
     * @return the allowDeposit
     */
    @Placeholder("allow_deposit")
    public boolean isAllowDeposit() {
        return flags.getBoolean("allowDeposit", true);
    }

    /**
     * @param allowDeposit the allowDeposit to set
     */
    public void setAllowDeposit(boolean allowDeposit) {
        flags.put("allowDeposit", allowDeposit);
    }

    /**
     * Checks if the clan has the specified rank
     *
     * @param name the rank
     */
    public boolean hasRank(@Nullable String name) {
        return getRank(name) != null;
    }

    /**
     * Creates a rank
     */
    public void createRank(String name) {
        Rank rank = new Rank(name);
        ranks.add(rank);
    }

    /**
     * Returns the clan's ranks
     *
     * @return the ranks
     */
    public List<Rank> getRanks() {
        return ranks;
    }

    /**
     * Sets the clan's ranks
     */
    public void setRanks(@Nullable List<Rank> ranks) {
        if (ranks == null) {
            ranks = new ArrayList<>();
        }
        this.ranks = ranks;
    }

    /**
     * Deletes a rank with the specified name
     */
    public void deleteRank(String name) {
        Rank r = getRank(name);
        if (r != null) {
            ranks.remove(r);

            getMembers().forEach(cp -> {
                if (Objects.equals(cp.getRankId(), r.getName())) {
                    cp.setRank("");
                    SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
                }
            });
        }
    }

    /**
     * Gets a rank with the specified name or null if not found
     *
     * @param name the rank name
     * @return a rank or null
     */
    public @Nullable Rank getRank(@Nullable String name) {
        if (name != null) {
            for (Rank r : ranks) {
                if (r.getName().equals(name)) {
                    return r;
                }
            }
        }
        return null;
    }

    /**
     * Sets the default rank for this clan.
     *
     * @param name The name of the rank to be set as default
     */
    public void setDefaultRank(@Nullable String name) {
        // I don't know how this could happen, but if it somehow does, here's a check for it
        if (!hasRank(name)) {
            defaultRank = null;
        } else {
            defaultRank = name;
        }
    }

    /**
     * Gets the default rank for this clan.
     *
     * @return The default rank or null if there is no default
     */
    public @Nullable String getDefaultRank() {
        return defaultRank;
    }

    public void setBanner(@Nullable ItemStack banner) {
        if (banner == null) {
            this.banner = null;
            return;
        }
        banner = banner.clone();
        banner.setAmount(1);
        ItemMeta itemMeta = banner.getItemMeta();
        if (itemMeta != null) {
            // hides the banner patterns from the lore (I don't know why it's called POTION_EFFECTS)
            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemMeta.setLore(null);
            itemMeta.setDisplayName(null);
            banner.setItemMeta(itemMeta);
        }
        this.banner = banner;
    }

    public @Nullable ItemStack getBanner() {
        if (banner != null) {
            return banner.clone();
        }
        return null;
    }
}
