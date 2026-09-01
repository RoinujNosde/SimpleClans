package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Collection;

/**
 * 
 * @author RoinujNosde
 *
 */
public abstract class SCFrame {

	private final SCFrame parent;
	private final Player viewer;
	private final Set<SCComponent> components = ConcurrentHashMap.newKeySet();

	public SCFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
		this.parent = parent;
		this.viewer = viewer;
	}

	@NotNull
	public abstract String getTitle();

	@NotNull
	public Player getViewer() {
		return viewer;
	}

	@Nullable
	public SCFrame getParent() {
		return parent;
	}

	public int getSize() {
		return getConfig().getInt("rows", 6) * 9;
	}

	@OverridingMethodsMustInvokeSuper
	public void createComponents() {
		SCComponent back = new SCComponentImpl.Builder(getConfig(), "back")
				.withViewer(getViewer())
				.withDisplayNameKey("gui.back.title").build();
		back.setListener(ClickType.LEFT, () -> InventoryDrawer.open(getParent()));
		add(back);

		ConfigurationSection decorSection = getConfig().getConfigurationSection("components.decorations");
		if (decorSection != null) {
			for (String key : decorSection.getKeys(false)) {
				addAll(new SCComponentImpl.ListBuilder<>(getConfig(), "decorations." + key)
						.withViewer(getViewer())
						.withDisplayNameKey(" ").build());
			}
		}
	}

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
		SCComponent old = getComponent(c.getSlot());
		if (old != null) {
			components.remove(old);
		}
		components.add(c);
	}

	public void addAll(@NotNull Collection<SCComponent> collection) {
		collection.forEach(this::add);
	}

	public void clear() {
		components.clear();
	}

	public void update() {
		InventoryDrawer.open(this);
	}

	@NotNull
	public Set<SCComponent> getComponents() {
		return components;
	}

	protected FileConfiguration getConfig() {
		return SimpleClans.getInstance().getSettingsManager().getConfig(getClass());
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

}
