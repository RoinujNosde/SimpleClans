package net.sacredlabyrinth.phaed.simpleclans;

import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author phaed
 */
public class Helper {

    private Helper() {
    }

    /**
     * Dumps stacktrace to log
     */
    @Deprecated
    public static void dumpStackTrace() {
        for (StackTraceElement el : Thread.currentThread().getStackTrace()) {
            SimpleClans.debug(el.toString());
        }
    }

    /**
     * @deprecated use {@link KDRFormat}
     */
    @Deprecated
    public static String formatKDR(float kdr) {
    	DecimalFormat formatter = new DecimalFormat("#.#");
    	return formatter.format(kdr);
    }

    @NotNull
    public static Locale forLanguageTag(@Nullable String languageTag) {
        Locale defaultLanguage = SimpleClans.getInstance().getSettingsManager().getLanguage();
        if (languageTag == null) {
            return defaultLanguage;
        }
        return Locale.forLanguageTag(languageTag);
    }
    
    /**
     * Gets the Player locale
     * 
     * @param player the player
     * @return the locale
     */
    @Nullable
    public static Locale getLocale(@NotNull Player player) {
        ClanPlayer clanPlayer = SimpleClans.getInstance().getClanManager().getAnyClanPlayer(player.getUniqueId());
        if (clanPlayer != null) {
            return clanPlayer.getLocale();
        }
        return null;
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
    
    /**
     * Parses a list of ranks from the specified JSONObject
     * 
     * @param jo the JSON Object
     * @return a list of ranks or null if the JSON String is null/empty
     */
	public static @Nullable List<Rank> ranksFromJson(JSONObject jo) {
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
    public static @Nullable String defaultRankFromJson(JSONObject jo) {
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
     * @param ranks the ranks
     * @param defaultRank the default rank
     * @return a JSON String
     */
    @SuppressWarnings("unchecked")
	public static String ranksToJson(List<Rank> ranks, @Nullable String defaultRank) {
    	if (ranks == null)
    		ranks = new ArrayList<Rank>();
    	
    	JSONArray array = new JSONArray();
    	for (Rank rank : ranks) {
    		JSONObject o = new JSONObject();
    		o.put("name", rank.getName());
    		o.put("displayName", rank.getDisplayName());
    		JSONArray permArray = new JSONArray();
    		for (String p : rank.getPermissions()) {
    			permArray.add(p);
    		}
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
     * @param hour hour
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
     * Check for integer
     *
     * @param o
     * @return
     */
    public static boolean isInteger(Object o) {
        return o instanceof java.lang.Integer;
    }

    /**
     * Check for byte
     *
     * @param input
     * @return
     */
    public static boolean isByte(String input) {
        try {
            Byte.parseByte(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Check for short
     *
     * @param input
     * @return
     */
    public static boolean isShort(String input) {
        try {
            Short.parseShort(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Check for integer
     *
     * @param input
     * @return
     */
    public static boolean isInteger(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Check for float
     *
     * @param input
     * @return
     */
    public static boolean isFloat(String input) {
        try {
            Float.parseFloat(input);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Check for string
     *
     * @param o
     * @return
     */
    public static boolean isString(Object o) {
        return o instanceof java.lang.String;
    }

    /**
     * Check for boolean
     *
     * @param o
     * @return
     */
    public static boolean isBoolean(Object o) {
        return o instanceof java.lang.Boolean;
    }

    /**
     * Remove a character from a string
     *
     * @param s
     * @param c
     * @return
     */
    public static String removeChar(String s, char c) {
        String r = "";

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                r += s.charAt(i);
            }
        }

        return r;
    }

    /**
     * Remove first character from a string
     *
     * @param s
     * @param c
     * @return
     */
    public static String removeFirstChar(String s, char c) {
        String r = "";

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                r += s.charAt(i);
                break;
            }
        }

        return r;
    }

    /**
     * Capitalize first word of sentence
     *
     * @param content
     * @return
     */
    public static String capitalize(String content) {
        if (content.length() < 2) {
            return content;
        }

        String first = content.substring(0, 1).toUpperCase();
        return first + content.substring(1);
    }

    /**
     * Return plural word if count is bigger than one
     *
     * @param count
     * @param word
     * @param ending
     * @return
     */
    public static String plural(int count, String word, String ending) {
        return count == 1 ? word : word + ending;
    }

    /**
     * Hex value to ChatColor
     *
     * @param hexValue
     * @return
     */
    @NotNull
    public static String toColor(@Nullable String hexValue) {
        if (hexValue == null) {
            return "";
        }
        if (hexValue.startsWith("&#")) {
            return ChatUtils.parseColors(hexValue);
        }

        ChatColor color = ChatColor.getByChar(hexValue);
        if (color == null) {
            return "";
        }
        return color.toString();
    }

    /**
     * Converts string array to ArrayList<String>, remove empty strings
     *
     * @param values
     * @return
     */
    public static List<String> fromArray(String... values) {
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
    public static Set<String> fromArray2(String... values) {
        HashSet<String> results = new HashSet<>();
        Collections.addAll(results, values);
        results.remove("");
        return results;
    }

    /**
     * Converts a player array to ArrayList<Player>
     *
     * @param values
     * @return
     */
    public static List<Player> fromPlayerArray(Player... values) {
        List<Player> results = new ArrayList<>();
        Collections.addAll(results, values);
        return results;
    }

    /**
     * Converts ArrayList<String> to string array
     *
     * @param list
     * @return
     */
    public static String[] toArray(List<String> list) {
        return list.toArray(new String[list.size()]);
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
     * Removes first item from a string array
     *
     * @param args
     * @return
     */
    public static String[] removeFirst(String[] args) {
        List<String> out = fromArray(args);

        if (!out.isEmpty()) {
            out.remove(0);
        }
        return toArray(out);
    }

    /**
     * Converts a string array to a space separated string
     *
     * @param args
     * @return
     */
    public static String toMessage(String[] args) {
        StringBuilder out = new StringBuilder();

        for (String arg : args) {
            out.append(arg).append(" ");
        }

        return out.toString().trim();
    }

    /**
     * Converts a string array to a string with custom separators
     *
     * @param args
     * @param sep
     * @return
     */
    public static String toMessage(String[] args, String sep) {
        String out = "";

        for (String arg : args) {
            out += arg + ", ";
        }

        return stripTrailing(out, ", ");
    }

    /**
     * Converts a string array to a string with custom separators
     *
     * @param args
     * @param sep
     * @return
     */
    public static String toMessage(List<String> args, String sep) {
        String out = "";

        for (String arg : args) {
            out += arg + sep;
        }

        return stripTrailing(out, sep);
    }

    /**
     * @deprecated use {@link ChatUtils#parseColors(String)}
     */
    @Deprecated
    public static String parseColors(String msg) {
        return ChatUtils.parseColors(msg);
    }

    /**
     * Removes color codes from strings
     *
     * @deprecated use {@link ChatUtils#stripColors(String)}
     */
    @Deprecated
    public static String stripColors(String msg) {
        return ChatUtils.stripColors(msg);
    }

    /*
     * Retrieves the last color code @param msg @return
     */
    /**
     * @param msg
     * @return
     */
    public static String getLastColorCode(String msg) {
        msg = msg.replaceAll(String.valueOf((char) 194), "").trim();

        if (msg.length() < 2) {
            return "";
        }

        String one = msg.substring(msg.length() - 2, msg.length() - 1);
        String two = msg.substring(msg.length() - 1);

        if (one.equals("\u00a7")) {
            return one + two;
        }

        if (one.equals("&")) {
            return Helper.toColor(two);
        }

        return "";
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
     * Removes trailing separators
     *
     * @param msg
     * @param sep
     * @return
     */
    public static String stripTrailing(String msg, String sep) {
        if (msg.length() < sep.length()) {
            return msg;
        }

        String out = msg;
        String first = msg.substring(0, sep.length());
        String last = msg.substring(msg.length() - sep.length());

        if (first.equals(sep)) {
            out = msg.substring(sep.length());
        }

        if (last.equals(sep)) {
            out = msg.substring(0, msg.length() - sep.length());
        }

        return out;
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
     * Check whether a player is online
     *
     * @param playerName
     * @return
     */
    @Deprecated
    public static boolean isOnline(String playerName) {
        Collection<Player> online = getOnlinePlayers();

        for (Player o : online) {
            if (o.getName().equalsIgnoreCase(playerName)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check whether a player is online
     *
     * @param playerUniqueId
     * @return
     */
    public static boolean isOnline(UUID playerUniqueId) {
        Collection<Player> online = getOnlinePlayers();

        for (Player o : online) {
            if (o.getUniqueId().equals(playerUniqueId)) {
                return true;
            }
        }

        return false;
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
     * Test if a url is valid
     *
     * @param strUrl
     * @return
     */
    public static boolean testURL(String strUrl) {
        try {
            URL url = new URL(strUrl);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.connect();

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }
        } catch (IOException e) {
            return false;
        }

        return true;
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
     * Whether the two locations refer to the same location, ignoring pitch and
     * yaw
     *
     * @param loc
     * @param loc2
     * @return
     */
    public static boolean isSameLocation(Location loc, Location loc2) {
        return loc.getX() == loc2.getX() && loc.getY() == loc2.getY() && loc.getZ() == loc2.getZ();
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
     * @deprecated use {@link VanishUtils#isVanished(CommandSender, Player)}
     */
    @Deprecated
    @Contract("_, null -> false")
    public static boolean isVanished(@Nullable CommandSender viewer, @Nullable Player player) {
        if (isVanished(player)) {
            return true;
        }
        if (viewer instanceof Player && player != null) {
            return !((Player) viewer).canSee(player);
        }

        return false;
    }

    /**
     * @deprecated use {@link VanishUtils#isVanished(Player)}
     */
    @Deprecated
    @Contract("null -> false")
    public static boolean isVanished(@Nullable Player player) {
        if (player != null && player.hasMetadata("vanished") && !player.getMetadata("vanished").isEmpty()) {
            return player.getMetadata("vanished").get(0).asBoolean();
        }
        return false;
    }

    @SuppressWarnings("unchecked")
	public static Collection<Player> getOnlinePlayers() {
        try {
            Method method = Bukkit.class.getDeclaredMethod("getOnlinePlayers");
            Object players = method.invoke(null);

            if (players instanceof Player[]) {
                return new ArrayList<>(Arrays.asList((Player[]) players));
            } else {
                return (Collection<Player>) players;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    @Deprecated
    @Nullable
    public static Player getPlayer(String playerName) {
    	return Bukkit.getPlayerExact(playerName);
    }

    /**
     * Formats the ally chat
     * 
     * @param cp Sender
     * @param msg The message
     * @param placeholders The placeholders
     * @return The formated message
     */
    public static String formatAllyChat(ClanPlayer cp, String msg, Map<String, String> placeholders) {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();

        String leaderColor = sm.getAllyChatLeaderColor();
        String memberColor = sm.getAllyChatMemberColor();
        String rank = cp.getRankId().isEmpty() ? null : ChatUtils.parseColors(cp.getRankDisplayName());
        String rankFormat = rank != null ? ChatUtils.parseColors(sm.getAllyChatRank()).replace("%rank%", rank) : "";

        String message = replacePlaceholders(sm.getAllyChatFormat(), cp, leaderColor, memberColor, rankFormat, msg);
        if (placeholders != null) {
            for (Entry<String, String> e : placeholders.entrySet()) {
                message = message.replace("%"+e.getKey()+"%", e.getValue());
            }
        }
        return message;
    }

    private static String replacePlaceholders(String messageFormat,
                                              ClanPlayer cp,
                                              String leaderColor,
                                              String memberColor,
                                              String rankFormat,
                                              String msg) {
        return ChatUtils.parseColors(messageFormat)
                .replace("%clan%", Objects.requireNonNull(cp.getClan()).getColorTag())
                .replace("%nick-color%", (cp.isLeader() ? leaderColor : memberColor))
                .replace("%player%", cp.getName())
                .replace("%rank%", rankFormat)
                .replace("%message%", msg);
    }

    /**
     * Formats the clan chat
     * 
     * @param cp Sender
     * @param msg The message
     * @param placeholders The placeholders
     * @return The formated message
     */
    public static String formatClanChat(ClanPlayer cp, String msg, Map<String, String> placeholders) {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();

        String leaderColor = sm.getClanChatLeaderColor();
        String memberColor = sm.getClanChatMemberColor();
        String rank = cp.getRankId().isEmpty() ? null : ChatUtils.parseColors(cp.getRankDisplayName());
        String rankFormat = rank != null ? ChatUtils.parseColors(sm.getClanChatRank()).replace("%rank%", rank) : "";

        String message = replacePlaceholders(sm.getClanChatFormat(), cp, leaderColor, memberColor, rankFormat, msg);
        
        if (placeholders != null) {
            for (Entry<String, String> e : placeholders.entrySet()) {
                message = message.replace("%"+e.getKey()+"%", e.getValue());
            }
        }
        return message;
    }

    /**
     * Formats the chat in a way that the Clan Tag is always there, so infractors can be easily identified
     * 
     * @param cp Sender
     * @param msg The chat message
     * @return The formatted message
     */
    public static String formatSpyClanChat(ClanPlayer cp, String msg) {
        msg = stripColors(msg);
        
        if (msg.contains(stripColors(cp.getClan().getColorTag()))) {
            return ChatColor.DARK_GRAY + msg;
        } else {
            return ChatColor.DARK_GRAY + "[" + cp.getTag() + "] " + msg;
        }
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
}
