package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * 
 * @author RoinujNosde
 *
 */
public abstract class SCFrame {

	private final SCFrame parent;
	private final Player viewer;
	private final Set<SCComponent> components = ConcurrentHashMap.newKeySet();
	private FileConfiguration config;
	
	public SCFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
		this.parent = parent;
		this.viewer = viewer;
	}

	@NotNull
	public String getTitle() {
		return lang(getConfig().getString("title", getClass().getSimpleName()), getViewer());
	}

	@NotNull
	public Player getViewer() {
		return viewer;
	}

	@Nullable
	public SCFrame getParent() {
		return parent;
	}

	public int getSize() {
		return getConfig().getInt("rows", 3) * 9;
	}

	public abstract void createComponents();

	@Nullable
	public SCComponent getComponent(int slot) {
		for (SCComponent c : getComponents()) {
			if (c.getSlot() == slot) {
				return c;
			}
		}
		return null;
	}
	
	public void add(@NotNull SCComponent c) {
		components.add(c);
	}

	public void clear() {
		components.clear();
	}

	@NotNull
	public Set<SCComponent> getComponents() {
		return components;
	}

	protected FileConfiguration getConfig() {
		if (config == null) {
			return config = readConfig();
		}
		return config;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof SCFrame) {
			SCFrame otherFrame = (SCFrame) other;
			return getSize() == otherFrame.getSize() && getTitle().equals(otherFrame.getTitle())
					&& getComponents().equals(otherFrame.getComponents());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getTitle().hashCode() + Integer.hashCode(getSize()) + getComponents().hashCode();
	}

	protected FileConfiguration readConfig() {
		SimpleClans plugin = SimpleClans.getInstance();

		String configPath = getConfigPath();
		File externalFile = new File(plugin.getDataFolder(), configPath);
		InputStream resource = plugin.getResource(configPath);
		YamlConfiguration config = YamlConfiguration.loadConfiguration(externalFile);
		if (resource != null) {
			YamlConfiguration defaults = YamlConfiguration.loadConfiguration(new InputStreamReader(resource));
			config.setDefaults(defaults);
			if (!externalFile.exists()) {
				try {
					defaults.save(externalFile);
				} catch (IOException e) {
					plugin.getLogger().log(Level.SEVERE, String.format("Error saving defaults to %s", configPath), e);
				}
			}
		}
		return config;
	}

	private String getConfigPath() {
		String name = getClass().getName().replace("net.sacredlabyrinth.phaed.simpleclans.ui.frames.", "");

		return ("frames." + name).replace(".", File.separator) + ".yml";
	}

}
