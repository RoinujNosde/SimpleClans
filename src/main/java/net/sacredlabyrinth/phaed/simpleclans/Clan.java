package net.sacredlabyrinth.phaed.simpleclans;

import com.cryptomorin.xseries.XMaterial;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.sacredlabyrinth.phaed.simpleclans.events.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author phaed
 */
public class Clan implements Serializable, Comparable<Clan> {

    private static final long serialVersionUID = 1L;
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
    private final HashMap<String, Clan> warringClans = new HashMap<>();
    private int homeX = 0;
    private int homeY = 0;
    private int homeZ = 0;
    private String homeWorld = "";
    private boolean allowWithdraw = false;
    private boolean allowDeposit = true;
    private boolean feeEnabled;
    private List<Rank> ranks = new ArrayList<>();
    private @Nullable ItemStack banner;

    /**
     *
     */
    public Clan() {
        this.capeUrl = "";
        this.tag = "";
    }

    /**
     * @param tag
     * @param name
     * @param verified
     */
    public Clan(String tag, String name, boolean verified) {
        this.tag = Helper.cleanTag(tag);
        this.colorTag = Helper.parseColors(tag);
        this.name = name;
        this.founded = (new Date()).getTime();
        this.lastUsed = (new Date()).getTime();
        this.verified = verified;
        this.capeUrl = "";

        if (SimpleClans.getInstance().getSettingsManager().isClanFFOnByDefault()) {
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
     * deposits money to the clan
     *
     * @param amount
     * @param player
     */
    public void deposit(double amount, Player player) {
        BankDepositEvent event = new BankDepositEvent(player, this, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        amount = event.getAmount();
        if (SimpleClans.getInstance().getPermissionsManager().playerHasMoney(player, amount)) {
            if (SimpleClans.getInstance().getPermissionsManager().playerChargeMoney(player, amount)) {
                player.sendMessage(ChatColor.AQUA + lang("player.clan.deposit", player, amount));
                addBb(player.getName(), ChatColor.AQUA + lang("bb.clan.deposit", amount));
                setBalance(getBalance() + amount);
                SimpleClans.getInstance().getStorageManager().updateClan(this);
            } else {
                player.sendMessage(ChatColor.AQUA + lang("not.sufficient.money",player));
            }
        } else {
            player.sendMessage(ChatColor.AQUA + lang("not.sufficient.money",player));
        }
    }

    /**
     * withdraws money to the clan
     *
     * @param amount
     * @param player
     */
    public void withdraw(double amount, Player player) {
        BankWithdrawEvent event = new BankWithdrawEvent(player, this, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        amount = event.getAmount();
        if (getBalance() >= amount) {
            if (SimpleClans.getInstance().getPermissionsManager().playerGrantMoney(player, amount)) {
                player.sendMessage(ChatColor.AQUA + lang("player.clan.withdraw", player, amount));
                addBb(player.getName(), ChatColor.AQUA + lang("bb.clan.withdraw", amount));
                setBalance(getBalance() - amount);
                SimpleClans.getInstance().getStorageManager().updateClan(this);
            }
        } else {
            player.sendMessage(ChatColor.AQUA + lang("clan.bank.not.enough.money",player));
        }
    }

    /**
     * Returns the clan's name
     *
     * @return the name
     */
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
     * 
     * @param description
     */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
     * Sets the clan's fee
     * 
     * @param fee 
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
    public double getBalance() {
        return balance;
    }

    /**
     * (used internally)
     *
     * @param balance the balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }

    /**
     * Returns the clan's tag clean (no colors)
     *
     * @return the tag
     */
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
     *
     * @return
     */
    public int getInactiveDays() {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastUsed()), now));
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
    	return this.members.contains(player.getUniqueId().toString());
    }

    /**
     * Check if the player is a member of this clan
     *
     * @param playerUniqueId the Player's UUID
     * @return confirmation
     */
    public boolean isMember(UUID playerUniqueId) {
        return this.members.contains(playerUniqueId.toString());
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
     *
     * @return
     */
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
    public String getColorTag() {
        return colorTag;
    }

    /**
     * (used internally)
     *
     * @param colorTag the colorTag to set
     */
    public void setColorTag(String colorTag) {
        this.colorTag = Helper.parseColors(colorTag);
    }

    /**
     * Adds a bulletin board message without announcer
     *
     * @param msg
     */
    public void addBb(String msg) {
        addBbWithoutSaving(msg);
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }
    
    
    /**
     * Adds a bulletin board message without saving it to the database
     * 
     * @param msg
     */
    public void addBbWithoutSaving(String msg) {
        while (bb.size() > SimpleClans.getInstance().getSettingsManager().getBbSize()) {
            bb.remove(0);
        }

        bb.add(System.currentTimeMillis() + "_" + msg);   
    }
    
    /**
     * Adds a bulletin board message without announcer and saves it to the database
     *
     * @param msg
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
     *
     * @param cp
     */
    public void importMember(ClanPlayer cp) {
        String uuid;
		if (cp.getUniqueId() != null) {
			uuid = cp.getUniqueId().toString();
		} else {
			return;
		}
     
        if (!this.members.contains(uuid)) {
            this.members.add(uuid);
        }
    }

    /**
     * (used internally)
     *
     * @param playerUniqueId
     */
    public void removeMember(UUID playerUniqueId) {
        this.members.remove(playerUniqueId.toString());
    }

    /**
     * Get total clan size
     *
     * @return
     */
    public int getSize() {
        return this.members.size();
    }

    /**
     * Returns a list of all rival tags clean (no colors)
     *
     * @return the rivals
     */
    public List<String> getRivals() {
        return Collections.unmodifiableList(rivals);
    }

    private void addRival(String tag) {
        rivals.add(tag);
    }

    private boolean removeRival(String rival) {
        if (!rivals.contains(rival)) {
            return false;
        }

        rivals.remove(rival);
        return true;
    }

    /**
     * Check if the tag is a rival
     *
     * @param tag
     * @return
     */
    public boolean isRival(String tag) {
        return rivals.contains(tag);
    }

    /**
     * Check if the tag is an ally
     *
     * @param tag
     * @return
     */
    public boolean isAlly(String tag) {
        return allies.contains(tag);
    }

    /**
     * Tells you if the clan is verified, always returns true if no verification
     * is required
     *
     * @return
     */
    public boolean isVerified() {
        return !SimpleClans.getInstance().getSettingsManager().isRequireVerification() || verified;

    }

    /**
     * (used internally)
     *
     * @param verified the verified to set
     */
    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * Returns the cape url for this clan
     *
     * @return the capeUrl
     */
    public String getCapeUrl() {
        return capeUrl;
    }

    /**
     * (used internally)
     *
     * @param capeUrl the capeUrl to set
     */
    public void setCapeUrl(String capeUrl) {
        this.capeUrl = capeUrl;
    }

    /**
     * (used internally)
     *
     * @return the packedBb
     */
    public String getPackedBb() {
        return Helper.toMessage(bb, "|");
    }

    /**
     * (used internally)
     *
     * @param packedBb the packedBb to set
     */
    public void setPackedBb(String packedBb) {
        this.bb = Helper.fromArray(packedBb.split("[|]"));
    }

    /**
     * (used internally)
     *
     * @return the packedAllies
     */
    public String getPackedAllies() {
        return Helper.toMessage(allies, "|");
    }

    /**
     * (used internally)
     *
     * @param packedAllies the packedAllies to set
     */
    public void setPackedAllies(String packedAllies) {
        this.allies = Helper.fromArray(packedAllies.split("[|]"));
    }

    /**
     * (used internally)
     *
     * @return the packedRivals
     */
    public String getPackedRivals() {
        return Helper.toMessage(rivals, "|");
    }

    /**
     * (used internally)
     *
     * @param packedRivals the packedRivals to set
     */
    public void setPackedRivals(String packedRivals) {
        this.rivals = Helper.fromArray(packedRivals.split("[|]"));
    }

    /**
     * Returns a separator delimited string with all the ally clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getAllyString(String sep) {
        String out = "";

        for (String allyTag : getAllies()) {
            Clan ally = SimpleClans.getInstance().getClanManager().getClan(allyTag);

            if (ally != null) {
                out += ally.getColorTag() + sep;
            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }

        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the rival clan's colored
     * tags
     *
     * @param sep
     * @return
     */
    public String getRivalString(String sep) {
        String out = "";

        for (String rivalTag : getRivals()) {
            Clan rival = SimpleClans.getInstance().getClanManager().getClan(rivalTag);

            if (rival != null) {
                if (isWarring(rivalTag)) {
                    out += ChatColor.DARK_RED + "[" + Helper.stripColors(rival.getColorTag()) + "]" + sep;
                } else {
                    out += rival.getColorTag() + sep;
                }

            }
        }

        out = Helper.stripTrailing(out, sep);

        if (out.trim().isEmpty()) {
            return ChatColor.BLACK + "None";
        }

        return Helper.parseColors(out);
    }

    /**
     * Returns a separator delimited string with all the leaders
     *
     * @param prefix
     * @param sep
     * @return the formatted leaders string
     */
    public String getLeadersString(String prefix, String sep) {
        String out = "";

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            if (cp.isLeader()) {
                out += prefix + cp.getName() + sep;
            }
        }

        return Helper.stripTrailing(out, sep);
    }

    /**
     * Check if a player is a leader of a clan
     *
     * @param player
     * @return the leaders
     */
    public boolean isLeader(Player player) {
    	return isLeader(player.getUniqueId());
    }


    /**
     * Check if a player is a leader of a clan
     *
     * @param playerUniqueId
     * @return the leaders
     */
    public boolean isLeader(UUID playerUniqueId) {
        if (isMember(playerUniqueId)) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);

            if (cp != null && cp.isLeader()) {
                return true;
            }
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
    	Set<ClanPlayer> feePayers = new HashSet<>();
    	
    	getNonLeaders().forEach(cp -> {
    		OfflinePlayer op=  Bukkit.getOfflinePlayer(cp.getUniqueId());
    		if (!SimpleClans.getInstance().getPermissionsManager().has(null, op, "simpleclans.member.bypass-fee")) {
        		feePayers.add(cp);
        	}
    	});
    	
    	return feePayers;
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

        return out;
    }

