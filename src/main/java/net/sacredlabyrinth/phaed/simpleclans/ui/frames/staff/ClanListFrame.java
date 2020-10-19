package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanListFrame extends SCFrame {
	private final Type type;
	private final @Nullable OfflinePlayer toPlace;
	private List<Clan> clans;
	private final Paginator paginator;

	public ClanListFrame(@Nullable SCFrame parent, @NotNull Player viewer, @NotNull Type type, @Nullable OfflinePlayer toPlace) {
		super(parent, viewer);
		this.type = type;
		this.toPlace = toPlace;
		SimpleClans plugin = SimpleClans.getInstance();
		clans = plugin.getClanManager().getClans();
		if (type == Type.UNVERIFIED) {
			clans = clans.stream().filter(c -> !c.isVerified()).collect(Collectors.toList());
		}
		paginator = new Paginator(getSize() - 9, clans.size());
		plugin.getClanManager().sortClansByName(clans, true);
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}
		add(Components.getBackComponent(getParent(), 2, getViewer()));

		add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
			Clan clan = clans.get(i);
			SCComponent c = Components.getClanComponent(this, getViewer(), clan, slot, false);
			if (type != Type.PLACE) {
				c.setListener(ClickType.LEFT, () ->
						InventoryDrawer.open(new ClanDetailsFrame(this, getViewer(), clan)));
			} else {
				if (toPlace != null) {
					c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
							"place", false, toPlace.getName(), clan.getTag()));
					c.setConfirmationRequired(ClickType.LEFT);
					c.setPermission(ClickType.LEFT, "simpleclans.mod.place");
				}
			}
			add(c);
			slot++;
		}
	}

	private void previousPage() {
		if (paginator.previousPage()) {
			updateFrame();
		}
	}

	private void nextPage() {
		if (paginator.nextPage()) {
			updateFrame();
		}
	}

	private void updateFrame() {
		InventoryDrawer.update(this);
	}

	@Override
	public @NotNull String getTitle() {
		if (type == Type.PLACE && toPlace != null) {
			return lang("gui.staff.clanlist.toplace.title", getViewer(), toPlace.getName());
		}
		if (type == Type.UNVERIFIED) {
			return lang("gui.staff.clanlist.unverified.title", getViewer(), clans.size());
		}
		return lang("gui.clanlist.title", getViewer(), clans.size());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

	public enum Type {
		ALL, UNVERIFIED, PLACE
	}
}
