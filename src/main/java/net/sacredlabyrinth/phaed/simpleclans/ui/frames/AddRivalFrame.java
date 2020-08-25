package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AddRivalFrame extends SCFrame {

	private final List<Clan> notRivals;
	private final Paginator paginator;

	public AddRivalFrame(SCFrame parent, Player viewer, Clan subject) {
		super(parent, viewer);
		SimpleClans plugin = SimpleClans.getInstance();
		notRivals = plugin.getClanManager().getClans().stream()
				.filter(c -> !c.equals(subject) && !c.isRival(subject.getTag()) && !c.isAlly(subject.getTag()))
				.collect(Collectors.toList());
		paginator = new Paginator(getSize() - 9, notRivals.size());
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

			Clan notRival = notRivals.get(i);
			SCComponent c = new SCComponentImpl(
					lang("gui.clanlist.clan.title",getViewer(), notRival.getColorTag(), notRival.getName()),
					Collections.singletonList(lang("gui.add.rival.clan.lore", getViewer())), XMaterial.RED_BANNER,
					slot);

			c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("rival %s %s", lang("add",getViewer()), notRival.getTag()), false));
			c.setConfirmationRequired(ClickType.LEFT);
			c.setPermission(ClickType.LEFT, RankPermission.RIVAL_ADD);
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
		InventoryDrawer.open(this);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.add.rival.title",getViewer());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}
}
