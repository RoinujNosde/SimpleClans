package net.sacredlabyrinth.phaed.simpleclans.uuid;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;

/**
 * @author evilmidget38 (previous author)
 * @see <a href="http://forums.bukkit.org/threads/250926/">Bukkit Thread</a>
 * @see <a href="http://web.archive.org/web/20140909143249/https://gist.github.com/evilmidget38/26d70114b834f71fb3b4">Github Gist</a>
 */
public final class UUIDFetcher {
    private static final String PROFILE_URL = "https://api.minetools.eu/uuid/";
    private static final String FALLBACK_PROFILE_URL = "https://api.mojang.com/users/profiles/minecraft/";
    private static final int BATCH_SIZE = 100;
    private static final Gson gson = new Gson();

    private UUIDFetcher() {
        // Private constructor to prevent instantiation
    }

    /**
     * Fetches UUIDs for a list of ClanPlayer objects.
     * This method extracts the names from the ClanPlayer objects and fetches their corresponding UUIDs.
     *
     * @param clanPlayers A list of ClanPlayer objects for which to fetch UUIDs
     * @return A Map where the keys are player names and the values are their corresponding UUIDs
     * @throws InterruptedException If the operation is interrupted while waiting
     * @throws ExecutionException   If the computation threw an exception
     */
    public static Map<String, UUID> fetchUUIDsForClanPlayers(List<ClanPlayer> clanPlayers) throws InterruptedException, ExecutionException {
        List<String> names = clanPlayers.stream().map(ClanPlayer::getName).collect(Collectors.toList());
        return fetchUUIDsConcurrently(names);
    }

    /**
     * Fetches the UUID for a single player name.
     * This method is a convenience wrapper around the batch UUID fetching process.
     *
     * @param name The name of the player whose UUID is to be fetched
     * @return The UUID of the specified player, or null if not found
     * @throws InterruptedException If the operation is interrupted while waiting
     * @throws ExecutionException   If the computation threw an exception
     */
    public static @Nullable UUID getUUIDOf(@NotNull String name) throws InterruptedException, ExecutionException {
        return fetchUUIDsConcurrently(singletonList(name)).get(name);
    }

    // Fetch UUIDs in batches with concurrency
    private static Map<String, UUID> fetchUUIDsConcurrently(List<String> names) throws InterruptedException, ExecutionException {
        Map<String, UUID> resultMap = new ConcurrentHashMap<>();
        ExecutorService executorService = Executors.newFixedThreadPool(10);  // Thread pool for parallel execution
        List<Callable<Map<String, UUID>>> tasks = new ArrayList<>();

        // Split the list of names into batches and create tasks
        for (int i = 0; i < names.size(); i += BATCH_SIZE) {
            List<String> batch = names.subList(i, Math.min(i + BATCH_SIZE, names.size()));
            tasks.add(createTask(batch));
        }

        // Execute all tasks in parallel
        List<Future<Map<String, UUID>>> futures = executorService.invokeAll(tasks);

        // Collect results from all batches
        for (Future<Map<String, UUID>> future : futures) {
            resultMap.putAll(future.get());  // Merge each batch result into the final result map
        }

        // Shutdown the executor service
        executorService.shutdownNow();
        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            SimpleClans.getInstance().getLogger().warning("Executor did not terminate in time.");
        }

        return resultMap;
    }

    // Create connection for each name
    private static HttpURLConnection createConnection(@NotNull URL url) throws IOException {
        var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setUseCaches(false);
        connection.setDoInput(true);

        return connection;
    }

    // Get UUID by name
    private static @NotNull UUID getUUID(@NotNull String id) {
        return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
    }

    // Handle single batch of names
    private static Map<String, UUID> fetchUUIDsForBatch(List<String> batch) {
        Map<String, UUID> uuidMap = new HashMap<>();

        for (String name : batch) {
            uuidMap.computeIfAbsent(name, k -> {
                try {
                    return tryFetchUUIDWithFallback(k);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return uuidMap;
    }

    private static @Nullable UUID tryFetchUUIDWithFallback(@NotNull String name) throws IOException {
        try {
            // Try the primary URL
            return fetchUUID(URI.create(PROFILE_URL + name).toURL());
        } catch (IOException e) {
            // If the primary URL fails, attempt the fallback URL
            SimpleClans.getInstance().getLogger().log(Level.WARNING,
                    String.format("Failed to fetch %s UUID by MineTools API. Trying to use Mojang API instead...", name), e);
            return fetchUUID(URI.create(FALLBACK_PROFILE_URL + name).toURL());
        }
    }

    private static @Nullable UUID fetchUUID(@NotNull URL url) throws IOException {
        HttpURLConnection connection = createConnection(url);
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException(String.format("Unexpected response code: %d. Response: %s",
                    connection.getResponseCode(), connection.getResponseMessage()));
        }

        JsonObject response = gson.fromJson(new InputStreamReader(connection.getInputStream(), UTF_8), JsonObject.class);

        if (!response.has("id")) {
            return null;
        }

        var id = response.get("id");
        var status = response.get("status");

        if (id.isJsonNull() ||
                (response.has("status") && status.getAsString().equals("ERR"))) {
            throw new IOException(String.format("Invalid UUID: %s", id));
        }

        return getUUID(id.getAsString());
    }

    // Callable task for batch processing
    private static Callable<Map<String, UUID>> createTask(List<String> batch) {
        return () -> fetchUUIDsForBatch(batch);
    }
}