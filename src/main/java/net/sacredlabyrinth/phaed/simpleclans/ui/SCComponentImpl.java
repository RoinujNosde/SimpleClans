package net.sacredlabyrinth.phaed.simpleclans.ui;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class SCComponentImpl extends SCComponent {

	@NotNull
	private ItemStack item;
	private int slot;

	private SCComponentImpl() {
		item = new ItemStack(Material.STONE);
		slot = 0;
	}

	public SCComponentImpl(@Nullable String displayName, @Nullable List<String> lore, @NotNull Material material,
						   int slot) {
		this(displayName, lore, new ItemStack(material), slot);
	}

	public SCComponentImpl(@Nullable String displayName, @Nullable List<String> lore, @NotNull XMaterial material,
						   int slot) {
		this(displayName, lore, material.parseItem(), slot);
	}

	public SCComponentImpl(@Nullable String displayName, @Nullable List<String> lore, @Nullable ItemStack item,
						   int slot) {
		if (item == null) {
			item = new ItemStack(Material.STONE);
		}
		this.item = item;
		ItemMeta itemMeta = item.getItemMeta();
		if (itemMeta != null) {
			itemMeta.setDisplayName(displayName);
			itemMeta.setLore(lore);
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			item.setItemMeta(itemMeta);
		}
		checkSlot(slot);
		this.slot = slot;
	}
	
	@Override
	public @NotNull ItemStack getItem() {
		return item;
	}

	@Override
	public int getSlot() {
		return slot;
	}

	private static void checkMaterial(String material) {
		if (material == null || material.isEmpty()) {
			throw new IllegalStateException("material cannot be null or empty");
		}
	}

	private static void checkSlot(int slot) {
		if (slot < 0) {
			throw new IllegalStateException("slot must be >= 0 and < 54");
		}
	}

	public static class Builder {
		private boolean enabled;
		private final ItemStack item;
		private String displayName;
		private int slot;
		private @Nullable List<String> lore;
		private Player viewer;

		public Builder(@NotNull XMaterial material) {
			this(material.parseItem());
		}

		@SuppressWarnings("unused")
		public Builder(@NotNull Material material) {
			this(new ItemStack(material));
		}

		public Builder(@Nullable ItemStack item) {
			if (item == null) {
				item = new ItemStack(Material.STONE);
			}
			this.item = item;
		}

		public Builder(FileConfiguration config, String id) {
			String materialName = config.getString("components." + id + ".material");
			checkMaterial(materialName);
			item = Objects.requireNonNull(XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE).parseItem());
			slot = config.getInt("components." + id + ".slot");
			checkSlot(slot);
			enabled = config.getBoolean("components." + id + ".enabled");
		}

		public Builder withViewer(@NotNull Player player) {
			this.viewer = player;
			return this;
		}

		public Builder withDisplayName(@Nullable String displayName) {
			this.displayName = displayName;
			return this;
		}

		public Builder withDisplayNameKey(@NotNull String key, Object... args) {
			if (viewer == null) throw new IllegalStateException("viewer is null");
			return withDisplayName(lang(key, viewer, args));
		}

		public Builder withLore(@Nullable List<String> lore) {
			this.lore = lore;
			return this;
		}

		public Builder withLoreLine(@NotNull String line) {
			if (lore == null) {
				lore = new ArrayList<>();
			}
			lore.add(line);
			return this;
		}

		public Builder withLoreKey(@NotNull String key, Object... args) {
			if (viewer == null) throw new IllegalStateException("viewer is null");
			return withLoreLine(lang(key, viewer, args));
		}

		public Builder withSlot(int slot) {
			checkSlot(slot);
			this.slot = slot;
			return this;
		}

		public SCComponent build() {
			SCComponentImpl component = new SCComponentImpl();
			component.item = item;
			component.setEnabled(enabled);
			ItemMeta itemMeta = item.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setLore(lore);
				itemMeta.setDisplayName(displayName);
				component.setItemMeta(itemMeta);
			}
			component.slot = slot;
			return component;
		}
	}

	public static class ListBuilder<T> {
		private final boolean enabled;
		private final XMaterial material;
		private Function<T, ItemStack> item;
		private Function<T, OfflinePlayer> owningPlayer;
		private final List<Integer> slots;
		private final List<T> elements;
		private Player viewer;
		private Function<T, String> displayName = (t) -> null;
		private final List<Function<T, String>> lore = new ArrayList<>();
		private final Map<ClickType, Function<T, Runnable>> listeners = new HashMap<>();
		private final Map<ClickType, String> bukkitPermissions = new HashMap<>();
		private final Map<ClickType, RankPermission> rankPermissions = new HashMap<>();
		private final Set<ClickType> confirmationRequired = new HashSet<>();
		private String lorePermission;

		public ListBuilder(@NotNull FileConfiguration config, @NotNull String id, @NotNull List<T> elements) {
			String materialName = config.getString("components." + id + ".material");
			checkMaterial(materialName);
			material = XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE);
			item = (t) -> Objects.requireNonNull(material.parseItem());
			slots = config.getIntegerList("components." + id + ".slots").stream().filter(i -> i >= 0 && i < 54)
					.collect(Collectors.toList());
			this.elements = elements;
			enabled = config.getBoolean("components." + id + ".enabled");
		}

		public ListBuilder(@NotNull FileConfiguration config, @NotNull String id) {
			this(config, id, new ArrayList<>());
			slots.forEach(e -> elements.add(null));
		}

		public ListBuilder<T> withItem(@NotNull Function<T, ItemStack> item) {
			this.item = (t) -> {
				ItemStack result = item.apply(t);
				return result == null ? material.parseItem() : result;
			};
			return this;
		}

		public ListBuilder<T> withViewer(@NotNull Player player) {
			viewer = player;
			return this;
		}

		@SafeVarargs
		public final ListBuilder<T> withDisplayNameKey(@NotNull String key, Function<T, Object>... args) {
			displayName = (t) -> processLang(key, t, args);
			return this;
		}

		@SafeVarargs
		public final ListBuilder<T> withLoreKey(@NotNull String key, Function<T, Object>... args) {
			lore.add((t) -> processLang(key, t, args));
			return this;
		}

		public final ListBuilder<T> withLoreLine(Function<T, String> line) {
			lore.add(line);
			return this;
		}

		public final ListBuilder<T> withOwningPlayer(Function<T, OfflinePlayer> owningPlayer) {
			this.owningPlayer = owningPlayer;
			return this;
		}

		@NotNull
		private String processLang(@NotNull String key, T t, Function<T, Object>[] args) {
			Object[] processedArgs = Arrays.stream(args).map(func -> func.apply(t)).toArray();
			if (viewer == null) throw new IllegalStateException("viewer is null");
			return lang(key, viewer, processedArgs);
		}

		public ListBuilder<T> withLorePermission(@Nullable String permission) {
			lorePermission = permission;
			return this;
		}

		public ListBuilder<T> withListener(@NotNull ClickType click, @Nullable Function<T, Runnable> runnable, @Nullable String permission) {
			bukkitPermissions.put(click, permission);
			listeners.put(click, runnable);
			return this;
		}

		public ListBuilder<T> withListener(@NotNull ClickType click, @Nullable Function<T, Runnable> runnable, @Nullable RankPermission permission) {
			rankPermissions.put(click, permission);
			listeners.put(click, runnable);
			return this;
		}

		public ListBuilder<T> withConfirmationRequired(@NotNull ClickType click) {
			confirmationRequired.add(click);
			return this;
		}

		public List<SCComponent> build() {
			List<SCComponent> components = new ArrayList<>();
			for (int i = 0; i < elements.size() && i < slots.size(); i++) {
				T t = elements.get(i);
				SCComponentImpl component = new SCComponentImpl();
				component.setEnabled(enabled);
				component.item = item.apply(t);
				component.slot = slots.get(i);
				ItemMeta itemMeta = component.getItemMeta();
				if (itemMeta != null) {
					itemMeta.setDisplayName(displayName.apply(t));
					List<String> processedLore = lore.stream().map(f -> f.apply(t)).collect(Collectors.toList());
					itemMeta.setLore(processedLore);
					if (itemMeta instanceof SkullMeta && owningPlayer != null) {
						Components.setOwningPlayer(((SkullMeta) itemMeta), owningPlayer.apply(t));
					}
					component.setItemMeta(itemMeta);
				}
				component.setLorePermission(lorePermission);
				listeners.forEach((click, fn) -> component.setListener(click, fn.apply(t)));
				bukkitPermissions.forEach(component::setPermission);
				rankPermissions.forEach(component::setPermission);
				confirmationRequired.forEach(component::setConfirmationRequired);
				components.add(component);
			}
			return components;
		}
	}
}
