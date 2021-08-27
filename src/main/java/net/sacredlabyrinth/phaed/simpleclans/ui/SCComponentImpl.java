package net.sacredlabyrinth.phaed.simpleclans.ui;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
		private final SCComponentImpl component = new SCComponentImpl();
		private @Nullable List<String> lore;

		public Builder(@NotNull XMaterial material) {
			this(material.parseItem());
		}

		public Builder(@NotNull Material material) {
			component.item = new ItemStack(material);
		}

		public Builder(@Nullable ItemStack item) {
			if (item == null) {
				item = new ItemStack(Material.STONE);
			}
			component.item = item;
		}

		public Builder withDisplayName(@Nullable String displayName) {
			ItemMeta itemMeta = component.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setDisplayName(displayName);
				component.setItemMeta(itemMeta);
			}
			return this;
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

		public Builder withSlot(int slot) {
			component.slot = slot;
			return this;
		}

		public SCComponent build() {
			ItemMeta itemMeta = component.getItemMeta();
			if (itemMeta != null) {
				itemMeta.setLore(lore);
				component.setItemMeta(itemMeta);
			}
			return component;
		}
	}
}
