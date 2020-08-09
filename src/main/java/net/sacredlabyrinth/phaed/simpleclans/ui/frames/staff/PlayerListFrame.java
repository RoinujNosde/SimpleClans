package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class PlayerListFrame extends SCFrame {

	private final boolean onlineOnly;
	private Paginator paginator;
	private List<OfflinePlayer> players;

	public PlayerListFrame(@NotNull Player viewer, @Nullable SCFrame parent, boolean onlineOnly) {
		super(parent, viewer);
		this.onlineOnly = onlineOnly;
		loadPlayers();
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
			OfflinePlayer player = players.get(i);
			SCComponent c = Components.getPlayerComponent(this, getViewer(), player, slot, false);
			c.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new PlayerDetailsFrame(getViewer(), this, player)));

			add(c);
			slot++;
		}
	}

	private void loadPlayers() {
		SimpleClans plugin = SimpleClans.getInstance();
		ClanManager clanManager = plugin.getClanManager();
		if (onlineOnly) {
			players = new ArrayList<>(Bukkit.getOnlinePlayers());
		} else {
			players = clanManager.getAllClanPlayers().stream().map(ClanPlayer::getUniqueId)
					.map(Bukkit::getOfflinePlayer).collect(Collectors.toList());
			List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream().filter(p -> !players.contains(p))
					.collect(Collectors.toList());
			players.addAll(onlinePlayers);
		}
		players = players.stream().filter(p -> p.getName() != null).collect(Collectors.toList());
		players.sort(Comparator.comparing(OfflinePlayer::getName));
		paginator = new Paginator(getSize() - 9, players.size());
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
		return lang("gui.player.list.title", getViewer());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
