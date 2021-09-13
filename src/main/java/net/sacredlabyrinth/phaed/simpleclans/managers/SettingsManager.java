package net.sacredlabyrinth.phaed.simpleclans.managers;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType;
import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.util.NumberConversions.toDouble;
import static org.bukkit.util.NumberConversions.toInt;

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
        warnAboutPluginDependencies();
    }

    public <T> void set(ConfigField field, T value) {
        config.set(field.path, value);
    }

    public int getInt(ConfigField field) {
        return config.getInt(field.path, toInt(field.defaultValue));
    }

    public double getDouble(ConfigField field) {
        return config.getDouble(field.path, toDouble(field.defaultValue));
    }

    public List<String> getStringList(ConfigField field) {
        return config.getStringList(field.path);
    }

    public String getString(ConfigField field) {
        return config.getString(field.path, String.valueOf(field.defaultValue));
    }

    public String getColored(ConfigField field) {
        String value = getString(field);
        return (value.length() == 1) ? ChatUtils.getColorByChar(value.charAt(0)) : ChatUtils.parseColors(value);
    }

    public int getMinutes(ConfigField field) {
        int value = getInt(field);
        return (value >= 1) ? value * 20 * 60 : toInt(field.defaultValue) * 20 * 60;
    }

    public int getSeconds(ConfigField field) {
        int value = getInt(field);
        return (value >= 1) ? value * 20 : toInt(field.defaultValue) * 20;
    }

    public double getPercent(ConfigField field) {
        double value = getDouble(field);
        return (getDouble(field) >= 0 || getDouble(field) <= 100) ? value : toDouble(field.defaultValue);
    }

    public boolean is(ConfigField field) {
        return config.getBoolean(field.path, (Boolean) field.defaultValue);
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

    private void warnAboutPluginDependencies() {
        Plugin luckPerms = getPluginManager().getPlugin("LuckPerms");
        Plugin discordSrv = getPluginManager().getPlugin("DiscordSRV");

        if (luckPerms != null && is(PERMISSIONS_AUTO_GROUP_GROUPNAME)) {
            plugin.getLogger().warning("LuckPerms was found and the setting auto-group-groupname is enabled.");
            plugin.getLogger().warning("Be careful with that as players will be automatically added in the group" +
                    " that matches their clan tag.");
        }

        if (discordSrv == null && is (DISCORDCHAT_ENABLE)) {
            plugin.getLogger().warning("DiscordChat can't be initialized, please, install DiscordSRV.");
        }
    }

    public Locale getLanguage() {
        String language = getString(LANGUAGE);
        String[] split = language.split("_");

        if (split.length == 2) {
            return new Locale(split[0], split[1]);
        }

        return new Locale(language);
    }

    public List<Material> getItemList() {
        List<Material> itemsList = new ArrayList<>();
        for (String material : getStringList(ITEM_LIST)) {
            Optional<XMaterial> x = XMaterial.matchXMaterial(material);
            if (x.isPresent()) {
                itemsList.add(x.get().parseMaterial());
            } else {
                plugin.getLogger().warning("Error with Material: " + material);
            }
        }
        return itemsList;
    }

    /**
     * Check whether a word is disallowed
     *
     * @param word the world
     * @return whether its a disallowed word
     */
    public boolean isDisallowedWord(String word) {
        for (String disallowedTag : getStringList(DISALLOWED_TAGS)) {
            return disallowedTag.equalsIgnoreCase(word);
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
        return String.join(", ", getStringList(DISALLOWED_TAG_COLORS));
    }

    /**
     * Check whether a clan is unrivable
     *
     * @param tag the tag
     * @return whether the clan is unrivable
     */
    public boolean isUnrivable(String tag) {
        return getStringList(UNRIVABLE_CLANS).stream().
                map(String::toLowerCase).
                anyMatch(unrivable -> unrivable.equals(tag.toLowerCase()));
    }

    /**
     * Add a player to the banned list
     *
     * @param playerUniqueId the player's name
     */
    public void addBanned(UUID playerUniqueId) {
        List<String> bannedPlayers = getStringList(BANNED_PLAYERS);
        if (isBanned(playerUniqueId)) {
            return;
        }

        bannedPlayers.add(playerUniqueId.toString());
        set(BANNED_PLAYERS, bannedPlayers);
        save();
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

    @NotNull
    public RankingType getRankingType() {
        try {
            return RankingType.valueOf(getString(RANKING_TYPE));
        } catch (IllegalArgumentException ex) {
            return RankingType.DENSE;
        }
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
        ENABLE_GUI("settings.enable-gui", true),
        DISABLE_MESSAGES("settings.disable-messages", false),
        TAMABLE_MOBS_SHARING("settings.tameable-mobs-sharing", false),
        TELEPORT_BLOCKS("settings.teleport-blocks", false),
        TELEPORT_HOME_ON_SPAWN("settings.teleport-home-on-spawn", false),
        DROP_ITEMS_ON_CLAN_HOME("settings.drop-items-on-clan-home", false),
        KEEP_ITEMS_ON_CLAN_HOME("settings.keep-items-on-clan-home", false),
        ITEM_LIST("settings.item-list"),
        DEBUG("settings.show-debug-info", false),
        ENABLE_AUTO_GROUPS("settings.enable-auto-groups", false),
        CHAT_COMPATIBILITY_MODE("settings.chat-compatibility-mode", true),
        RIVAL_LIMIT_PERCENT("settings.rival-limit-percent", 50),
        COLOR_CODE_FROM_PREFIX_FOR_NAME("settings.use-colorcode-from-prefix-for-name", true),
        DISPLAY_CHAT_TAGS("settings.display-chat-tags", true),
        GLOBAL_FRIENDLY_FIRE("settings.global-friendly-fire", false),
        UNRIVABLE_CLANS("settings.unrivable-clans"),
        SHOW_UNVERIFIED_ON_LIST("settings.show-unverified-on-list", false),
        BLACKLISTED_WORLDS("settings.blacklisted-worlds"),
        BANNED_PLAYERS("settings.banned-players"),
        DISALLOWED_TAGS("settings.disallowed-tags"),
        LANGUAGE("settings.language", "en"),
        LANGUAGE_SELECTOR("settings.user-language-selector", true),
        DISALLOWED_TAG_COLORS("settings.disallowed-tag-colors"),
        SERVER_NAME("settings.server-name", "&4SimpleClans"),
        REQUIRE_VERIFICATION("settings.new-clan-verification-required", true),
        ALLOW_REGROUP("settings.allow-regroup-command", true),
        ALLOW_RESET_KDR("settings.allow-reset-kdr", false),
        REJOIN_COOLDOWN("settings.rejoin-cooldown", 60),
        ENABLE_REJOIN_COOLDOWN("settings.rejoin-cooldown-enabled", false),
        ACCEPT_OTHER_ALPHABETS_LETTERS("settings.accept-other-alphabets-letters-on-tag", false),
        RANKING_TYPE("settings.ranking-type", "DENSE"),
        LIST_DEFAULT_ORDER_BY("settings.list-default-order-by", "kdr"),
        LORE_LENGTH("settings.lore-length", 36),
        PVP_ONLY_WHILE_IN_WAR("settings.pvp-only-while-at-war", false),
        /*
        ================
        > Tag Settings
        ================
         *
         */
        TAG_DEFAULT_COLOR("tag.default-color", "8"),
        TAG_MAX_LENGTH("tag.max-length", 5),
        TAG_BRACKET_COLOR("tag.bracket.color", "8"),
        TAG_BRACKET_LEADER_COLOR("tag.bracket.leader-color", "4"),
        TAG_BRACKET_LEFT("tag.bracket.left", ""),
        TAG_BRACKET_RIGHT("tag.bracket.right", ""),
        TAG_MIN_LENGTH("tag.min-length", 2),
        TAG_SEPARATOR_COLOR("tag.separator.color", "8"),
        TAG_SEPARATOR_LEADER_COLOR("tag.separator.leader-color", "4"),
        TAG_SEPARATOR_char("tag.separator.char", " ."),
        /*
        ================
        > War and Protection Settings
        ================
         *
         */
        ENABLE_WAR("war-and-protection.war-enabled", false),
        LAND_SHARING("war-and-protection.land-sharing", true),
        LAND_PROTECTION_PROVIDERS("war-and-protection.protection-providers"),
        WAR_LISTENERS_PRIORITY("war-and-protection.listeners.priority", "HIGHEST"),
        WAR_LISTENERS_IGNORED_LIST_PLACE("war-and-protection.listeners.ignored-list.PLACE"),
        WAR_LISTENERS_IGNORED_LIST_BREAK("war-and-protection.listeners.ignored-list.BREAK"),
        LAND_SET_BASE_ONLY_IN_LAND("war-and-protection.set-base-only-in-land", false),
        WAR_NORMAL_EXPIRATION_TIME("war-and-protection.war-normal-expiration-time", 0),
        WAR_DISCONNECT_EXPIRATION_TIME("war-and-protection.war-disconnect-expiration-time", 0),
        LAND_EDIT_ALL_LANDS("war-and-protection.edit-all-lands", false),
        LAND_CREATION_ONLY_LEADERS("war-and-protection.land-creation.only-leaders", false),
        LAND_CREATION_ONLY_ONE_PER_CLAN("war-and-protection.land-creation.only-one-per-clan", false),
        WAR_ACTIONS_CONTAINER("war-and-protection.war-actions.CONTAINER", true),
        WAR_ACTIONS_INTERACT("war-and-protection.war-actions.INTERACT", true),
        WAR_ACTIONS_BREAK("war-and-protection.war-actions.BREAK", true),
        WAR_ACTIONS_PLACE("war-and-protection.war-actions.PLACE", true),
        WAR_ACTIONS_DAMAGE("war-and-protection.war-actions.DAMAGE", true),
        WAR_ACTIONS_INTERACT_ENTITY("war-and-protection.war-actions.INTERACT_ENTITY", true),
        WAR_START_REQUEST_ENABLED("war-and-protection.war-start.request-enabled", true),
        WAR_MAX_MEMBERS_DIFFERENCE("war-and-protection.war-start.members-online-max-difference", 5),
        /*
        ================
        > KDR Grinding Prevention Settings
        ================
         *
         */
        KDR_ENABLE_MAX_KILLS("kdr-grinding-prevention.enable-max-kills", false),
        KDR_MAX_KILLS_PER_VICTIM("kdr-grinding-prevention.max-kills-per-victim", 10),
        KDR_ENABLE_KILL_DELAY("kdr-grinding-prevention.enable-kill-delay", false),
        KDR_DELAY_BETWEEN_KILLS("kdr-grinding-prevention.delay-between-kills", 5),
        /*
        ================
        > Commands Settings
        ================
         *
         */
        COMMANDS_MORE("commands.more", "more"),
        COMMANDS_ALLY("commands.ally", "ally"),
        COMMANDS_CLAN("commands.clan", "clan"),
        COMMANDS_ACCEPT("commands.accept", "accept"),
        COMMANDS_DENY("commands.deny", "deny"),
        COMMANDS_GLOBAL("commands.global", "global"),
        COMMANDS_CLAN_CHAT("commands.clan_chat", "."),
        COMMANDS_FORCE_PRIORITY("commands.force-priority", true),
        /*
        ================
        > Economy Settings
        ================
         *
         */
        ECONOMY_CREATION_PRICE("economy.creation-price", 100.0),
        ECONOMY_PURCHASE_CLAN_CREATE("economy.purchase-clan-create", false),
        ECONOMY_VERIFICATION_PRICE("economy.verification-price", 1000.0),
        ECONOMY_PURCHASE_CLAN_VERIFY("economy.purchase-clan-verify", false),
        ECONOMY_INVITE_PRICE("economy.invite-price", 20),
        ECONOMY_PURCHASE_CLAN_INVITE("economy.purchase-clan-invite", false),
        ECONOMY_HOME_TELEPORT_PRICE("economy.home-teleport-price", 5.0),
        ECONOMY_PURCHASE_HOME_TELEPORT("economy.purchase-home-teleport", false),
        ECONOMY_HOME_TELEPORT_SET_PRICE("economy.home-teleport-set-price", 5.0),
        ECONOMY_PURCHASE_HOME_TELEPORT_SET("economy.purchase-home-teleport-set", false),
        ECONOMY_REGROUP_PRICE("economy.home-regroup-price", 5.0),
        ECONOMY_PURCHASE_HOME_REGROUP("economy.purchase-home-regroup", false),
        ECONOMY_UNIQUE_TAX_ON_REGROUP("economy.unique-tax-on-regroup", true),
        ECONOMY_ISSUER_PAYS_REGROUP("economy.issuer-pays-regroup", true),
        ECONOMY_MONEY_PER_KILL("economy.money-per-kill", false),
        ECONOMY_MONEY_PER_KILL_KDR_MULTIPLIER("economy.money-per-kill-kdr-multipier", 10),
        ECONOMY_RESET_KDR_PRICE("economy.reset-kdr-price", 10000.0),
        ECONOMY_PURCHASE_RESET_KDR("economy.purchase-reset-kdr", true),
        ECONOMY_PURCHASE_MEMBER_FEE_SET("economy.purchase-member-fee-set", false),
        ECONOMY_MEMBER_FEE_SET_PRICE("economy.member-fee-set-price", 1000.0),
        ECONOMY_MEMBER_FEE_ENABLED("economy.member-fee-enabled", false),
        ECONOMY_MEMBER_FEE_LAST_MINUTE_CHANGE_INTERVAL("economy.member-fee-last-minute-change-interval", 8),
        ECONOMY_MAX_MEMBER_FEE("economy.max-member-fee", 200.0),
        ECONOMY_UPKEEP("economy.upkeep", false),
        ECONOMY_UPKEEP_ENABLED("economy.upkeep-enabled", false),
        ECONOMY_MULTIPLY_UPKEEP_BY_CLAN_SIZE("economy.multiply-upkeep-by-clan-size", false),
        ECONOMY_UPKEEP_REQUIRES_MEMBER_FEE("economy.charge-upkeep-only-if-member-fee-enabled", true),
        ECONOMY_BANK_LOG_ENABLED("economy.bank-log.enable", true),
        /*
        ================
        > Kill Weights Settings
        ================
         *
         */
        KILL_WEIGHTS_RIVAL("kill-weights.rival", 2.0),
        KILL_WEIGHTS_CIVILIAN("kill-weights.civilian", 0.0),
        KILL_WEIGHTS_NEUTRAL("kill-weights.neutral", 1.0),
        KILL_WEIGHTS_ALLY("kill-weights.ally", -1.0),
        KILL_WEIGHTS_DENY_SAME_IP_KILLS("kill-weights.deny-same-ip-kills", false),
        /*
        ================
        > Clan Settings
        ================
         *
         */
        CLAN_TELEPORT_DELAY("clan.homebase-teleport-wait-secs", 10),
        CLAN_HOMEBASE_CAN_BE_SET_ONLY_ONCE("clan.homebase-can-be-set-only-once", true),
        CLAN_MIN_SIZE_TO_SET_RIVAL("clan.min-size-to-set-rival", 3),
        CLAN_MIN_SIZE_TO_SET_ALLY("clan.min-size-to-set-ally", 3),
        CLAN_MAX_LENGTH("clan.max-length", 25),
        CLAN_MIN_LENGTH("clan.min-length", 2),
        CLAN_MAX_DESCRIPTION_LENGTH("clan.max-description-length", 120),
        CLAN_MIN_DESCRIPTION_LENGTH("clan.min-description-length", 10),
        CLAN_MAX_MEMBERS("clan.max-members", 25),
        CLAN_MAX_ALLIANCES("clan.max-alliances", -1),
        CLAN_CONFIRMATION_FOR_PROMOTE("clan.confirmation-for-promote", false),
        CLAN_TRUST_MEMBERS_BY_DEFAULT("clan.trust-members-by-default", false),
        CLAN_CONFIRMATION_FOR_DEMOTE("clan.confirmation-for-demote", false),
        CLAN_PERCENTAGE_ONLINE_TO_DEMOTE("clan.percentage-online-to-demote", 100.0),
        CLAN_FF_ON_BY_DEFAULT("clan.ff-on-by-default", false),
        CLAN_MIN_TO_VERIFY("clan.min-to-verify", 1),
        /*
        ================
        > Tasks Settings
        ================
         *
         */
        TASKS_COLLECT_UPKEEP_HOUR("tasks.collect-upkeep.hour", 1),
        TASKS_COLLECT_UPKEEP_MINUTE("tasks.collect-upkeep.minute", 30),
        TASKS_COLLECT_UPKEEP_WARNING_HOUR("tasks.collect-upkeep-warning.hour", 12),
        TASKS_COLLECT_UPKEEP_WARNING_MINUTE("tasks.collect-upkeep-warning.minute", 0),
        TASKS_COLLECT_FEE_HOUR("tasks.collect-fee.hour", 1),
        TASKS_COLLECT_FEE_MINUTE("tasks.collect-fee.minute", 0),
        /*
        ================
        > Page Settings
        ================
         */
        PAGE_LEADER_COLOR("page.leader-color", "4"),
        PAGE_UNTRUSTED_COLOR("page.untrusted-color", "8"),
        PAGE_TRUSTED_COLOR("page.trusted-color", "f"),
        PAGE_CLAN_NAME_COLOR("page.clan-name-color", "b"),
        PAGE_SUBTITLE_COLOR("page.subtitle-color", "7"),
        PAGE_HEADINGS_COLOR("page.headings-color", "8"),
        PAGE_SEPARATOR("page.separator", "-"),
        PAGE_SIZE("page.size", 100),
        /*
        ================
        > Clan Chat Settings
        ================
         *
         */
        CLANCHAT_ENABLE("clanchat.enable", true),
        CLANCHAT_TAG_BASED("clanchat.tag-based-clan-chat", false),
        CLANCHAT_ANNOUNCEMENT_COLOR("clanchat.announcement-color", "e"),
        CLANCHAT_FORMAT("clanchat.format", "&b[%clan%&b] &4<%nick-color%%player%&4> %rank%: &b%message%"),
        CLANCHAT_SPYFORMAT("clanchat.spy-format", "&8[Spy] [&bC&8] <%clan%&8> <%nick-color%*&8%player%>&8 %rank%: %message%"),
        CLANCHAT_RANK("clanchat.rank", "&f[%rank%&f]"),
        CLANCHAT_LEADER_COLOR("clanchat.leader-color", "4"),
        CLANCHAT_TRUSTED_COLOR("clanchat.trusted-color", "f"),
        CLANCHAT_MEMBER_COLOR("clanchat.member-color", "7"),
        CLANCHAT_BRACKET_COLOR("clanchat.tag-bracket.color", "e"),
        CLANCHAT_BRACKET_LEFT("clanchat.tag-bracket.left", ""),
        CLANCHAT_BRACKET_RIGHT("clanchat.tag-bracket.right", ""),
        CLANCHAT_NAME_COLOR("clanchat.name-color", "e"),
        CLANCHAT_PLAYER_BRACKET_LEFT("clanchat.player-bracket.left", ""),
        CLANCHAT_PLAYER_BRACKET_RIGHT("clanchat.player-bracket.right", ""),
        CLANCHAT_MESSAGE_COLOR("clanchat.message-color", "b"),
        /*
        ================
        > Request Settings
        ================
         *
         */
        REQUEST_MESSAGE_COLOR("request.message-color", "b"),
        REQUEST_FREQUENCY("request.ask-frequency-secs", 60),
        REQUEST_MAX("request.max-asks-per-request", 1440),
        /*
        ================
        > BB Settings
        ================
         */
        BB_COLOR("clanchat.color", "e"),
        BB_ACCENT_COLOR("clanchat.accent-color", "8"),
        BB_SHOW_ON_LOGIN("clanchat.show-on-login", true),
        BB_SIZE("clanchat.size", 6),
        BB_LOGIN_SIZE("clanchat.login-size", 6),
        /*
        ================
        > Ally Chat Settings
        ================
         */
        ALLYCHAT_ENABLE("allychat.enable", true),
        ALLYCHAT_FORMAT("allychat.format", "&b[Ally Chat] &4<%clan%&4> <%nick-color%%player%&4> %rank%: &b%message%"),
        ALLYCHAT_SPYFORMAT("allychat.spy-format", "&8[Spy] [&cA&8] <%clan%&8> <%nick-color%*&8%player%>&8 %rank%: %message%"),
        ALLYCHAT_RANK("allychat.rank", "&f[%rank%&f]"),
        ALLYCHAT_LEADER_COLOR("allychat.leader-color", "4"),
        ALLYCHAT_TRUSTED_COLOR("allychat.trusted-color", "f"),
        ALLYCHAT_MEMBER_COLOR("allychat.member-color", "7"),
        ALLYCHAT_BRACKET_COLOR("allychat.tag-bracket.color", "8"),
        ALLYCHAT_BRACKET_lEFT("allychat.tag-bracket.left", ""),
        ALLYCHAT_BRACKET_RIGHT("allychat.tag-bracket.right", ""),
        ALLYCHAT_PLAYER_BRACKET_LEFT("allychat.player-bracket.left", ""),
        ALLYCHAT_PLAYER_BRACKET_RIGHT("allychat.player-bracket.right", ""),
        ALLYCHAT_MESSAGE_COLOR("allychat.message-color", "3"),
        ALLYCHAT_TAG_COLOR("allychat.tag-color", ""),
        /*
        ================
        > Discord Chat Settings
        ================
         */
        DISCORDCHAT_ENABLE("discordchat.enable", false),
        DISCORDCHAT_FORMAT_TO("discordchat.discord-format", "%player% » %message%"),
        DISCORDCHAT_FORMAT("discordchat.format", "&b[&9D&b] &b[%clan%&b] &4<%nick-color%%player%&4> %rank%: &b%message%"),
        DISCORDCHAT_SPYFORMAT("discordchat.spy-format", "&8[Spy] [&9D&8] <%clan%&8> <%nick-color%*&8%player%>&8 %rank%: %message%"),
        DISCORDCHAT_RANK("discordchat.rank", "[%rank%]"),
        DISCORDCHAT_LEADER_ROLE("discordchat.leader-role", "Leader"),
        DISCORDCHAT_LEADER_ID("discordchat.leader-id", "0"),
        DISCORDCHAT_LEADER_COLOR("discordchat.leader-color", "231, 76, 60, 100"),
        DISCORDCHAT_TEXT_CATEGORY_FORMAT("discordchat.text.category-format", "SC - TextChannels"),
        DISCORDCHAT_TEXT_CATEGORY_IDS("discordchat.text.category-ids"),
        DISCORDCHAT_TEXT_WHITELIST("discordchat.text.whitelist"),
        DISCORDCHAT_TEXT_LIMIT("discordchat.text.clans-limit", 100),
        /*
        ================
        > Purge Settings
        ================
         */
        PURGE_INACTIVE_PLAYER_DAYS("purge.inactive-player-data-days", 30),
        PURGE_INACTIVE_CLAN_DAYS("purge.inactive-clan-days", 7),
        PURGE_UNVERIFIED_CLAN_DAYS("purge.unverified-clan-days", 2),
        /*
        ================
        > MySQL Settings
        ================
         */
        MYSQL_USERNAME("mysql.username", ""),
        MYSQL_HOST("mysql.host", "localhost"),
        MYSQL_PORT("mysql.port", 3306),
        MYSQL_ENABLE("mysql.enable", false),
        MYSQL_PASSWORD("mysql.password", ""),
        MYSQL_DATABASE("mysql.database", ""),
        /*
        ================
        > Permissions Settings
        ================
         */
        PERMISSIONS_AUTO_GROUP_GROUPNAME("permissions.auto-group-groupname", false),
        /*
        ================
        > Performance Settings
        ================
         */
        PERFORMANCE_SAVE_PERIODICALLY("performance.save-periodically", true),
        PERFORMANCE_SAVE_INTERVAL("performance.save-interval", 10),
        PERFORMANCE_USE_THREADS("performance.use-threads", true),
        PERFORMANCE_USE_BUNGEECORD("performance.use-bungeecord", false),
        PERFORMANCE_HEAD_CACHING("performance.cache-player-heads", false),

        SAFE_CIVILIANS("safe-civilians", false);

        private final String path;
        private final Object defaultValue;

        ConfigField(String path, Object defaultValue) {
            this.path = path;
            this.defaultValue = defaultValue;
        }

        ConfigField(String path) {
            this.path = path;
            this.defaultValue = null;
        }
    }
}
