package net.sacredlabyrinth.phaed.simpleclans;

import co.aikar.commands.BukkitCommandIssuer;
import net.sacredlabyrinth.phaed.simpleclans.commands.SCCommandManager;
import net.sacredlabyrinth.phaed.simpleclans.hooks.papi.SimpleClansExpansion;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageMigration;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.listeners.*;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.storage.BankLogger;
import net.sacredlabyrinth.phaed.simpleclans.storage.CSVBankLogger;
import net.sacredlabyrinth.phaed.simpleclans.tasks.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatFormatMigration;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.UpdateChecker;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bstats.bukkit.Metrics;
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
        if (getInstance().getSettingsManager().isDebugging()) {
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
        settingsManager = new SettingsManager();
        languageResource = new LanguageResource();
        this.hasUUID = UUIDMigration.canReturnUUID();

        permissionsManager = new PermissionsManager();
        requestManager = new RequestManager();
        clanManager = new ClanManager();
        storageManager = new StorageManager();
        teleportManager = new TeleportManager();
        protectionManager = new ProtectionManager();
        protectionManager.registerListeners();
        migrateChatFormat();
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
        getLogger().info("Multithreading: " + SimpleClans.getInstance().getSettingsManager().getUseThreads());
        getLogger().info("BungeeCord: " + SimpleClans.getInstance().getSettingsManager().getUseBungeeCord());
        getLogger().info("Help us translate SimpleClans to your language! " +
                "Access https://crowdin.com/project/simpleclans/");
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerDeath(), this);
        pm.registerEvents(new SCPlayerListener(), this);
        pm.registerEvents(new InventoryController(), this);
        pm.registerEvents(new TamableMobsSharing(this), this);
        pm.registerEvents(new PvPOnlyInWar(this), this);
        pm.registerEvents(new FriendlyFire(this), this);
    }

    private void migrateChatFormat() {
        ChatFormatMigration chatFormatMigration = new ChatFormatMigration();
        chatFormatMigration.migrateAllyChat();
        chatFormatMigration.migrateClanChat();
    }

    private void hookIntoPAPI() {
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
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

        metrics.addCustomChart(new Metrics.SingleLineChart("clans", () -> cm.getClans().size()));
        metrics.addCustomChart(new Metrics.SingleLineChart("clan_players", () -> cm.getAllClanPlayers().size()));
        metrics.addCustomChart(new Metrics.SimplePie("language", () -> sm.getLanguage().toString()));
        metrics.addCustomChart(new Metrics.SimplePie("machine_language", () -> Locale.getDefault().toString()));
        metrics.addCustomChart(new Metrics.SimplePie("language_chooser", () -> sm.isLanguagePerPlayer() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("database", () -> sm.isUseMysql() ? "MySQL" : "SQLite"));
        metrics.addCustomChart(new Metrics.SimplePie("save_periodically", () -> sm.isSavePeriodically() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("save_interval", () -> String.valueOf(sm.getSaveInterval())));
        metrics.addCustomChart(new Metrics.SimplePie("upkeep", () -> sm.isClanUpkeep() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("member_fee", () -> sm.isMemberFee() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("rejoin_cooldown", () -> sm.isRejoinCooldown() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("clan_verification", () -> sm.isRequireVerification() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("money_per_kill", () -> sm.isMoneyPerKill() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("threads", () -> sm.getUseThreads() ? on : off));
        metrics.addCustomChart(new Metrics.SimplePie("bungeecord", () -> sm.getUseBungeeCord() ? on : off));
    }

    private void startTasks() {
        if (getSettingsManager().isSavePeriodically()) {
            new SaveDataTask().start();
        }
        if (getSettingsManager().isMemberFee()) {
            new CollectFeeTask().start();
        }
        if (getSettingsManager().isClanUpkeep()) {
            new CollectUpkeepTask().start();
            new UpkeepWarningTask().start();
        }
        if (getSettingsManager().isCachePlayerHeads()) {
            new PlayerHeadCacheTask(this).start();
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        if (getSettingsManager().isSavePeriodically()) {
            getStorageManager().saveModified();
        }
        getStorageManager().closeConnection();
        getPermissionsManager().savePermissions();
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
    public static String optionalLang(@NotNull String key, @Nullable Player player, Object... arguments) {
        Locale locale = getInstance().getSettingsManager().getLanguage();
        if (player != null && instance.getSettingsManager().isLanguagePerPlayer()) {
            Locale playerLocale = Helper.getLocale(player);
            if (playerLocale != null) {
                locale = playerLocale;
            }
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

    @NotNull
    public static String lang(@NotNull String key, @Nullable Player player, Object... arguments) {
        String lang = optionalLang(key, player, arguments);
        if (lang == null) {
            return key;
        }
        return lang;
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

    public void setUUID(boolean trueOrFalse) {
        this.hasUUID = trueOrFalse;
    }
}