    /**
     * Get all online members (leaders, and non-leaders) in the clan
     *
     * @return the members
     */
    public List<ClanPlayer> getOnlineMembers() {
        List<ClanPlayer> out = new ArrayList<>();

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }
            if (cp.toPlayer() != null && cp.toPlayer().isOnline()) {
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
     * @return
     */
    public List<ClanPlayer> getAllMembers() {
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
     * Get all the ally clan's members
     *
     * @return
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
     *
     * @return
     */
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
     *
     * @return
     */
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
     *
     * @return
     */
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

    /**
     * Gets total rival kills for the clan
     *
     * @return
     */
    public int getTotalRival() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            total += cp.getRivalKills();
        }

        return total;
    }

    /**
     * Gets total neutral kills for the clan
     *
     * @return
     */
    public int getTotalNeutral() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            total += cp.getNeutralKills();
        }

        return total;
    }

    /**
     * Gets total civilian kills for the clan
     *
     * @return
     */
    public int getTotalCivilian() {
        int total = 0;

        if (members.isEmpty()) {
            return total;
        }

        for (String member : members) {
            ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(UUID.fromString(member));
            if (cp == null) {
                continue;
            }

            total += cp.getCivilianKills();
        }

        return total;
    }

    /**
     * Check whether the clan has crossed the rival limit
     *
     * @return
     */
    public boolean reachedRivalLimit() {
        int rivalCount = rivals.size();
        int clanCount = SimpleClans.getInstance().getClanManager().getRivableClanCount() - 1;
        int rivalPercent = SimpleClans.getInstance().getSettingsManager().getRivalLimitPercent();

        double limit = ((double) clanCount) * (((double) rivalPercent) / ((double) 100));

        return rivalCount > limit;
    }

    /**
     * Add a new player to the clan
     *
     * @param cp
     */
    public void addPlayerToClan(ClanPlayer cp) {
        cp.removePastClan(getColorTag());
        cp.setClan(this);
        cp.setLeader(false);
        cp.setTrusted(SimpleClans.getInstance().getSettingsManager().isClanTrustByDefault());

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
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new PlayerJoinedClanEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void removePlayerFromClan(String playerName) {
        removePlayerFromClan(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Remove a player from a clan
     *
     * @param playerUniqueId
     */
    public void removePlayerFromClan(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);

        // remove clan group-permission
        SimpleClans.getInstance().getPermissionsManager().removeClanPermissions(cp);

        // remove permissions
        SimpleClans.getInstance().getPermissionsManager().removeClanPlayerPermissions(cp);

        cp.setClan(null);
        cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
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
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new PlayerKickedClanEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void promote(String playerName) {
        promote(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Promote a member to a leader of a clan
     *
     * @param playerUniqueId
     */
    public void promote(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);

        cp.setLeader(true);
        cp.setTrusted(true);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        // add clan permission
        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new PlayerPromoteEvent(this, cp));
    }

    @SuppressWarnings("deprecation")
    public void demote(String playerName) {
        demote(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * Demote a leader back to a member of a clan
     *
     * @param playerUniqueId
     */
    public void demote(UUID playerUniqueId) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getClanPlayer(playerUniqueId);

        cp.setLeader(false);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(cp);
        SimpleClans.getInstance().getStorageManager().updateClan(this);

        // add clan permission
        SimpleClans.getInstance().getPermissionsManager().addClanPermissions(cp);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new PlayerDemoteEvent(this, cp));
    }

    /**
     * Add an ally to a clan, and the clan to the ally
     *
     * @param ally
     */
    public void addAlly(Clan ally) {
        removeRival(ally.getTag());
        addAlly(ally.getTag());

        ally.removeRival(getTag());
        ally.addAlly(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(ally);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new AllyClanAddEvent(this, ally));
    }

    /**
     * Remove an ally form the clan, and the clan from the ally
     *
     * @param ally
     */
    public void removeAlly(Clan ally) {
        removeAlly(ally.getTag());
        ally.removeAlly(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(ally);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new AllyClanRemoveEvent(this, ally));
    }

    /**
     * Add a rival to the clan, and the clan to the rival
     *
     * @param rival
     */
    public void addRival(Clan rival) {
        removeAlly(rival.getTag());
        addRival(rival.getTag());

        rival.removeAlly(getTag());
        rival.addRival(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(rival);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RivalClanAddEvent(this, rival));
    }

    /**
     * Removes a rival from the clan, the clan from the rival
     *
     * @param rival
     */
    public void removeRival(Clan rival) {
        removeRival(rival.getTag());
        rival.removeRival(getTag());

        SimpleClans.getInstance().getStorageManager().updateClan(this);
        SimpleClans.getInstance().getStorageManager().updateClan(rival);
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new RivalClanRemoveEvent(this, rival));
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
     *
     * @return
     */
    public boolean isAnyOnline() {
		for (String member : members) {
			if (Helper.isOnline(UUID.fromString(member))) {
				return true;
			}
		}

        return false;
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
        
        double minimum = SimpleClans.getInstance().getSettingsManager().getPercentageOnlineToDemote();
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
     *
     * @return
     */
    public boolean allLeadersOnline() {
        List<ClanPlayer> leaders = getLeaders();

		for (ClanPlayer leader : leaders) {
			if (!Helper.isOnline(leader.getUniqueId())) {
				return false;
			}
		}

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     *
     * @param playerName
     * @return
     */
    @Deprecated
    public boolean allOtherLeadersOnline(String playerName) {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.getName().equalsIgnoreCase(playerName)) {
                continue;
            }

            if (!Helper.isOnline(leader.getName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Check whether all leaders, except for the one passed in, are online
     *
     * @param playerUniqueId
     * @return
     */
    public boolean allOtherLeadersOnline(UUID playerUniqueId) {
        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer leader : leaders) {
            if (leader.getUniqueId().equals(playerUniqueId)) {
                continue;
            }

            if (!Helper.isOnline(leader.getUniqueId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Change a clan's tag
     *
     * @param tag
     */
    public void changeClanTag(String tag) {
        setColorTag(tag);
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    /**
     * Announce message to a whole clan
     *
     * @param playerName
     * @param msg
     */
    public void clanAnnounce(String playerName, String msg) {
        String message = SimpleClans.getInstance().getSettingsManager().getClanChatAnnouncementColor() + msg;

        for (ClanPlayer cp : getMembers()) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }

        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + lang("clan.announce") + ChatColor.AQUA + "] " + ChatColor.AQUA + "[" + Helper.getColorName(playerName) + ChatColor.WHITE + "] " + message);
    }

    /**
     * Announce message to a all the leaders of a clan
     *
     * @param msg
     */
    public void leaderAnnounce(String msg) {
        String message = SimpleClans.getInstance().getSettingsManager().getClanChatAnnouncementColor() + msg;

        List<ClanPlayer> leaders = getLeaders();

        for (ClanPlayer cp : leaders) {
            Player pl = cp.toPlayer();

            if (pl != null) {
                ChatBlock.sendMessage(pl, message);
            }
        }
        SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[" + lang("leader.announce") + ChatColor.AQUA + "] " + ChatColor.WHITE + message);
    }

    /**
     * Add a new bb message and announce it to all online members of a clan
     *
     * @param announcerName
     * @param msg
     */
    public void addBb(String announcerName, String msg) {
        if (isVerified()) {
            addBb(SimpleClans.getInstance().getSettingsManager().getBbColor() + msg);
            clanAnnounce(announcerName, SimpleClans.getInstance().getSettingsManager().getBbAccentColor() + "* " + SimpleClans.getInstance().getSettingsManager().getBbColor() + Helper.parseColors(msg));
        }
    }
    
    /**
     * Add a new bb message and announce it to all online members of a clan
     *
     * @param announcerName
     * @param msg
     * @param updateLastUsed
     */
    public void addBb(String announcerName, String msg, boolean updateLastUsed) {
        if (isVerified()) {
            addBb(SimpleClans.getInstance().getSettingsManager().getBbColor() + msg, updateLastUsed);
            clanAnnounce(announcerName, SimpleClans.getInstance().getSettingsManager().getBbAccentColor() + "* " + SimpleClans.getInstance().getSettingsManager().getBbColor() + Helper.parseColors(msg));
        }
    }

    /**
     * Displays bb to a player
     *
     * @param player
     */
    public void displayBb(Player player) {
        if (isVerified()) {
            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, lang("bulletin.board.header", SimpleClans.getInstance().getSettingsManager().getBbAccentColor(), SimpleClans.getInstance().getSettingsManager().getPageHeadingsColor(), getName()));

            int maxSize = SimpleClans.getInstance().getSettingsManager().getBbSize();

            while (bb.size() > maxSize) {
                bb.remove(0);
            }

            for (String msg : bb) {
                if (!sendBbTime(player, msg)) {
                    ChatBlock.sendMessage(player, SimpleClans.getInstance().getSettingsManager().getBbAccentColor() + "* " + SimpleClans.getInstance().getSettingsManager().getBbColor() + Helper.parseColors(msg));
                }
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Displays bb to a player
     * @implNote may want to refactor displaybb(Player) to use this?
     *
     * @param player
     * @param maxSize amount of lines to display
     */
    public void displayBb(Player player, int maxSize) {
        if (isVerified()) {
            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, lang("bulletin.board.header", SimpleClans.getInstance().getSettingsManager().getBbAccentColor(), SimpleClans.getInstance().getSettingsManager().getPageHeadingsColor(), getName()));

            List<String> localBb = new ArrayList<>(bb);
            while (localBb.size() > maxSize) {
                localBb.remove(0);
            }

            for (String msg : localBb) {
                if (!sendBbTime(player, msg)) {
                    ChatBlock.sendMessage(player, SimpleClans.getInstance().getSettingsManager().getBbAccentColor() + "* " + SimpleClans.getInstance().getSettingsManager().getBbColor() + Helper.parseColors(msg));
                }
            }
            ChatBlock.sendBlank(player);
        }
    }

    /**
     * Sends a bb message with the timestamp in a hover message, if the bb message is timestamped
     * @param msg the bb message
     * @return true if sent
     */
    private boolean sendBbTime(Player player, String msg) {
        try {
            int index = msg.indexOf("_");
            if (index < 1)
                return false;
            long time;
            time = (System.currentTimeMillis() 
                    - Long.parseLong(msg.substring(0, index))) / 1000L;
            msg = String.join("", ChatBlock
                    .getColorizedMessage(SimpleClans.getInstance()
                            .getSettingsManager().getBbAccentColor() + "* " + 
                            SimpleClans.getInstance().getSettingsManager()
                                    .getBbColor() + Helper.parseColors(
                                            msg.substring(++index, msg.length()))));
            TextComponent textComponent = new TextComponent(msg);
            textComponent.setHoverEvent(
                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            TextComponent.fromLegacyText(
                                    Dates.formatTime(time, 1) + lang("bb.ago"))));
            player.spigot().sendMessage(textComponent);
            return true;
        } catch (Throwable rock) {
            return false;
        }
    }

    /**
     * Disband a clan
     */
    public void disband() {
        SimpleClans.getInstance().getServer().getPluginManager().callEvent(new DisbandClanEvent(this));
        Collection<ClanPlayer> clanPlayers = SimpleClans.getInstance().getClanManager().getAllClanPlayers();
        List<Clan> clans = SimpleClans.getInstance().getClanManager().getClans();

        for (ClanPlayer cp : clanPlayers) {
            if (cp.getTag().equals(getTag())) {
                SimpleClans.getInstance().getPermissionsManager().removeClanPermissions(this);
                cp.setClan(null);

                if (isVerified()) {
                    cp.addPastClan(getColorTag() + (cp.isLeader() ? ChatColor.DARK_RED + "*" : ""));
                }

                cp.setLeader(false);
            }
        }

        clans.remove(this);

        for (Clan c : clans) {
            String disbanded = lang("clan.disbanded");

            if (c.removeWarringClan(this)) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(lang("you.are.no.longer.at.war"), c.getName(), getColorTag()));
            }

            if (c.removeRival(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(lang("has.been.disbanded.rivalry.ended"), getName()));
            }

            if (c.removeAlly(getTag())) {
                c.addBb(disbanded, ChatColor.AQUA + MessageFormat.format(lang("has.been.disbanded.alliance.ended"), getName()));
            }
        }

        final Clan thisOne = this;

        SimpleClans.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(SimpleClans.getInstance(), new Runnable() {

            @Override
            public void run() {
                SimpleClans.getInstance().getClanManager().removeClan(thisOne.getTag());
                SimpleClans.getInstance().getStorageManager().deleteClan(thisOne);
            }
        }, 1);
    }

    /**
     * Whether this clan can be rivaled
     *
     * @return
     */
    public boolean isUnrivable() {
        return SimpleClans.getInstance().getSettingsManager().isUnrivable(getTag());
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param tag the tag of the clan we are at war with
     * @return
     */
    public boolean isWarring(String tag) {
        return warringClans.containsKey(tag);
    }

    /**
     * Returns whether this clan is warring with another clan
     *
     * @param clan the clan we are testing against
     * @return
     */
    public boolean isWarring(Clan clan) {
        return warringClans.containsKey(clan.getTag());
    }

    /**
     * Add a clan to be at war with
     *
     * @param clan
     */
    public void addWarringClan(Clan clan) {
        if (!warringClans.containsKey(clan.getTag())) {
            warringClans.put(clan.getTag(), clan);
        }
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    /**
     * Remove a warring clan
     *
     * @param clan
     * @return
     */
    public boolean removeWarringClan(Clan clan) {
        Clan warring = warringClans.remove(clan.getTag());

        if (warring != null) {
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
        return new ArrayList<>(warringClans.values());
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    @SuppressWarnings("unchecked")
	public String getFlags() {
        JSONObject json = new JSONObject();

        // writing the list of flags to json

        JSONArray warring = new JSONArray();
        warring.addAll(warringClans.keySet());

        json.put("warring", warring);
        json.put("homeX", homeX);
        json.put("homeY", homeY);
        json.put("homeZ", homeZ);
        json.put("homeWorld", homeWorld == null ? "" : homeWorld);

        return json.toString();
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString) {
        if (flagString != null && !flagString.isEmpty()) {
            JSONObject flags = (JSONObject) JSONValue.parse(flagString);

            if (flags != null) {
                for (Object flag : flags.keySet()) {
                    // reading the list of flags from json

                    try {
                        if (flag.equals("warring")) {
                            JSONArray clans = (JSONArray) flags.get(flag);

                            if (clans != null) {
                                for (Object tag : clans) {
                                    SimpleClans.debug("warring added: " + tag.toString());
                                    warringClans.put(tag.toString(), null);
                                }
                            }
                        }

                        if (flag.equals("homeX")) {
                            homeX = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag.equals("homeY")) {
                            homeY = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag.equals("homeZ")) {
                            homeZ = ((Long) flags.get(flag)).intValue();
                        }

                        if (flag.equals("homeWorld")) {
                            homeWorld = (String) flags.get(flag);
                        }
                    } catch (Exception ex) {
                        for (StackTraceElement el : ex.getStackTrace()) {
                            System.out.print("Failed reading flag: " + flag);
                            System.out.print("Value: " + flags.get(flag));
                            System.out.print(el.toString());
                        }
                    }
                }
            }
        }
    }

    public void validateWarring() {
        for (Iterator<String> iter = warringClans.keySet().iterator(); iter.hasNext(); ) {
            String clanName = iter.next();

            Clan clan = SimpleClans.getInstance().getClanManager().getClan(clanName);

            if (clan == null) {
                iter.remove();
            } else {
                SimpleClans.debug("validated: " + clanName);
                warringClans.put(clanName, clan);
            }
        }
    }

    public void setHomeLocation(@Nullable Location home) {
        if (home == null) {
            homeY = 0;
            homeX = 0;
            homeZ = 0;
            homeWorld = null;
        } else {
            home.setY(home.getBlockY() + 1);

            homeX = home.getBlockX();
            homeY = home.getBlockY();
            homeZ = home.getBlockZ();
            homeWorld = home.getWorld().getName();
        }
        SimpleClans.getInstance().getStorageManager().updateClan(this);
    }

    public @Nullable Location getHomeLocation() {
        if (homeWorld == null) {
            return null;
        }

        World world = SimpleClans.getInstance().getServer().getWorld(homeWorld);

        if (world == null) {
            return null;
        }

        if (!(world.getBlockAt(homeX, homeY, homeZ).getType().equals(XMaterial.AIR.parseMaterial())) ||
                !(world.getBlockAt(homeX, homeY + 1, homeZ).getType().equals(XMaterial.AIR.parseMaterial()))
                || homeY == 0) {
            return new Location(world, homeX, world.getHighestBlockYAt(homeX, homeZ), homeZ);
        } else {
            return new Location(world, homeX, homeY, homeZ);
        }
    }

    public String getTagLabel(boolean isLeader) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (isLeader) {
            return plugin.getSettingsManager().getTagBracketLeaderColor() +
                    plugin.getSettingsManager().getTagBracketLeft() +
                    plugin.getSettingsManager().getTagDefaultColor() +
                    getColorTag() +
                    plugin.getSettingsManager().getTagBracketLeaderColor() +
                    plugin.getSettingsManager().getTagBracketRight() +
                    plugin.getSettingsManager().getTagSeparatorLeaderColor() +
                    plugin.getSettingsManager().getTagSeparator();
        } else {
            return plugin.getSettingsManager().getTagBracketColor() +
                    plugin.getSettingsManager().getTagBracketLeft() +
                    plugin.getSettingsManager().getTagDefaultColor() +
                    getColorTag() +
                    plugin.getSettingsManager().getTagBracketColor() +
                    plugin.getSettingsManager().getTagBracketRight() +
                    plugin.getSettingsManager().getTagSeparatorColor() +
                    plugin.getSettingsManager().getTagSeparator();
        }
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
     * 
     * @param enable 
     */
    public void setMemberFeeEnabled(boolean enable) {
        feeEnabled = enable;
    }
    
    /**
     * @return the allowWithdraw
     */
    public boolean isAllowWithdraw() {
        return allowWithdraw;
    }

    /**
     * @param allowWithdraw the allowWithdraw to set
     */
    public void setAllowWithdraw(boolean allowWithdraw) {
        this.allowWithdraw = allowWithdraw;
    }

    /**
     * @return the allowDeposit
     */
    public boolean isAllowDeposit() {
        return allowDeposit;
    }

    /**
     * @param allowDeposit the allowDeposit to set
     */
    public void setAllowDeposit(boolean allowDeposit) {
        this.allowDeposit = allowDeposit;
    }

    /**
     * Checks if the clan has the specified rank
     * 
     * @param name the rank
     * @return
     */
	public boolean hasRank(String name) {
		return getRank(name) != null;
	}

	/**
	 * Creates a rank
	 * 
	 * @param name
	 */
	public void createRank(String name) {
		ranks.add(new Rank(name));
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
	 * 
	 * @param ranks
	 */
	public void setRanks(List<Rank> ranks) {
		if (ranks == null) {
			ranks = new ArrayList<>();
		}
		this.ranks = ranks;
	}

	/**
	 * Deletes a rank with the specified name
	 * 
	 * @param name
	 */
	public void deleteRank(String name) {
		Rank r = getRank(name);
		if (r != null) {
			ranks.remove(r);
			getMembers().forEach(cp -> {
				if (cp.getRankId().equals(r.getName())) {
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
	public @Nullable Rank getRank(String name) {
		if (name != null) {
			for (Rank r : ranks) {
				if (r.getName().equals(name)) {
					return r;
				}
			}	
		}
		return null;
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
