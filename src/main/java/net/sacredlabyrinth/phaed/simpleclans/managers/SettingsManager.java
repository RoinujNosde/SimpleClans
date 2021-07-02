package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author phaed
 */
public final class SettingsManager {
    enum ConfigField {
        /*
        ================
        > General Settings
        ================
         *
         * Enables the GUI
         */
        ENABLE_GUI("settings.enable-gui"),
        /*
         * Disables broadcasts from plugin ("Clan Created", "Clan Disbanded", etc.)
         */
        DISABLE_MESSAGES("settings.disable-messages"),
        /*
         * If true, tamable mobs will be shared with your clan members.
         * It also disables any clan damage to them
         */
        TAMABLE_MOBS_SHARING("settings.tameable-mobs-sharing"),
        /*
         * Fancy teleporting (placed glass block below)
         */
        TELEPORT_BLOCKS("settings.teleport-blocks"),
        /*
         * Players will be teleported to their clan's home when they respawn
         */
        TELEPORT_HOME_ON_SPAWN("settings.teleport-home-on-spawn"),
        /*
         * Drops defined items on teleporting to clan home
         */
        DROP_ITEMS_ON_CLAN_HOME("settings.drop-items-on-clan-home"),
        /*
         * Keeps defined items on teleporting to clan home
         */
        KEEP_ITEMS_ON_CLAN_HOME("settings.keep-items-on-clan-home"),
        /*
         * List of defined items (used with keep-item-on-clan-home and drop-items-on-clan-home)
         */
        ITEM_LIST("settings.item-list"),
        /*
         * Shows debug info on console
         */
        SHOW_DEBUG_INFO("settings.show-debug-info"),
        /*
         * Manages group of a clan player by auto.
         * (For example, a leader would be added to sc_leader group, trusted player to sc_trusted, etc.)
         */
        ENABLE_AUTO_GROUPS("settings.enable-auto-groups"),
        /*
         * Changes method of initiate tags
         */
        CHAT_COMPATIBILITY_MODE("settings.chat-compatibility-mode"),
        /*
         * The percent of possible rivals per clan
         */
        RIVAL_LIMIT_PERCENT("settings.rival-limit-percent"),
        /*
         * Uses the last color code in the end of prefix
         */
        COLOR_CODE_FROM_PREFIX_FOR_NAME("settings.use-colorcode-from-prefix-for-name"),
        /*
         * Shows clan tags in chat
         */
        DISPLAY_CHAT_TAGS("settings.display-chat-tags"),
        /*
         * The list of clans, which can't be rivaled
         */
        UNRIVABLE_CLANS("settings.unrivable-clans"),
        /*
         * Shows or not unverified clans on /clan list
         * (Doesn't affect on GUI)
         */
        SHOW_UNVERIFIED_ON_LIST("settings.show-unverified-on-list"),
        /*
         * Disables SimpleClans at defined worlds
         */
        BLACKLISTED_WORLDS("settings.blacklisted-worlds"),
        /*
         * List of banned players from using plugin
         */
        BANNED_PLAYERS("settings.banned-players"),
        /*
         * List of tags, which wouldn't be used on clan creation
         */
        DISALLOWED_TAGS("settings.disallowed-tags"),
        /*
         * Default language
         */
        LANGUAGE("settings.language"),
        /*
         * Allows players to change their language
         */
        LANGUAGE_SELECTOR("settings.user-language-selector"),
        /*
         * The list of tag colors, which wouldn't be used on clan creation
         */
        DISALLOWED_TAG_COLORS("settings.disallowed-tag-colors"),
        /*
         * The name of your server
         */
        SERVER_NAME("settings.server-name"),
        /*
         * Should new clans requires being verified?
         */
        NEW_CLAN_VERIFICATION_REQUIRED("settings.new-clan-verification-required"),
        /*
         * Allows players to use regroup command
         */
        ALLOW_REGROUP("settings.allow-regroup-command"),
        /*
         * Allows players to reset their KDR
         */
        ALLOW_RESET_KDR("settings.allow-reset-kdr"),
        /*
         * The time in minutes, when player will can join after resigning to the same clan
         */
        REJOIN_COOLDOWN("settings.rejoin-cooldown"),
        /*
         * Should rejoin cooldown be enabled?
         */
        ENABLE_REJOIN_COOLDOWN("settings.rejoin-cooldown-enabled"),
        ACCEPT_OTHER_ALPHABETS_LETTERS("settings.accept-other-alphabets-letters-on-tag"),
        /**
         * DENSE: if players have the same KDR, they will have the same rank position. Ex.: 12234
         * ORDINAL: Every player will have a different rank position. Ex.: 12345
         */
        RANKING_TYPE("settings.ranking-type"),
        LIST_DEFAULT_ORDER_BY("settings.list-default-order-by"),
        LORE_LENGTH("settings.lore-length"),
        /*
        ================
        > Tag Settings
        ================
         *
         * Color used when the clan leader didn't set any colors
         */
        TAG_DEFAULT_COLOR("tag.default-color"),
        /*
         * Maximum length for tags
         */
        TAG_MAX_LENGTH("tag.max-length"),
        /*
         * The bracket color
         */
        TAG_BRACKET_COLOR("tag.bracket.color"),
        /*
         * This color is used when the player is a leader
         */
        TAG_BRACKET_LEADER_COLOR("tag.bracket.leader-color"),
        /*
         * The left character
         */
        TAG_BRACKET_LEFT("tag.bracket.left"),
        /*
         * The right character
         */
        TAG_BRACKET_RIGHT("tag.bracket.right"),
        /*
         * Minimum length for tags
         */
        TAG_MIN_LENGTH("tag.min-length"),
        /*
         * The separator color
         */
        TAG_SEPARATOR_COLOR("tag.separator.color"),
        /*
         * This color is used when the player is a leader
         */
        TAG_SEPARATOR_LEADER_COLOR("tag.separator.leader-color"),
        /*
         * the separator character
         */
        TAG_SEPARATOR_char("tag.separator.char"),
        /*
        ================
        > War and Protection Settings
        ================
         *
         * Enables the war feature on the server
         */
        ENABLE_WAR("war-and-protection.war-enabled"),
        /*
         * Enables the land sharing feature on the server
         */
        LAND_SHARING("war-and-protection.land-sharing"),
        /*
         * The list of land claim providers
         */
        LAND_PROTECTION_PROVIDERS("war-and-protection.protection-providers"),
        /*
         * Used to set the priority of the overridden events
         */
        WAR_LISTENERS_PRIORITY("war-and-protection.listeners.priority"),
        /*
         * The list of items that will be ignored by SimpleClans
         */
        WAR_LISTENERS_IGNORED_LIST_PLACE("war-and-protection.listeners.ignored-list.PLACE"),
        /*
         * Allows a clan player to set the clan base only on claimed land
         */
        SET_BASE_ONLY_IN_LAND("war-and-protection.set-base-only-in-land"),
        /*
         * The time of war expiration independently
         * (in minutes)
         */
        WAR_NORMAL_EXPIRATION_TIME("war-and-protection.war-normal-expiration-time"),
        /*
         * The time of war expiration if all members
         * from one clan disconnects (in minutes)
         */
        WAR_DISCONNECT_EXPIRATION_TIME("war-and-protection.war-disconnect-expiration-time"),
        /*
         * Allows a clan player to change the action of all the lands
         * instead of the one on which it stands
         */
        LAND_EDIT_ALL_LANDS("war-and-protection.edit-all-lands"),
        /*
         * Allows only clan leaders to create lands
         */
        LAND_CREATION_ONLY_LEADERS("war-and-protection.land-creation.only-leaders"),
        /*
         * Allows to have only one land per clan
         */
        LAND_CREATION_ONLY_ONE_PER_CLAN("war-and-protection.land-creation.only-one-per-clan"),
        /*
         * The list of permitted actions regarding clan lands during the war
         */
        WAR_ACTIONS_CONTAINER("war-and-protection.war-actions.CONTAINER"),
        WAR_ACTIONS_INTERACT("war-and-protection.war-actions.INTERACT"),
        WAR_ACTIONS_BREAK("war-and-protection.war-actions.BREAK"),
        WAR_ACTIONS_PLACE("war-and-protection.war-actions.PLACE"),
        WAR_ACTIONS_DAMAGE("war-and-protection.war-actions.DAMAGE"),
        WAR_ACTIONS_INTERACT_ENTITY("war-and-protection.war-actions.INTERACT_ENTITY"),
        /*
         * If true, a war will require the approval from the clan leaders
         */
        WAR_START_REQUEST_ENABLED("war-and-protection.war-start.request-enabled"),
        /*
         * If the difference between the online members of two clans
         * is greater than the one set, the war will not start
         */
        WAR_START_MEMBERS_ONLINE_MAX_DIFFERENCE("war-and-protection.war-start.members-online-max-difference"),
        /*
        ================
        > KDR Grinding Prevention Settings
        ================
         *
         * By enabling this, you can set a limit on kills per victim
         */
        KDR_ENABLE_MAX_KILLS("kdr-grinding-prevention.enable-max-kills"),
        /*
         * The limit of kills per victim.
         */
        KDR_MAX_KILLS_PER_VICTIM("kdr-grinding-prevention.max-kills-per-victim"),
        /*
         * Enables a delay between kills
         */
        KDR_ENABLE_KILL_DELAY("kdr-grinding-prevention.enable-kill-delay"),
        /*
         * The delay in minutes between kills
         */
        KDR_DELAY_BETWEEN_KILLS("kdr-grinding-prevention.delay-between-kills"),
        /*
        ================
        > Commands Settings
        ================
         *
         * In this section you can edit the base commands of the plugin
         */
        COMMANDS_MORE("commands.more"),
        COMMANDS_ALLY("commands.ally"),
        COMMANDS_CLAN("commands.clan"),
        COMMANDS_ACCEPT("commands.accept"),
        COMMANDS_DENY("commands.deny"),
        COMMANDS_GLOBAL("commands.global"),
        COMMANDS_CLAN_CHAT("commands.clan_chat"),
        /*
         * Enable if other plugins are interfering with the commands
         */
        COMMANDS_FORCE_PRIORITY("commands.force-priority"),
        /*
        ================
        > Economy Settings
        ================
         *
         * The price to create a clan
         */
        ECONOMY_CREATION_PRICE("economy.creation-price"),
        /*
         * Players must pay to create a clan
         */
        ECONOMY_PURCHASE_CLAN_CREATE("economy.purchase-clan-create"),
        /*
         * The price to verify a clan
         */
        ECONOMY_VERIFICATION_PRICE("economy.verification-price"),
        /*
         * Players must pay to verify their clans
         */
        ECONOMY_PURCHASE_CLAN_VERIFY("economy.purchase-clan-verify"),
        /*
         * The price to invite a player to your clan
         */
        ECONOMY_INVITE_PRICE("economy.invite-price"),
        /*
         * Players must pay to invite
         */
        ECONOMY_PURCHASE_CLAN_INVITE("economy.purchase-clan-invite"),
        /*
         * The price to teleport to the clan's home
         */
        ECONOMY_HOME_TELEPORT_PRICE("economy.home-teleport-price"),
        /*
         * Players must pay to teleport
         */
        ECONOMY_PURCHASE_HOME_TELEPORT("economy.purchase-home-teleport"),
        /*
         * The price to set the clan's home
         */
        ECONOMY_HOME_TELEPORT_SET_PRICE("economy.home-teleport-set-price"),
        /*
         * Players must pay to set the clan's home
         */
        ECONOMY_PURCHASE_HOME_TELEPORT_SET("economy.purchase-home-teleport-set"),
        /*
         * The price for regrouping the clan members
         */
        ECONOMY_REGROUP_PRICE("economy.home-regroup-price"),
        /*
         * Players (or the clan) must pay to regroup
         */
        ECONOMY_PURCHASE_HOME_REGROUP("economy.purchase-home-regroup"),
        /*
         * if false, the price is multiplied by the amount of online members of the clan
         */
        ECONOMY_UNIQUE_TAX_ON_REGROUP("economy.unique-tax-on-regroup"),
        /*
         * if enabled, the player issuing the command pays for the regroup,
         * otherwise the clan pays it
         */
        ECONOMY_ISSUER_PAYS_REGROUP("economy.issuer-pays-regroup"),
        /*
         * Enables a prize in money for the killer clan
         */
        ECONOMY_MONEY_PER_KILL("economy.money-per-kill"),
        /*
         * This is multiplied by the attacker's KDR,
         * the result is the money prize for the clan
         */
        ECONOMY_MONEY_PER_KILL_KDR_MULTIPLAYER("economy.money-per-kill-kdr-multipier"),
        /*
         * The price to reset one's KDR
         */
        ECONOMY_RESET_KDR_PRICE("economy.reset-kdr-price"),
        ECONOMY_PURCHASE_RESET_KDR("economy.purchase-reset-kdr"),
        /*
         * Players must pay to set the member fee
         */
        ECONOMY_PURCHASE_MEMBER_FEE_SET("economy.purchase-member-fee-set"),
        /*
         * The price to set the member fee
         */
        ECONOMY_MEMBER_FEE_SET_PRICE("economy.member-fee-set-price"),
        /*
         * If clans can charge a daily fee from their members
         */
        ECONOMY_MEMBER_FEE_ENABLED("economy.member-fee-enabled"),
        ECONOMY_MEMBER_FEE_LAST_MINUTE_CHANGE_INTERVAL("economy.member-fee-last-minute-change-interval"),
        /*
         * The maximum amount clans can set for their member fee
         */
        ECONOMY_MAX_MEMBER_FEE("economy.max-member-fee"),
        /*
         * the daily price for maintaining a clan (if not paid, the clan is disbanded)
         */
        ECONOMY_UPKEEP("economy.upkeep"),
        /*
         * If clans must pay the upkeep
         */
        ECONOMY_UPKEEP_ENABLED("economy.upkeep-enabled"),
        /*
         * If the upkeep price should be multiplied by the amount of members
         */
        ECONOMY_MULTIPLY_UPKEEP_BY_CLAN_SIZE("economy.multiply-upkeep-by-clan-size"),
        /*
         * If the upkeep should be charged only for clans that choose to enable the member fee
         */
        ECONOMY_CHARGE_UPKEEP_ONLY_IF_MEMBER_FEE_ENABLED("economy.charge-upkeep-only-if-member-fee-enabled"),
        ECONOMY_BANK_LOG_ENABLED("economy.bank-log.enable"),
        /*
        ================
        > Kill Weights Settings
        ================
         *
         * Here you can set the weight of every kill type.
         * The weight can be negative too.
         * It's used to calculate the KDR like so: (Kill Count * Kill Wight) / Death Count = KDR
         */
        KILL_WEIGHTS_RIVAL("kill-weights.rival"),
        KILL_WEIGHTS_CIVILIAN("kill-weights.civilian"),
        KILL_WEIGHTS_NEUTRAL("kill-weights.neutral"),
        KILL_WEIGHTS_ALLY("kill-weights.ally"),
        KILL_WEIGHTS_DENY_SAME_IP_KILLS("kill-weights.deny-same-ip-kills"),
        /*
        ================
        > Clan Settings
        ================
         *
         * The amount of seconds players must wait before teleporting to their clan' home
         */
        CLAN_HOMEBASE_TELEPORT_WAIT_SECS("clan.homebase-teleport-wait-secs"),
        /*
         * If the clan's home can be set only once
         */
        CLAN_HOMEBASE_CAN_BE_SET_ONLY_ONCE("clan.homebase-can-be-set-only-once"),
        /*
         * The minimum amount of members a clan needs to add rivals
         */
        CLAN_MIN_SIZE_TO_SET_RIVAL("clan.min-size-to-set-rival"),
        /*
         * The minimum amount of members a clan needs to add allies
         */
        CLAN_MIN_SIZE_TO_SET_ALLY("clan.min-size-to-set-ally"),
        /*
         * The max length of the clan's name
         */
        CLAN_MAX_LENGTH("clan.max-length"),
        /*
         * The minimum length of the clan's name
         */
        CLAN_MIN_LENGTH("clan.min-length"),
        /*
         * The maximum length of the clan's description
         */
        CLAN_MAX_DESCRIPTION_LENGTH("clan.max-description-length"),
        /*
         * The minimum length of the clan's description
         */
        CLAN_MIN_DESCRIPTION_LENGTH("clan.min-description-length"),
        /*
         * The maximum amount of members a clan can have
         */
        CLAN_MAX_MEMBERS("clan.max-members"),
        /*
         * If other leaders must confirm the promotion of members
         */
        CLAN_CONFIRMATION_FOR_PROMOTE("clan.confirmation-for-promote"),
        /*
         * If members are set as trusted by default
         */
        CLAN_TRUST_MEMBERS_BY_DEFAULT("clan.trust-members-by-default"),
        /*
         * If other leaders (except the one being demoted, of course)
         * must confirm the demotion percentage-online-to-demote
         */
        CLAN_CONFIRMATION_FOR_DEMOTE("clan.confirmation-for-demote"),
        /*
         * The percentage of online leaders to demote
         */
        CLAN_PERCENTAGE_ONLINE_TO_DEMOTE("clan.percentage-online-to-demote"),
        /*
         * If the clan's friendly-fire is enabled by default
         */
        CLAN_FF_ON_BY_DEFAULT("clan.ff-on-by-default"),
        /*
         * The clan must have this amount of members to get verified (moderators can bypass this)
         */
        CLAN_MIN_TO_VERIFY("clan.min-to-verify"),
        /*
        ================
        > Tasks Settings
        ================
         *
         * This section allows you to set the time of collection for the two types of fee.
         * The fees are described on the economy section.
         * The collect-upkeep-warning is sent when the clan doesn't have enough money to pay for its upkeep.
         * The time is in the 24-hour clock.
         */
        TASKS_COLLECT_UPKEEP_HOUR("tasks.collect-upkeep.hour"),
        TASKS_COLLECT_UPKEEP_MINUTE("tasks.collect-upkeep.minute"),
        TASKS_COLLECT_UPKEEP_WARNING_HOUR("tasks.collect-upkeep-warning.hour"),
        TASKS_COLLECT_UPKEEP_WARNING_MINUTE("tasks.collect-upkeep-warning.minute"),
        TASKS_COLLECT_FEE_HOUR("tasks.collect-fee.hour"),
        TASKS_COLLECT_FEE_MINUTE("tasks.collect-fee.minute"),
        /*
        ================
        > Page Settings
        ================
         */
        PAGE_LEADER_COLOR("page.leader-color"),
        PAGE_UNTRUSTED_COLOR("page.untrusted-color"),
        PAGE_TRUSTED_COLOR("page.trusted-color"),
        PAGE_CLAN_NAME_COLOR("page.clan-name-color"),
        PAGE_SUBTITLE_COLOR("page.subtitle-color"),
        PAGE_HEADINGS_COLOR("page.headings-color"),
        PAGE_SEPARATOR("page.separator"),
        PAGE_SIZE("page.size"),
        /*
        ================
        > Clan Chat Settings
        ================
         *
         * Enables the clan chat
         */
        CLANCHAT_ENABLE("clanchat.enable"),
        /*
         * If true, the command to talk on the clan chat is the clan tag
         */
        CLANCHAT_TAG_BASED_CLAN_CHAT("clanchat.tag-based-clan-chat"),
        /*
         * Color used for announcements
         */
        CLANCHAT_ANNOUNCEMENT_COLOR("clanchat.announcement-color"),
        /*
         * The chat format
         */
        CLANCHAT_FORMAT("clanchat.format"),
        /*
         * The member's rank format (used on the format)
         */
        CLANCHAT_RANK("clanchat.rank"),
        /*
         * The color for leaders (%nick-color%)
         */
        CLANCHAT_LEADER_COLOR("clanchat.leader-color"),
        /*
         * The color for trusted players (%nick-color%)
         */
        CLANCHAT_TRUSTED_COLOR("clanchat.trusted-color"),
        /*
         * The color for non-leaders and non-trusted players (%nick-color%)
         */
        CLAN_MEMBER_COLOR("clanchat.member-color"),
        /*
        ================
        > BB Settings
        ================
         */
        BB_COLOR("clanchat.member-color"),
        BB_ACCENT_COLOR("clanchat.member-color"),
        BB_SHOW_ON_LOGIN("clanchat.member-color"),
        BB_SIZE("clanchat.member-color"),
        BB_LOGIN_SIZE("clanchat.member-color"),
        /*
        ================
        > Ally Chat Settings
        ================
         */
        ALLYCHAT_ENABLE("allychat.enable"),
        ALLYCHAT_FORMAT("allychat.format"),
        ALLYCHAT_LEADER_COLOR("allychat.leader-color"),
        ALLYCHAT_TRUSTED_COLOR("allychat.trusted-color"),
        ALLYCHAT_MEMBER_COLOR("allychat.member-color"),
        /*
        ================
        > Discord Chat Settings
        ================
         */
        DISCORDCHAT_ENABLE("discordchat.enable"),
        DISCORDCHAT_FORMAT("discordchat.format"),
        DISCORDCHAT_RANK("discordchat.rank"),
        DISCORDCHAT_VOICE_CHANNEL_FORMAT("discordchat.voice.channel-format"),
        DISCORDCHAT_VOICE_CATEGORY_FORMAT("discordchat.voice.category-format"),
        DISCORDCHAT_TEXT_CHANNEL_FORMAT("discordchat.text.channel-format"),
        DISCORDCHAT_TEXT_CATEGORY_FORMAT("discordchat.text.category-foramt"),
        /*
        ================
        > Purge Settings
        ================
         */
        PURGE_INACTIVE_PLAYER_DATA_DAYS("purge.inactive-player-data-days"),
        PURGE_INACTIVE_CLAN_DAYS("purge.inactive-clan-days"),
        PURGE_UNVERIFIED_CLAN_DAYS("purge.unverified-clan-days"),
        /*
        ================
        > MySQL Settings
        ================
         */
        MYSQL_USERNAME("mysql.username"),
        MYSQL_HOST("mysql.host"),
        MYSQL_PORT("mysql.port"),
        MYSQL_ENABLE("mysql.enable"),
        MYSQL_PASSWORD("mysql.password"),
        MYSQL_DATABASE("mysql.database"),
        /*
        ================
        > Permissions Settings
        ================
         */
        PERMISSIONS_AUTO_GROUP_GROUPNAME("permissions.auto-group-groupname"),
        /*
        ================
        > Performance Settings
        ================
         */
        PERFORMANCE_SAVE_PERIODICALLY("performance.save-periodically"),
        PERFORMANCE_SAVE_INTERVAL("performance.save-interval"),
        PERFORMANCE_USE_THREADS("performance.use-threads"),
        PERFORMANCE_USE_BUNGEECORD("performance.uuse-bungeecord");

