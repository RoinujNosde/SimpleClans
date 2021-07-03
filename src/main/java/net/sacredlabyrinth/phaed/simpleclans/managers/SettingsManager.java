package net.sacredlabyrinth.phaed.simpleclans.managers;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

/**
 * @author phaed
 */
public final class SettingsManager {

    private final SimpleClans plugin;

    private final FileConfiguration config;
    private final File configFile;

    public SettingsManager(SimpleClans plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        config.options().copyDefaults(true);
        configFile = new File(plugin.getDataFolder() + File.separator + "config.yml");
        loadAndSave();
        warnAboutAutoGroupGroupName();
    }

    public <T> void set(ConfigField field, T value) {
        config.set(field.path, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(ConfigField field) {
        return (T) config.get(field.path);
    }

    public int getInt(ConfigField field) {
        return config.getInt(field.path);
    }

    public double getDouble(ConfigField field) {
        return config.getDouble(field.path);
    }

    public List<String> getStringList(ConfigField field) {
        return config.getStringList(field.path);
    }

    public String getString(ConfigField field) {
        return config.getString(field.path);
    }

    public String getColor(ConfigField field) {
        return Helper.toColor(config.getString(field.path));
    }

    public boolean is(ConfigField field) {
        return config.getBoolean(field.path);
    }

    /**
     * Load the configuration
     */
    public void loadAndSave() {
        if (configFile.exists()) {
            try {
                config.load(configFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        save();
    }

    public void save() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void warnAboutAutoGroupGroupName() {
        Plugin luckPerms = Bukkit.getServer().getPluginManager().getPlugin("LuckPerms");
        if (luckPerms != null && is(PERMISSIONS_AUTO_GROUP_GROUPNAME)) {
            plugin.getLogger().warning("LuckPerms was found and the setting auto-group-groupname is enabled.");
            plugin.getLogger().warning("Be careful with that as players will be automatically added in the group" +
                    " that matches their clan tag.");
        }
    }

    public Locale getLanguage() {
        String language = get(LANGUAGE);
        String[] split = language.split("_");

        if (split.length == 2) {
            return new Locale(split[0], split[1]);
        }

        return new Locale(language);
    }

    /**
     * Check whether a word is disallowed
     *
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word) {
        for (String disallowedTag : getStringList(DISALLOWED_TAGS)) {
            return disallowedTag.equals(word);
        }

        return word.equalsIgnoreCase(getString(COMMANDS_CLAN)) || word.equalsIgnoreCase(getString(COMMANDS_MORE)) ||
                word.equalsIgnoreCase(getString(COMMANDS_DENY)) || word.equalsIgnoreCase(getString(COMMANDS_ACCEPT));
    }

    /**
     * Check whether a string has a disallowed color
     *
     * @param str the string
     * @return whether the string contains the color code
     */
    public boolean hasDisallowedColor(String str) {
        for (String disallowedTag : getStringList(DISALLOWED_TAG_COLORS)) {
            if (str.contains("&" + disallowedTag)) {
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

        for (Object c : getStringList(DISALLOWED_TAG_COLORS)) {
            out += c + ", ";
        }

        return Helper.stripTrailing(out, ", ");
    }

    /**
     * Check whether a clan is unrivable
     *
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag) {
        return getStringList(UNRIVABLE_CLANS).contains(tag);
    }

    /**
     * Add a player to the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void addBanned(UUID playerUniqueId) {
        List<String> bannedPlayers = getStringList(BANNED_PLAYERS);
        if (bannedPlayers.contains(playerUniqueId.toString())) {
            return;
        }

        bannedPlayers.add(playerUniqueId.toString());
        set(BANNED_PLAYERS, bannedPlayers);
    }

    /**
     * Check whether a player is banned
     *
     * @param playerUniqueId the player's name
     * @return whether player is banned
     */
    public boolean isBanned(UUID playerUniqueId) {
        return getStringList(BANNED_PLAYERS).contains(playerUniqueId.toString());
    }

    /**
     * Remove a player from the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void removeBanned(UUID playerUniqueId) {
        List<String> bannedPlayers = getStringList(BANNED_PLAYERS);
        bannedPlayers.remove(playerUniqueId.toString());
        set(BANNED_PLAYERS, bannedPlayers);
    }

    public boolean isActionAllowedInWar(@NotNull ProtectionManager.Action action) {
        return is(ConfigField.valueOf("WAR_ACTIONS_" + action.name()));
    }

    public List<String> getIgnoredList(@NotNull ProtectionManager.Action action) {
        return getStringList(ConfigField.valueOf("WAR_LISTENERS_IGNORED_LIST_" + action.name()));
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public enum ConfigField {
        /*
        ================
        > General Settings
        ================
         *
         */
        ENABLE_GUI("settings.enable-gui"),
        DISABLE_MESSAGES("settings.disable-messages"),
        TAMABLE_MOBS_SHARING("settings.tameable-mobs-sharing"),
        TELEPORT_BLOCKS("settings.teleport-blocks"),
        TELEPORT_HOME_ON_SPAWN("settings.teleport-home-on-spawn"),
        DROP_ITEMS_ON_CLAN_HOME("settings.drop-items-on-clan-home"),
        KEEP_ITEMS_ON_CLAN_HOME("settings.keep-items-on-clan-home"),
        ITEM_LIST("settings.item-list"),
        DEBUG("settings.show-debug-info"),
        ENABLE_AUTO_GROUPS("settings.enable-auto-groups"),
        CHAT_COMPATIBILITY_MODE("settings.chat-compatibility-mode"),
        RIVAL_LIMIT_PERCENT("settings.rival-limit-percent"),
        COLOR_CODE_FROM_PREFIX_FOR_NAME("settings.use-colorcode-from-prefix-for-name"),
        DISPLAY_CHAT_TAGS("settings.display-chat-tags"),
        GLOBAL_FRIENDLY_FIRE("settings.global-friendly-fire"),
        UNRIVABLE_CLANS("settings.unrivable-clans"),
        SHOW_UNVERIFIED_ON_LIST("settings.show-unverified-on-list"),
        BLACKLISTED_WORLDS("settings.blacklisted-worlds"),
        BANNED_PLAYERS("settings.banned-players"),
        DISALLOWED_TAGS("settings.disallowed-tags"),
        LANGUAGE("settings.language"),
        LANGUAGE_SELECTOR("settings.user-language-selector"),
        DISALLOWED_TAG_COLORS("settings.disallowed-tag-colors"),
        SERVER_NAME("settings.server-name"),
        REQUIRE_VERIFICATION("settings.new-clan-verification-required"),
        ALLOW_REGROUP("settings.allow-regroup-command"),
        ALLOW_RESET_KDR("settings.allow-reset-kdr"),
        REJOIN_COOLDOWN("settings.rejoin-cooldown"),
        ENABLE_REJOIN_COOLDOWN("settings.rejoin-cooldown-enabled"),
        ACCEPT_OTHER_ALPHABETS_LETTERS("settings.accept-other-alphabets-letters-on-tag"),
        RANKING_TYPE("settings.ranking-type"),
        LIST_DEFAULT_ORDER_BY("settings.list-default-order-by"),
        LORE_LENGTH("settings.lore-length"),
        PVP_ONLY_WHILE_IN_WAR("settings.pvp-only-while-at-war"),
        /*
        ================
        > Tag Settings
        ================
         *
         */
        TAG_DEFAULT_COLOR("tag.default-color"),
        TAG_MAX_LENGTH("tag.max-length"),
        TAG_BRACKET_COLOR("tag.bracket.color"),
        TAG_BRACKET_LEADER_COLOR("tag.bracket.leader-color"),
        TAG_BRACKET_LEFT("tag.bracket.left"),
        TAG_BRACKET_RIGHT("tag.bracket.right"),
        TAG_MIN_LENGTH("tag.min-length"),
        TAG_SEPARATOR_COLOR("tag.separator.color"),
        TAG_SEPARATOR_LEADER_COLOR("tag.separator.leader-color"),
        TAG_SEPARATOR_char("tag.separator.char"),
        /*
        ================
        > War and Protection Settings
        ================
         *
         */
        ENABLE_WAR("war-and-protection.war-enabled"),
        LAND_SHARING("war-and-protection.land-sharing"),
        LAND_PROTECTION_PROVIDERS("war-and-protection.protection-providers"),
        WAR_LISTENERS_PRIORITY("war-and-protection.listeners.priority"),
        WAR_LISTENERS_IGNORED_LIST_PLACE("war-and-protection.listeners.ignored-list.PLACE"),
        WAR_LISTENERS_IGNORED_LIST_BREAK("war-and-protection.listeners.ignored-list.BREAK"),
        LAND_SET_BASE_ONLY_IN_LAND("war-and-protection.set-base-only-in-land"),
        WAR_NORMAL_EXPIRATION_TIME("war-and-protection.war-normal-expiration-time"),
        WAR_DISCONNECT_EXPIRATION_TIME("war-and-protection.war-disconnect-expiration-time"),
        LAND_EDIT_ALL_LANDS("war-and-protection.edit-all-lands"),
        LAND_CREATION_ONLY_LEADERS("war-and-protection.land-creation.only-leaders"),
        LAND_CREATION_ONLY_ONE_PER_CLAN("war-and-protection.land-creation.only-one-per-clan"),
        WAR_ACTIONS_CONTAINER("war-and-protection.war-actions.CONTAINER"),
        WAR_ACTIONS_INTERACT("war-and-protection.war-actions.INTERACT"),
        WAR_ACTIONS_BREAK("war-and-protection.war-actions.BREAK"),
        WAR_ACTIONS_PLACE("war-and-protection.war-actions.PLACE"),
        WAR_ACTIONS_DAMAGE("war-and-protection.war-actions.DAMAGE"),
        WAR_ACTIONS_INTERACT_ENTITY("war-and-protection.war-actions.INTERACT_ENTITY"),
        WAR_START_REQUEST_ENABLED("war-and-protection.war-start.request-enabled"),
        WAR_MAX_MEMBERS_DIFFERENCE("war-and-protection.war-start.members-online-max-difference"),
        /*
        ================
        > KDR Grinding Prevention Settings
        ================
         *
         */
        KDR_ENABLE_MAX_KILLS("kdr-grinding-prevention.enable-max-kills"),
        KDR_MAX_KILLS_PER_VICTIM("kdr-grinding-prevention.max-kills-per-victim"),
        KDR_ENABLE_KILL_DELAY("kdr-grinding-prevention.enable-kill-delay"),
        KDR_DELAY_BETWEEN_KILLS("kdr-grinding-prevention.delay-between-kills"),
        /*
        ================
        > Commands Settings
        ================
         *
         */
        COMMANDS_MORE("commands.more"),
        COMMANDS_ALLY("commands.ally"),
        COMMANDS_CLAN("commands.clan"),
        COMMANDS_ACCEPT("commands.accept"),
        COMMANDS_DENY("commands.deny"),
        COMMANDS_GLOBAL("commands.global"),
        COMMANDS_CLAN_CHAT("commands.clan_chat"),
        COMMANDS_FORCE_PRIORITY("commands.force-priority"),
        /*
        ================
        > Economy Settings
        ================
         *
         */
        ECONOMY_CREATION_PRICE("economy.creation-price"),
        ECONOMY_PURCHASE_CLAN_CREATE("economy.purchase-clan-create"),
        ECONOMY_VERIFICATION_PRICE("economy.verification-price"),
        ECONOMY_PURCHASE_CLAN_VERIFY("economy.purchase-clan-verify"),
        ECONOMY_INVITE_PRICE("economy.invite-price"),
        ECONOMY_PURCHASE_CLAN_INVITE("economy.purchase-clan-invite"),
        ECONOMY_HOME_TELEPORT_PRICE("economy.home-teleport-price"),
        ECONOMY_PURCHASE_HOME_TELEPORT("economy.purchase-home-teleport"),
        ECONOMY_HOME_TELEPORT_SET_PRICE("economy.home-teleport-set-price"),
        ECONOMY_PURCHASE_HOME_TELEPORT_SET("economy.purchase-home-teleport-set"),
        ECONOMY_REGROUP_PRICE("economy.home-regroup-price"),
        ECONOMY_PURCHASE_HOME_REGROUP("economy.purchase-home-regroup"),
        ECONOMY_UNIQUE_TAX_ON_REGROUP("economy.unique-tax-on-regroup"),
        ECONOMY_ISSUER_PAYS_REGROUP("economy.issuer-pays-regroup"),
        ECONOMY_MONEY_PER_KILL("economy.money-per-kill"),
        ECONOMY_MONEY_PER_KILL_KDR_MULTIPLIER("economy.money-per-kill-kdr-multipier"),
        ECONOMY_RESET_KDR_PRICE("economy.reset-kdr-price"),
        ECONOMY_PURCHASE_RESET_KDR("economy.purchase-reset-kdr"),
        ECONOMY_PURCHASE_MEMBER_FEE_SET("economy.purchase-member-fee-set"),
        ECONOMY_MEMBER_FEE_SET_PRICE("economy.member-fee-set-price"),
        ECONOMY_MEMBER_FEE_ENABLED("economy.member-fee-enabled"),
        ECONOMY_MEMBER_FEE_LAST_MINUTE_CHANGE_INTERVAL("economy.member-fee-last-minute-change-interval"),
        ECONOMY_MAX_MEMBER_FEE("economy.max-member-fee"),
        ECONOMY_UPKEEP("economy.upkeep"),
        ECONOMY_UPKEEP_ENABLED("economy.upkeep-enabled"),
        ECONOMY_MULTIPLY_UPKEEP_BY_CLAN_SIZE("economy.multiply-upkeep-by-clan-size"),
        ECONOMY_UPKEEP_REQUIRES_MEMBER_FEE("economy.charge-upkeep-only-if-member-fee-enabled"),
        ECONOMY_BANK_LOG_ENABLED("economy.bank-log.enable"),
        /*
        ================
        > Kill Weights Settings
        ================
         *
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
         */
        CLAN_HOMEBASE_TELEPORT_WAIT_SECS("clan.homebase-teleport-wait-secs"),
        CLAN_HOMEBASE_CAN_BE_SET_ONLY_ONCE("clan.homebase-can-be-set-only-once"),
        CLAN_MIN_SIZE_TO_SET_RIVAL("clan.min-size-to-set-rival"),
        CLAN_MIN_SIZE_TO_SET_ALLY("clan.min-size-to-set-ally"),
        CLAN_MAX_LENGTH("clan.max-length"),
        CLAN_MIN_LENGTH("clan.min-length"),
        CLAN_MAX_DESCRIPTION_LENGTH("clan.max-description-length"),
        CLAN_MIN_DESCRIPTION_LENGTH("clan.min-description-length"),
        CLAN_MAX_MEMBERS("clan.max-members"),
        CLAN_MAX_ALLIANCES("clan.max-alliances"),
        CLAN_CONFIRMATION_FOR_PROMOTE("clan.confirmation-for-promote"),
        CLAN_TRUST_MEMBERS_BY_DEFAULT("clan.trust-members-by-default"),
        CLAN_CONFIRMATION_FOR_DEMOTE("clan.confirmation-for-demote"),
        CLAN_PERCENTAGE_ONLINE_TO_DEMOTE("clan.percentage-online-to-demote"),
        CLAN_FF_ON_BY_DEFAULT("clan.ff-on-by-default"),
        CLAN_MIN_TO_VERIFY("clan.min-to-verify"),
        /*
        ================
        > Tasks Settings
        ================
         *
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
         */
        CLANCHAT_ENABLE("clanchat.enable"),
        CLANCHAT_TAG_BASED_CLAN_CHAT("clanchat.tag-based-clan-chat"),
        CLANCHAT_ANNOUNCEMENT_COLOR("clanchat.announcement-color"),
        CLANCHAT_FORMAT("clanchat.format"),
        CLANCHAT_RANK("clanchat.rank"),
        CLANCHAT_LEADER_COLOR("clanchat.leader-color"),
        CLANCHAT_TRUSTED_COLOR("clanchat.trusted-color"),
        CLANCHAT_MEMBER_COLOR("clanchat.member-color"),
        CLANCHAT_BRACKET_COLOR("clanchat.tag-bracket.color"),
        CLANCHAT_BRACKET_LEFT("clanchat.tag-bracket.left"),
        CLANCHAT_BRACKET_RIGHT("clanchat.tag-bracket.right"),
        CLANCHAT_NAME_COLOR("clanchat.name-color"),
        CLANCHAT_PLAYER_BRACKET_LEFT("clanchat.player-bracket.left"),
        CLANCHAT_PLAYER_BRACKET_RIGHT("clanchat.player-bracket.right"),
        CLANCHAT_MESSAGE_COLOR("clanchat.message-color"),
        /*
        ================
        > Request Settings
        ================
         *
         */
        REQUEST_MESSAGE_COLOR("request.message-color"),
        REQUEST_FREQUENCY("request.ask-frequency-secs"),
        REQUEST_MAX("request.max-asks-per-request"),
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
        ALLYCHAT_BRACKET_COLOR("allychat.tag-bracket.color"),
        ALLYCHAT_BRACKET_lEFT("allychat.tag-bracket.left"),
        ALLYCHAT_BRACKET_RIGHT("allychat.tag-bracket.right"),
        ALLYCHAT_PLAYER_BRACKET_LEFT("allychat.player-bracket.left"),
        ALLYCHAT_PLAYER_BRACKET_RIGHT("allychat.player-bracket.right"),
        ALLYCHAT_MESSAGE_COLOR("allychat.message-color"),
        ALLYCHAT_TAG_COLOR("allychat.tag-color"),
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
        PERFORMANCE_USE_BUNGEECORD("performance.use-bungeecord"),
        PERFORMANCE_HEAD_CACHING("performance.cache-player-heads"),

        SAFE_CIVILIANS("safe-civilians");

        private final String path;

        ConfigField(String path) {
            this.path = path;
        }
    }
}
