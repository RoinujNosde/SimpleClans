package net.sacredlabyrinth.phaed.simpleclans.language;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author RoinujNosde
 */
public class LanguageResource {

    private static final ResourceLoader DATA_FOLDER_LOADER = new ResourceLoader(SimpleClans.getInstance().getDataFolder());
    private final Locale defaultLocale;
    private static List<Locale> availableLocales;
    private static final Map<Locale, Integer> TRANSLATION_STATUS = new HashMap<>();

    public LanguageResource() {
        this.defaultLocale = SimpleClans.getInstance().getSettingsManager().getLanguage();
    }

    @Nullable
    public String getLang(@NotNull String key, @NotNull Locale locale) {
        if (!key.startsWith("acf-core.parameter.") && key.startsWith("acf")) {
            return getACFLang(key, locale);
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale, DATA_FOLDER_LOADER,
                    new ResourceControl(defaultLocale, false));
            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }
        if (locale.equals(Locale.ENGLISH)) {
            // English is the root messages.properties inside the jar
            locale = Locale.ROOT;
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("messages", locale,
                    SimpleClans.getInstance().getClass().getClassLoader(), new ResourceControl(defaultLocale));

            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        return null;
    }

    @Nullable
    private String getACFMinecraftLang(@NotNull String key, @NotNull Locale locale) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("acf-minecraft", locale, DATA_FOLDER_LOADER,
                    new ResourceControl(defaultLocale, false));

            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("acf-minecraft", locale,
                    SimpleClans.getInstance().getClass().getClassLoader(), new ResourceControl(defaultLocale));

            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        return null;
    }

    @Nullable
    private String getACFLang(@NotNull String key, @NotNull Locale locale) {
        if (key.startsWith("acf-minecraft")) {
            return getACFMinecraftLang(key, locale);
        }
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("acf-core", locale, DATA_FOLDER_LOADER,
                    new ResourceControl(defaultLocale, false));

            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("acf-core", locale,
                    SimpleClans.getInstance().getClass().getClassLoader(), new ResourceControl(defaultLocale));

            return bundle.getString(key);
        } catch (MissingResourceException ignored) {
        }

        return null;
    }

    public static int getTranslationStatus(@NotNull Locale locale) {
        if (locale.equals(Locale.ENGLISH)) {
            return 100;
        }
        Integer lines = TRANSLATION_STATUS.get(locale);
        Integer englishLines = TRANSLATION_STATUS.get(Locale.ENGLISH);
        if (lines == null || englishLines == null) {
            return -1;
        }

        return Math.round((lines / englishLines.floatValue()) * 100);
    }

    public static void clearCache() {
        ResourceBundle.clearCache(DATA_FOLDER_LOADER);
    }

    public static List<Locale> getAvailableLocales() {
        if (availableLocales != null) {
            return availableLocales;
        }
        loadAvailableLocales();
        return availableLocales;
    }

    private static void loadAvailableLocales() {
        ArrayList<Locale> locales = new ArrayList<>();

        Predicate<Path> filter = entry -> {
            String path = (entry.getFileName() != null) ? entry.getFileName().toString() : "";
            return path.startsWith("messages") && path.endsWith(".properties");
        };

        for (Path filePath : Helper.getPathsIn("/", filter)) {
            String fileName = filePath.getFileName().toString();
            Locale locale = getLocaleByFileName(fileName);
            locales.add(locale);
            loadTranslationStatus(locale, fileName);
        }

        locales.sort(Comparator.comparing(Locale::toLanguageTag));
        availableLocales = locales;
    }

    private static @NotNull Locale getLocaleByFileName(@NotNull String name) {
        Locale locale;
        if (name.equalsIgnoreCase("messages.properties")) {
            locale = Locale.ENGLISH;
        } else {
            String[] extensionSplit = name.split(".properties");
            String[] split = extensionSplit[0].split("_");
            if (split.length == 2) {
                locale = new Locale(split[1]);
            } else {
                locale = new Locale(split[1], split[2]);
            }
        }
        return locale;
    }

    private static void loadTranslationStatus(@NotNull Locale locale, @NotNull String fileName) {
        InputStream stream = SimpleClans.getInstance().getClass().getResourceAsStream("/" + fileName);
        if (stream == null) return;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        int lineCount = 0;
        try {
            while (true) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                if (!line.startsWith("#")) {
                    lineCount++;
                }
            }
        } catch (IOException ex) {
            lineCount = -1;
        }
        TRANSLATION_STATUS.put(locale, lineCount);
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
        private final boolean defaultAsFallback;

        public ResourceControl(@NotNull Locale defaultLocale) {
            this.defaultLocale = defaultLocale;
            this.defaultAsFallback = true;
        }

        public ResourceControl(@NotNull Locale defaultLocale, boolean defaultAsFallback) {
            this.defaultLocale = defaultLocale;
            this.defaultAsFallback = defaultAsFallback;
        }

        @Override
        public List<String> getFormats(String baseName) {
            if (baseName == null) {
                throw new NullPointerException();
            }

            return Collections.singletonList("java.properties");
        }

        @Override
        public List<Locale> getCandidateLocales(String baseName, Locale locale) {
            List<Locale> candidateLocales = new ArrayList<>(super.getCandidateLocales(baseName, locale));
            if (!defaultAsFallback && candidateLocales.size() != 1) {
                candidateLocales.remove(Locale.ROOT);
            }
            if (baseName.startsWith("acf") && !candidateLocales.contains(Locale.ENGLISH) && defaultAsFallback) {
                candidateLocales.add(Locale.ENGLISH);
            }
            return candidateLocales;
        }

        @Override
        @Nullable
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                                        boolean reload) throws IOException {
            String bundleName = toBundleName(baseName, locale);

            ResourceBundle bundle;
            if (format.equals("java.properties")) {

                String resourceName = toResourceName(bundleName, "properties");

                URL url = loader.getResource(resourceName);
                if (url == null) {
                    return null;
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
        @Nullable
        public Locale getFallbackLocale(String baseName, Locale locale) {
            if (!defaultAsFallback) {
                return null;
            }
            Locale root = baseName.startsWith("acf") ? Locale.ENGLISH : Locale.ROOT;
            if (!locale.equals(defaultLocale) && !locale.equals(root)) {
                return defaultLocale;
            }
            if (locale.equals(defaultLocale) && !defaultLocale.equals(root)) {
                return root;
            }

            return null;
        }
    }
}
