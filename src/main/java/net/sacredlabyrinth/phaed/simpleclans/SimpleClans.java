package net.sacredlabyrinth.phaed.simpleclans;

import co.aikar.commands.*;
import co.aikar.locales.MessageKeyProvider;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanCommand;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageMigration;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCEntityListener;
import net.sacredlabyrinth.phaed.simpleclans.listeners.SCPlayerListener;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectFeeTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.CollectUpkeepTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.SaveDataTask;
import net.sacredlabyrinth.phaed.simpleclans.tasks.UpkeepWarningTask;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatFormatMigration;
import net.sacredlabyrinth.phaed.simpleclans.utils.UpdateChecker;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.bukkit.ChatColor.RED;

/**
 * @author Phaed
 */
public class SimpleClans extends JavaPlugin {

    private final ArrayList<String> messages = new ArrayList<>();
    private static SimpleClans instance;
    private static LanguageResource languageResource;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private PaperCommandManager commandManager;
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

        commandManager = new PaperCommandManager(this) {
            @Override
            public BukkitLocales getLocales() {
                if (this.locales == null) {
                    this.locales = new BukkitLocales(this) {
                        @Override
                        public String getMessage(CommandIssuer issuer, MessageKeyProvider key) {
                            // TODO Mensagens do afc
                            return lang(key.getMessageKey().getKey(), Bukkit.getPlayer(issuer.getUniqueId()));
                        }
                    };
                    this.locales.loadLanguages();
                }

                return this.locales;
            }
        };
        commandManager.registerDependency(ClanManager.class, clanManager);
        commandManager.registerDependency(SettingsManager.class, settingsManager);
        commandManager.registerDependency(StorageManager.class, storageManager);
        commandManager.registerDependency(PermissionsManager.class, permissionsManager);

