package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class InviteFrame extends SCFrame {

	private Paginator paginator;

	public InviteFrame(SCFrame parent, Player viewer) {
		super(parent, viewer);
	}

	@Override
	public void createComponents() {
		SimpleClans plugin = SimpleClans.getInstance();
		ClanManager cm = plugin.getClanManager();
		List<Player> players = plugin.getServer().getOnlinePlayers().stream().filter(p -> cm.getClanPlayer(p) == null).collect(Collectors.toList());
		paginator = new Paginator(getSize() - 9, players.size());

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

			Player player = players.get(i);
			SCComponent c = new SCComponentImpl(
					lang("gui.invite.player.title",getViewer(), player.getName()),
					Collections.singletonList(lang("gui.invite.player.lore",getViewer())), Material.PLAYER_HEAD, slot);
			SkullMeta itemMeta = (SkullMeta) c.getItemMeta();
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
			if (itemMeta != null) {
				itemMeta.setOwningPlayer(offlinePlayer);
				c.setItemMeta(itemMeta);
			}
			c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "invite " + player.getName(), false));
			c.setPermission(ClickType.LEFT, RankPermission.INVITE);
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
		return lang("gui.invite.title",getViewer());
	}
	
	@Override
	public int getSize() {
		return 3 * 9;
	}
}
