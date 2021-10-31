package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils.stripColors;

/**
 * @author phaed
 */

public class Helper {

    private Helper() {
    }

    @NotNull
    public static Locale forLanguageTag(@Nullable String languageTag) {
        Locale defaultLanguage = SimpleClans.getInstance().getSettingsManager().getLanguage();
        if (languageTag == null) {
            return defaultLanguage;
        }
        return Locale.forLanguageTag(languageTag);
    }

    public static @Nullable JSONObject parseJson(String json) {
        if (json != null && !json.isEmpty()) {
            try {
                return (JSONObject) new JSONParser().parse(json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Set<Path> getPathsIn(String path, Predicate<? super Path> filter) {
        Set<Path> files = new LinkedHashSet<>();
        String packagePath = path.replace(".", "/");

        try {
            URI uri = SimpleClans.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri), Collections.emptyMap());
            files = Files.walk(fileSystem.getPath(packagePath)).
                    filter(Objects::nonNull).
                    filter(filter).
                    collect(Collectors.toSet());
            fileSystem.close();
        } catch (URISyntaxException | IOException ex) {
            SimpleClans.getInstance().getLogger().
                    log(Level.WARNING, "An error occurred while trying to load files: " + ex.getMessage(), ex);
        }

        return files;
    }

    public static Set<Class<?>> getClasses(String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();

        Predicate<? super Path> filter = entry -> {
            String path = entry.getFileName().toString();
            return !path.contains("$") && path.endsWith(".class");
        };

        for (Path filesPath : getPathsIn(packageName, filter)) {
            // Compatibility with different Java versions
            String path = filesPath.toString();
            if (path.charAt(0) == '/') {
                path = path.substring(1);
            }

            String fileName = path.replace("/", ".").split(".class")[0];

            try {
                Class<?> clazz = Class.forName(fileName);
                classes.add(clazz);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        return classes;
    }

    @SuppressWarnings("unchecked")
    public static <T> Set<Class<? extends T>> getSubTypesOf(String packageName, Class<?> type) {
        return getClasses(packageName).stream().
                filter(type::isAssignableFrom).
                map(aClass -> ((Class<? extends T>) aClass)).
                collect(Collectors.toSet());
    }

    /**
     * Parses a list of ranks from the specified JSONObject
     *
     * @param jo the JSON Object
     * @return a list of ranks or null if the JSON String is null/empty
     */
    public static @Nullable List<Rank> ranksFromJson(@Nullable JSONObject jo) {
        if (jo != null && !jo.isEmpty()) {
            Object ranks = jo.get("ranks");
            if (ranks != null) {
                JSONArray array = (JSONArray) ranks;
                List<Rank> rankList = new ArrayList<>();
                for (Object o : array) {
                    JSONObject r = (JSONObject) o;
                    String name = (String) r.get("name");
                    String displayName = (String) r.get("displayName");
                    Set<String> permissions = new HashSet<>();
                    for (Object p : (JSONArray) r.get("permissions")) {
                        permissions.add((String) p);
                    }
                    Rank rank = new Rank(name, displayName, permissions);
                    rankList.add(rank);
                }

                return rankList;
            }
        }
        return null;
    }

    /**
     * Parses the default rank from the specified JSONObject
     *
     * @param jo the JSON object
     * @return the default rank or null if not found and/or it does not exist
     */
    public static @Nullable String defaultRankFromJson(@Nullable JSONObject jo) {
        if (jo != null && !jo.isEmpty()) {
            if (!jo.containsKey("defaultRank")) {
                return null;
            } else {
                return (String) jo.get("defaultRank");
            }
        }
        return null;
    }

    /**
     * Converts a list of ranks and the default rank to a JSON String
     *
     * @param ranks       the ranks
     * @param defaultRank the default rank
     * @return a JSON String
     */
    @SuppressWarnings("unchecked")
    public static String ranksToJson(List<Rank> ranks, @Nullable String defaultRank) {
        if (ranks == null)
            ranks = new ArrayList<>();

        JSONArray array = new JSONArray();
        for (Rank rank : ranks) {
            JSONObject o = new JSONObject();
            o.put("name", rank.getName());
            o.put("displayName", rank.getDisplayName());
            JSONArray permArray = new JSONArray();
            permArray.addAll(rank.getPermissions());
            o.put("permissions", permArray);
            array.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("ranks", array);
        object.put("defaultRank", defaultRank);
        return object.toJSONString();
    }

    /**
     * Converts a resign times map to a JSON String
     *
     * @param resignTimes
     * @return a JSON String
     */
    public static String resignTimesToJson(Map<String, Long> resignTimes) {
        return JSONObject.toJSONString(resignTimes);
    }

    /**
     * Converts a JSON String to a resign times map
     *
     * @param json JSON String
     * @return a map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Long> resignTimesFromJson(String json) {
        if (json != null) {
            try {
                return (Map<String, Long>) new JSONParser().parse(json);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns the delay in seconds to the specified hour and minute.
     *
     * @param hour   hour
     * @param minute minute
     * @return the delay in seconds
     */
    public static long getDelayTo(int hour, int minute) {
        if (hour < 0 || hour > 23) hour = 1;
        if (minute < 0 || minute > 59) minute = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime d = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute));
        long delay;
        if (now.isAfter(d)) {
            delay = now.until(d.plusDays(1), ChronoUnit.SECONDS);
        } else {
            delay = now.until(d, ChronoUnit.SECONDS);
        }
        return delay;
    }

    /**
     * Get a players full color name if he is online
     *
     * @param playerName
     * @return
     */
    public static String getColorName(String playerName) {
        List<Player> players = SimpleClans.getInstance().getServer().matchPlayer(playerName);

        if (players.size() == 1) {
            PermissionsManager pm = SimpleClans.getInstance().getPermissionsManager();
            return pm.getPrefix(players.get(0)) + players.get(0).getName() + pm.getSuffix(players.get(0));
        }

        return playerName;
    }

    /**
     * Converts string array to ArrayList<String>, remove empty strings
     *
     * @param values
     * @return
     */
    public static List<String> fromArrayToList(String... values) {
        List<String> results = new ArrayList<>();
        Collections.addAll(results, values);
        results.remove("");
        return results;
    }

    /**
     * Converts string array to HashSet<String>, remove empty strings
     *
     * @param values
     * @return
     */
    public static Set<String> fromArrayToSet(String... values) {
        HashSet<String> results = new HashSet<>();
        Collections.addAll(results, values);
        results.remove("");
        return results;
    }

    /**
     * Converts ArrayList<String> to string array
     *
     * @param list
     * @return
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[0]);
    }

    /**
     * Converts the Permission values array to a String array
     *
     * @return
     */
    public static String[] fromPermissionArray() {
        RankPermission[] permissions = RankPermission.values();
        String[] sa = new String[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            sa[i] = permissions[i].toString();
        }
        return sa;
    }

    /**
     * Cleans up the tag from color codes and makes it lowercase
     *
     * @param tag
     * @return
     */
    public static String cleanTag(String tag) {
        return stripColors(tag).toLowerCase();
    }

    /**
     * Generates page separator line
     *
     * @param sep
     * @return
     */
    public static String generatePageSeparator(String sep) {
        String out = "";

        for (int i = 0; i < 320; i++) {
            out += sep;
        }
        return out;
    }

    /**
     * Remove offline players from a ClanPlayer array
     *
     * @param in
     * @return
     */
    public static List<ClanPlayer> stripOffLinePlayers(List<ClanPlayer> in) {
        List<ClanPlayer> out = new ArrayList<>();

        for (ClanPlayer cp : in) {
            if (cp.toPlayer() != null) {
                out.add(cp);
            }
        }

        return out;
    }

    /**
     * Escapes single quotes
     *
     * @param str
     * @return
     */
    public static String escapeQuotes(@Nullable String str) {
        if (str == null) {
            return "";
        }
        return str.replace("'", "''");
    }

    /**
     * Returns a prettier coordinate, does not include world
     *
     * @param loc
     * @return
     */
    public static String toLocationString(Location loc) {
        return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + loc.getWorld().getName();
    }

    /**
     * Whether the two locations refer to the same block
     *
     * @param loc
     * @param loc2
     * @return
     */
    public static boolean isSameBlock(Location loc, Location loc2) {
        return loc.getBlockX() == loc2.getBlockX() && loc.getBlockY() == loc2.getBlockY() && loc.getBlockZ() == loc2.getBlockZ();
    }

    /**
     * Sorts a Map by value
     *
     * @param map the Map to sort
     * @return the Map sorted
     */
    public static <K, V extends Comparable<V>> Map<K, V> sortByValue(Map<K, V> map) {
        LinkedList<Map.Entry<K, V>> entryList = new LinkedList<>(map.entrySet());
        entryList.sort(Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Entry<K, V> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Formats max inactive days to an infinity symbol if it's negative or 0
     *
     * @param max inactive days
     * @return formatted message
     */
    public static String formatMaxInactiveDays(int max) {
        if (max <= 0) {
            return "∞";
        } else {
            return String.valueOf(max);
        }
    }

    @NotNull
    public static String getFormattedClanStatus(Clan clan, CommandSender sender) {
        ArrayList<String> statuses = new ArrayList<>();
        if (clan.isPermanent()) {
            statuses.add(lang("permanent", sender));
        }
        if (clan.isVerified()) {
            statuses.add(lang("verified", sender));
        } else {
            statuses.add(lang("unverified", sender));
        }
        return String.join(", ", statuses);
    }

    /**
     * Checks if all passed materials are some kind of AIR
     *
     * @param materials materials to test
     * @return true if all materials are AIR
     */
    public static boolean isAir(@NotNull Material... materials) {
        for (Material m : materials) {
            if (!m.name().contains("AIR")) {
                return false;
            }
        }
        return true;
    }
}
