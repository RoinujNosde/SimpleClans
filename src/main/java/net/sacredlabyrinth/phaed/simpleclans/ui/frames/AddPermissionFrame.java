package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AddPermissionFrame extends SCFrame {
	private final String[] availablePermissions;
	private final Paginator paginator;
	private final Rank rank;

	public AddPermissionFrame(SCFrame parent, Player viewer, Rank rank) {
		super(parent, viewer);
		this.rank = rank;
		Set<String> rankPerms = rank.getPermissions();
		availablePermissions = Arrays.stream(Helper.fromPermissionArray()).filter(p -> !rankPerms.contains(p))
				.toArray(String[]::new);
		paginator = new Paginator(getSize() - 9, availablePermissions.length);
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

			String permission = availablePermissions[i];

			SCComponent c = new SCComponentImpl(
					lang("gui.add.permission.permission.title",getViewer(), permission),
					Collections.singletonList(lang("gui.add.permission.permission.lore",getViewer())),
					XMaterial.PAPER, slot);
			c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("rank permissions %s add %s", rank.getName(), permission), true));
			c.setPermission(ClickType.LEFT, "simpleclans.leader.rank.permissions.add");
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
		return lang("gui.add.permission.title",getViewer());
	}

	@Override
	public int getSize() {
		return 4 * 9;
	}

}
