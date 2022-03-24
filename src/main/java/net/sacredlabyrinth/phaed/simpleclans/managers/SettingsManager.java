package net.sacredlabyrinth.phaed.simpleclans.managers;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * @author phaed
 */
public final class SettingsManager {
	private boolean enableGUI;
    private boolean disableMessages;
    private String clanChatRankColor;
    private boolean tagBasedClanChat;
    private boolean teleportOnSpawn;
    private boolean dropOnHome;
    private boolean keepOnHome;
    private boolean debugging;
    private final SimpleClans plugin;
    private boolean mChatIntegration;
    private boolean pvpOnlywhileInWar;
    private boolean useColorCodeFromPrefix;
    private boolean confirmationForPromote;
    private boolean confirmationForDemote;
    private double percentageOnlineToDemote;
    private boolean globalff;
    private boolean allowResetKdr;
    private boolean showUnverifiedOnList;
    private boolean requireVerification;
    private boolean rejoinCooldownEnabled;
    private int rejoinCooldown;
    private String listDefault;
    private String listSize;
    private String listKdr;
    private String listName;
    private String listFounded;
    private String listActive;
    private String listAsc;
    private String listDesc;
    private List<Integer> itemsList;
    private List<String> blacklistedWorlds;
    private List<String> bannedPlayers;
    private List<String> disallowedWords;
    private List<String> disallowedColors;
    private List<String> unRivableClans;
    private int rivalLimitPercent;
    private boolean ePurchaseCreation;
    private boolean ePurchaseVerification;
    private boolean ePurchaseInvite;
    private boolean ePurchaseHomeTeleport;
    private boolean ePurchaseHomeRegroup;
    private boolean eUniqueTaxOnRegroup;
    private boolean eIssuerPaysRegroup;
    private boolean ePurchaseHomeTeleportSet;
    private boolean ePurchaseResetKdr;
    private boolean eMemberFee;
    private boolean ePurchaseMemberFeeSet;
    private boolean eClanUpkeepEnabled;
    private boolean eMultiplyUpkeepBySize;
    private boolean eChargeUpkeepOnlyIfMemberFeeEnabled;
    private double eCreationPrice;
    private double eVerificationPrice;
    private double eInvitePrice;
    private double eHomeTeleportPrice;
    private double eHomeRegroupPrice;
    private double eHomeTeleportPriceSet;
    private double eResetKdr;
    private double eMaxMemberFee;
    private double eMemberFeeSetPrice;
    private double eClanUpkeep;
    private String serverName;
    private boolean chatTags;
    private int purgeClan;
    private int purgeUnverified;
    private int purgePlayers;
    private int requestFreqencySecs;
    private String requestMessageColor;
    private int pageSize;
    private String pageSep;
    private String pageHeadingsColor;
    private String pageSubTitleColor;
    private String pageLeaderColor;
    private String pageTrustedColor;
    private String pageUnTrustedColor;
    private boolean bbShowOnLogin;
    private int bbSize;
    private int bbLoginSize;
    private String bbColor;
    private String bbAccentColor;
    private String commandClan;
    private String commandAlly;
    private String commandGlobal;
    private String commandMore;
    private String commandDeny;
    private String commandAccept;
    private String commandClanChat;
    private int clanMinSizeToAlly;
    private int clanMinSizeToRival;
    private int clanMinLength;
    private int clanMaxAlliances;
    private int clanMaxLength;
    private int clanMaxDescriptionLength;
    private int clanMinDescriptionLength;
    private String pageClanNameColor;
    private int tagMinLength;
    private int tagMaxLength;
    private String tagDefaultColor;
    private String tagSeparator;
    private String tagSeparatorColor;
    private String tagSeparatorLeaderColor;
    private String tagBracketLeft;
    private String tagBracketRight;
    private String tagBracketColor;
    private String tagBracketLeaderColor;
    private boolean clanTrustByDefault;
    private boolean allyChatEnable;
    private String allyChatFormat;
    private String allyChatRank;
    private String allyChatLeaderColor;
    private String allyChatTrustedColor;
    private String allyChatMemberColor;
    private String allyChatMessageColor;
    private String allyChatNameColor;
    private String allyChatTagColor;
    private String allyChatTagBracketLeft;
    private String allyChatTagBracketRight;
    private String allyChatBracketColor;
    private String allyChatPlayerBracketLeft;
    private String allyChatPlayerBracketRight;
    private boolean clanChatEnable;
    private String clanChatFormat;
    private String clanChatRank;
    private String clanChatLeaderColor;
    private String clanChatTrustedColor;
    private String clanChatMemberColor;
    private String clanChatAnnouncementColor;
    private String clanChatMessageColor;
    private String clanChatNameColor;
    private String clanChatTagBracketLeft;
    private String clanChatTagBracketRight;
    private String clanChatBracketColor;
    private String clanChatPlayerBracketLeft;
    private String clanChatPlayerBracketRight;
    private int tasksCollectUpkeepHour;
    private int tasksCollectUpkeepMinute;
    private int tasksCollectUpkeepWarningHour;
    private int tasksCollectUpkeepWarningMinute;
    private int tasksCollectFeeHour;
    private int tasksCollectFeeMinute;
    private boolean clanFFOnByDefault;
    private double kwRival;
    private double kwNeutral;
    private double kwCivilian;
    private boolean useMysql;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private boolean safeCivilians;
    private final File main;
    private final FileConfiguration config;
    private boolean compatMode;
    private boolean homebaseSetOnce;
    private int waitSecs;
    private boolean enableAutoGroups;
    private boolean denySameIPKills;
    private boolean moneyperkill;
    private double KDRMultipliesPerKill;
    private boolean teleportBlocks;
    private boolean AutoGroupGroupName;
    private boolean tamableMobsSharing;
    private boolean allowReGroupCommand;
    private boolean useThreads;
    private boolean useBungeeCord;
    private boolean forceCommandPriority;
    private int maxAsksPerRequest;
    private int maxMembers;
    private boolean maxKillsPerVictimEnabled;
    private int maxKillsPerVictim;
    private boolean delayBetweenKillsEnabled;
    private int delayBetweenKills;
	private String language;
	private boolean languagePerPlayer;
	private boolean savePeriodically;
	private int saveInterval;

