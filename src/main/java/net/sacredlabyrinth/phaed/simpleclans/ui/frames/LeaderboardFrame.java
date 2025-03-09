package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.PageableFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl.ListBuilder;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class LeaderboardFrame extends PageableFrame<ClanPlayer> {

    private final Paginator<ClanPlayer> paginator;
    private final List<ClanPlayer> clanPlayers;
    private final RankingNumberResolver<ClanPlayer, BigDecimal> rankingResolver;

    public LeaderboardFrame(Player viewer, SCFrame parent) {
        super(parent, viewer);

        SimpleClans plugin = SimpleClans.getInstance();
        clanPlayers = plugin.getClanManager().getAllClanPlayers();

        rankingResolver = new RankingNumberResolver<>(clanPlayers, c -> KDRFormat.toBigDecimal(c.getKDR()), false,
                plugin.getSettingsManager().getRankingType());
        paginator = new Paginator<>(getSize() - 9, this.clanPlayers);
    }

    @Override
    public void createComponents() {
        super.createComponents();
        List<SCComponent> list = new ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withDisplayNameKey("gui.leaderboard.player.title",
                        cp -> getViewer(), rankingResolver::getRankingNumber, ClanPlayer::getName)
                .withListener(ClickType.LEFT, this::openDetails, null)
                .withLorePermission("simpleclans.anyone.leaderboard")
                .withLoreLine(cp ->
                        cp.getClan() == null ? lang("gui.playerdetails.player.lore.noclan", getViewer())
                                : lang("gui.playerdetails.player.lore.clan", getViewer()
                        ))
                .withLoreKey("gui.playerdetails.player.lore.kdr", cp -> getViewer(),
                        cp -> KDRFormat.format(cp.getKDR()))
                .withLoreKey("gui.playerdetails.player.lore.last.seen", cp -> getViewer(),
                        cp -> cp.getLastSeenString(getViewer()))
                .withOwningPlayer(cp -> Bukkit.getOfflinePlayer(cp.getUniqueId())).build();
        addAll(list);
    }

    private Runnable openDetails(ClanPlayer cp) {
        return () -> InventoryDrawer.open(new PlayerDetailsFrame(getViewer(), this,
                Bukkit.getOfflinePlayer(cp.getUniqueId())));
    }

    @Override
    public Paginator<ClanPlayer> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.leaderboard.title", getViewer(), clanPlayers.size());
    }

}
