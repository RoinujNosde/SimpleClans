package net.sacredlabyrinth.phaed.simpleclans.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitLocales;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.PaperCommandManager;
import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.completions.AbstractAsyncCompletion;
import net.sacredlabyrinth.phaed.simpleclans.commands.completions.AbstractCompletion;
import net.sacredlabyrinth.phaed.simpleclans.commands.completions.AbstractStaticCompletion;
import net.sacredlabyrinth.phaed.simpleclans.commands.completions.AbstractSyncCompletion;
import net.sacredlabyrinth.phaed.simpleclans.commands.conditions.AbstractCommandCondition;
import net.sacredlabyrinth.phaed.simpleclans.commands.conditions.AbstractCondition;
import net.sacredlabyrinth.phaed.simpleclans.commands.conditions.AbstractParameterCondition;
import net.sacredlabyrinth.phaed.simpleclans.commands.contexts.AbstractContextResolver;
import net.sacredlabyrinth.phaed.simpleclans.commands.contexts.AbstractInputOnlyContextResolver;
import net.sacredlabyrinth.phaed.simpleclans.commands.contexts.AbstractIssuerOnlyContextResolver;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.optionalLang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class SCCommandManager extends PaperCommandManager {
    private final SimpleClans plugin;
    private static final List<String> SUBCOMMANDS;

    public SCCommandManager(@NotNull SimpleClans plugin) {
        super(plugin);
        this.plugin = plugin;
        configure();
    }
    
    private void configure() {
        enableUnstableAPI("help");
        registerDependencies();
        addCommandReplacements();
        registerContexts();
        registerCommands();
        registerConditions();
        registerCompletions();
    }

    private void registerDependencies() {
        registerDependency(ClanManager.class, plugin.getClanManager());
        registerDependency(SettingsManager.class, plugin.getSettingsManager());
        registerDependency(StorageManager.class, plugin.getStorageManager());
        registerDependency(PermissionsManager.class, plugin.getPermissionsManager());
        registerDependency(RequestManager.class, plugin.getRequestManager());
        registerDependency(ProtectionManager.class, plugin.getProtectionManager());
        registerDependency(ChatManager.class, plugin.getChatManager());
    }

    private void registerCompletions() {
        Set<Class<? extends AbstractCompletion>> completions =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.commands.completions",
                        AbstractCompletion.class);
        plugin.getLogger().info(String.format("Registering %d command completions...", completions.size()));
        for (Class<? extends AbstractCompletion> c : completions) {
            if (Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            try {
                AbstractCompletion obj = c.getConstructor(SimpleClans.class).newInstance(plugin);
                if (obj instanceof AbstractStaticCompletion) {
                    getCommandCompletions().registerStaticCompletion(obj.getId(),
                            ((AbstractStaticCompletion) obj).getCompletions());
                }
                if (obj instanceof AbstractAsyncCompletion) {
                    getCommandCompletions().registerAsyncCompletion(obj.getId(), (AbstractAsyncCompletion) obj);
                }
                if (obj instanceof AbstractSyncCompletion) {
                    getCommandCompletions().registerCompletion(obj.getId(), ((AbstractSyncCompletion) obj));
                }
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering completion", ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void registerConditions() {
        Set<Class<? extends AbstractCondition>> conditions =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.commands.conditions",
                        AbstractCondition.class);
        plugin.getLogger().info(String.format("Registering %d command conditions...", conditions.size()));
        for (Class<? extends AbstractCondition> c : conditions) {
            if (Modifier.isAbstract(c.getModifiers())) {
                continue;
            }
            try {
                AbstractCondition obj = c.getConstructor(SimpleClans.class).newInstance(plugin);
                if (obj instanceof AbstractParameterCondition) {
                    @SuppressWarnings("rawtypes")
                    AbstractParameterCondition condition = (AbstractParameterCondition) obj;
                    getCommandConditions().addCondition(condition.getType(), condition.getId(), condition);
                }
                if (obj instanceof AbstractCommandCondition) {
                    AbstractCommandCondition condition = (AbstractCommandCondition) obj;
                    getCommandConditions().addCondition(condition.getId(), condition);
                }
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering condition", ex);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerContexts() {
        Set<Class<? extends AbstractContextResolver>> resolvers =
                Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.commands.contexts",
                        AbstractContextResolver.class);
        plugin.getLogger().info(String.format("Registering %d command contexts...", resolvers.size()));
        for (Class<? extends AbstractContextResolver> cr : resolvers) {
            if (Modifier.isAbstract(cr.getModifiers())) {
                continue;
            }
            try {
                AbstractContextResolver obj = cr.getConstructor(SimpleClans.class).newInstance(plugin);
                if (obj instanceof AbstractIssuerOnlyContextResolver) {
                    getCommandContexts().registerIssuerOnlyContext(obj.getType(), ((AbstractIssuerOnlyContextResolver) obj));
                }
                if (obj instanceof AbstractInputOnlyContextResolver) {
                    getCommandContexts().registerContext(obj.getType(), ((AbstractInputOnlyContextResolver<?>) obj));
                }
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering context", ex);
            }
        }
    }

    private void registerCommands() {
        boolean forceCommandPriority = plugin.getSettingsManager().is(COMMANDS_FORCE_PRIORITY);
        Set<Class<? extends BaseCommand>> commands = Helper.getSubTypesOf("net.sacredlabyrinth.phaed.simpleclans.commands", BaseCommand.class);
        plugin.getLogger().info(String.format("Registering %d base commands...", commands.size()));
        for (Class<? extends BaseCommand> c : commands) {
            //ACF already registers nested classes
            if (c.isMemberClass() || Modifier.isStatic(c.getModifiers())) {
                continue;
            }
            try {
                BaseCommand baseCommand = c.getConstructor().newInstance();
                registerCommand(baseCommand, forceCommandPriority);
            } catch (Exception ex) {
                plugin.getLogger().log(Level.SEVERE, "Error registering command", ex);
            }
        }
    }

    private void addCommandReplacements() {
        SettingsManager sm = plugin.getSettingsManager();
        getCommandReplacements().addReplacements(
                "basic_conditions", "not_blacklisted|not_banned",
                "clan", sm.getString(COMMANDS_CLAN),
                "deny", sm.getString(COMMANDS_DENY) + "|deny",
                "more", sm.getString(COMMANDS_MORE),
                "ally_chat", sm.getString(COMMANDS_ALLY),
                "accept", sm.getString(COMMANDS_ACCEPT) + "|accept",
                "clan_chat", sm.getString(COMMANDS_CLAN_CHAT)
        );

        SUBCOMMANDS.forEach(s -> {
            String command = optionalLang(s + ".command", (ClanPlayer) null);
            if (command == null) {
                command = s;
            }
            command = command.replace(" ", "");
            String replacement = command.equals(s) ? s : command + "|" + s;
            getCommandReplacements().addReplacement(s, replacement);
        });
    }
    
    @Override
    public BukkitLocales getLocales() {
        if (this.locales == null) {
            this.locales = new BukkitLocales(this) {

                @Nullable
                private Player getPlayer(CommandIssuer issuer) {
                    if (issuer != null) {
                        return Bukkit.getPlayer(issuer.getUniqueId());
                    }
                    return null;
                }

                @Override
                @Nullable
                public String getOptionalMessage(CommandIssuer issuer, MessageKey key) {
                    return optionalLang(key.getKey(), getPlayer(issuer));
                }

                @Override
                @NotNull
                public String getMessage(CommandIssuer issuer, MessageKeyProvider key) {
                    return lang(key.getMessageKey().getKey(), getPlayer(issuer));
                }
            };
            //this.locales.loadLanguages();
        }
        return this.locales;
    }

    static {
        SUBCOMMANDS = Arrays.asList("setbanner", "resetkdr", "place", "rank", "home", "war", "regroup",
                "mostkilled", "kills", "globalff", "reload", "unban", "ban", "verify", "disband", "resign", "ff",
                "clanff", "demote", "promote", "untrust", "trust", "purge", "fee", "bank", "kick", "invite", "toggle",
                "modtag", "bb", "clear", "rival", "ally", "add", "remove", "stats", "coords", "vitals", "rivalries",
                "alliances", "leaderboard", "allow", "block", "auto", "check", "assign", "unassign", "delete", "me",
                "setdisplayname", "permissions", "tag", "deposit", "withdraw", "set", "status", "tp", "all", "everyone",
                "lookup", "roster", "profile", "list", "create", "description", "start", "end", "admin", "help", "mod",
                "setdefault", "removedefault", "land", "break", "interact", "place_block", "damage", "interact_entity",
                "container", "permanent", "take", "give", "join", "leave", "mute", "confirm");
    }
}