    /**
     *
     */
    public SettingsManager() {
        plugin = SimpleClans.getInstance();
        config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        load();
    }

    /**
     * Load the configuration
     */

    @SuppressWarnings({"CallToPrintStackTrace", "UseSpecificCatch"})
    public void load() {
        boolean exists = (main).exists();

        if (exists) {
            try {
                getConfig().options().copyDefaults(true);
                getConfig().load(main);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getConfig().options().copyDefaults(true);
        }

        enableGUI = getConfig().getBoolean("settings.enable-gui");
        disableMessages = getConfig().getBoolean("settings.disable-messages");
        teleportOnSpawn = getConfig().getBoolean("settings.teleport-home-on-spawn");
        dropOnHome = getConfig().getBoolean("settings.drop-items-on-clan-home");
        keepOnHome = getConfig().getBoolean("settings.keep-items-on-clan-home");
        itemsList = getConfig().getIntegerList("settings.item-list");
        debugging = getConfig().getBoolean("settings.show-debug-info");
        mChatIntegration = getConfig().getBoolean("settings.mchat-integration");
        pvpOnlywhileInWar = getConfig().getBoolean("settings.pvp-only-while-at-war");
        enableAutoGroups = getConfig().getBoolean("settings.enable-auto-groups");
        useColorCodeFromPrefix = getConfig().getBoolean("settings.use-colorcode-from-prefix-for-name");
        bannedPlayers = getConfig().getStringList("settings.banned-players");
        allowResetKdr = getConfig().getBoolean("settings.allow-reset-kdr");
        compatMode = getConfig().getBoolean("settings.chat-compatibility-mode");
        disallowedColors = getConfig().getStringList("settings.disallowed-tag-colors");
        blacklistedWorlds = getConfig().getStringList("settings.blacklisted-worlds");
        disallowedWords = getConfig().getStringList("settings.disallowed-tags");
        unRivableClans = getConfig().getStringList("settings.unrivable-clans");
        showUnverifiedOnList = getConfig().getBoolean("settings.show-unverified-on-list");
        requireVerification = getConfig().getBoolean("settings.new-clan-verification-required");
        rejoinCooldown = getConfig().getInt("settings.rejoin-cooldown");
        rejoinCooldownEnabled = getConfig().getBoolean("settings.rejoin-cooldown-enabled");
        listActive = getConfig().getString("list.active", "active");
        listKdr = getConfig().getString("list.kdr", "kdr");
        listDefault = getConfig().getString("list.default", listKdr);
        listSize = getConfig().getString("list.size", "size");
        listName = getConfig().getString("list.name", "name");
        listFounded = getConfig().getString("list.founded", "founded");
        listAsc = getConfig().getString("list.asc", "asc");
        listDesc = getConfig().getString("list.desc", "desc");
        serverName = getConfig().getString("settings.server-name");
        chatTags = getConfig().getBoolean("settings.display-chat-tags");
        rivalLimitPercent = getConfig().getInt("settings.rival-limit-percent");
        ePurchaseCreation = getConfig().getBoolean("economy.purchase-clan-create");
        ePurchaseVerification = getConfig().getBoolean("economy.purchase-clan-verify");
        ePurchaseInvite = getConfig().getBoolean("economy.purchase-clan-invite");
        ePurchaseHomeTeleport = getConfig().getBoolean("economy.purchase-home-teleport");
        ePurchaseHomeRegroup = getConfig().getBoolean("economy.purchase-home-regroup");
        ePurchaseHomeTeleportSet = getConfig().getBoolean("economy.purchase-home-teleport-set");
        ePurchaseResetKdr = getConfig().getBoolean("economy.purchase-reset-kdr");
        ePurchaseMemberFeeSet = getConfig().getBoolean("economy.purchase-member-fee-set");
        eMemberFeeSetPrice = getConfig().getDouble("economy.member-fee-set-price");
        eResetKdr = getConfig().getDouble("economy.reset-kdr-price");
        eCreationPrice = getConfig().getDouble("economy.creation-price");
        eVerificationPrice = getConfig().getDouble("economy.verification-price");
        eInvitePrice = getConfig().getDouble("economy.invite-price");
        eHomeTeleportPrice = getConfig().getDouble("economy.home-teleport-price");
        eHomeRegroupPrice = getConfig().getDouble("economy.home-regroup-price");
        eUniqueTaxOnRegroup = getConfig().getBoolean("economy.unique-tax-on-regroup");
        eIssuerPaysRegroup = getConfig().getBoolean("economy.issuer-pays-regroup");
        eHomeTeleportPriceSet = getConfig().getDouble("economy.home-teleport-set-price");
        eMaxMemberFee = getConfig().getDouble("economy.max-member-fee");
        eClanUpkeep = getConfig().getDouble("economy.upkeep");
        eClanUpkeepEnabled = getConfig().getBoolean("economy.upkeep-enabled");
        eChargeUpkeepOnlyIfMemberFeeEnabled = getConfig().getBoolean("economy.charge-upkeep-only-if-member-fee-enabled");
        eMultiplyUpkeepBySize = getConfig().getBoolean("economy.multiply-upkeep-by-clan-size");
        eMemberFee = getConfig().getBoolean("economy.member-fee-enabled");
        purgeClan = getConfig().getInt("purge.inactive-clan-days");
        purgeUnverified = getConfig().getInt("purge.unverified-clan-days");
        purgePlayers = getConfig().getInt("purge.inactive-player-data-days");
        requestFreqencySecs = getConfig().getInt("request.ask-frequency-secs");
        requestMessageColor = getConfig().getString("request.message-color");
        setMaxAsksPerRequest(getConfig().getInt("request.max-asks-per-request"));
        pageSize = getConfig().getInt("page.size");
        pageSep = getConfig().getString("page.separator");
        pageSubTitleColor = getConfig().getString("page.subtitle-color");
        pageHeadingsColor = getConfig().getString("page.headings-color");
        pageLeaderColor = getConfig().getString("page.leader-color");
        pageTrustedColor = getConfig().getString("page.trusted-color");
        pageUnTrustedColor = getConfig().getString("page.untrusted-color");
        pageClanNameColor = getConfig().getString("page.clan-name-color");
        bbShowOnLogin = getConfig().getBoolean("bb.show-on-login");
        bbSize = getConfig().getInt("bb.size");
        bbLoginSize = getConfig().getInt("bb.login-size", bbSize);
        bbColor = getConfig().getString("bb.color");
        bbAccentColor = getConfig().getString("bb.accent-color");
        commandClan = getConfig().getString("commands.clan");
        commandAlly = getConfig().getString("commands.ally");
        commandGlobal = getConfig().getString("commands.global");
        commandMore = getConfig().getString("commands.more");
        commandDeny = getConfig().getString("commands.deny");
        commandAccept = getConfig().getString("commands.accept");
        commandClanChat = getConfig().getString("commands.clan_chat");
        forceCommandPriority = getConfig().getBoolean("commands.force-priority");
        homebaseSetOnce = getConfig().getBoolean("clan.homebase-can-be-set-only-once");
        waitSecs = getConfig().getInt("clan.homebase-teleport-wait-secs");
        confirmationForPromote = getConfig().getBoolean("clan.confirmation-for-demote");
        confirmationForDemote = getConfig().getBoolean("clan.confirmation-for-promote");
        percentageOnlineToDemote = getConfig().getDouble("clan.percentage-online-to-demote");
        clanTrustByDefault = getConfig().getBoolean("clan.trust-members-by-default");
        clanMinSizeToAlly = getConfig().getInt("clan.min-size-to-set-ally");
        clanMinSizeToRival = getConfig().getInt("clan.min-size-to-set-rival");
        clanMinLength = getConfig().getInt("clan.min-length");
        clanMaxAlliances = getConfig().getInt("clan.max-alliances");
        clanMaxLength = getConfig().getInt("clan.max-length");
        clanMaxDescriptionLength = getConfig().getInt("clan.max-description-length");
        clanMinDescriptionLength = getConfig().getInt("clan.min-description-length");
        clanFFOnByDefault = getConfig().getBoolean("clan.ff-on-by-default");
        tagMinLength = getConfig().getInt("tag.min-length");
        tagMaxLength = getConfig().getInt("tag.max-length");
        tagDefaultColor = getConfig().getString("tag.default-color");
        tagSeparator = getConfig().getString("tag.separator.char");
        tagSeparatorColor = getConfig().getString("tag.separator.color");
        tagSeparatorLeaderColor = getConfig().getString("tag.separator.leader-color");
        tagBracketColor = getConfig().getString("tag.bracket.color");
        tagBracketLeaderColor = getConfig().getString("tag.bracket.leader-color");
        tagBracketLeft = getConfig().getString("tag.bracket.left");
        tagBracketRight = getConfig().getString("tag.bracket.right");
        allyChatEnable = getConfig().getBoolean("allychat.enable");
        allyChatFormat = getConfig().getString("allychat.format");
        allyChatRank = getConfig().getString("allychat.rank");
        allyChatLeaderColor = getConfig().getString("allychat.leader-color");
        allyChatTrustedColor = getConfig().getString("allychat.trusted-color");
        allyChatMemberColor = getConfig().getString("allychat.member-color");
        allyChatMessageColor = getConfig().getString("allychat.message-color");
        allyChatTagColor = getConfig().getString("allychat.tag-color");
        allyChatNameColor = getConfig().getString("allychat.name-color");
        allyChatBracketColor = getConfig().getString("allychat.tag-bracket.color");
        allyChatTagBracketLeft = getConfig().getString("allychat.tag-bracket.left");
        allyChatTagBracketRight = getConfig().getString("allychat.tag-bracket.right");
        allyChatPlayerBracketLeft = getConfig().getString("allychat.player-bracket.left");
        allyChatPlayerBracketRight = getConfig().getString("allychat.player-bracket.right");
        clanChatEnable = getConfig().getBoolean("clanchat.enable");
        clanChatFormat = getConfig().getString("clanchat.format");
        clanChatRank = getConfig().getString("clanchat.rank");
        clanChatLeaderColor = getConfig().getString("clanchat.leader-color");
        clanChatTrustedColor = getConfig().getString("clanchat.trusted-color");
        clanChatMemberColor = getConfig().getString("clanchat.member-color");
        tagBasedClanChat = getConfig().getBoolean("clanchat.tag-based-clan-chat");
        clanChatAnnouncementColor = getConfig().getString("clanchat.announcement-color");
        clanChatMessageColor = getConfig().getString("clanchat.message-color");
        clanChatNameColor = getConfig().getString("clanchat.name-color");
        clanChatRankColor = getConfig().getString("clanchat.rank.color");
        clanChatBracketColor = getConfig().getString("clanchat.tag-bracket.color");
        clanChatTagBracketLeft = getConfig().getString("clanchat.tag-bracket.left");
        clanChatTagBracketRight = getConfig().getString("clanchat.tag-bracket.right");
        clanChatPlayerBracketLeft = getConfig().getString("clanchat.player-bracket.left");
        clanChatPlayerBracketRight = getConfig().getString("clanchat.player-bracket.right");
        tasksCollectFeeHour = getConfig().getInt("tasks.collect-fee.hour");
        tasksCollectFeeMinute = getConfig().getInt("tasks.collect-fee.minute");
        tasksCollectUpkeepHour = getConfig().getInt("tasks.collect-upkeep.hour");
        tasksCollectUpkeepMinute = getConfig().getInt("tasks.collect-upkeep.minute");
        tasksCollectUpkeepWarningHour = getConfig().getInt("tasks.collect-upkeep-warning.hour");
        tasksCollectUpkeepWarningMinute = getConfig().getInt("tasks.collect-upkeep-warning.minute");
        kwRival = getConfig().getDouble("kill-weights.rival");
        kwNeutral = getConfig().getDouble("kill-weights.neutral");
        kwCivilian = getConfig().getDouble("kill-weights.civilian");
        denySameIPKills = getConfig().getBoolean("kill-weights.deny-same-ip-kills");
        useMysql = getConfig().getBoolean("mysql.enable");
        host = getConfig().getString("mysql.host");
        port = getConfig().getInt("mysql.port");
        database = getConfig().getString("mysql.database");
        username = getConfig().getString("mysql.username");
        password = getConfig().getString("mysql.password");
        port = getConfig().getInt("mysql.port");
        safeCivilians = getConfig().getBoolean("safe-civilians");
        moneyperkill = getConfig().getBoolean("economy.money-per-kill");
        KDRMultipliesPerKill = getConfig().getDouble("economy.money-per-kill-kdr-multipier");
        teleportBlocks = getConfig().getBoolean("settings.teleport-blocks");
        language = getConfig().getString("settings.language", "");
        languagePerPlayer = getConfig().getBoolean("settings.language-per-player", false);
        AutoGroupGroupName = getConfig().getBoolean("permissions.auto-group-groupname");
        tamableMobsSharing = getConfig().getBoolean("settings.tameable-mobs-sharing");
        allowReGroupCommand = getConfig().getBoolean("settings.allow-regroup-command");
        savePeriodically = getConfig().getBoolean("performance.save-periodically");
        saveInterval = getConfig().getInt("performance.save-interval");
        useThreads = getConfig().getBoolean("performance.use-threads");
        useBungeeCord = getConfig().getBoolean("performance.use-bungeecord");
        maxMembers = getConfig().getInt("clan.max-members");
        maxKillsPerVictim = getConfig().getInt("kdr-grinding-prevention.max-kills-per-victim");
        maxKillsPerVictimEnabled = getConfig().getBoolean("kdr-grinding-prevention.enable-max-kills");
        delayBetweenKills = getConfig().getInt("kdr-grinding-prevention.delay-between-kills");
        delayBetweenKillsEnabled = getConfig().getBoolean("kdr-grinding-prevention.enable-kill-delay");
        
        // migrate from old way of adding ports
        if (database.contains(":")) {
            String[] strings = database.split(":");
            database = strings[0];
            port = Integer.valueOf(strings[1]);
        }

        save();
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void save() {
        try {
            getConfig().save(main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean isEnableGUI() {
		return enableGUI;
	}
    
    public void setEnableGUI(boolean enableGUI) {
		this.enableGUI = enableGUI;
        getConfig().set("settings.enable-gui", enableGUI);
        save();
    }
    
    public Locale getLanguage() {
    	String[] split = language.split("_");
    	
    	if (split.length == 2) {
    		return new Locale(split[0], split[1]);
    	}

    	return new Locale(language);
    }
    
    public boolean isLanguagePerPlayer() {
    	return languagePerPlayer;
    }
    
    public int getTasksCollectUpkeepHour() {
		return tasksCollectUpkeepHour;
	}

	public void setTasksCollectUpkeepHour(int tasksCollectUpkeepHour) {
		this.tasksCollectUpkeepHour = tasksCollectUpkeepHour;
	}

	public int getTasksCollectUpkeepMinute() {
		return tasksCollectUpkeepMinute;
	}

	public void setTasksCollectUpkeepMinute(int tasksCollectUpkeepMinute) {
		this.tasksCollectUpkeepMinute = tasksCollectUpkeepMinute;
	}

	public int getTasksCollectUpkeepWarningHour() {
		return tasksCollectUpkeepWarningHour;
	}

	public void setTasksCollectUpkeepWarningHour(int tasksCollectUpkeepWarningHour) {
		this.tasksCollectUpkeepWarningHour = tasksCollectUpkeepWarningHour;
	}

	public int getTasksCollectUpkeepWarningMinute() {
		return tasksCollectUpkeepWarningMinute;
	}

	public void setTasksCollectUpkeepWarningMinute(int tasksCollectUpkeepWarningMinute) {
		this.tasksCollectUpkeepWarningMinute = tasksCollectUpkeepWarningMinute;
	}

	public int getTasksCollectFeeHour() {
		return tasksCollectFeeHour;
	}

	public void setTasksCollectFeeHour(int tasksCollectFeeHour) {
		this.tasksCollectFeeHour = tasksCollectFeeHour;
	}

	public int getTasksCollectFeeMinute() {
		return tasksCollectFeeMinute;
	}

	public void setTasksCollectFeeMinute(int tasksCollectFeeMinute) {
		this.tasksCollectFeeMinute = tasksCollectFeeMinute;
	}
	
	/**
     * Returns the delay between kills
     * 
     * @return
     */
    public int getDelayBetweenKills() {
    	return delayBetweenKills;
    }
    
    /**
     * Checks if the delay between kills is enabled
     * 
     * @return
     */
    public boolean isDelayBetweenKills() {
    	return delayBetweenKillsEnabled;
    }
    
    /**
     * Returns the max number of kills per victim
     * 
     * @return
     */
    public int getMaxKillsPerVictim() {
    	return maxKillsPerVictim;
    }
    
    /**
     * Checks if there is a max number of kills per victim
     * 
     * @return
     */
    public boolean isMaxKillsPerVictim() {
    	return maxKillsPerVictimEnabled;
    }

    /**
     * Check whether an item is in the list
     *
     * @param typeId the type
     * @return whether the world is blacklisted
     */
    public boolean isItemInList(int typeId) {
        return itemsList.contains(typeId);
    }

    /**
     * Gets the command set as the clan chat command
     * 
     * @return the clan chat command
     */
    public String getCommandClanChat() {
        return commandClanChat;
    }

    /**
     * Check whether a worlds is blacklisted
     *
     * @param world the world
     * @return whether the world is blacklisted
     */
    public boolean isBlacklistedWorld(String world) {
        for (Object w : blacklistedWorlds) {
            if (((String) w).equalsIgnoreCase(world)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a word is disallowed
     *
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word) {
        for (Object w : disallowedWords) {
            if (((String) w).equalsIgnoreCase(word)) {
                return true;
            }
        }

        return word.equalsIgnoreCase("clan") || word.equalsIgnoreCase(commandMore) || word.equalsIgnoreCase(commandDeny) || word.equalsIgnoreCase(commandAccept);

    }
    
    /**
     * Checks if the upkeep is to be charged only from clans with the member fee enabled
     * 
     * @return
     */
    public boolean isChargeUpkeepOnlyIfMemberFeeEnabled() {
    	return eChargeUpkeepOnlyIfMemberFeeEnabled;
    }
    
    /**
     * Checks if the upkeep should be multiplied by the clan size
     * 
     * @return 
     */
    public boolean isMultiplyUpkeepBySize() {
        return eMultiplyUpkeepBySize;
    }
    
    /**
     * Checks if the upkeep is enabled
     * 
     * @return 
     */
    public boolean isClanUpkeep() {
        return eClanUpkeepEnabled;
    }
    
    /**
     * Returns the upkeep
     * 
     * @return 
     */
    public double getClanUpkeep() {
        if (eClanUpkeep < 0) {
            eClanUpkeep = 0;
        }
        return eClanUpkeep;
    }
    
    
    /**
     * Returns the max member fee allowed
     * @return 
     */
    public double getMaxMemberFee() {
        return eMaxMemberFee;
    }
    
    /**
     * Checks if the member fee is enabled
     * @return 
     */
    public boolean isMemberFee() {
        return eMemberFee;
    }

    public boolean isAllowResetKdr() {
        return allowResetKdr;
    }

    public boolean isePurchaseResetKdr() {
        return ePurchaseResetKdr;
    }
    
    /**
     * Gets the price to pay for setting the member fee
     * 
     * @return the price
     */
    public double geteMemberFeeSetPrice() {
        return eMemberFeeSetPrice;
    }
    
    /**
     * Do leaders need to pay for setting the member fee?
     * @return true if so
     */
    public boolean isePurchaseMemberFeeSet() {
        return ePurchaseMemberFeeSet;
    }

    /**
     * Gets the price to reset the KDR
     * @return the price
     */
    public double geteResetKdr() {
        return eResetKdr;
    }
    
    /**
     * Check whether a string has a disallowed color
     *
     * @param str the string
     * @return whether the string contains the color code
     */
    public boolean hasDisallowedColor(String str) {
        for (Object c : getDisallowedColors()) {
            if (str.contains("&" + c)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return a comma delimited string with all disallowed colors
     */
    public String getDisallowedColorString() {
        String out = "";

        for (Object c : getDisallowedColors()) {
            out += c + ", ";
        }

        return Helper.stripTrailing(out, ", ");
    }

    /**
     * Check whether a clan is un-rivable
     *
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag) {
        for (Object t : getunRivableClans()) {
            if (((String) t).equalsIgnoreCase(tag)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a player is banned
     *
     * @param playerUniqueId the player's name
     * @return whether player is banned
     */
    public boolean isBanned(UUID playerUniqueId) {
        for (String pl : getBannedPlayers()) {
            if (pl.equals(playerUniqueId.toString())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a player to the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void addBanned(UUID playerUniqueId) {
        if (!bannedPlayers.contains(playerUniqueId.toString())) {
            bannedPlayers.add(playerUniqueId.toString());
        }
        
        getConfig().set("settings.banned-players", bannedPlayers);
        save();
    }

    /**
     * Remove a player from the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void removeBanned(UUID playerUniqueId) {
        bannedPlayers.remove(playerUniqueId.toString());
        
        getConfig().set("settings.banned-players", bannedPlayers);
        save();
    }

    /**
     * @return the plugin
     */
    public SimpleClans getPlugin() {
        return plugin;
    }

    /**
     * @return the requireVerification
     */
    public boolean isRequireVerification() {
        return requireVerification;
    }
    
    public boolean isRejoinCooldown() {
    	return rejoinCooldownEnabled;
    }
    
    public int getRejoinCooldown() {
    	return rejoinCooldown;
    }
    
    public String getListDefault() {
		return listDefault;
	}

	public String getListSize() {
		return listSize;
	}

	public String getListKdr() {
		return listKdr;
	}

	public String getListName() {
		return listName;
	}

	public String getListFounded() {
		return listFounded;
	}

	public String getListActive() {
		return listActive;
	}

	public String getListAsc() {
		return listAsc;
	}

	public String getListDesc() {
		return listDesc;
	}

	/**
     * @return the bannedPlayers
     */
    public List<String> getBannedPlayers() {
        return Collections.unmodifiableList(bannedPlayers);
    }

    /**
     * @return the disallowedColors
     */
    public List<String> getDisallowedColors() {
        return Collections.unmodifiableList(disallowedColors);
    }

    /**
     * @return the unRivableClans
     */
    public List<String> getunRivableClans() {
        return Collections.unmodifiableList(unRivableClans);
    }

    /**
     * @return the rivalLimitPercent
     */
    public int getRivalLimitPercent() {
        return rivalLimitPercent;
    }

    /**
     * @return the serverName
     */
    public String getServerName() {
        return Helper.parseColors(serverName);
    }

    /**
     * @return the chatTags
     */
    public boolean isChatTags() {
        return chatTags;
    }

    /**
     * @return the purgeClan
     */
    public int getPurgeClan() {
        return purgeClan;
    }

    /**
     * @return the purgeUnverified
     */
    public int getPurgeUnverified() {
        return purgeUnverified;
    }

    /**
     * @return the purgePlayers
     */
    public int getPurgePlayers() {
        return purgePlayers;
    }

    /**
     * @return the requestFreqencySecs
     */
    public int getRequestFreqencySecs() {
        return requestFreqencySecs;
    }

    /**
     * @return the requestMessageColor
     */
    public String getRequestMessageColor() {
        return Helper.toColor(requestMessageColor);
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @return the pageSep
     */
    public String getPageSep() {
        return pageSep;
    }

    /**
     * @return the pageHeadingsColor
     */
    public String getPageHeadingsColor() {
        return Helper.toColor(pageHeadingsColor);
    }

    /**
     * @return the pageSubTitleColor
     */
    public String getPageSubTitleColor() {
        return Helper.toColor(pageSubTitleColor);
    }

    /**
     * @return the pageLeaderColor
     */
    public String getPageLeaderColor() {
        return Helper.toColor(pageLeaderColor);
    }

    /**
     * @return the bbSize
     */
    public int getBbSize() {
        return bbSize;
    }

    /**
     * @return the bbLoginSize
     */
    public int getBbLoginSize() {
        return bbLoginSize;
    }

    /**
     * @return the bbColor
     */
    public String getBbColor() {
        return Helper.toColor(bbColor);
    }

    /**
     * @return the bbAccentColor
     */
    public String getBbAccentColor() {
        return Helper.toColor(bbAccentColor);
    }

    /**
     * @return the commandClan
     */
    public String getCommandClan() {
        return commandClan;
    }

    /**
     * @return the commandMore
     */
    public String getCommandMore() {
        return commandMore;
    }

    /**
     * @return the commandDeny
     */
    public String getCommandDeny() {
        return commandDeny;
    }

    /**
     * @return the commandAccept
     */
    public String getCommandAccept() {
        return commandAccept;
    }

    /**
     * @return the clanMinSizeToAlly
     */
    public int getClanMinSizeToAlly() {
        return clanMinSizeToAlly;
    }

    /**
     * @return the clanMinSizeToRival
     */
    public int getClanMinSizeToRival() {
        return clanMinSizeToRival;
    }
    
    /**
     * Returns the max length of the clan description
     * 
     * @return the max length
     */
    public int getClanMaxDescriptionLength() {
    	if (clanMaxDescriptionLength > 255 || clanMaxDescriptionLength < 0) {
    		clanMaxDescriptionLength = 255;
    	}
		return clanMaxDescriptionLength;
	}

    /**
     * Returns the min length of the clan description
     * 
     * @return the min length
     */
	public int getClanMinDescriptionLength() {
		if (clanMinDescriptionLength < 0 || clanMinDescriptionLength > getClanMaxDescriptionLength()) {
			clanMinDescriptionLength = 0;
		}
		return clanMinDescriptionLength;
	}

	/**
     * @return the clanMinLength
     */
    public int getClanMinLength() {
        return clanMinLength;
    }

    /**
     *
     * @return the max number of alliances a clan can have
     */
    public int getClanMaxAlliances() {
        return clanMaxAlliances;
    }

    /**
     * @return the clanMaxLength
     */
    public int getClanMaxLength() {
        return clanMaxLength;
    }

    /**
     * @return the pageClanNameColor
     */
    public String getPageClanNameColor() {
        return Helper.toColor(pageClanNameColor);
    }

    /**
     * @return the tagMinLength
     */
    public int getTagMinLength() {
        return tagMinLength;
    }

    /**
     * @return the tagMaxLength
     */
    public int getTagMaxLength() {
        return tagMaxLength;
    }

    /**
     * @return the tagDefaultColor
     */
    public String getTagDefaultColor() {
        return Helper.toColor(tagDefaultColor);
    }

    /**
     * @return the tagSeparator
     */
    public String getTagSeparator() {
        if (tagSeparator == null) {
            return "";
        }

        if (tagSeparator.equals(" .")) {
            return ".";
        }

        return tagSeparator;
    }

    /**
     * @return the tagSeparatorColor
     */
    public String getTagSeparatorColor() {
        return Helper.toColor(tagSeparatorColor);
    }
    
    public String getClanChatFormat() {
        return clanChatFormat;
    }
    
    public String getClanChatRank() {
        return clanChatRank;
    }
    
    public String getClanChatLeaderColor() {
        return Helper.toColor(clanChatLeaderColor);
    }
    
    public String getClanChatTrustedColor() {
 		return Helper.toColor(clanChatTrustedColor);
 	}
    
    public String getClanChatMemberColor() {
        return Helper.toColor(clanChatMemberColor);
    }

    /**
     * @return the clanChatAnnouncementColor
     */
    public String getClanChatAnnouncementColor() {
        return Helper.toColor(clanChatAnnouncementColor);
    }

    @Deprecated
    /**
     * @return the clanChatMessageColor
     */
    public String getClanChatMessageColor() {
        return clanChatMessageColor;
    }

    @Deprecated
    /**
     * @return the clanChatNameColor
     */
    public String getClanChatNameColor() {
        return clanChatNameColor;
    }
    
    @Deprecated
    /**
     * @return the clanChatTagBracketLeft
     */
    public String getClanChatTagBracketLeft() {
        return clanChatTagBracketLeft == null ? "[" : clanChatTagBracketLeft;
    }

    @Deprecated
    /**
     * @return the clanChatTagBracketRight
     */
    public String getClanChatTagBracketRight() {
        return clanChatTagBracketRight == null ? "]" : clanChatTagBracketRight;
    }

    @Deprecated
    /**
     * @return the clanChatBracketColor
     */
    public String getClanChatBracketColor() {
        return clanChatBracketColor == null ? Helper.toColor("e") : clanChatBracketColor;
    }

    @Deprecated
    /**
     * @return the clanChatPlayerBracketLeft
     */
    public String getClanChatPlayerBracketLeft() {
        return clanChatPlayerBracketLeft;
    }

    @Deprecated
    /**
     * @return the clanChatPlayerBracketRight
     */
    public String getClanChatPlayerBracketRight() {
        return clanChatPlayerBracketRight;
    }

    /**
     * @return the kwRival
     */
    public double getKwRival() {
        return kwRival;
    }

    /**
     * @return the kwNeutral
     */
    public double getKwNeutral() {
        return kwNeutral;
    }

    /**
     * @return the kwCivilian
     */
    public double getKwCivilian() {
        return kwCivilian;
    }

    /**
     * @return the useMysql
     */
    public boolean isUseMysql() {
        return useMysql;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the showUnverifiedOnList
     */
    public boolean isShowUnverifiedOnList() {
        return showUnverifiedOnList;
    }

    /**
     * @return the clanTrustByDefault
     */
    public boolean isClanTrustByDefault() {
        return clanTrustByDefault;
    }

    /**
     * @return the pageTrustedColor
     */
    public String getPageTrustedColor() {
        return Helper.toColor(pageTrustedColor);
    }

    /**
     * @return the pageUnTrustedColor
     */
    public String getPageUnTrustedColor() {
        return Helper.toColor(pageUnTrustedColor);
    }

    /**
     * @return the globalff
     */
    public boolean isGlobalff() {
        return globalff;
    }

    /**
     * @param globalff the globalff to set
     */
    public void setGlobalff(boolean globalff) {
        this.globalff = globalff;
    }

    /**
     * @return the clanChatEnable
     */
    public boolean getClanChatEnable() {
        return clanChatEnable;
    }

    /**
     * @return the tagBracketLeft
     */
    public String getTagBracketLeft() {
        return tagBracketLeft;
    }

    /**
     * @return the tagBracketRight
     */
    public String getTagBracketRight() {
        return tagBracketRight;
    }

    /**
     * @return the tagBracketColor
     */
    public String getTagBracketColor() {
        return Helper.toColor(tagBracketColor);
    }

    /**
     * @return the ePurchaseCreation
     */
    public boolean isePurchaseCreation() {
        return ePurchaseCreation;
    }

    /**
     * @return the ePurchaseVerification
     */
    public boolean isePurchaseVerification() {
        return ePurchaseVerification;
    }

    /**
     * @return the ePurchaseInvite
     */
    public boolean isePurchaseInvite() {
        return ePurchaseInvite;
    }

    /**
     * @return the eCreationPrice
     */
    public double getCreationPrice() {
        return eCreationPrice;
    }

    /**
     * @return the eVerificationPrice
     */
    public double getVerificationPrice() {
        return eVerificationPrice;
    }

    /**
     * @return the eInvitePrice
     */
    public double getInvitePrice() {
        return eInvitePrice;
    }

    public boolean isBbShowOnLogin() {
        return bbShowOnLogin;
    }

    public boolean getSafeCivilians() {
        return safeCivilians;
    }

    public boolean isConfirmationForPromote() {
        return confirmationForPromote;
    }

    public boolean isConfirmationForDemote() {
        return confirmationForDemote;
    }
    
    /**
     * Returns the min percentage of leaders online required to demote someone
     * 
     * @return the percentage
     */
    public double getPercentageOnlineToDemote() {
        if (percentageOnlineToDemote <= 0 || percentageOnlineToDemote > 100) {
            percentageOnlineToDemote = 100;
        }
        return percentageOnlineToDemote;
    }

    public boolean isDenySameIPKills() {
        return denySameIPKills;
    }

    public boolean isUseColorCodeFromPrefix() {
        return useColorCodeFromPrefix;
    }

    public String getCommandAlly() {
        return commandAlly;
    }

    public boolean isAllyChatEnable() {
        return allyChatEnable;
    }

    @Deprecated
    public String getAllyChatMessageColor() {
        return allyChatMessageColor;
    }
    
    public String getAllyChatFormat() {
        return allyChatFormat;
    }
    
    public String getAllyChatRank() {
        return allyChatRank;
    }
    
    public String getAllyChatLeaderColor() {
        return Helper.toColor(allyChatLeaderColor);
    }
    
    public String getAllyChatTrustedColor() {
		return Helper.toColor(allyChatTrustedColor);
	}
    
    public String getAllyChatMemberColor() {
        return Helper.toColor(allyChatMemberColor);
    }

    @Deprecated
    public String getAllyChatNameColor() {
        return allyChatNameColor;
    }

    @Deprecated
    public String getAllyChatTagBracketLeft() {
        return allyChatTagBracketLeft;
    }

    @Deprecated
    public String getAllyChatTagBracketRight() {
        return allyChatTagBracketRight;
    }

    @Deprecated
    public String getAllyChatBracketColor() {
        return allyChatBracketColor;
    }

    @Deprecated
    public String getAllyChatPlayerBracketLeft() {
        return allyChatPlayerBracketLeft;
    }

    @Deprecated
    public String getAllyChatPlayerBracketRight() {
        return allyChatPlayerBracketRight;
    }
    
    public String getCommandGlobal() {
        return commandGlobal;
    }

    @Deprecated
    public String getAllyChatTagColor() {
        return allyChatTagColor;
    }

    public boolean isClanFFOnByDefault() {
        return clanFFOnByDefault;
    }

    public boolean isCompatMode() {
        return compatMode;
    }

    public void setCompatMode(boolean compatMode) {
        this.compatMode = compatMode;
    }

    public boolean isHomebaseSetOnce() {
        return homebaseSetOnce;
    }

    public int getWaitSecs() {
        return waitSecs;
    }

    public void setWaitSecs(int waitSecs) {
        this.waitSecs = waitSecs;
    }

    public boolean isEnableAutoGroups() {
        return enableAutoGroups;
    }

    public boolean isPvpOnlywhileInWar() {
        return pvpOnlywhileInWar;
    }

    public boolean ismChatIntegration() {
        return mChatIntegration;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public boolean isKeepOnHome() {
        return keepOnHome;
    }

    public boolean isDropOnHome() {
        return dropOnHome;
    }

    public List<Integer> getItemsList() {
        return Collections.unmodifiableList(itemsList);
    }

    public boolean isTeleportOnSpawn() {
        return teleportOnSpawn;
    }

    public boolean isTagBasedClanChat() {
        return tagBasedClanChat;
    }

    public String getClanChatRankColor() {
        return Helper.toColor(clanChatRankColor);
    }

    /**
     * @return the ePurchaseHomeTeleport
     */
    public boolean isePurchaseHomeTeleport() {
        return ePurchaseHomeTeleport;
    }
    
    /**
     * @return the eUniqueTaxOnRegroup
     */
    public boolean iseUniqueTaxOnRegroup() {
        return eUniqueTaxOnRegroup;
    }

    /**
     * 
     * @return the eIssuerPaysRegroup
     */
    public boolean iseIssuerPaysRegroup() {
        return eIssuerPaysRegroup;
    }
    
    /**
     * @return the ePurchaseHomeRegroup
     */
    public boolean isePurchaseHomeRegroup() {
        return ePurchaseHomeRegroup;
    }

    /**
     * @return the HomeTeleportPrice
     */
    public double getHomeTeleportPrice() {
        return eHomeTeleportPrice;
    }
    
    /**
     * @return the HomeRegroupPrice
     */
    public double getHomeRegroupPrice() {
        return eHomeRegroupPrice;
    }

    /**
     * @return the ePurchaseHomeTeleportSet
     */
    public boolean isePurchaseHomeTeleportSet() {
        return ePurchaseHomeTeleportSet;
    }

    /**
     * @return the HomeTeleportPriceSet
     */
    public double getHomeTeleportPriceSet() {
        return eHomeTeleportPriceSet;
    }

    /**
     * @return the config
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /**
     * @return the moneyperkill
     */
    public boolean isMoneyPerKill() {
        return moneyperkill;
    }

    /**
     * @return the KDRMultipliesPerKill
     */
    public double getKDRMultipliesPerKill() {
        return KDRMultipliesPerKill;
    }

    /**
     * @return the teleportBlocks
     */
    public boolean isTeleportBlocks() {
        return teleportBlocks;
    }

    /**
     * @return the AutoGroupGroupName
     */
    public boolean isAutoGroupGroupName() {
        return AutoGroupGroupName;
    }

    /**
     * @return the tamableMobsSharing
     */
    public boolean isTamableMobsSharing() {
        return tamableMobsSharing;
    }

    @Deprecated
    public boolean isOnlineMode() {
        return true;
    }

    public boolean isDisableMessages() {
        return disableMessages;
    }

    /**
     * @return the allowReGroupCommand
     */
    public boolean getAllowReGroupCommand() {
        return allowReGroupCommand;
    }

    /**
     * @return the useThreads
     */
    public boolean getUseThreads() {
        return useThreads;
    }

    /**
     * @return the useBungeeCord
     */
    public boolean getUseBungeeCord() {
        return useBungeeCord;
    }

    public String getTagSeparatorLeaderColor() {
        return Helper.toColor(tagSeparatorLeaderColor);
    }

    public String getTagBracketLeaderColor() {
        return Helper.toColor(tagBracketLeaderColor);
    }

    public int getMaxAsksPerRequest() {
        return maxAsksPerRequest;
    }

    public void setMaxAsksPerRequest(int maxAsksPerRequest) {
        this.maxAsksPerRequest = maxAsksPerRequest;
    }

    public boolean isForceCommandPriority() {
        return forceCommandPriority;
    }

    public void setForceCommandPriority(boolean forceCommandPriority) {
        this.forceCommandPriority = forceCommandPriority;
    }

    public int getMaxMembers() {
        return this.maxMembers;
    }

	public boolean isSavePeriodically() {
		return savePeriodically;
	}

	/**
	 * Gets the interval to save the data
	 * 
	 * @return the interval in seconds
	 */
	public int getSaveInterval() {
		if (saveInterval < 1) {
			saveInterval = 5;
		}
		
		return saveInterval * 60;
	}
}