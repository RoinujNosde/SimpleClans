package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class MainFrame extends SCFrame {

	private final SimpleClans plugin = SimpleClans.getInstance();

	public MainFrame(Player viewer) {
		super(null, viewer);
	}

	@Override
	public void createComponents() {
		add(Components.getPlayerComponent(this, getViewer(), getViewer(), 0, false));
		add(Components.getClanComponent(this, getViewer(),
				plugin.getClanManager().getCreateClanPlayer(getViewer().getUniqueId()).getClan(), 1, true));

		SCComponent leaderboard = new SCComponentImpl(lang("gui.main.leaderboard.title"),
				Collections.singletonList(lang("gui.main.leaderboard.lore")), Material.PAINTING, 3);
		leaderboard.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new LeaderboardFrame(getViewer(), this)));
		leaderboard.setPermission(ClickType.LEFT, "simpleclans.anyone.leaderboard");
		add(leaderboard);

		SCComponent clanList = new SCComponentImpl(lang("gui.main.clan.list.title"),
				Collections.singletonList(lang("gui.main.clan.list.lore")), Material.PURPLE_BANNER, 4);
		clanList.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer())));
		clanList.setPermission(ClickType.LEFT, "simpleclans.anyone.list");
		add(clanList);

		addResetKdr();

		SCComponent otherCommands = new SCComponentImpl(lang("gui.main.other.commands.title"),
				Collections.singletonList(lang("gui.main.other.commands.lore")), Material.BOOK, 8);
		otherCommands.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "help", false));
		add(otherCommands);
	}

	public void addResetKdr() {
		List<String> resetKrLore;
		if (plugin.getSettingsManager().isePurchaseResetKdr()) {
			resetKrLore = Arrays.asList(
					lang("gui.main.reset.kdr.lore.price", getViewer(), plugin.getSettingsManager().geteResetKdr()),
					lang("gui.main.reset.kdr.lore", getViewer()));
		} else {
			resetKrLore = Collections.singletonList(lang("gui.main.reset.kdr.lore"));
		}
		SCComponent resetKdr = new SCComponentImpl(lang("gui.main.reset.kdr.title"),
				resetKrLore, Material.ANVIL, 6);
		resetKdr.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "resetkdr", false));
		resetKdr.setPermission(ClickType.LEFT, "simpleclans.vip.resetkdr");
		add(resetKdr);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.main.title");
	}

	@Override
	public int getSize() {
		return 9;
	}

}
