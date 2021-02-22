package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.Placeholder;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class ClanPlayer implements Serializable, Comparable<ClanPlayer> {
    private static final long serialVersionUID = 1L;
    private UUID uniqueId;
    private String displayName;
    private boolean leader;
    private boolean trusted;
    private String tag;
    private @Nullable Clan clan;
    private boolean friendlyFire;
    private int neutralKills;
    private int rivalKills;
    private int civilianKills;
    private int deaths;
    private long lastSeen;
    private long joinDate;
    private Set<String> pastClans = new HashSet<>();
    private final Map<String, Long> resignTimes = new HashMap<>();
    private @Nullable VoteResult vote;
    private Flags flags = new Flags(null);

    private boolean allyChatMute = false;
    private boolean clanChatMute = false;

    private @Nullable Locale locale;

    /**
     *
     */
    public ClanPlayer() {
        this.tag = "";
    }

    /**
     *
     */
    @Deprecated
    public ClanPlayer(String playerName) {
        this(Bukkit.getOfflinePlayer(playerName).getUniqueId());
    }

    /**
     * @param uuid the Player's UUID
     */
    public ClanPlayer(UUID uuid) {
        this.uniqueId = uuid;
        Player onlinePlayer = SimpleClans.getInstance().getServer().getPlayer(uuid);
        if (onlinePlayer != null) {
            this.displayName = onlinePlayer.getName();
        } else {
            OfflinePlayer offlinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(uuid);
            this.displayName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "null";
        }
        this.lastSeen = (new Date()).getTime();
        this.joinDate = (new Date()).getTime();
        this.neutralKills = 0;
        this.rivalKills = 0;
        this.civilianKills = 0;
        this.tag = "";
    }

    @Override
    public int hashCode() {
        return getName().hashCode() >> 13;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ClanPlayer)) {
            return false;
        }

        ClanPlayer other = (ClanPlayer) obj;
        return other.getName().equals(this.getName());
    }

    @Override
    public int compareTo(ClanPlayer other) {
        return this.getUniqueId().compareTo(other.getUniqueId());
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * (used internally)
     *
     * @return the name
     */
    public String getName() {
        return displayName;
    }

    /**
     * (used internally)
     *
     * @return the uniqueId
     */
    public UUID getUniqueId() {
        return uniqueId;
    }

    /**
     * Returns the clean name for this player (lowercase)
     *
     * @return the name
     */
    public String getCleanName() {
        return displayName.toLowerCase();
    }

    /**
     * (used internally)
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.displayName = name;
    }

    /**
     * (used internally)
     *
     * @param uniqueId the name to set
     */
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * Whether this player is a leader or not
     *
     * @return the leader
     */
    @Placeholder("is_leader")
    public boolean isLeader() {
        return leader;
    }

    /**
     * Sets this player as a leader (does not update clanplayer to db)
     *
     * @param leader the leader to set
     */
    public void setLeader(boolean leader) {
        if (leader) {
            trusted = true;
        }

        this.leader = leader;
    }

    /**
     * Check whether the player is an ally with another player
     */
    public boolean isAlly(Player player) {
        ClanPlayer allycp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (allycp != null) {
            //noinspection ConstantConditions
            return allycp.getClan().isAlly(tag);
        }

        return false;
    }

    /**
     * Check whether the player is an rival with another player
     */
    public boolean isRival(Player player) {
        ClanPlayer allycp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (allycp != null) {
            //noinspection ConstantConditions
            return allycp.getClan().isRival(tag);
        }

        return false;
    }


    /**
     * Returns the last seen date for this player in milliseconds
     *
     * @return the lastSeen
     */
    public long getLastSeen() {
        return lastSeen;
    }

    /**
     * (used internally)
     *
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    /**
     * Updates last seen date to today
     */
    public void updateLastSeen() {
        this.lastSeen = (new Date()).getTime();
    }

    /**
     * @return a verbal representation of how many days ago a player was last seen
     */
    @Placeholder("lastseendays")
    public String getLastSeenDaysString() {
        return getLastSeenDaysString(null);
    }

    /**
     * @param sender the Player viewing the last seen days
     * @return a verbal representation of how many days ago a player was last seen
     */
    public String getLastSeenDaysString(@Nullable CommandSender sender) {
        double days = Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));

        if (days < 1) {
            return lang("today", sender);
        } else if (Math.round(days) == 1) {
            return lang("1.color.day", sender, ChatColor.GRAY);
        } else {
            return lang("many.color.days", sender, Math.round(days), ChatColor.GRAY);
        }
    }

    public String getLastSeenDaysString(@Nullable Player viewer) {
        return getLastSeenDaysString((CommandSender) viewer);
    }

    /**
     * Returns number of days since the player was last seen
     */
    public double getLastSeenDays() {
        return Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));
    }

    @Placeholder("total_kills")
    public int getTotalKills() {
        return getRivalKills() + getCivilianKills() + getNeutralKills();
    }

    /**
     * Returns the number of rival kills this player has
     *
     * @return the rivalKills
     */
    @Placeholder("rival_kills")
    public int getRivalKills() {
        return rivalKills;
    }

    /**
     * (used internally)
     *
     * @param rivalKills the rivalKills to set
     */
    public void setRivalKills(int rivalKills) {
        this.rivalKills = rivalKills;
    }

    /**
     * Adds one rival kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addRivalKill() {
        setRivalKills(getRivalKills() + 1);
    }

    /**
     * Returns the number of civilian kills this player has
     *
     * @return the civilianKills
     */
    @Placeholder("civilian_kills")
    public int getCivilianKills() {
        return civilianKills;
    }

    /**
     * (used internally)
     *
     * @param civilianKills the civilianKills to set
     */
    public void setCivilianKills(int civilianKills) {
        this.civilianKills = civilianKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addCivilianKill() {
        setCivilianKills(getCivilianKills() + 1);
    }

    /**
     * Returns the number of neutral kills this player has
     *
     * @return the neutralKills
     */
    @Placeholder("neutral_kills")
    public int getNeutralKills() {
        return neutralKills;
    }

    /**
     * (used internally)
     *
     * @param neutralKills the neutralKills to set
     */
    public void setNeutralKills(int neutralKills) {
        this.neutralKills = neutralKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addNeutralKill() {
        setNeutralKills(getNeutralKills() + 1);
    }

    /**
     * Adds one kill to this player (does not update to db)
     */
    public void addKill(Kill.Type type) {
        switch (type) {
            case CIVILIAN:
                civilianKills++;
                break;
            case NEUTRAL:
                neutralKills++;
                break;
            case RIVAL:
                rivalKills++;
        }
    }

    /**
     * Whether this player is allowing friendly fire
     *
     * @return the friendlyFire
     */
    @Placeholder("is_friendlyfire_on")
    public boolean isFriendlyFire() {
        return friendlyFire;
    }

    /**
     * Sets whether this player is allowing friendly fire (does not update clanplayer to db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    /**
     * (used internally)
     *
     * @return the vote
     */
    public @Nullable VoteResult getVote() {
        return vote;
    }

    /**
     * (used internally)
     *
     * @param vote the vote to set
     */
    public void setVote(@Nullable VoteResult vote) {
        this.vote = vote;
    }

    /**
     * Returns the number of deaths this player has
     *
     * @return the deaths
     */
    @Placeholder("deaths")
    public int getDeaths() {
        return deaths;
    }

    /**
     * (used internally)
     *
     * @param deaths the deaths to set
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    /**
     * Adds one death to this player  (does not update clanplayer to db)
     */
    public void addDeath() {
        setDeaths(getDeaths() + 1);
    }

    /**
     * Returns weighted kill score for this player (kills multiplied by the different weights)
     */
    @Placeholder("weighted_kills")
    public double getWeightedKills() {
        SimpleClans plugin = SimpleClans.getInstance();
        return ((double) rivalKills * plugin.getSettingsManager().getKwRival()) + ((double) neutralKills * plugin.getSettingsManager().getKwNeutral()) + ((double) civilianKills * plugin.getSettingsManager().getKwCivilian());
    }

    /**
     * Returns weighted-kill/death ratio
     */
    @Placeholder("kdr")
    @Placeholder(value = "topplayers_position", resolver = "ranking_position")
    public float getKDR() {
        int totalDeaths = getDeaths();

        if (totalDeaths == 0) {
            totalDeaths = 1;
        }

        return ((float) getWeightedKills()) / ((float) totalDeaths);
    }

    /**
     * Returns the player's join date to his current clan in milliseconds, 0 if not in a clan
     *
     * @return the joinDate
     */
    public long getJoinDate() {
        return joinDate;
    }

    /**
     * (used internally)
     *
     * @param joinDate the joinDate to set
     */
    public void setJoinDate(long joinDate) {
        this.joinDate = joinDate;
    }

    /**
     * Returns a string representation of the join date, blank if not in a clan
     */
    @Placeholder("join_date")
    public String getJoinDateString() {
        if (joinDate == 0) {
            return "";
        }

        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.joinDate));
    }

    /**
     * @return a string representation of the last seen date
     */
    @Placeholder("lastseen")
    @NotNull
    public String getLastSeenString() {
        return getLastSeenString(null);
    }

    /**
     * @param viewer the Player viewing the last seen
     * @return a string representation of the last seen date
     */
    public String getLastSeenString(@Nullable Player viewer) {
        return getLastSeenString((CommandSender) viewer);
    }

    public String getLastSeenString(@Nullable CommandSender sender) {
        Player player = toPlayer();
        if (player != null && player.isOnline() && !VanishUtils.isVanished(sender, player)) {
            return lang("online", sender);
        }
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.lastSeen));
    }

    /**
     * Returns the number of days the player has been inactive
     */
    @Placeholder("inactive_days")
    public int getInactiveDays() {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastSeen()), now));
    }

    /**
     * (used internally)
     *
     * @return the PackedPastClans
     */
    public String getPackedPastClans() {
        StringBuilder packedPastClans = new StringBuilder();

        Set<String> pt = getPastClans();

        for (String pastClan : pt) {
            packedPastClans.append(pastClan).append("|");
        }

        return Helper.stripTrailing(packedPastClans.toString(), "|");
    }

    /**
     * (used internally)
     *
     * @param PackedPastClans the PackedPastClans to set
     */
    public void setPackedPastClans(String PackedPastClans) {
        this.pastClans = Helper.fromArray2(PackedPastClans.split("[|]"));
    }

    /**
     * Adds a past clan to the player (does not update the clanplayer to db)
     */
    public void addPastClan(String tag) {
        this.getPastClans().add(tag);
    }

    /**
     * Removes a past clan from the player (does not update the clanplayer to db)
     *
     * @param tag is the clan's colored tag
     */
    public void removePastClan(String tag) {
        this.getPastClans().remove(tag);
    }

    /**
     * @param sep the separator
     * @return a separator delimited string with the color tags for all past clans this player has been in
     */
    @NotNull
    public String getPastClansString(@NotNull String sep) {
        return getPastClansString(sep, null);
    }

    /**
     * @param sep    the separator
     * @param viewer the Player viewing the clan's string
     * @return a separator delimited string with the color tags for all past clans this player has been in
     */
    @NotNull
    public String getPastClansString(String sep, @Nullable Player viewer) {
        StringBuilder out = new StringBuilder();

        for (String pastClan : getPastClans()) {
            out.append(pastClan).append(sep);
        }

        out = new StringBuilder(Helper.stripTrailing(out.toString(), sep));

        if (out.toString().trim().isEmpty()) {
            return lang("none", viewer);
        }

        return out.toString();
    }

    /**
     * Returns a list with all past clans color tags this player has been in
     *
     * @return the pastClans
     */
    public Set<String> getPastClans() {
        return pastClans;
    }

    /**
     * Returns a map containing the time the player resigned from certain clans
     *
     * @return the resign times
     */
    public Map<String, Long> getResignTimes() {
        return resignTimes;
    }

    /**
     * Returns the time in millis when the player resigned from the clan
     *
     * @return the time in millis
     */
    public Long getResignTime(String tag) {
        return resignTimes.get(tag);
    }

    /**
     * Sets the resign times (does not update to db)
     */
    public void setResignTimes(Map<String, Long> resignTimes) {
        if (resignTimes != null) {
            final int cooldown = SimpleClans.getInstance().getSettingsManager().getRejoinCooldown();
            resignTimes.forEach((k, v) -> {
                long timePassed = Instant.ofEpochMilli(v).until(Instant.now(), ChronoUnit.MINUTES);
                if (timePassed < cooldown) {
                    this.resignTimes.put(k, v);
                }
            });
        }
    }

    /**
     * Adds the clan to the resign times map
     */
    public void addResignTime(String tag) {
        if (tag != null) resignTimes.put(tag, System.currentTimeMillis());
    }

    /**
     * Returns this player's clan
     *
     * @return the clan
     */
    @Nullable
    @Placeholder(value = "in_clan", resolver = "not_null_return")
    public Clan getClan() {
        return clan;
    }

    /**
     * (used internally)
     *
     * @param clan the clan to set
     */
    public void setClan(@Nullable Clan clan) {
        if (clan == null) {
            this.tag = "";
        } else {
            this.tag = clan.getTag();
        }

        this.clan = clan;
    }

    /**
     * Returns this player's clan's tag.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    @Placeholder("tag")
    public String getTag() {
        return tag;
    }

    /**
     * Returns this player's clan's tag label.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    @Placeholder("tag_label")
    public String getTagLabel() {
        if (clan == null) {
            return "";
        }

        return clan.getTagLabel(isLeader());
    }

    /**
     * Returns this player's trusted status
     *
     * @return the trusted
     */
    @Placeholder(value = "is_trusted", resolver = "member_status")
    @Placeholder(value = "is_member", resolver = "member_status")
    public boolean isTrusted() {
        return leader || trusted;
    }

    /**
     * Sets this player's trusted status (does not update the clanplayer to db)
     *
     * @param trusted the trusted to set
     */
    public void setTrusted(boolean trusted) {
        this.trusted = trusted;
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

    @Placeholder(value = "clanchat_player_color", resolver = "player_color")
    @Placeholder(value = "allychat_player_color", resolver = "player_color")
    public @NotNull Channel getChannel() {
        String channelName = flags.getString("channel");
        try {
            if (channelName != null) {
                return Channel.valueOf(channelName);
            }
        } catch (IllegalArgumentException ignored) {}
        return Channel.NONE;
    }

    @Deprecated
    public boolean isGlobalChat() {
        return false;
    }

    @Deprecated
    public boolean isAllyChat() {
        return true;
    }

    @Deprecated
    public boolean isClanChat() {
        return true;
    }

    @Deprecated
    public void setGlobalChat(boolean globalChat) {}

    @Deprecated
    public void setAllyChat(boolean allyChat) {}

    @Deprecated
    public void setClanChat(boolean clanChat) {}

    public void setChannel(@NotNull Channel channel) {
        flags.put("channel", channel.toString());
    }

    @Placeholder("is_bb_enabled")
    public boolean isBbEnabled() {
        return flags.getBoolean("bb-enabled", true);
    }

    public void setBbEnabled(boolean bbEnabled) {
        flags.put("bb-enabled", bbEnabled);
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
    }

    @Deprecated
    public boolean isCapeEnabled() {
        return false;
    }

    @Deprecated
    public void setCapeEnabled(boolean capeEnabled) {}

    @Placeholder("is_tag_enabled")
    public boolean isTagEnabled() {
        return flags.getBoolean("hide-tag", true);
    }

    public void setTagEnabled(boolean tagEnabled) {
        flags.put("hide-tag", tagEnabled);
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
        SimpleClans.getInstance().getClanManager().updateDisplayName(this.toPlayer());
    }

    @Deprecated
    public boolean isUseChatShortcut() {
        return false;
    }

    @Placeholder("rank_displayname")
    public String getRankDisplayName() {
        if (clan != null) {
            Rank r = clan.getRank(getRankId());
            if (r != null) {
                return r.getDisplayName();
            }
        }
        return "";
    }

    @Placeholder("rank")
    public @Nullable String getRankId() {
        return flags.getString("rank");
    }

    /**
     * Gets the rank displayname
     */
    @Deprecated
    public String getRank() {
        return getRankDisplayName();
    }

    /**
     * Sets the rank id
     *
     * @param rank the rank id
     */
    public void setRank(@Nullable String rank) {
        flags.put("rank", rank == null ? "" : rank);
    }

    public @NotNull Locale getLocale() {
        if (locale == null) {
            return SimpleClans.getInstance().getSettingsManager().getLanguage();
        }
        return locale;
    }

    public void setLocale(@Nullable Locale locale) {
        this.locale = locale;
    }

    public enum Channel {
        CLAN,
        ALLY,
        NONE
    }

    public @Nullable Player toPlayer() {
        if (uniqueId != null) {
            return Bukkit.getPlayer(uniqueId);
        } else {
            return Bukkit.getPlayer(displayName);
        }
    }

    public void setMuted(boolean b) {
        clanChatMute = b;
    }

    public void setMutedAlly(boolean b) {
        allyChatMute = b;
    }

    @Placeholder("is_muted")
    public boolean isMuted() {
        return clanChatMute;
    }

    @Placeholder("is_mutedally")
    public boolean isMutedAlly() {
        return allyChatMute;
    }
}
