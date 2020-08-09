package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AlliesFrame extends SCFrame {
	private final SimpleClans plugin = SimpleClans.getInstance();
	private final Paginator paginator;
	private final List<String> allies;
	private final Clan subject;

	public AlliesFrame(Player viewer, SCFrame parent, Clan subject) {
		super(parent, viewer);
		this.subject = subject;
		allies = subject.getAllies();
		paginator = new Paginator(getSize() - 9, allies.size());
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 4 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 2, getViewer()));

		SCComponent add = new SCComponentImpl(lang("gui.allies.add.title",getViewer()), null, Material.CYAN_WOOL, 4);
		add.setVerifiedOnly(ClickType.LEFT);
		add.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AddAllyFrame(this, getViewer(), subject)));
		add.setPermission(ClickType.LEFT, RankPermission.ALLY_ADD);
		add(add);

		add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {

			Clan clan = plugin.getClanManager().getClan(allies.get(i));
			if (clan == null)
				continue;
			SCComponent c = new SCComponentImpl(
					lang("gui.clanlist.clan.title",getViewer(), clan.getColorTag(), clan.getName()),
					Collections.singletonList(lang("gui.allies.clan.lore",getViewer())), Material.CYAN_BANNER, slot);
			c.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("ally %s %s", lang("remove",getViewer()), clan.getTag()), false));
			c.setPermission(ClickType.RIGHT, RankPermission.ALLY_REMOVE);
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
		return lang("gui.allies.title",getViewer());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
