package net.sacredlabyrinth.phaed.simpleclans.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.sacredlabyrinth.phaed.simpleclans.RankPermission;

/**
 * Class that represents a button
 *
 * @author RoinujNosde
 *
 */
public abstract class SCComponent {

	private boolean enabled = true;
	private final HashMap<ClickType, Runnable> listeners = new HashMap<>();
	private final HashMap<ClickType, Object> permissions = new HashMap<>();
	private final Set<ClickType> verified = new HashSet<>();
	private final Set<ClickType> confirmationRequired = new HashSet<>();
	private @Nullable Object lorePermission;

	@NotNull
	public abstract ItemStack getItem();

	public abstract int getSlot();

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Nullable
	public ItemMeta getItemMeta() {
		return getItem().getItemMeta();
	}

	public void setItemMeta(@NotNull ItemMeta itemMeta) {
		getItem().setItemMeta(itemMeta);
	}

	public void setVerifiedOnly(@NotNull ClickType clickType) {
		verified.add(clickType);
	}

	public boolean isVerifiedOnly(@NotNull ClickType clickType) {
		return verified.contains(clickType);
	}

	public void setLorePermission(@Nullable RankPermission permission) {
		lorePermission = permission;
	}

	public void setLorePermission(@Nullable String permission) {
		lorePermission = permission;
	}

	@Nullable
	public Object getLorePermission() {
		return lorePermission;
	}

	public void setPermission(@NotNull ClickType click, @Nullable RankPermission permission) {
		permissions.put(click, permission);
	}

	public void setPermission(@NotNull ClickType click, @Nullable String permission) {
		permissions.put(click, permission);
	}

	@Nullable
	public Object getPermission(@NotNull ClickType click) {
		return permissions.get(click);
	}

	public void setListener(@NotNull ClickType click, @Nullable Runnable listener) {
		listeners.put(click, listener);
	}

	@Nullable
	public Runnable getListener(@NotNull ClickType click) {
		return listeners.get(click);
	}

	public void setConfirmationRequired(@NotNull ClickType click) {
		confirmationRequired.add(click);
	}

	public boolean isConfirmationRequired(@NotNull ClickType click) {
		return confirmationRequired.contains(click);
	}
}
