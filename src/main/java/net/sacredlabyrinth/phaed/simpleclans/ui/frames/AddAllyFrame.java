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
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AddAllyFrame extends SCFrame {
	private final List<Clan> notAllies;
	private final Paginator paginator;

	public AddAllyFrame(SCFrame parent, Player viewer, Clan subject) {
		super(parent, viewer);
		SimpleClans plugin = SimpleClans.getInstance();
		notAllies = plugin.getClanManager().getClans().stream()
				.filter(c -> !c.equals(subject) && !c.isRival(subject.getTag()) && !c.isAlly(subject.getTag()))
				.collect(Collectors.toList());
		paginator = new Paginator(getSize() - 9, notAllies.size());
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

			Clan notRival = notAllies.get(i);
			SCComponent c = new SCComponentImpl(
					lang("gui.clanlist.clan.title",getViewer(), notRival.getColorTag(), notRival.getName()),
					Collections.singletonList(lang("gui.add.ally.clan.lore",getViewer())), Material.CYAN_BANNER, slot);

			c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("ally %s %s", lang("add"), notRival.getTag()), false));
			c.setPermission(ClickType.LEFT, RankPermission.ALLY_ADD);
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
		return lang("gui.add.ally.title",getViewer());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}
}
