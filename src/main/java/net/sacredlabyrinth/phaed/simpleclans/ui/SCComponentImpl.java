package net.sacredlabyrinth.phaed.simpleclans.ui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SCComponentImpl extends SCComponent {
	
	private final ItemStack item;
	private final int slot;

	public SCComponentImpl(String displayName, @Nullable List<String> lore, Material material, int slot) {
		item = new ItemStack(material);
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
}
