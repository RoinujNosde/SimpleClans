package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.SHOW_UNVERIFIED_ON_LIST;

public class ClanListFrame extends SCFrame {
	private final List<Clan> clans;
	private final Paginator paginator;
	private final RankingNumberResolver<Clan, BigDecimal> rankingResolver;

	public ClanListFrame(SCFrame parent, Player viewer) {
		super(parent, viewer);
		SimpleClans plugin = SimpleClans.getInstance();
		SettingsManager sm = plugin.getSettingsManager();
		clans = plugin.getClanManager().getClans().stream()
				.filter(clan -> clan.isVerified() || sm.is(SHOW_UNVERIFIED_ON_LIST)).collect(Collectors.toList());
		paginator = new Paginator(getSize() - 9, clans);
		plugin.getClanManager().sortClansByKDR(clans);

		rankingResolver = new RankingNumberResolver<>(clans, c -> KDRFormat.toBigDecimal(c.getTotalKDR()), false,
				sm.getRankingType());
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
			Clan clan = clans.get(i);
			ItemStack banner = clan.getBanner() != null ? clan.getBanner() : XMaterial.BLACK_BANNER.parseItem();
			SCComponent c = new SCComponentImpl(
					lang("gui.clanlist.clan.title", getViewer(), clan.getColorTag(), clan.getName()),
					Arrays.asList(lang("gui.clanlist.clan.lore.position", getViewer(),
							rankingResolver.getRankingNumber(clan)),
							lang("gui.clanlist.clan.lore.kdr", getViewer(), KDRFormat.format(clan.getTotalKDR())),
							lang("gui.clanlist.clan.lore.members", getViewer(), clan.getMembers().size())),
					banner, slot);
			c.setLorePermission("simpleclans.anyone.list");
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
		return lang("gui.clanlist.title",getViewer(), clans.size());
	}

	@Override
	public int getSize() {
		return 6 * 9;
	}

}
