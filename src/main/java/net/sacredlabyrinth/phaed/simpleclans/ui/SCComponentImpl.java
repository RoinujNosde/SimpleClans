package net.sacredlabyrinth.phaed.simpleclans.ui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

	public static class Builder {
		private final ItemStack item;
		private String displayName;
		private int slot;
		private @Nullable List<String> lore;
		private Player viewer;

		public Builder(@NotNull XMaterial material) {
			this(material.parseItem());
		}

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
			String materialName = config.getString("components." + id + ".material", "STONE");
			item = Objects.requireNonNull(XMaterial.matchXMaterial(materialName).orElse(XMaterial.STONE).parseItem());
			slot = config.getInt("components." + id + ".slot");
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
			return withLoreLine(lang(key, viewer, args));
		}

		public Builder withSlot(int slot) {
			this.slot = slot;
			return this;
		}

		public SCComponent build() {
			SCComponentImpl component = new SCComponentImpl();
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
}
