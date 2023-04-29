package net.sacredlabyrinth.phaed.simpleclans;

import co.aikar.commands.BukkitCommandIssuer;
import net.sacredlabyrinth.phaed.simpleclans.commands.SCCommandManager;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.SimpleClansExpansion;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.listeners.*;
import net.sacredlabyrinth.phaed.simpleclans.loggers.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.loggers.CSVBankLogger;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.migrations.BbMigration;
import net.sacredlabyrinth.phaed.simpleclans.migrations.LanguageMigration;
import net.sacredlabyrinth.phaed.simpleclans.migrations.TagRegexMigration;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import net.sacredlabyrinth.phaed.simpleclans.proxy.ProxyManager;
import net.sacredlabyrinth.phaed.simpleclans.tasks.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.migrations.ChatFormatMigration;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.UpdateChecker;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.Bukkit.getPluginManager;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin {

    private final ArrayList<String> messages = new ArrayList<>();

    private static SimpleClans instance;
    private static LanguageResource languageResource;
    private static final Logger logger = Logger.getLogger("SimpleClans");
    private SCCommandManager commandManager;
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private TeleportManager teleportManager;
    private ProtectionManager protectionManager;
    private ChatManager chatManager;
    private ProxyManager proxyManager;
    private boolean hasUUID;
    private static final Pattern ACF_PLACEHOLDER_PATTERN = Pattern.compile("\\{(?<key>[a-zA-Z]+?)}");

    private BankLogger bankLogger;

    /**
     * @return the logger
     */
    @Deprecated
    public static Logger getLog() {
        return logger;
    }

    public static void debug(String msg) {
        if (getInstance().getSettingsManager().is(DEBUG)) {
            logger.log(Level.INFO, msg);
        }
    }

    /**
     * @return the instance
     */
    public static SimpleClans getInstance() {
        return instance;
    }

    @Deprecated
    public static void log(String msg, Object... arg) {
        if (arg == null || arg.length == 0) {
            logger.log(Level.INFO, msg);
        } else {
            logger.log(Level.INFO, MessageFormat.format(msg, arg));
        }
    }

    @Override
    public void onEnable() {
        instance = this;
        new LanguageMigration(this).migrate();
        settingsManager = new SettingsManager(this);
        new BbMigration(settingsManager);
        new TagRegexMigration(settingsManager);
        new ChatFormatMigration(settingsManager);
        languageResource = new LanguageResource();
        this.hasUUID = UUIDMigration.canReturnUUID();

        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        proxyManager = new BungeeManager(this);
        storageManager = new StorageManager();
        teleportManager = new TeleportManager();
        protectionManager = new ProtectionManager();
        protectionManager.registerListeners();
        chatManager = new ChatManager(this);
        registerEvents();
        permissionsManager.loadPermissions();
        commandManager = new SCCommandManager(this);
        bankLogger = new CSVBankLogger(this);

        logStatus();
        startTasks();
        startMetrics();
        hookIntoPAPI();
        new UpdateChecker(this).check();
    }

    private void logStatus() {
        getLogger().info("Multithreading: " + settingsManager.is(PERFORMANCE_USE_THREADS));
        getLogger().info("BungeeCord: " + settingsManager.is(PERFORMANCE_USE_BUNGEECORD));
        getLogger().info("HEX support: " + ChatUtils.HEX_COLOR_SUPPORT);
        getLogger().info("Help us translate SimpleClans to your language! " +
                "Access https://crowdin.com/project/simpleclans/");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDeath(this), this);
        pm.registerEvents(new SCPlayerListener(this), this);
        pm.registerEvents(new InventoryController(), this);
        pm.registerEvents(new TamableMobsSharing(this), this);
        pm.registerEvents(new PvPOnlyInWar(this), this);
        pm.registerEvents(new FriendlyFire(this), this);
    }

    private void hookIntoPAPI() {
        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getLogger().info("PlaceholderAPI found. Registering hook...");
            new SimpleClansExpansion(this).register();
        }
    }

    private void startMetrics() {
        Metrics metrics = new Metrics(this, 7131);
        SettingsManager sm = getSettingsManager();
        ClanManager cm = getClanManager();
        String on = "enabled";
        String off = "disabled";
        metrics.addCustomChart(new SingleLineChart("clans", () -> cm.getClans().size()));
        metrics.addCustomChart(new SingleLineChart("clan_players", () -> cm.getAllClanPlayers().size()));
        metrics.addCustomChart(new SimplePie("language", () -> sm.getLanguage().toString()));
        metrics.addCustomChart(new SimplePie("machine_language", () -> Locale.getDefault().toString()));
        metrics.addCustomChart(new SimplePie("language_chooser", () -> sm.is(LANGUAGE_SELECTOR) ? on : off));
        metrics.addCustomChart(new SimplePie("database", () -> sm.is(MYSQL_ENABLE) ? "MySQL" : "SQLite"));
        metrics.addCustomChart(new SimplePie("save_periodically", () -> sm.is(PERFORMANCE_SAVE_PERIODICALLY) ? on : off));
        metrics.addCustomChart(new SimplePie("save_interval", () -> sm.getString(PERFORMANCE_SAVE_INTERVAL)));
        metrics.addCustomChart(new SimplePie("upkeep", () -> sm.is(ECONOMY_UPKEEP_ENABLED) ? on : off));
        metrics.addCustomChart(new SimplePie("member_fee", () -> sm.is(ECONOMY_MEMBER_FEE_ENABLED) ? on : off));
        metrics.addCustomChart(new SimplePie("rejoin_cooldown", () -> sm.is(ENABLE_REJOIN_COOLDOWN) ? on : off));
        metrics.addCustomChart(new SimplePie("clan_verification", () -> sm.is(REQUIRE_VERIFICATION) ? on : off));
        metrics.addCustomChart(new SimplePie("money_per_kill", () -> sm.is(ECONOMY_MONEY_PER_KILL) ? on : off));
        metrics.addCustomChart(new SimplePie("threads", () -> sm.is(PERFORMANCE_USE_THREADS) ? on : off));
        metrics.addCustomChart(new SimplePie("bungeecord", () -> sm.is(PERFORMANCE_USE_BUNGEECORD) ? on : off));
        metrics.addCustomChart(new SimplePie("discord_chat", () -> sm.is(DISCORDCHAT_ENABLE) ? on : off));
    }

    private void startTasks() {
        if (getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            new SaveDataTask().start();
        }
        if (getSettingsManager().is(ECONOMY_MEMBER_FEE_ENABLED)) {
            new CollectFeeTask().start();
        }
        if (getSettingsManager().is(ECONOMY_UPKEEP_ENABLED)) {
            new CollectUpkeepTask().start();
            new UpkeepWarningTask().start();
        }
        if (getSettingsManager().is(PERFORMANCE_HEAD_CACHING)) {
            new PlayerHeadCacheTask(this).start();
        }
    }

    @Override
    public void onDisable() {
        if (getSettingsManager().is(PERFORMANCE_SAVE_PERIODICALLY)) {
            getStorageManager().saveModified();
        }
        getStorageManager().closeConnection();
        getPermissionsManager().savePermissions();
        getSettingsManager().loadAndSave();
    }

    /**
     * @return the clanManager
     */
    public ClanManager getClanManager() {
        return clanManager;
    }

    /**
     * @return the requestManager
     */
    public RequestManager getRequestManager() {
        return requestManager;
    }

    /**
     * @return the storageManager
     */
    public StorageManager getStorageManager() {
        return storageManager;
    }

    /**
     * @return the settingsManager
     */
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    /**
     * @return the permissionsManager
     */
    @NotNull
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public SCCommandManager getCommandManager() {
        return commandManager;
    }

    public ProtectionManager getProtectionManager() {
        return protectionManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    public ProxyManager getProxyManager() {
        return proxyManager;
    }

    public BankLogger getBankLogger() {
        return bankLogger;
    }

    /**
     * @param key the path within the language file
     * @return the lang
     */
    @Deprecated
    public String getLang(@NotNull String key) {
        return getLang(key, null);
    }

    @Deprecated
    public String getLang(@NotNull String key, @Nullable Player player) {
        return lang(key, player);
    }

    @Nullable
    public static String optionalLang(@NotNull String key, @Nullable ClanPlayer clanPlayer, Object... arguments) {
        Locale locale = instance.getSettingsManager().getLanguage();
        if (clanPlayer != null && clanPlayer.getLocale() != null && instance.getSettingsManager().is(LANGUAGE_SELECTOR)) {
            locale = clanPlayer.getLocale();
        }

        String lang = languageResource.getLang(key, locale);
        if (lang == null) {
            return null;
        }
        String message = ChatUtils.parseColors(lang);
        // contains acf placeholders like {commandprefix}
        if (ACF_PLACEHOLDER_PATTERN.matcher(message).find()) {
            return message;
        }
        return MessageFormat.format(message, arguments);
    }

    @Nullable
    public static String optionalLang(@NotNull String key, @Nullable Player player, Object... arguments) {
        ClanPlayer clanPlayer = null;
        if (player != null) {
            clanPlayer = instance.getClanManager().getAnyClanPlayer(player.getUniqueId());
        }
        return optionalLang(key, clanPlayer, arguments);
    }

    @NotNull
    public static String lang(@NotNull String key, @Nullable Player player, Object... arguments) {
        String lang = optionalLang(key, player, arguments);
        return (lang == null) ? key : lang;
    }

    @NotNull
    public static String lang(@NotNull String key, @Nullable ClanPlayer clanPlayer, Object... arguments) {
        String lang = optionalLang(key, clanPlayer, arguments);
        return (lang == null) ? key : lang;
    }

    @NotNull
    public static String lang(@NotNull String key, @Nullable CommandSender sender, Object... arguments) {
        if (sender instanceof Player) {
            return lang(key, (Player) sender, arguments);
        } else {
            return lang(key, (Player) null, arguments);
        }
    }

    @NotNull
    public static String lang(@NotNull String key, @Nullable BukkitCommandIssuer issuer, Object... arguments) {
        if (issuer != null) {
            return lang(key, issuer.getIssuer(), arguments);
        }
        return lang(key, arguments);
    }

    @NotNull
    public static String lang(@NotNull String key, Object... arguments) {
        return lang(key, (Player) null, arguments);
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    @Deprecated
    public List<String> getMessages() {
        return messages;
    }

    @Deprecated
    public boolean hasUUID() {
        return this.hasUUID;
    }

    @Deprecated
    public void setUUID(boolean trueOrFalse) {
        this.hasUUID = trueOrFalse;
    }
}