        private final String path;

        ConfigField(String path) {
            this.path = path;
        }
    }

    public <T> void set(ConfigField field, T value) {
        config.set(field.path, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigField field) {
        return (T) config.get(field.path);
    }

    private boolean enableGUI;
    private boolean disableMessages;
    private String clanChatRankColor;
    private boolean tagBasedClanChat;
    private boolean teleportOnSpawn;
    private boolean dropOnHome;
    private boolean keepOnHome;
    private boolean debugging;
    private final SimpleClans plugin;
    private boolean pvpOnlyWhileInWar;
    private boolean useColorCodeFromPrefix;
    private boolean confirmationForPromote;
    private boolean confirmationForDemote;
    private double percentageOnlineToDemote;
    private boolean globalff;
    private boolean allowResetKdr;
    private boolean showUnverifiedOnList;
    private boolean requireVerification;
    private boolean rejoinCooldownEnabled;
    private boolean acceptOtherAlphabetsLettersOnTag;
    private int minToVerify;
    private int rejoinCooldown;
    private String listDefaultOrderBy;
    private final List<Material> itemsList = new ArrayList<>();
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
    private int eMemberFeeLastMinuteChangeInterval;
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
    private FileConfiguration config;
    private boolean compatMode;
    private boolean homebaseSetOnce;
    private int waitSecs;
    private boolean enableAutoGroups;
    private boolean denySameIPKills;
    private boolean moneyperkill;
    private double KDRMultipliesPerKill;
    private boolean teleportBlocks;
    private boolean autoGroupGroupName;
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
    private boolean cachePlayerHeads;
    private int saveInterval;
    private String rankingType;
    private int loreLength;
    private boolean warEnabled;
    private boolean landSharing;
    private List<String> protectionProviders;
    private boolean onlyLeadersCanCreateLands;
    private boolean onlyOneLandPerClan;
    private boolean setBaseOnlyInLand;

    /**
     *
     */
    public SettingsManager() {
        plugin = SimpleClans.getInstance();
        config = plugin.getConfig();
        main = new File(plugin.getDataFolder() + File.separator + "config.yml");
        load();
        warnAboutAutoGroupGroupName();
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
        for (String material : getConfig().getStringList("settings.item-list")) {
            Optional<XMaterial> x = XMaterial.matchXMaterial(material);
            if (x.isPresent()) {
                itemsList.add(x.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Error with Material: " + material);
            }
        }
        debugging = getConfig().getBoolean("settings.show-debug-info");
        pvpOnlyWhileInWar = getConfig().getBoolean("settings.pvp-only-while-at-war");
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
        acceptOtherAlphabetsLettersOnTag = getConfig().getBoolean("settings.accept-other-alphabets-letters-on-tag");
        minToVerify = getConfig().getInt("clan.min-to-verify", 1);
        rankingType = getConfig().getString("settings.ranking-type", "DENSE");
        listDefaultOrderBy = getConfig().getString("settings.list-default-order-by", "kdr");
        serverName = getConfig().getString("settings.server-name", "SimpleClans");
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
        eMemberFeeLastMinuteChangeInterval = getConfig().getInt("member-fee-last-minute-change-interval", 8);
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
        commandClan = getConfig().getString("commands.clan", "clan");
        commandAlly = getConfig().getString("commands.ally", "ally");
        commandGlobal = getConfig().getString("commands.global", "global");
        commandMore = getConfig().getString("commands.more", "more");
        commandDeny = getConfig().getString("commands.deny", "deny");
        commandAccept = getConfig().getString("commands.accept", "accept");
        commandClanChat = getConfig().getString("commands.clan_chat", ".");
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
        languagePerPlayer = getConfig().getBoolean("settings.user-language-selector", true);
        autoGroupGroupName = getConfig().getBoolean("permissions.auto-group-groupname");
        tamableMobsSharing = getConfig().getBoolean("settings.tameable-mobs-sharing");
        allowReGroupCommand = getConfig().getBoolean("settings.allow-regroup-command");
        loreLength = getConfig().getInt("settings.lore-length", 38);
        savePeriodically = getConfig().getBoolean("performance.save-periodically");
        saveInterval = getConfig().getInt("performance.save-interval");
        useThreads = getConfig().getBoolean("performance.use-threads");
        useBungeeCord = getConfig().getBoolean("performance.use-bungeecord");
        cachePlayerHeads = getConfig().getBoolean("performance.cache-player-heads");
        maxMembers = getConfig().getInt("clan.max-members");
        maxKillsPerVictim = getConfig().getInt("kdr-grinding-prevention.max-kills-per-victim");
        maxKillsPerVictimEnabled = getConfig().getBoolean("kdr-grinding-prevention.enable-max-kills");
        delayBetweenKills = getConfig().getInt("kdr-grinding-prevention.delay-between-kills");
        delayBetweenKillsEnabled = getConfig().getBoolean("kdr-grinding-prevention.enable-kill-delay");
        warEnabled = getConfig().getBoolean("war-and-protection.war-enabled", false);
        landSharing = getConfig().getBoolean("war-and-protection.land-sharing", true);
        protectionProviders = getConfig().getStringList("war-and-protection.protection-providers");
        onlyLeadersCanCreateLands = getConfig().getBoolean("war-and-protection.land-creation.only-leaders", false);
        onlyOneLandPerClan = getConfig().getBoolean("war-and-protection.land-creation.only-one-per-clan", false);
        setBaseOnlyInLand = getConfig().getBoolean("war-and-protection.set-base-only-in-land", false);

        // migrate from old way of adding ports
        if (database.contains(":")) {
            String[] strings = database.split(":");
            database = strings[0];
            port = Integer.parseInt(strings[1]);
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

    public boolean isDiscordChatEnabled() {
        return getConfig().getBoolean("discordchat.enable", false);
    }
    public String getDiscordChatFormat() {
        return getConfig().getString("discordchat.format", "<%player%> %rank%: %message%");
    }

    public String getDiscordRankFormat() {
        return getConfig().getString("discordchat.rank", "[%rank%]");
    }

    public String getDiscordTextCategoryFormat() {
        return getConfig().getString("discordchat.text.category-format", "SC - TextChannels");
    }

    public String getDiscordTextChannelFormat() {
        return getConfig().getString("discordchat.text.channel-format", "%clan%");
    }

    public String getDiscordVoiceCategoryFormat() {
        return getConfig().getString("discordchat.voice.category-format", "SC - VoiceChannels");
    }

    public String getDiscordVoiceChannelFormat() {
        return getConfig().getString("discordchat.voice.channel-format", "%clan%");
    }

    private void warnAboutAutoGroupGroupName() {
        Plugin luckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null && autoGroupGroupName) {
            plugin.getLogger().warning("LuckPerms was found and the setting auto-group-groupname is enabled.");
            plugin.getLogger().warning("Be careful with that as players will be automatically added in the group" +
                    " that matches their clan tag.");
        }
    }

    public boolean isBankLogEnabled() {
        return getConfig().getBoolean("economy.bank-log.enable", false);
    }

    public boolean isWarRequestEnabled() {
        return getConfig().getBoolean("war-and-protection.war-start.request-enabled", true);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isWarEnabled() {
        return warEnabled;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isLandSharing() {
        return landSharing;
    }

    public boolean isEditAllLands() {
        return getConfig().getBoolean("war-and-protection.edit-all-lands", false);
    }

    public int getWarNormalExpirationTime() {
        return getConfig().getInt("war-and-protection.war-normal-expiration-time", 60) * 60 * 20;
    }

    public int getWarDisconnectExpirationTime() {
        return getConfig().getInt("war-and-protection.war-disconnect-expiration-time", 10) * 60 * 20;
    }

    public List<String> getProtectionProviders() {
        return protectionProviders;
    }

    public EventPriority getWarListenerPriority() {
        return EventPriority.valueOf(getConfig().getString("war-and-protection.listeners.priority", "HIGHEST"));
    }

    public List<String> getIgnoredList(ProtectionManager.Action action) {
        return getConfig().getStringList("war-and-protection.listeners.ignored-list." + action.name());
    }

    public boolean isActionAllowedInWar(@NotNull ProtectionManager.Action action) {
        return getConfig().getBoolean("war-and-protection.war-actions." + action.name(), false);
    }

    public boolean isOnlyLeadersCanCreateLands() {
        return onlyLeadersCanCreateLands;
    }

    public boolean isOnlyOneLandPerClan() {
        return onlyOneLandPerClan;
    }

    public boolean isSetBaseOnlyInLand() {
        return setBaseOnlyInLand;
    }

    public int getLoreLength() {
        return loreLength;
    }

    public boolean isCachePlayerHeads() {
        return cachePlayerHeads;
    }

    public boolean isEnableGUI() {
        return enableGUI;
    }

    public void setEnableGUI(boolean enableGUI) {
        this.enableGUI = enableGUI;
        getConfig().set("settings.enable-gui", enableGUI);
        save();
    }

    /**
     * @return if the tag can contain letters from other alphabets
     */
    public boolean isAcceptOtherAlphabetsLettersOnTag() {
        return acceptOtherAlphabetsLettersOnTag;
    }

    public Locale getLanguage() {
        String[] split = language.split("_");

        if (split.length == 2) {
            return new Locale(split[0], split[1]);
        }

        return new Locale(language);
    }

    @NotNull
    public RankingType getRankingType() {
        try {
            return RankingType.valueOf(rankingType);
        } catch (IllegalArgumentException ex) {
            return RankingType.DENSE;
        }
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

    public int getMinToVerify() {
        return minToVerify;
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
    public boolean isItemInList(Material typeId) {
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

    @Contract("null -> false")
    public boolean isBlacklistedWorld(@Nullable World world) {
        if (world != null) {
            return isBlacklistedWorld(world.getName());
        }
        return false;
    }

    /**
     * Check whether a worlds is blacklisted
     *
     * @param world the world
     * @return whether the world is blacklisted
     */
    public boolean isBlacklistedWorld(String world) {
        for (String w : blacklistedWorlds) {
            if (w.equalsIgnoreCase(world)) {
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
     *
     * @return
     */
    public double getMaxMemberFee() {
        return eMaxMemberFee;
    }

    /**
     * Checks if the member fee is enabled
     *
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

    public int geteMemberFeeLastMinuteChangeInterval() {
        return eMemberFeeLastMinuteChangeInterval;
    }

    /**
     * Do leaders need to pay for setting the member fee?
     *
     * @return true if so
     */
    public boolean isePurchaseMemberFeeSet() {
        return ePurchaseMemberFeeSet;
    }

    /**
     * Gets the price to reset the KDR
     *
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

    @SuppressWarnings("deprecation")
    public boolean isBanned(String playerName) {
        return isBanned(Bukkit.getOfflinePlayer(playerName).getUniqueId());
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

    @SuppressWarnings("deprecation")
    public void addBanned(String playerName) {
        addBanned(Bukkit.getOfflinePlayer(playerName).getUniqueId());
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

    @SuppressWarnings("deprecation")
    public void removeBanned(String playerName) {
        removeBanned(Bukkit.getOfflinePlayer(playerName).getUniqueId());
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

    @NotNull
    public String getListDefaultOrderBy() {
        return listDefaultOrderBy;
    }

    @Deprecated
    public String getListDefault() {
        return getListDefaultOrderBy();
    }

    @Deprecated
    public String getListSize() {
        return "size";
    }

    @Deprecated
    public String getListKdr() {
        return "kdr";
    }

    @Deprecated
    public String getListName() {
        return "name";
    }

    @Deprecated
    public String getListFounded() {
        return "founded";
    }

    @Deprecated
    public String getListActive() {
        return "active";
    }

    @Deprecated
    public String getListAsc() {
        return "asc";
    }

    @Deprecated
    public String getListDesc() {
        return "desc";
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
        return ChatUtils.parseColors(serverName);
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
     * @return the requestFrequencySecs
     */
    public int getRequestFrequencySecs() {
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

    public double getKwAlly() {
        return getConfig().getDouble("kill-weights.ally");
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

    public boolean isPvpOnlyWhileInWar() {
        return pvpOnlyWhileInWar;
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

    public List<Material> getItemsList() {
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
        return Math.abs(eHomeRegroupPrice);
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
        return autoGroupGroupName;
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

    /**
     * Checks if server announcements are disabled
     *
     * @return if they are disabled
     */
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

    public int getMembersOnlineMaxDifference() {
        return getConfig().getInt("war-and-protection.war-start.members-online-max-difference", 5);
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
