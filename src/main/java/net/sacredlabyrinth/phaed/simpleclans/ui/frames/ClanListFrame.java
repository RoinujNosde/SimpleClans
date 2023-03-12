package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.PageableFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl.ListBuilder;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.SHOW_UNVERIFIED_ON_LIST;

public class ClanListFrame extends PageableFrame<Clan> {
	private final List<Clan> clans;
	private final Paginator<Clan> paginator;
	private final RankingNumberResolver<Clan, BigDecimal> rankingResolver;

	public ClanListFrame(SCFrame parent, Player viewer) {
		super(parent, viewer);
		SimpleClans plugin = SimpleClans.getInstance();
		SettingsManager sm = plugin.getSettingsManager();
		clans = plugin.getClanManager().getClans().stream()
				.filter(clan -> clan.isVerified() || sm.is(SHOW_UNVERIFIED_ON_LIST)).collect(Collectors.toList());
		plugin.getClanManager().sortClansByKDR(clans);
		paginator = new Paginator<>(getPageSize(), clans);

		rankingResolver = new RankingNumberResolver<>(clans, c -> KDRFormat.toBigDecimal(c.getTotalKDR()), false,
				sm.getRankingType());
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
				.withItem(Clan::getBanner)
				.withViewer(getViewer())
				.withDisplayNameKey("gui.clanlist.clan.title", Clan::getColorTag, Clan::getName)
				.withLoreKey("gui.clanlist.clan.lore.position", rankingResolver::getRankingNumber)
				.withLoreKey("gui.clanlist.clan.lore.kdr", clan -> KDRFormat.format(clan.getTotalKDR()))
				.withLoreKey("gui.clanlist.clan.lore.members", clan -> clan.getMembers().size())
				.withLorePermission("simpleclans.anyone.list").build();
		addAll(list);
	}

	@Override
	public Paginator<Clan> getPaginator() {
		return paginator;
	}

	@Override
	public @NotNull String getTitle() {
		return lang("gui.clanlist.title", getViewer(), clans.size());
	}

}
