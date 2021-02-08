package net.sacredlabyrinth.phaed.simpleclans.hooks.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleClansExpansion extends PlaceholderExpansion {

    private static final Pattern TOP_CLANS_PATTERN = Pattern.compile("(?<strip>^topclans_(?<position>\\d+)_)clan_");
    private static final Pattern TOP_PLAYERS_PATTERN = Pattern.compile("(?<strip>^topplayers_(?<position>\\d+)_)");
    private static final Map<String, PlaceholderResolver> RESOLVERS = new HashMap<>();
    private List<String> placeholders;
    private final SimpleClans plugin;
    private final ClanManager clanManager;

    public SimpleClansExpansion(SimpleClans plugin) {
        this.plugin = plugin;
        clanManager = plugin.getClanManager();
        registerResolvers();
    }

    @Override
    public @NotNull String getName() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getIdentifier() {
        return getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        if (placeholders == null) {
            this.placeholders = new ArrayList<>();
            addPlaceholders("simpleclans_", ClanPlayer.class, placeholders);
            addPlaceholders("simpleclans_clan_", Clan.class, placeholders);
        }
        return placeholders;
    }

    private void addPlaceholders(String prefix, Class<?> clazz, List<String> placeholders) {
        for (Method method : clazz.getDeclaredMethods()) {
            Placeholder[] annotations = method.getAnnotationsByType(Placeholder.class);
            for (Placeholder annotation : annotations) {
                placeholders.add("%" + prefix + annotation.value() + "%");
                //Commented because the list would be very long
                //placeholders.add("%simpleclans_topplayers_<number>_" + annotation.value() + "%");
            }
        }
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
        ClanPlayer cp = null;
        if (player != null) {
            cp = clanManager.getAnyClanPlayer(player.getUniqueId());
        }
        Clan clan = cp != null ? cp.getClan() : null;
        Matcher matcher = TOP_CLANS_PATTERN.matcher(params);
        if (matcher.find()) {
            int position = Integer.parseInt(matcher.group("position"));
            clan = getFromPosition(clanManager.getClans(), position, clanManager::sortClansByKDR);
            params = params.replace(matcher.group("strip"), "");
        }
        matcher = TOP_PLAYERS_PATTERN.matcher(params);
        if (matcher.find()) {
            int position = Integer.parseInt(matcher.group("position"));
            cp = getFromPosition(clanManager.getAllClanPlayers(), position, clanManager::sortClanPlayersByKDR);
            params = params.replace(matcher.group("strip"), "");
        }
        return getValue(player, cp, clan, params);
    }

    @Nullable
    private <T> T getFromPosition(List<T> list, int position, Consumer<List<T>> sort) {
        if (isPositionValid(list, position)) {
            sort.accept(list);
            return list.get(position - 1);
        }
        return null;
    }

    private boolean isPositionValid(@NotNull Collection<?> collection, int position) {
        return position >= 1 && position <= collection.size();
    }

    @NotNull
    private String getValue(@Nullable OfflinePlayer player, @Nullable ClanPlayer cp, @Nullable Clan clan,
                            @NotNull String placeholder) {
        if (placeholder.startsWith("clan_")) {
            placeholder = placeholder.replace("clan_", "");
            return getValue(player, clan, placeholder);
        }
        return getValue(player, cp, placeholder);
    }

    @NotNull
    private String getValue(@Nullable OfflinePlayer player, @Nullable Object object, @NotNull String placeholder) {
        if (object != null) {
            for (Method declaredMethod : object.getClass().getDeclaredMethods()) {
                Placeholder[] annotations = declaredMethod.getAnnotationsByType(Placeholder.class);
                for (Placeholder p : annotations) {
                    if (p.value().equals(placeholder)) {
                        return resolve(player, object, declaredMethod, p.resolver(), placeholder, p.config());
                    }
                }
            }
            plugin.getLogger().warning(String.format("Placeholder %s not found", placeholder));
        }
        return "";
    }

    private String resolve(@Nullable OfflinePlayer player, @NotNull Object object, @NotNull Method method,
                           @NotNull String resolverId, @NotNull String placeholder, @NotNull String config) {
        PlaceholderResolver resolver = RESOLVERS.get(resolverId);
        if (resolver != null) {
            return resolver.resolve(player, object, method, placeholder, getConfigMap(config));
        }
        plugin.getLogger().warning(String.format("Resolver %s for %s not found", resolverId, placeholder));
        return "";
    }

    @NotNull
    private Map<String, String> getConfigMap(@NotNull String config) {
        HashMap<String, String> map = new HashMap<>();
        String[] elements = config.split(",");
        for (String element : elements) {
            String[] keyAndValue = element.split(":");
            map.put(keyAndValue[0], keyAndValue.length > 1 ? keyAndValue[1] : null);
        }
        return map;
    }

    private void registerResolvers() {
        Reflections reflections = new Reflections("net.sacredlabyrinth.phaed.simpleclans.hooks.papi.resolvers");
        Set<Class<? extends PlaceholderResolver>> resolvers = reflections.getSubTypesOf(PlaceholderResolver.class);
        plugin.getLogger().info(String.format("Registering %d placeholder resolvers...", resolvers.size()));
        for (Class<? extends PlaceholderResolver> r : resolvers) {
            try {
                PlaceholderResolver resolver = r.getConstructor(SimpleClans.class).newInstance(plugin);
                RESOLVERS.put(resolver.getId(), resolver);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                    NoSuchMethodException e) {
                plugin.getLogger().log(Level.SEVERE, "Error registering placeholder resolver", e);
            }
        }
    }

}
