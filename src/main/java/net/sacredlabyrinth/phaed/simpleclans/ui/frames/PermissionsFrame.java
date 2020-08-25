package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class PermissionsFrame extends SCFrame {
	private final Rank rank;
	private Paginator paginator;

	public PermissionsFrame(SCFrame parent, Player viewer, Rank rank) {
		super(parent, viewer);
		this.rank = rank;
	}

	@Override
	public void createComponents() {
		String[] permissions = rank.getPermissions().toArray(new String[0]);
		paginator = new Paginator(getSize() - 9, permissions.length);

		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 4 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}

		add(Components.getBackComponent(getParent(), 2, getViewer()));

		SCComponent add = new SCComponentImpl(lang("gui.permissions.add.title",getViewer()), null, XMaterial.WHITE_WOOL,
				4);
		add.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AddPermissionFrame(this, getViewer(), rank)));
		add.setPermission(ClickType.LEFT, "simpleclans.leader.rank.permissions.add");
		add(add);

		add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {

			String permission = permissions[i];

			SCComponent c = new SCComponentImpl(lang("gui.permissions.permission.title",getViewer(), permission),
					Collections.singletonList(lang("gui.permissions.permission.lore",getViewer())), XMaterial.PAPER, slot);
			c.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
					String.format("rank permissions %s remove %s", rank.getName(), permission), true));
			c.setPermission(ClickType.RIGHT, "simpleclans.leader.rank.permissions.remove");
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
		return lang("gui.permissions.title",getViewer(), rank.getName());
	}

	@Override
	public int getSize() {
		return 4 * 9;
	}

}
