package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanListFrame extends SCFrame {
	private final SimpleClans plugin = SimpleClans.getInstance();
	private final List<Clan> clans = plugin.getClanManager().getClans();
	private final Paginator paginator;

	public ClanListFrame(SCFrame parent, Player viewer) {
		super(parent, viewer);
		paginator = new Paginator(getSize() - 9, clans.size());
		plugin.getClanManager().sortClansByKDR(clans);
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
			SCComponent c = new SCComponentImpl(
					lang("gui.clanlist.clan.title",getViewer(), clan.getColorTag(), clan.getName()),
					Arrays.asList(lang("gui.clanlist.clan.lore.position",getViewer(), i + 1),
							lang("gui.clanlist.clan.lore.kdr",getViewer(), Helper.formatKDR(clan.getTotalKDR())),
							lang("gui.clanlist.clan.lore.members",getViewer(), clan.getMembers().size())),
					Material.BLACK_BANNER, slot);
			c.setLorePermission("simpleclans.anyone.list");
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
		return lang("gui.clanlist.title",getViewer(), clans.size());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
