package net.sacredlabyrinth.phaed.simpleclans;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.sacredlabyrinth.phaed.simpleclans.executors.AcceptCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.AllyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.ClanCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.DenyCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.GlobalCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.executors.MoreCommandExecutor;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageMigration;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PlaceholdersManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.TeleportManager;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectFeeTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectUpkeepTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.SaveDataTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.UpkeepWarningTask;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatFormatMigration;
import net.sacredlabyrinth.phaed.simpleclans.utils.UpdateChecker;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin {

    private final ArrayList<String> messages = new ArrayList<>();
    private static SimpleClans instance;
    private static LanguageResource languageResource;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private ClanManager clanManager;
    private RequestManager requestManager;
    private StorageManager storageManager;
    private SettingsManager settingsManager;
    private PermissionsManager permissionsManager;
    private TeleportManager teleportManager;
    private boolean hasUUID;

    /**
     * @return the logger
     */
    @Deprecated
    public static Logger getLog() {
        return logger;
    }

    /**
     * @param msg
     */
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

        ChatFormatMigration chatFormatMigration = new ChatFormatMigration();
        chatFormatMigration.migrateAllyChat();
        chatFormatMigration.migrateClanChat();
        
        getServer().getPluginManager().registerEvents(new SCEntityListener(), this);
        getServer().getPluginManager().registerEvents(new SCPlayerListener(), this);
        getServer().getPluginManager().registerEvents(new InventoryController(), this);

        permissionsManager.loadPermissions();

        CommandHelper.registerCommand(getSettingsManager().getCommandClan());
        CommandHelper.registerCommand(getSettingsManager().getCommandAccept());
        CommandHelper.registerCommand(getSettingsManager().getCommandDeny());
        CommandHelper.registerCommand(getSettingsManager().getCommandMore());
        CommandHelper.registerCommand(getSettingsManager().getCommandAlly());
        CommandHelper.registerCommand(getSettingsManager().getCommandGlobal());

        getCommand(getSettingsManager().getCommandClan()).setExecutor(new ClanCommandExecutor());
        getCommand(getSettingsManager().getCommandAccept()).setExecutor(new AcceptCommandExecutor());
        getCommand(getSettingsManager().getCommandDeny()).setExecutor(new DenyCommandExecutor());
        getCommand(getSettingsManager().getCommandMore()).setExecutor(new MoreCommandExecutor());
        getCommand(getSettingsManager().getCommandAlly()).setExecutor(new AllyCommandExecutor());
        getCommand(getSettingsManager().getCommandGlobal()).setExecutor(new GlobalCommandExecutor());

        getCommand(getSettingsManager().getCommandClan()).setTabCompleter(new PlayerNameTabCompleter());
        getLogger().info("Multithreading: " + SimpleClans.getInstance().getSettingsManager().getUseThreads());
        getLogger().info("BungeeCord: " + SimpleClans.getInstance().getSettingsManager().getUseBungeeCord());
        if (!Locale.getDefault().getLanguage().equals("en")) {
        	getLogger().info("Help us translate SimpleClans to your language! Access https://crowdin.com/project/simpleclans/");
        }
        
        startTasks();
        startMetrics();
        hookIntoPAPI();
        new UpdateChecker(this).check();
    }
    
    private void hookIntoPAPI() {
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("PlaceholderAPI found. Registering hook...");
			new PlaceholdersManager(this);
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
    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    /**
     * @param key the path within the language file
     * @return the lang
     */
    @Deprecated
    public String getLang(String key) {
        return getLang(key, null);
    }

    @Deprecated
    public String getLang(String key, Player player) {
    	Locale locale;
    	if (player == null) {
    		locale = getSettingsManager().getLanguage();
    	} else {
    		locale = Helper.getLocale(player);
    	}
    	return ChatColor.translateAlternateColorCodes('&', languageResource.getLang(key, locale));
    }

    @NotNull
    public static String lang(@NotNull String key, @Nullable Player player, Object... arguments) {
        Locale locale;
        if (player != null && instance.getSettingsManager().isLanguagePerPlayer()) {
            locale = Helper.getLocale(player);
        } else {
            locale = getInstance().getSettingsManager().getLanguage();
        }

        return MessageFormat.format(
                ChatColor.translateAlternateColorCodes(
                        '&', languageResource.getLang(key, locale)), arguments);
    }
    
    @NotNull
    public static String lang(@NotNull String key, @NotNull CommandSender sender, Object... arguments) {
    	if (sender instanceof Player) {
            return lang(key, (Player) sender, arguments);
        } else {
            return lang(key, (Player) null, arguments);
        }
    }

    @NotNull
    public static String lang(@NotNull String key, Object... arguments) {
        return lang(key, (Player) null, arguments);
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
     * @return the hasUUID
     */
    @Deprecated
    public boolean hasUUID() {
        return this.hasUUID;
    }

    /**
     * @param trueOrFalse
     */
    public void setUUID(boolean trueOrFalse) {
        this.hasUUID = trueOrFalse;
    }
}