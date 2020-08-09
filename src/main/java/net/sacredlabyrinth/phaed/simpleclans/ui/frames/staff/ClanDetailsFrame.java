package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.RosterFrame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanDetailsFrame extends SCFrame {
	private final Clan clan;

	public ClanDetailsFrame(@Nullable SCFrame parent, @NotNull Player viewer, @NotNull Clan clan) {
		super(parent, viewer);
		this.clan = clan;
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 4)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 4, getViewer()));
		add(Components.getClanComponent(this, getViewer(), clan, 13, false));

		addRoster();
		addHome();
		addVerify();
		addDisband();
	}

	private void addDisband() {
		SCComponent disband = new SCComponentImpl(lang("gui.clandetails.disband.title", getViewer()),
				Collections.singletonList(lang("gui.staffclandetails.disband.lore", getViewer())),
				Material.BARRIER, 34);
		disband.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
				String.format("disband %s", clan.getTag()), false));
		disband.setConfirmationRequired(ClickType.LEFT);
		disband.setPermission(ClickType.LEFT, "simpleclans.mod.disband");
		add(disband);
	}

	private void addVerify() {
		boolean verified = clan.isVerified();

		Material material = verified ? Material.REDSTONE_TORCH : Material.LEVER;
		String title = verified ? lang("gui.clandetails.verified.title", getViewer())
				: lang("gui.clandetails.not.verified.title", getViewer());
		List<String> lore = verified ? null : new ArrayList<>();
		if (!verified) {
			lore.add(lang("gui.staffclandetails.not.verified.lore", getViewer()));
		}
		SCComponent verify = new SCComponentImpl(title, lore, material, 32);
		if (!verified) {
			verify.setPermission(ClickType.LEFT, "simpleclans.mod.verify");
			verify.setConfirmationRequired(ClickType.LEFT);
			verify.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("verify %s", clan.getTag()), false));
		}
		add(verify);
	}

	private void addHome() {
		List<String> lore = new ArrayList<>();
		lore.add(lang("gui.staffclandetails.home.lore.teleport", getViewer()));
		lore.add(lang("gui.staffclandetails.home.lore.set", getViewer()));

		SCComponent home = new SCComponentImpl(lang("gui.clandetails.home.title", getViewer()), lore,
				Material.MAGENTA_BED, 30);
		home.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
				String.format("home tp %s", clan.getTag()), false));
		home.setPermission(ClickType.LEFT, "simpleclans.mod.hometp");
		home.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
				String.format("home set %s", clan.getTag()), false));
		home.setPermission(ClickType.RIGHT, "simpleclans.mod.home");
		home.setConfirmationRequired(ClickType.RIGHT);
		add(home);
	}

	private void addRoster() {
		SCComponent roster = new SCComponentImpl(lang("gui.clandetails.roster.title", getViewer()),
				Collections.singletonList(lang("gui.staffclandetails.roster.lore", getViewer())), Material.PLAYER_HEAD, 28);
		if (roster.getItemMeta() != null) {
			SkullMeta itemMeta = (SkullMeta) roster.getItemMeta();
			List<ClanPlayer> members = clan.getMembers();
			itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(
					members.get((int) (Math.random() * members.size())).getUniqueId()));
			roster.setItemMeta(itemMeta);
		}
		roster.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new RosterFrame(getViewer(), this, clan, true)));
		add(roster);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.clandetails.title", getViewer(), Helper.stripColors(clan.getColorTag()),
				clan.getName());
	}

	@Override
	public int getSize() {
		return 5 * 9;
	}

}
