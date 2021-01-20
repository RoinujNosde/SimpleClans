package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class RanksFrame extends SCFrame {
	private final Paginator paginator;
	private final OfflinePlayer toEdit;
	private final List<Rank> ranks;

	public RanksFrame(SCFrame parent, Player viewer, Clan subject, @Nullable OfflinePlayer toEdit) {
		super(parent, viewer);
		this.toEdit = toEdit;
		ranks = subject != null ? subject.getRanks() : new ArrayList<>();
		paginator = new Paginator(getSize() - 9, ranks.size());
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 4 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}
		
		add(Components.getBackComponent(getParent(), 2, getViewer()));

		SCComponent create = new SCComponentImpl(lang("gui.ranks.create.title",getViewer()),
				Collections.singletonList(lang("gui.ranks.create.lore",getViewer())), XMaterial.WHITE_WOOL, 4);
		create.setVerifiedOnly(ClickType.LEFT);
		create.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "rank create", false));
		create.setPermission(ClickType.LEFT, "simpleclans.leader.rank.create");
		add(create);

		add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
			Rank rank = ranks.get(i);
			List<String> lore;
			if (toEdit == null) {
				lore = Arrays.asList(
						lang("gui.ranks.rank.displayname.lore",getViewer(),
								ChatUtils.parseColors(rank.getDisplayName())),
						lang("gui.ranks.rank.edit.permissions.lore",getViewer()),
						lang("gui.ranks.rank.remove.lore",getViewer()));
			} else {
				lore = Arrays.asList(
						lang("gui.ranks.rank.displayname.lore",getViewer(),
								ChatUtils.parseColors(rank.getDisplayName())),
						lang("gui.ranks.rank.assign.lore",getViewer(), toEdit.getName()));
			}
			SCComponent c = new SCComponentImpl(lang("gui.ranks.rank.title",getViewer(), rank.getName()), lore,
					XMaterial.FILLED_MAP, slot);
			if (toEdit != null) {
				c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(),
						"rank assign", true, toEdit.getName(), rank.getName()));
				c.setConfirmationRequired(ClickType.LEFT);
				c.setPermission(ClickType.LEFT, "simpleclans.leader.rank.assign");
			} else {
				c.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new PermissionsFrame(this, getViewer(), rank)));
				c.setPermission(ClickType.LEFT, "simpleclans.leader.rank.permissions.list");
				c.setListener(ClickType.RIGHT, () -> InventoryController.runSubcommand(getViewer(),
						"rank delete", true, rank.getName()));
				c.setConfirmationRequired(ClickType.RIGHT);
				c.setPermission(ClickType.RIGHT, "simpleclans.leader.rank.delete");
			}
			c.setVerifiedOnly(ClickType.LEFT);
			c.setVerifiedOnly(ClickType.RIGHT);
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
		if (toEdit != null) {
			ClanManager clanManager = SimpleClans.getInstance().getClanManager();
			String rank = clanManager.getCreateClanPlayer(toEdit.getUniqueId()).getRankId();
			return lang("gui.ranks.title.set.rank",getViewer(), rank);
		}
		return lang("gui.ranks.title",getViewer());
	}

	@Override
	public int getSize() {
		return 3 * 9;
	}

}
