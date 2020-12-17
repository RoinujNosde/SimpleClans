package net.sacredlabyrinth.phaed.simpleclans.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

/**
 * 
 * @author RoinujNosde
 * @since 2.10.2
 */
public class UpdateChecker {

	private final SimpleClans plugin;
	private static final String LATEST_VERSION_URL = "https://api.spiget.org/v2/resources/71242/versions/latest";
	private final String version;
	private final String userAgent;

	public UpdateChecker(SimpleClans plugin) {
		this.plugin = plugin;
		version = plugin.getDescription().getVersion();
		userAgent = "SimpleClans/" + version;
	}

	/**
	 * Checks if the version installed is up-to-date
	 * 
	 */
	public void check() {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					URL url = new URL(LATEST_VERSION_URL);
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.addRequestProperty("User-Agent", userAgent);
					InputStreamReader reader = new InputStreamReader(connection.getInputStream());
					
					JsonElement parse = new JsonParser().parse(reader);
					if (parse.isJsonObject()) {
						String latestVersion = parse.getAsJsonObject().get("name").getAsString();

						if (compareVersions(version, latestVersion) < 0) {
							plugin.getLogger().info(String.format("You're running an outdated version (%s).", version));
							plugin.getLogger().info(String.format("The latest version is %s. Download it at:", latestVersion));
							plugin.getLogger().info("https://www.spigotmc.org/resources/simpleclans.71242/");
						}
						
					}
					
					reader.close();
				} catch (MalformedURLException ignored) {
				} catch (IOException | JsonParseException ex) {
					plugin.getLogger().log(Level.WARNING, "Error checking the plugin version...");
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public static int compareVersions(@NotNull String a, @NotNull String b) {
		String[] aSplit = a.split("\\.");
		String[] bSplit = b.split("\\.");
		int length = Math.max(aSplit.length, bSplit.length);
		for (int i = 0; i < length; i++) {
			int aPart = aSplit.length > i ? Integer.parseInt(aSplit[i]) : 0;
			int bPart = bSplit.length > i ? Integer.parseInt(bSplit[i]) : 0;
			if (aPart != bPart) {
				return Integer.compare(aPart, bPart);
			}
		}
		return 0;
	}

}
