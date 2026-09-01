package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.ui.PageableFrame;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl.ListBuilder;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.CurrencyFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ECONOMY_INVITE_PRICE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ECONOMY_PURCHASE_CLAN_INVITE;

public class InviteFrame extends PageableFrame<Player> {

    private final Paginator<Player> paginator;
    private final SimpleClans plugin;

    public InviteFrame(SCFrame parent, Player viewer) {
        super(parent, viewer);
        this.plugin = SimpleClans.getInstance();
        ClanManager cm = plugin.getClanManager();
        List<Player> players = plugin.getServer().getOnlinePlayers().stream().filter(p -> cm.getClanPlayer(p) == null)
                .collect(Collectors.toList());
        paginator = new Paginator<>(getPageSize(), players);
    }

    @Override
    public void createComponents() {
        super.createComponents();
        SettingsManager settings = plugin.getSettingsManager();
        double price = settings.is(ECONOMY_PURCHASE_CLAN_INVITE) ? settings.getDouble(ECONOMY_INVITE_PRICE) : 0;

        ListBuilder<Player> builder = new ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.invite.player.title", Player::getName)
                .withOwningPlayer(p -> Bukkit.getOfflinePlayer(p.getUniqueId()))
                .withListener(ClickType.LEFT, this::invite, RankPermission.INVITE);

        if (price != 0) {
            String formattedPrice = CurrencyFormat.format(price);
            builder.withLoreKey("gui.invite.player.price.lore", p -> formattedPrice);
        }
        builder.withLoreKey("gui.invite.player.lore")
        ;

    }

    private Runnable invite(Player player) {
        return () -> InventoryController.runSubcommand(getViewer(), "invite", false, player.getName());
    }

    @Override
    public Paginator<Player> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.invite.title", getViewer());
    }

}
