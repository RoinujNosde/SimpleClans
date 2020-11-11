package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class ClanPlayer implements Serializable, Comparable<ClanPlayer>
{
    private static final long serialVersionUID = 1L;
    private UUID uniqueId;
    private String displayName;
    private boolean leader;
    private boolean trusted;
    private String tag;
    private Clan clan;
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
    private Channel channel;

    private boolean useChatShortcut = false;
    private boolean globalChat = true;
    private boolean allyChat = true;
    private boolean clanChat = true;
    private boolean bbEnabled = true;
    private boolean tagEnabled = true;
    private boolean capeEnabled = true;

    private boolean allyChatMute = false;
    private boolean clanChatMute = false;

    private String rank = "";
    private @Nullable Locale locale;

    /**
     *
     */
    public ClanPlayer()
    {
        this.tag = "";
        this.channel = Channel.NONE;
    }

    /**
     * @param playerName
     */
    @Deprecated
    public ClanPlayer(String playerName)
    {
        this.displayName = playerName;
        this.lastSeen = (new Date()).getTime();
        this.joinDate = (new Date()).getTime();
        this.neutralKills = 0;
        this.rivalKills = 0;
        this.civilianKills = 0;
        this.tag = "";
        this.channel = Channel.NONE;
    }

    /**
     * @param uuid the Player's UUID
     */
    public ClanPlayer(UUID uuid)
    {
        this.uniqueId = uuid;
        Player onlinePlayer = SimpleClans.getInstance().getServer().getPlayer(uuid);
        if (onlinePlayer != null)
        {
            this.displayName = onlinePlayer.getName();
        } else
        {
            OfflinePlayer offlinePlayer = SimpleClans.getInstance().getServer().getOfflinePlayer(uuid);
            this.displayName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "null";
        }
        this.lastSeen = (new Date()).getTime();
        this.joinDate = (new Date()).getTime();
        this.neutralKills = 0;
        this.rivalKills = 0;
        this.civilianKills = 0;
        this.tag = "";
        this.channel = Channel.NONE;
    }

    @Override
    public int hashCode()
    {
        return getName().hashCode() >> 13;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof ClanPlayer))
        {
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
    public String toString()
    {
        return displayName;
    }

    /**
     * (used internally)
     *
     * @return the name
     */
    public String getName()
    {
        return displayName;
    }

    /**
     * (used internally)
     *
     * @return the uniqueId
     */
    public UUID getUniqueId()
    {
        return uniqueId;
    }

    /**
     * Returns the clean name for this player (lowercase)
     *
     * @return the name
     */
    public String getCleanName()
    {
        return displayName.toLowerCase();
    }

    /**
     * (used internally)
     *
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.displayName = name;
    }

    /**
     * (used internally)
     *
     * @param uniqueId the name to set
     */
    public void setUniqueId(UUID uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    /**
     * Whether this player is a leader or not
     *
     * @return the leader
     */
    public boolean isLeader()
    {
        return leader;
    }

    /**
     * Sets this player as a leader (does not update clanplayer to db)
     *
     * @param leader the leader to set
     */
    public void setLeader(boolean leader)
    {
        if (leader)
        {
            trusted = leader;
        }

        this.leader = leader;
    }

    /**
     * Check whether the player is an ally with another player
     *
     * @param player
     * @return
     */
    public boolean isAlly(Player player)
    {
        ClanPlayer allycp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (allycp != null)
        {
            return allycp.getClan().isAlly(tag);
        }

        return false;
    }

    /**
     * Check whether the player is an rival with another player
     *
     * @param player
     * @return
     */
    public boolean isRival(Player player)
    {
        ClanPlayer allycp = SimpleClans.getInstance().getClanManager().getClanPlayer(player);

        if (allycp != null)
        {
            return allycp.getClan().isRival(tag);
        }

        return false;
    }


    /**
     * Returns the last seen date for this player in milliseconds
     *
     * @return the lastSeen
     */
    public long getLastSeen()
    {
        return lastSeen;
    }

    /**
     * (used internally)
     *
     * @param lastSeen the lastSeen to set
     */
    public void setLastSeen(long lastSeen)
    {
        this.lastSeen = lastSeen;
    }

    /**
     * Updates last seen date to today
     */
    public void updateLastSeen()
    {
        this.lastSeen = (new Date()).getTime();
    }

    /**
     *
     * @return a verbal representation of how many days ago a player was last seen
     */
    public String getLastSeenDaysString()
    {
        return getLastSeenDaysString(null);
    }

    /**
     * @param sender the Player viewing the last seen days
     * @return a verbal representation of how many days ago a player was last seen
     */
    public String getLastSeenDaysString(@Nullable CommandSender sender)
    {
        double days = Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));

        if (days < 1)
        {
            return lang("today", sender);
        }
        else if (Math.round(days) == 1)
        {
            return lang("1.color.day", sender, ChatColor.GRAY);
        }
        else
        {
            return lang("many.color.days", sender, Math.round(days), ChatColor.GRAY);
        }
    }

    public String getLastSeenDaysString(@Nullable Player viewer) {
        return getLastSeenDaysString((CommandSender) viewer);
    }

    /**
     * Returns number of days since the player was last seen
     *
     * @return
     */
    public double getLastSeenDays()
    {
        return Dates.differenceInDays(new Timestamp(lastSeen), new Timestamp((new Date()).getTime()));
    }

    /**
     * Returns the number of rival kills this player has
     *
     * @return the rivalKills
     */
    public int getRivalKills()
    {
        return rivalKills;
    }

    /**
     * (used internally)
     *
     * @param rivalKills the rivalKills to set
     */
    public void setRivalKills(int rivalKills)
    {
        this.rivalKills = rivalKills;
    }

    /**
     * Adds one rival kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addRivalKill()
    {
        setRivalKills(getRivalKills() + 1);
    }

    /**
     * Returns the number of civilian kills this player has
     *
     * @return the civilianKills
     */
    public int getCivilianKills()
    {
        return civilianKills;
    }

    /**
     * (used internally)
     *
     * @param civilianKills the civilianKills to set
     */
    public void setCivilianKills(int civilianKills)
    {
        this.civilianKills = civilianKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addCivilianKill()
    {
        setCivilianKills(getCivilianKills() + 1);
    }

    /**
     * Returns the number of neutral kills this player has
     *
     * @return the neutralKills
     */
    public int getNeutralKills()
    {
        return neutralKills;
    }

    /**
     * (used internally)
     *
     * @param neutralKills the neutralKills to set
     */
    public void setNeutralKills(int neutralKills)
    {
        this.neutralKills = neutralKills;
    }

    /**
     * Adds one civilian kill to this player (does not update clanplayer to db)
     */
    @Deprecated
    public void addNeutralKill()
    {
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
    public boolean isFriendlyFire()
    {
        return friendlyFire;
    }

    /**
     * Sets whether this player is allowing friendly fire (does not update clanplayer to db)
     *
     * @param friendlyFire the friendlyFire to set
     */
    public void setFriendlyFire(boolean friendlyFire)
    {
        this.friendlyFire = friendlyFire;
    }

    /**
     * (used internally)
     *
     * @return the vote
     */
    public @Nullable VoteResult getVote()
    {
        return vote;
    }

    /**
     * (used internally)
     *
     * @param vote the vote to set
     */
    public void setVote(@Nullable VoteResult vote)
    {
        this.vote = vote;
    }

    /**
     * Returns the number of deaths this player has
     *
     * @return the deaths
     */
    public int getDeaths()
    {
        return deaths;
    }

    /**
     * (used internally)
     *
     * @param deaths the deaths to set
     */
    public void setDeaths(int deaths)
    {
        this.deaths = deaths;
    }

    /**
     * Adds one death to this player  (does not update clanplayer to db)
     */
    public void addDeath()
    {
        setDeaths(getDeaths() + 1);
    }

    /**
     * Returns weighted kill score for this player (kills multiplied by the different weights)
     *
     * @return
     */
    public double getWeightedKills()
    {
        SimpleClans plugin = SimpleClans.getInstance();
        return ((double) rivalKills * plugin.getSettingsManager().getKwRival()) + ((double) neutralKills * plugin.getSettingsManager().getKwNeutral()) + ((double) civilianKills * plugin.getSettingsManager().getKwCivilian());
    }

    /**
     * Returns weighted-kill/death ratio
     *
     * @return
     */
    public float getKDR()
    {
        int totalDeaths = getDeaths();

        if (totalDeaths == 0)
        {
            totalDeaths = 1;
        }

        return ((float) getWeightedKills()) / ((float) totalDeaths);
    }

    /**
     * Returns the player's join date to his current clan in milliseconds, 0 if not in a clan
     *
     * @return the joinDate
     */
    public long getJoinDate()
    {
        return joinDate;
    }

    /**
     * (used internally)
     *
     * @param joinDate the joinDate to set
     */
    public void setJoinDate(long joinDate)
    {
        this.joinDate = joinDate;
    }

    /**
     * Returns a string representation of the join date, blank if not in a clan
     *
     * @return
     */
    public String getJoinDateString()
    {
        if (joinDate == 0)
        {
            return "";
        }

        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.joinDate));
    }

    /**
     *
     * @return a string representation of the last seen date
     */
    @NotNull
    public String getLastSeenString()
    {
    	return getLastSeenString(null);
    }

    /**
     *
     * @param viewer the Player viewing the last seen
     * @return a string representation of the last seen date
     */
    public String getLastSeenString(@Nullable Player viewer) {
        return getLastSeenString((CommandSender) viewer);
    }

    public String getLastSeenString(@Nullable CommandSender sender) {
        Player player = toPlayer();
        if (player != null && player.isOnline() && !Helper.isVanished(sender, player)) {
            return lang("online", sender);
        }
        return new java.text.SimpleDateFormat("MMM dd, ''yy h:mm a").format(new Date(this.lastSeen));
    }

    /**
     * Returns the number of days the player has been inactive
     *
     * @return
     */
    public int getInactiveDays()
    {
        Timestamp now = new Timestamp((new Date()).getTime());
        return (int) Math.floor(Dates.differenceInDays(new Timestamp(getLastSeen()), now));
    }

    /**
     * (used internally)
     *
     * @return the PackedPastClans
     */
    public String getPackedPastClans()
    {
        String packedPastClans = "";

        Set<String> pt = getPastClans();

        for (String pastClan : pt)
        {
            packedPastClans += pastClan + "|";
        }

        return Helper.stripTrailing(packedPastClans, "|");
    }

    /**
     * (used internally)
     *
     * @param PackedPastClans the PackedPastClans to set
     */
    public void setPackedPastClans(String PackedPastClans)
    {
        this.pastClans = Helper.fromArray2(PackedPastClans.split("[|]"));
    }

    /**
     * Adds a past clan to the player (does not update the clanplayer to db)
     *
     * @param tag
     */
    public void addPastClan(String tag)
    {
        this.getPastClans().add(tag);
    }

    /**
     * Removes a past clan from the player (does not update the clanplayer to db)
     *
     * @param tag is the clan's colored tag
     */
    public void removePastClan(String tag)
    {
        this.getPastClans().remove(tag);
    }

    /**
     *
     * @param sep the separator
     * @return a separator delimited string with the color tags for all past clans this player has been in
     */
    @NotNull
    public String getPastClansString(@NotNull String sep)
    {
        return getPastClansString(sep, null);
    }

    /**
     *
     * @param sep the separator
     * @param viewer the Player viewing the clan's string
     * @return a separator delimited string with the color tags for all past clans this player has been in
     */
    @NotNull
    public String getPastClansString(String sep, @Nullable Player viewer) {
        StringBuilder out = new StringBuilder();

        for (String pastClan : getPastClans())
        {
            out.append(pastClan).append(sep);
        }

        out = new StringBuilder(Helper.stripTrailing(out.toString(), sep));

        if (out.toString().trim().isEmpty())
        {
            return lang("none", viewer);
        }

        return out.toString();
    }

    /**
     * Returns a list with all past clans color tags this player has been in
     *
     * @return the pastClans
     */
    public Set<String> getPastClans()
    {
//        HashSet<String> pc = new HashSet<>();
//        pc.addAll(pastClans);
//        return pc;
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
     * @param tag
     * @return the time in millis
     */
    public Long getResignTime(String tag) {
    	return resignTimes.get(tag);
    }
    
    /**
     * Sets the resign times (does not update to db)
     * 
     * @param resignTimes
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
     * 
     * @param tag
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
    public Clan getClan()
    {
        return clan;
    }

    /**
     * (used internally)
     *
     * @param clan the clan to set
     */
    public void setClan(Clan clan)
    {
        if (clan == null)
        {
            this.tag = "";
        }
        else
        {
            this.tag = clan.getTag();
        }

        this.clan = clan;
    }

    /**
     * Returns this player's clan's tag.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    public String getTag()
    {
        return tag;
    }

    /**
     * Returns this player's clan's tag label.  Empty string if he's not in a clan.
     *
     * @return the tag
     */
    public String getTagLabel()
    {
        if (clan == null)
        {
            return "";
        }

        return clan.getTagLabel(isLeader());
    }

    /**
     * Returns this player's trusted status
     *
     * @return the trusted
     */
    public boolean isTrusted()
    {
        return leader || trusted;
    }

    /**
     * Sets this player's trusted status (does not update the clanplayer to db)
     *
     * @param trusted the trusted to set
     */
    public void setTrusted(boolean trusted)
    {
        this.trusted = trusted;
    }

    /**
     * Return the list of flags and their data as a json string
     *
     * @return the flags
     */
    @SuppressWarnings("unchecked")
	public String getFlags()
    {
        JSONObject json = new JSONObject();

        // the player's rank inside his clan

        if (rank != null)
        {
            json.put("rank", rank);
        }

        // writing the list of flags to json

        json.put("channel", channel.toString());

        // writing the channel state settings flags

        List<Boolean> settings = new LinkedList<>();
        settings.add(globalChat);
        settings.add(allyChat);
        settings.add(clanChat);

        json.put("channel-state", settings);

        // couple of toggles

        json.put("chat-shortcut", useChatShortcut);
        json.put("bb-enabled", bbEnabled);
        json.put("hide-tag", tagEnabled);
        json.put("cape-enabled", capeEnabled);

        return json.toString();
    }

    /**
     * Read the list of flags in from a json string
     *
     * @param flagString the flags to set
     */
    public void setFlags(String flagString)
    {
        if (flagString != null && !flagString.isEmpty())
        {
            Object obj = JSONValue.parse(flagString);
            JSONObject flags = (JSONObject) obj;

            if (flags != null)
            {
                for (Object flag : flags.keySet())
                {
                    try
                    {
                        if (flag.equals("rank"))
                        {
                            if(flags.get(flag) == null)
                            {
                                continue;
                            }

                            rank = flags.get(flag).toString();
                        }

                        if (flag.equals("channel"))
                        {
                            String chn = flags.get(flag).toString();

                            if (chn != null && !chn.isEmpty())
                            {
                                if (chn.equalsIgnoreCase("clan"))
                                {
                                    channel = Channel.CLAN;
                                }
                                else if (chn.equalsIgnoreCase("ally"))
                                {
                                    channel = Channel.ALLY;
                                }
                                else
                                {
                                    channel = Channel.NONE;
                                }
                            }
                        }

                        if (flag.equals("channel-state"))
                        {
                            JSONArray settings = (JSONArray) flags.get(flag);

                            if (settings != null && !settings.isEmpty())
                            {
                                globalChat = (Boolean) settings.get(0);
                                allyChat = (Boolean) settings.get(1);
                                clanChat = (Boolean) settings.get(2);
                            }
                        }

                        if (flag.equals("bb-enabled"))
                        {
                            bbEnabled = (Boolean) flags.get(flag);
                        }

                        if (flag.equals("hide-tag"))
                        {
                            tagEnabled = (Boolean) flags.get(flag);
                        }

                        if (flag.equals("cape-enabled"))
                        {
                            capeEnabled = (Boolean) flags.get(flag);
                        }

                        if (flag.equals("chat-shortcut"))
                        {
                            useChatShortcut = (Boolean) flags.get(flag);
                        }
                    }
                    catch (Exception ex)
                    {
                        for (StackTraceElement el : ex.getStackTrace())
                        {
                            System.out.print("Failed reading flag: " + flag);
                            System.out.print("Value: " + flags.get(flag));
                            System.out.print(el.toString());
                        }
                    }
                }
            }
        }
    }

    public Channel getChannel()
    {
        return channel;
    }

    @Deprecated
    public boolean isGlobalChat()
    {
        return globalChat;
    }

    @Deprecated
    public boolean isAllyChat()
    {
        return allyChat;
    }

    @Deprecated
    public boolean isClanChat()
    {
        return clanChat;
    }

    @Deprecated
    public void setGlobalChat(boolean globalChat)
    {
        this.globalChat = globalChat;
    }

    @Deprecated
    public void setAllyChat(boolean allyChat)
    {
        this.allyChat = allyChat;
    }

    @Deprecated
    public void setClanChat(boolean clanChat)
    {
        this.clanChat = clanChat;
    }

    public void setChannel(Channel channel)
    {
        this.channel = channel;
    }

    public boolean isBbEnabled()
    {
        return bbEnabled;
    }

    public void setBbEnabled(boolean bbEnabled)
    {
        this.bbEnabled = bbEnabled;
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
    }

    public boolean isCapeEnabled()
    {
        return capeEnabled;
    }

    public void setCapeEnabled(boolean capeEnabled)
    {
        this.capeEnabled = capeEnabled;
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
    }

    public boolean isTagEnabled()
    {
        return tagEnabled;
    }

    public void setTagEnabled(boolean tagEnabled)
    {
        this.tagEnabled = tagEnabled;
        SimpleClans.getInstance().getStorageManager().updateClanPlayer(this);
        SimpleClans.getInstance().getClanManager().updateDisplayName(this.toPlayer());
    }

    public boolean isUseChatShortcut()
    {
        return useChatShortcut;
    }

    public String getRankDisplayName() {
    	if (clan != null) {
    		Rank r = clan.getRank(rank);
    		if (r != null) {
    			return r.getDisplayName();
    		}
    	}
    	return "";
    }
    
    /**
     * Gets the rank id
     * 
     * @return
     */
    public String getRankId() {
    	return rank;
    }
    
    /**
     * Gets the rank displayname
     * 
     * @return
     */
    @Deprecated
    public String getRank()
    {
        return getRankDisplayName();
    }

    /**
     * Sets the rank id
     * 
     * @param rank the rank id
     */
    public void setRank(@Nullable String rank)
    {
    	if (rank == null) {
    		rank = "";
    	}
        this.rank = rank;
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

    public enum Channel
    {
        CLAN,
        ALLY,
        NONE
    }

    public @Nullable Player toPlayer()
    {
        if (this.uniqueId != null)
        {
            return SimpleClans.getInstance().getServer().getPlayer(this.uniqueId);
        } else
        {
            return SimpleClans.getInstance().getServer().getPlayer(this.displayName);
        }
    }
   public void setMuted(boolean b)
   {
       clanChatMute = b;
   }

   public void setMutedAlly(boolean b)
   {
       allyChatMute = b;
   }

   public boolean isMuted()
   {
       return clanChatMute;
   }

   public boolean isMutedAlly()
   {
       return allyChatMute;
   }
}