        commandManager.enableUnstableAPI("help");
        commandManager.getCommandContexts().registerContext(Rank.class, context -> {
            if (context.getIssuer().isPlayer()) {
                Player player = context.getPlayer();
                Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
                if (clan == null) {
                    throw new InvalidCommandArgument(lang("not.a.member.of.any.clan", player));
                }
                String rank = context.popFirstArg();
                if (!clan.hasRank(rank)) {
                    throw new InvalidCommandArgument(RED + lang("rank.0.does.not.exist", player, rank));
                }
                return clan.getRank(rank);
            }
            throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
        });
        commandManager.getCommandContexts().registerContext(ClanInput.class, context -> {
            String arg = context.popFirstArg();
            Clan clan = clanManager.getClan(arg);
            if (clan == null) {
                throw new InvalidCommandArgument(RED + lang("the.clan.does.not.exist", context.getSender()));
            }
            return new ClanInput(clan);
        });
        commandManager.getCommandContexts().registerContext(ClanPlayerInput.class, context -> {
            String arg = context.popFirstArg();
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);
            ClanPlayer cp = clanManager.getAnyClanPlayer(offlinePlayer.getUniqueId());
            if (cp == null) {
                throw new InvalidCommandArgument(RED + lang("no.player.matched", context.getSender()));
            }
            return new ClanPlayerInput(cp);
        });
        commandManager.getCommandContexts().registerIssuerOnlyContext(Clan.class, context -> {
            if (context.getIssuer().isPlayer()) {
                Player player = context.getPlayer();
                Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
                if (clan == null) {
                    throw new InvalidCommandArgument(RED + lang("not.a.member.of.any.clan", player));
                }
                return clan;
            }
            throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
        });
        commandManager.getCommandContexts().registerIssuerOnlyContext(ClanPlayer.class, context -> {
            if (context.getIssuer().isPlayer()) {
                Player player = context.getPlayer();
                return clanManager.getCreateClanPlayer(player.getUniqueId());
            }
            throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
        });
        registerCommandCompletions();
        registerCommandConditions();
        addCommandReplacements();

        commandManager.registerCommand(new ClanCommand(), settingsManager.isForceCommandPriority());
        // TODO Force priority (other commands) (config)

        getLogger().info("Multithreading: " + SimpleClans.getInstance().getSettingsManager().getUseThreads());
        getLogger().info("BungeeCord: " + SimpleClans.getInstance().getSettingsManager().getUseBungeeCord());
        getLogger().info("Help us translate SimpleClans to your language! " +
                "Access https://crowdin.com/project/simpleclans/");

        startTasks();
        startMetrics();
        hookIntoPAPI();
        new UpdateChecker(this).check();
    }

    private void registerCommandCompletions() {
        commandManager.getCommandCompletions().registerAsyncCompletion("rank_permissions",
                c -> Arrays.asList(Helper.fromPermissionArray()));
        commandManager.getCommandCompletions().registerAsyncCompletion("clans", c ->
                clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList()));
        commandManager.getCommandCompletions().registerAsyncCompletion("clan_members", c -> {
            if (c.getIssuer().isPlayer()) {
                Player player = c.getPlayer();
                Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
                if (clan != null) {
                    return clan.getAllMembers().stream().map(ClanPlayer::getName)
                            .collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        });
        commandManager.getCommandCompletions().registerAsyncCompletion("ranks", context -> {
            if (context.getIssuer().isPlayer()) {
                Clan clan = clanManager.getClanByPlayerUniqueId(context.getPlayer().getUniqueId());
                if (clan != null) {
                    return clan.getRanks().stream().map(Rank::getName).collect(Collectors.toList());
                }
            }
            return Collections.emptyList();
        });
    }

    private void registerCommandConditions() {
        commandManager.getCommandConditions().addCondition("not_blacklisted", context -> {
            if (context.getIssuer().isPlayer()) {
                World world = context.getIssuer().getPlayer().getLocation().getWorld();
                if (world != null) {
                    if (settingsManager.isBlacklistedWorld(world.getName())) {
                        // TODO Check if it sends message
                        throw new ConditionFailedException();
                    }
                }
            }
        });
        commandManager.getCommandConditions().addCondition(ClanPlayerInput.class, "not_in_clan", (i, c, h) -> {
            if (h.getClanPlayer().getClan() != null) {
                throw new ConditionFailedException(RED + lang("the.player.is.already.member.of.another.clan",
                        c.getSender()));
            }
        });
        commandManager.getCommandConditions().addCondition(ClanPlayerInput.class, "not_banned", (i, c, h) -> {
            UUID uniqueId = h.getClanPlayer().getUniqueId();
            if (settingsManager.isBanned(uniqueId)) {
                throw new ConditionFailedException(RED + lang("this.player.is.banned.from.using.clan.commands",
                        c.getSender()));
            }
        });
        commandManager.getCommandConditions().addCondition("not_banned", context -> {
            if (!context.getIssuer().isPlayer()) {
                return;
            }
            Player player = context.getIssuer().getPlayer();
            if (settingsManager.isBanned(context.getIssuer().getUniqueId())) {
                throw new ConditionFailedException(lang("banned", player));
            }
        });
        commandManager.getCommandConditions().addCondition("verified", context -> {
            Player player = context.getIssuer().getPlayer();
            if (player != null) {
                Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
                if (clan != null) {
                    if (!clan.isVerified()) {
                        throw new ConditionFailedException(lang("clan.is.not.verified",
                                context.getIssuer().getIssuer()));
                    }
                } else {
                    throw new ConditionFailedException(lang("not.a.member.of.any.clan", player));
                }
            } else {
                throw new ConditionFailedException(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
            }
        });
        commandManager.getCommandConditions().addCondition(ClanPlayer.class, "same_clan", (context, execContext, value) -> {
            Player player = context.getIssuer().getPlayer();
            ClanPlayer clanPlayer = clanManager.getClanPlayer(player);
            if (clanPlayer == null) {
                throw new ConditionFailedException(lang("not.a.member.of.any.clan", player));
            }
            if (value == null || value.getClan() == null || !value.getClan().equals(clanPlayer.getClan())) {
                throw new ConditionFailedException(lang("the.player.is.not.a.member.of.your.clan", player));
            }
        });
        commandManager.getCommandConditions().addCondition(Player.class, "clan_member", (context, execContext, value) -> {
            Player player = context.getIssuer().getPlayer();
            if (clanManager.getClanPlayer(player) == null) {
                throw new ConditionFailedException(lang("not.a.member.of.any.clan", player));
            }
        });
        commandManager.getCommandConditions().addCondition("member_fee_enabled", context -> {
            if (!settingsManager.isMemberFee()) {
                throw new ConditionFailedException(ChatColor.RED + lang("disabled.command",
                        context.getIssuer().getIssuer()));
            }
        });
        commandManager.getCommandConditions().addCondition("leader", context -> {
            if (context.getIssuer().isPlayer()) {
                Player player = context.getIssuer().getPlayer();
                ClanPlayer cp = clanManager.getAnyClanPlayer(player.getUniqueId());
                if (cp == null || cp.getClan() == null) {
                    throw new ConditionFailedException(lang("not.a.member.of.any.clan", player));
                }
                if (!cp.isLeader()) {
                    throw new ConditionFailedException(ChatColor.RED + lang("no.leader.permissions", player));
                }
            } else {
                throw new ConditionFailedException(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
            }
        });
    }

    private void addCommandReplacements() {
        commandManager.getCommandReplacements().addReplacement("clan", getSettingsManager().getCommandClan());

        List<String> subcommands = Arrays.asList("setbanner", "resetkdr", "place", "rank", "home", "war",
                "mostkilled", "kills", "globalff", "reload", "unban", "ban", "verify", "disband", "resign", "ff",
                "clanff", "demote", "promote", "untrust", "trust", "purge", "fee", "bank", "kick", "invite", "toggle",
                "modtag", "bb", "clear", "rival", "ally", "add", "remove", "stats", "coords", "vitals", "rivalries",
                "alliances", "leaderboard", "allow", "block", "auto", "check", "assign", "unassign", "delete",
                "setdisplayname", "permissions", "tag", "deposit", "withdraw", "set",
                "lookup", "roster", "profile", "list", "create", "description");

        subcommands.forEach(s ->
                commandManager.getCommandReplacements().addReplacement(s, (lang(s + ".command") + "|" + s)));
    }

    private void hookIntoPAPI() {
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
			getLogger().info("PlaceholderAPI found. Registering hook...");
			new PlaceholdersManager(this).register();
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

    @NotNull
    public static String lang(@NotNull String key, @Nullable Player player, Object... arguments) {
        Locale locale = getInstance().getSettingsManager().getLanguage();
        if (player != null && instance.getSettingsManager().isLanguagePerPlayer()) {
            Locale playerLocale = Helper.getLocale(player);
            if (playerLocale != null) {
                locale = playerLocale;
            }
        }

        String message = ChatColor.translateAlternateColorCodes(
                '&', languageResource.getLang(key, locale));
        // contains acf placeholders like {commandprefix}
        if (Pattern.compile("\\{(?<key>[a-zA-Z]+?)}").matcher(message).find()) {
            return message;
        }
        return MessageFormat.format(message, arguments);
    }
    
    @NotNull
    public static String lang(@NotNull String key, @NotNull CommandSender sender, Object... arguments) {
    	if (sender instanceof Player) {
            return lang(key, (Player) sender, arguments);
        } else {
            return lang(key, null, arguments);
        }
    }

    @NotNull
    public static String lang(@NotNull String key, Object... arguments) {
        return lang(key, null, arguments);
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