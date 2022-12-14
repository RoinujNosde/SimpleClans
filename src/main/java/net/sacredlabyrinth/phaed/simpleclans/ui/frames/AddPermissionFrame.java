package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.ui.PageableFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl.ListBuilder;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AddPermissionFrame extends PageableFrame<String> {
	private final List<String> availablePermissions;
	private final Paginator<String> paginator;
	private final Rank rank;

	public AddPermissionFrame(SCFrame parent, Player viewer, Rank rank) {
		super(parent, viewer);
		this.rank = rank;
		Set<String> rankPerms = rank.getPermissions();
		availablePermissions = Arrays.stream(Helper.fromPermissionArray()).filter(p -> !rankPerms.contains(p))
				.collect(Collectors.toList());
		paginator = new Paginator<>(getPageSize(), availablePermissions);
	}

	@Override
	public void createComponents() {
		super.createComponents();
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}

		List<SCComponent> list = new ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
				.withViewer(getViewer())
				.withDisplayNameKey("gui.add.permission.permission.title", p -> p)
				.withLoreKey("gui.add.permission.permission.lore")
				.withListener(ClickType.LEFT, this::addPermission, "simpleclans.leader.rank.permissions.add")
				.build();
		addAll(list);
	}

	private Runnable addPermission(String permission) {
		return () -> {
			availablePermissions.remove(permission);
			InventoryController.runSubcommand(getViewer(), "rank permissions add", true,
					rank.getName(), permission);
		};
	}

	@Override
	public Paginator<String> getPaginator() {
		return paginator;
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.add.permission.title", getViewer());
	}

}
