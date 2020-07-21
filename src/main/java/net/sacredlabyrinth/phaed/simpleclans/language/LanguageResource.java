package net.sacredlabyrinth.phaed.simpleclans.language;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;

import jdk.vm.ci.meta.Local;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;

/**
 * 
 * @author RoinujNosde
 *
 */
public class LanguageResource {

	private static ResourceLoader loader = new ResourceLoader(SimpleClans.getInstance().getDataFolder());
	private Locale defaultLocale;

	public LanguageResource() {
		this.defaultLocale = SimpleClans.getInstance().getSettingsManager().getLanguage();
	}

	public String getLang(String key, Locale locale) {
		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale, loader,
					new ResourceControl(defaultLocale));
			return bundle.getString(key);
		} catch (MissingResourceException ignored) {}

		try {
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale,
					SimpleClans.getInstance().getClass().getClassLoader(), new ResourceControl(defaultLocale));

			return bundle.getString(key);
		} catch (MissingResourceException ignored) {
		}

		return "Missing language key: " + key;
	}

	public static void clearCache() {
		ResourceBundle.clearCache(loader);
	}

	public static List<Locale> getAvailableLocales() {
		ArrayList<Locale> locales = new ArrayList<>();
		try {
			URI uri = LanguageResource.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			FileSystem fileSystem = FileSystems.newFileSystem(URI.create("jar:" + uri.toString()), Collections.<String, Object>emptyMap());
			Files.walk(fileSystem.getPath("/")).forEach(p -> {
				String name = p.getFileName() == null ? "" : p.getFileName().toString();
				if (name.startsWith("messages") && name.endsWith(".properties")) {
					if (name.equalsIgnoreCase("messages.properties")) {
						locales.add(Locale.ENGLISH);
					} else {
						String[] extensionSplit = name.split(".properties");
						String[] split = extensionSplit[0].split("_");
						if (split.length == 2) {
							locales.add(new Locale(split[1]));
						} else {
							locales.add(new Locale(split[1], split[2]));
						}
					}
				}
			});
			fileSystem.close();
		} catch (URISyntaxException | IOException e) {
			SimpleClans.getInstance().getLogger().log(Level.WARNING, "An error occurred while getting the available languages", e);
		}
		locales.sort(Comparator.comparing(Locale::getDisplayName));
		return locales;
	}

	static class ResourceLoader extends ClassLoader {

		private final File dataFolder;

		public ResourceLoader(File dataFolder) {
			this.dataFolder = dataFolder;
		}

		@Override
		public URL getResource(String name) {
			File file = new File(dataFolder, name);
			if (file.exists()) {
				try {
					return file.toURI().toURL();
				} catch (MalformedURLException ignored) {
				}
			}

			return null;
		}
	}

	static class ResourceControl extends ResourceBundle.Control {

		private final Locale defaultLocale;

		public ResourceControl(Locale defaultLocale) {
			this.defaultLocale = defaultLocale;

		}

		@Override
		public List<String> getFormats(String baseName) {
			if (baseName == null) {
				throw new NullPointerException();
			}

			return Collections.singletonList("java.properties");
		}

		@Override
		public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
				boolean reload) throws IllegalAccessException, InstantiationException, IOException {
			String bundleName = toBundleName(baseName, locale);

			ResourceBundle bundle = null;
			if (format.equals("java.properties")) {

				String resourceName = toResourceName(bundleName, "properties");

				if (resourceName == null) {
					return bundle;
				}
				URL url = loader.getResource(resourceName);
				if (url == null) {
					return bundle;
				}

				URLConnection connection = url.openConnection();
				if (reload) {

					connection.setUseCaches(false);
				}
				
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
				bundle = new PropertyResourceBundle(reader);
				reader.close();
			} else {
				throw new IllegalArgumentException("unknown format: " + format);
			}

			return bundle;
		}


		@Override
		public Locale getFallbackLocale(String baseName, Locale locale) {
			if (!locale.equals(defaultLocale) && !locale.equals(Locale.ROOT)) {
				return defaultLocale;
			}
			if (locale.equals(defaultLocale) && !defaultLocale.equals(Locale.ROOT)) {
				return Locale.ROOT;
			}

			return null;
		}
	}
}
