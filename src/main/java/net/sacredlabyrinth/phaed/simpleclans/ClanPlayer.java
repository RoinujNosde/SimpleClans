package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.Placeholder;
import net.sacredlabyrinth.phaed.simpleclans.managers.ProtectionManager.Action;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.DateFormat;
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
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

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
    private final Map<Kill.Type, Integer> kills = new HashMap<>();
    private int deaths;
    private long lastSeen;
    private long joinDate;
    private final Set<String> pastClans = new LinkedHashSet<>();
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
        uniqueId = uuid;
        Player onlinePlayer = SimpleClans.getInstance().getServer().getPlayer(uuid);
        if (onlinePlayer != null) {
            displayName = onlinePlayer.getName();
        } else {
            OfflinePlayer offlinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(uuid);
            displayName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "null";
        }
        lastSeen = (new Date()).getTime();
        joinDate = (new Date()).getTime();
        tag = "";
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
        return other.getName().equals(getName());
    }

    @Override
    public int compareTo(ClanPlayer other) {
        return getUniqueId().compareTo(other.getUniqueId());
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
    @Placeholder("name")
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
    @Placeholder("clean_name")
    public String getCleanName() {
        return displayName.toLowerCase();
    }

    /**
     * (used internally)
     *
     * @param name the name to set
     */
    public void setName(String name) {
        displayName = name;
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
        lastSeen = (new Date()).getTime();
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
        return getRivalKills() + getCivilianKills() + getNeutralKills() + getAllyKills();
    }

    /**
     * Returns the number of rival kills this player has
     *
     * @return the rivalKills
     */
    @Placeholder("rival_kills")
    public int getRivalKills() {
        return kills.getOrDefault(Kill.Type.RIVAL, 0);
    }

    /**
     * (used internally)
     *
     * @param rivalKills the rivalKills to set
     */
    public void setRivalKills(int rivalKills) {
        kills.put(Kill.Type.RIVAL, rivalKills);
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
        return kills.getOrDefault(Kill.Type.CIVILIAN, 0);
    }

    /**
     * (used internally)
     *
     * @param civilianKills the civilianKills to set
     */
    public void setCivilianKills(int civilianKills) {
        kills.put(Kill.Type.CIVILIAN, civilianKills);
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
        return kills.getOrDefault(Kill.Type.NEUTRAL, 0);
    }

    /**
     * (used internally)
     *
     * @param neutralKills the neutralKills to set
     */
    public void setNeutralKills(int neutralKills) {
        kills.put(Kill.Type.NEUTRAL, neutralKills);
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addNeutralKill() {
        setNeutralKills(getNeutralKills() + 1);
    }

    public void setAllyKills(int allyKills) {
        kills.put(Kill.Type.ALLY, allyKills);
    }

    @Placeholder("ally_kills")
    public int getAllyKills() {
        return kills.getOrDefault(Kill.Type.ALLY, 0);
    }

    /**
     * Adds one kill to this player (does not update to db)
     */
    public void addKill(Kill.Type type) {
        kills.compute(type, (t, c) -> c == null ? 1 : c + 1);
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
        SettingsManager settings = SimpleClans.getInstance().getSettingsManager();
        double kills = getRivalKills() * settings.getDouble(KILL_WEIGHTS_RIVAL) +
                getNeutralKills() * settings.getDouble(KILL_WEIGHTS_NEUTRAL) +
                getAllyKills() * settings.getDouble(KILL_WEIGHTS_ALLY) +
                getCivilianKills() * settings.getDouble(KILL_WEIGHTS_CIVILIAN);
        if (kills < 0) {
            return 0;
        }
        return kills;
    }

    /**
     * Returns weighted-kill/death ratio
     */
    @Placeholder(value = "kdr", resolver = "kdr")
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
        return DateFormat.formatDateTime(joinDate);
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
        if (!VanishUtils.isVanished(sender, this)) {
            return lang("online", sender);
        }
        return DateFormat.formatDateTime(lastSeen);
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
        return String.join("|", getPastClans());
    }

    /**
     * (used internally)
     *
     * @param packedPastClans the packedPastClans to set
     */
    public void setPackedPastClans(String packedPastClans) {
        String[] clans = packedPastClans.split("\\|");
        Collections.addAll(pastClans, clans);
    }

    /**
     * Adds a past clan to the player (does not update the clanplayer to db)
     */
    public void addPastClan(String tag) {
        getPastClans().add(tag);
    }

    /**
     * Removes a past clan from the player (does not update the clanplayer to db)
     *
     * @param tag is the clan's colored tag
     */
    public void removePastClan(String tag) {
        getPastClans().remove(tag);
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
        if (getPastClans().isEmpty()) {
            return lang("none", viewer);
        }

        SettingsManager settings = SimpleClans.getInstance().getSettingsManager();
        return getPastClans().stream().
                sorted(Collections.reverseOrder()).
                limit(settings.getInt(PAST_CLANS_LIMIT)).
                collect(Collectors.joining(sep));
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
    public void setResignTimes(@Nullable Map<String, Long> resignTimes) {
        if (resignTimes != null) {
            final int cooldown = SimpleClans.getInstance().getSettingsManager().getInt(REJOIN_COOLDOWN);
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
            tag = "";
        } else {
            tag = clan.getTag();
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
        } catch (IllegalArgumentException ignored) {
        }
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
    public void setGlobalChat(boolean globalChat) {
    }

    @Deprecated
    public void setAllyChat(boolean allyChat) {
    }

    @Deprecated
    public void setClanChat(boolean clanChat) {
    }

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

    @Placeholder("is_invite_enabled")
    public boolean isInviteEnabled() {
        return flags.getBoolean("invite", true);
    }

    public void setInviteEnabled(boolean inviteEnabled) {
        flags.put("invite", inviteEnabled);
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
    }

    @Deprecated
    public boolean isCapeEnabled() {
        return false;
    }

    @Deprecated
    public void setCapeEnabled(boolean capeEnabled) {
    }

    @Placeholder("is_tag_enabled")
    public boolean isTagEnabled() {
        return flags.getBoolean("hide-tag", true);
    }

    public void setTagEnabled(boolean tagEnabled) {
        flags.put("hide-tag", tagEnabled);

        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
        SimpleClans.getInstance().getClanManager().updateDisplayName(toPlayer());
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

    @Placeholder("has_rank")
    public boolean hasRank() {
        return !getRankId().isEmpty();
    }

    @Placeholder("rank")
    public @NotNull String getRankId() {
        return flags.getString("rank", "");
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

    public @Nullable Locale getLocale() {
        return locale;
    }

    public void setLocale(@Nullable Locale locale) {
        this.locale = locale;
    }

    public @Nullable Player toPlayer() {
        if (uniqueId != null) {
            return Bukkit.getPlayer(uniqueId);
        } else {
            return Bukkit.getPlayer(displayName);
        }
    }

    public void mute(Channel channel, boolean b) {
        switch (channel) {
            case CLAN:
                clanChatMute = b;
                break;
            case ALLY:
                allyChatMute = b;
        }
    }


    /**
     * @deprecated use {@link ClanPlayer#mute(Channel, boolean)}
     */
    @Deprecated
    public void setMuted(boolean b) {
        clanChatMute = b;
    }

    /**
     * @deprecated use {@link ClanPlayer#mute(Channel, boolean)}
     */
    @Deprecated
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

    public void disallow(@NotNull Action action, @NotNull String landId) {
        String key = "allow-" + action.name();
        List<String> allowed = flags.getStringList(key);
        if (allowed.remove(landId)) {
            flags.put(key, allowed);
        }
    }

    public void allow(@NotNull Action action, @NotNull String landId) {
        String key = "allow-" + action.name();
        List<String> allowed = flags.getStringList(key);
        if (!allowed.contains(landId)) {
            allowed.add(landId);
            flags.put(key, allowed);
        }
    }

    public boolean isAllowed(@NotNull Action action, @NotNull String landId) {
        String key = "allow-" + action.name();
        List<String> allowed = flags.getStringList(key);
        return allowed.contains(landId);
    }

    public enum Channel {
        CLAN,
        ALLY,
        NONE
    }
}
