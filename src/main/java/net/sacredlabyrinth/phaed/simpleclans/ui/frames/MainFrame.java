package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff.StaffFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

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
		addLeaderboard();
		addClanList();
		addResetKdr();
		addStaff();
		addLanguageSelector();
		addOtherCommands();
	}

	private void addOtherCommands() {
		SCComponent otherCommands = new SCComponentImpl(lang("gui.main.other.commands.title",getViewer()),
				Collections.singletonList(lang("gui.main.other.commands.lore",getViewer())), XMaterial.BOOK, 8);
		otherCommands.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "help", false));
		add(otherCommands);
	}

	private void addStaff() {
		if (plugin.getPermissionsManager().has(getViewer(), "simpleclans.mod.staffgui")) {
			SCComponent staff = new SCComponentImpl.Builder(XMaterial.COMMAND_BLOCK).withSlot(6).withDisplayName(
					lang("gui.main.staff.title", getViewer())).withLore(Collections.singletonList(lang("gui.main.staff.lore", getViewer()))).build();
			staff.setPermission(ClickType.LEFT, "simpleclans.mod.staffgui");
			staff.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new StaffFrame(this, getViewer())));
			add(staff);
		}
	}

	private void addLeaderboard() {
		SCComponent leaderboard = new SCComponentImpl(lang("gui.main.leaderboard.title",getViewer()),
				Collections.singletonList(lang("gui.main.leaderboard.lore",getViewer())), XMaterial.PAINTING, 3);
		leaderboard.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new LeaderboardFrame(getViewer(), this)));
		leaderboard.setPermission(ClickType.LEFT, "simpleclans.anyone.leaderboard");
		add(leaderboard);
	}

	private void addClanList() {
		SCComponent clanList = new SCComponentImpl(lang("gui.main.clan.list.title", getViewer()),
				Collections.singletonList(lang("gui.main.clan.list.lore", getViewer())), XMaterial.PURPLE_BANNER,
				4);
		clanList.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer())));
		clanList.setPermission(ClickType.LEFT, "simpleclans.anyone.list");
		add(clanList);
	}

	private void addLanguageSelector() {
		if (plugin.getSettingsManager().is(LANGUAGE_SELECTOR)) {
			SCComponent language = new SCComponentImpl.Builder(XMaterial.MAP)
					.withDisplayName(lang("gui.main.languageselector.title", getViewer()))
					.withSlot(7).withLore(Arrays.asList(lang("gui.main.languageselector.lore.left.click", getViewer())
					, lang("gui.main.languageselector.lore.right.click", getViewer()))).build();
			language.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new LanguageSelectorFrame(this, getViewer())));
			language.setListener(ClickType.RIGHT, () -> {
				getViewer().sendMessage(lang("click.to.help.translating", getViewer(),
						"https://crowdin.com/project/simpleclans"));
				getViewer().closeInventory();
			});
			add(language);
		}
	}

	public void addResetKdr() {
		if (!plugin.getPermissionsManager().has(getViewer(), "simpleclans.vip.resetkdr")) {
			return;
		}

		List<String> resetKdrLore;
		if (plugin.getSettingsManager().is(ECONOMY_PURCHASE_RESET_KDR)) {
			double price = plugin.getSettingsManager().getDouble(ECONOMY_RESET_KDR_PRICE);
			resetKrLore = Arrays.asList(
					lang("gui.main.reset.kdr.lore.price", getViewer(), ChatUtils.formatCurrency(price)),
					lang("gui.main.reset.kdr.lore", getViewer()));
		} else {
			resetKdrLore = Collections.singletonList(lang("gui.main.reset.kdr.lore", getViewer()));
		}
		SCComponent resetKdr = new SCComponentImpl(lang("gui.main.reset.kdr.title", getViewer()),
				resetKdrLore, XMaterial.ANVIL, 5);
		resetKdr.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(getViewer(), "resetkdr", false));
		resetKdr.setConfirmationRequired(ClickType.LEFT);
		resetKdr.setPermission(ClickType.LEFT, "simpleclans.vip.resetkdr");

		add(resetKdr);
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.main.title", getViewer(), plugin.getSettingsManager().getColored(SERVER_NAME));
	}

	@Override
	public int getSize() {
		return 3 * 9;
	}

}
