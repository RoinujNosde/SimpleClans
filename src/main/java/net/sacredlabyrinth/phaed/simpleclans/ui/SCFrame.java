package net.sacredlabyrinth.phaed.simpleclans.ui;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

	public abstract int getSize();

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
