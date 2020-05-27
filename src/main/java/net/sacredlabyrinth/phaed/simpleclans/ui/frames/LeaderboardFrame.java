package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class LeaderboardFrame extends SCFrame {

	private final Paginator paginator;
	private final List<ClanPlayer> clanPlayers = SimpleClans.getInstance().getClanManager().getAllClanPlayers();

	public LeaderboardFrame(Player viewer, SCFrame parent) {
		super(parent, viewer);
		paginator = new Paginator(getSize() - 9, clanPlayers.size());
		SimpleClans plugin = SimpleClans.getInstance();
		plugin.getClanManager().sortClanPlayersByKDR(clanPlayers);
	}

	@Override
	public void createComponents() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}
		add(Components.getBackComponent(getParent(), 2, getViewer()));

		add(Components.getPreviousPageComponent(6, this::previousPage, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, getViewer()));

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
			ClanPlayer cp = clanPlayers.get(i);
			SCComponent c = new SCComponentImpl(
					lang("gui.leaderboard.player.title",getViewer(), i + 1, cp.getName()),
					Arrays.asList(
							cp.getClan() == null ? lang("gui.playerdetails.player.lore.noclan",getViewer())
									: lang("gui.playerdetails.player.lore.clan",getViewer(),
											cp.getClan().getColorTag(), cp.getClan().getName()),
							lang("gui.playerdetails.player.lore.kdr",getViewer(), Helper.formatKDR(cp.getKDR())),
							lang("gui.playerdetails.player.lore.last.seen",getViewer(), cp.getLastSeenString(getViewer()))),
					Material.PLAYER_HEAD, slot);
			SkullMeta itemMeta = (SkullMeta) c.getItemMeta();
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cp.getUniqueId());
			if (itemMeta != null) {
				itemMeta.setOwningPlayer(offlinePlayer);
				c.setItemMeta(itemMeta);
			}
			c.setListener(ClickType.LEFT,
					() -> InventoryDrawer.open(new PlayerDetailsFrame(getViewer(), this, offlinePlayer)));
			c.setLorePermission("simpleclans.anyone.leaderboard");
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
		return lang("gui.leaderboard.title",getViewer(), clanPlayers.size());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
