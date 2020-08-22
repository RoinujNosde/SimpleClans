package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 * @author RoinujNosde
 */
public class YAMLSerializer {

    private static final Logger LOGGER = SimpleClans.getInstance().getLogger();

    private YAMLSerializer() {
    }

    public static @Nullable String serialize(@Nullable ConfigurationSerializable cs) {
        String serialized = null;
        if (cs != null) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("cs", cs);
            serialized = config.saveToString();
        }

        return serialized;
    }

    public static <T extends ConfigurationSerializable> @Nullable T deserialize(@Nullable String cs,
                                                                                @NotNull Class<T> clazz) {
        YamlConfiguration config = new YamlConfiguration();
        if (cs != null) {
            try {
                config.loadFromString(cs);
                //noinspection unchecked
                return (T) config.get("cs");
            } catch (NoSuchMethodError ex) {
                LOGGER.warning("Error deserializing... Are you using the correct version?");
            } catch (Exception e) {
                LOGGER.warning(String.format(
                        "Error deserializing %s... Content: %s", clazz, cs));
            }
        }

        return null;
    }
}
