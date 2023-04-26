package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class InviteFrame extends SCFrame {

	private final Paginator paginator;
	private final SimpleClans plugin;
	private final List<Player> players;

	public InviteFrame(SCFrame parent, Player viewer) {
		super(parent, viewer);
		this.plugin = SimpleClans.getInstance();
		ClanManager cm = plugin.getClanManager();
		players = plugin.getServer().getOnlinePlayers().stream().filter(p -> cm.getClanPlayer(p) == null)
				.collect(Collectors.toList());
		paginator = new Paginator(getSize() - 9, players.size());

	}

	@Override
	public void createComponents() {
		addHeader();

		int slot = 9;
		for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {

			Player player = players.get(i);
			SCComponent c = createPlayerComponent(player, slot);
			add(c);
			slot++;
		}
	}

	@NotNull
	private SCComponent createPlayerComponent(@NotNull Player player, int slot) {
		double price = plugin.getSettingsManager().is(ECONOMY_PURCHASE_CLAN_INVITE) ? plugin.getSettingsManager().getDouble(ECONOMY_INVITE_PRICE) : 0;
		List<String> lore = new ArrayList<>();
		if (price != 0) lore.add(lang("gui.invite.player.price.lore", getViewer(), ChatUtils.formatPrice(price)));
		lore.add(lang("gui.invite.player.lore", getViewer()));

		SCComponent c = new SCComponentImpl(
				lang("gui.invite.player.title", getViewer(), player.getName()), lore, XMaterial.PLAYER_HEAD, slot);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
		Components.setOwningPlayer(c.getItem(), offlinePlayer);
		c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "invite", false, player.getName()));
		c.setPermission(ClickType.LEFT, RankPermission.INVITE);
		return c;
	}

	public void addHeader() {
		for (int slot = 0; slot < 9; slot++) {
			if (slot == 2 || slot == 6 || slot == 7)
				continue;
			add(Components.getPanelComponent(slot));
		}
		add(Components.getBackComponent(getParent(), 2, getViewer()));

		add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
		add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));
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
		return lang("gui.invite.title",getViewer());
	}
	
	@Override
	public int getSize() {
		return 3 * 9;
	}
}
