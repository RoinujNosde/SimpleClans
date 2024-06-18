package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public class Components {

    private Components() {
    }

    public static SCComponent getPlayerComponent(SCFrame frame, Player viewer, OfflinePlayer subject, int slot,
                                                 boolean openDetails) {
        ClanPlayer cp = SimpleClans.getInstance().getClanManager().getCreateClanPlayer(subject.getUniqueId());

        return getPlayerComponent(frame, viewer, cp, slot, openDetails);
    }

    public static SCComponent getPlayerComponent(SCFrame frame, Player viewer, ClanPlayer cp, int slot,
                                                 boolean openDetails) {
        SimpleClans pl = SimpleClans.getInstance();
        String status = getPlayerStatus(viewer, cp);
        SCComponent c = new SCComponentImpl(lang("gui.playerdetails.player.title", viewer, cp.getName()),
                Arrays.asList(
                        cp.getClan() == null ? lang("gui.playerdetails.player.lore.noclan", viewer)
                                : lang("gui.playerdetails.player.lore.clan", viewer, cp.getClan().getColorTag(),
                                cp.getClan().getName()),
                        lang("gui.playerdetails.player.lore.rank", viewer,
                                ChatUtils.parseColors(cp.getRankDisplayName())),
                        lang("gui.playerdetails.player.lore.status", viewer, status),
                        lang("gui.playerdetails.player.lore.kdr", viewer,
                                new DecimalFormat("#.#").format(cp.getKDR())),
                        lang("gui.playerdetails.player.lore.kill.totals", viewer, cp.getRivalKills(),
                                cp.getNeutralKills(), cp.getCivilianKills()),
                        lang("gui.playerdetails.player.lore.deaths", viewer, cp.getDeaths()),
                        lang("gui.playerdetails.player.lore.join.date", viewer, cp.getJoinDateString()),
                        lang("gui.playerdetails.player.lore.last.seen", viewer, cp.getLastSeenString(viewer)),
                        lang("gui.playerdetails.player.lore.past.clans", viewer, cp.getPastClansString(
                                lang("gui.playerdetails.player.lore.past.clans.separator", viewer))),
                        lang("gui.playerdetails.player.lore.inactive", viewer, cp.getInactiveDays(),
                                pl.getSettingsManager().getInt(PURGE_INACTIVE_PLAYER_DAYS))),
                XMaterial.PLAYER_HEAD, slot);
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cp.getUniqueId());
        setOwningPlayer(c.getItem(), offlinePlayer);
        if (viewer.getUniqueId().equals(cp.getUniqueId())) {
            c.setLorePermission("simpleclans.member.lookup");
        } else {
            c.setLorePermission("simpleclans.anyone.lookup");
        }
        if (openDetails) {
            c.setListener(ClickType.LEFT,
                    () -> InventoryDrawer.open(new PlayerDetailsFrame(viewer, frame, offlinePlayer)));
        }
        return c;
    }

    @NotNull
    private static String getPlayerStatus(Player viewer, ClanPlayer cp) {
        if (cp.getClan() == null) {
            return lang("free.agent", viewer);
        }
        if (cp.isLeader()) {
            return lang("leader", viewer);
        }
        if (cp.isTrusted()) {
            return lang("trusted", viewer);
        }
        if (!cp.getRankId().isEmpty()) {
            return lang("in.rank", viewer);
        }
        return lang("untrusted", viewer);
    }

    public static SCComponent getClanComponent(@NotNull SCFrame frame, @NotNull Player viewer,
                                               @Nullable Clan clan, int slot, boolean openDetails) {
        SimpleClans pl = SimpleClans.getInstance();
        String name;
        List<String> lore;
        if (clan != null) {
            name = lang("gui.clandetails.clan.title", viewer, clan.getColorTag(), clan.getName());
            lore = Arrays.asList(
                    lang("gui.clandetails.clan.lore.description", viewer,
                            clan.getDescription() != null && !clan.getDescription().isEmpty() ? clan.getDescription() : lang("no.description", viewer)),
                    lang("gui.clandetails.clan.lore.status", viewer, Helper.getFormattedClanStatus(clan, viewer)),
                    lang("gui.clandetails.clan.lore.leaders", viewer, clan.getLeadersString("", ", ")),
                    lang("gui.clandetails.clan.lore.online.members", viewer, VanishUtils.getNonVanished(viewer, clan).size(), clan.getMembers().size()),
                    lang("gui.clandetails.clan.lore.kdr", viewer, KDRFormat.format(clan.getTotalKDR())),
                    lang("gui.clandetails.clan.lore.kill.totals", viewer, clan.getTotalRival(), clan.getTotalNeutral(), clan.getTotalCivilian()),
                    lang("gui.clandetails.clan.lore.deaths", viewer, clan.getTotalDeaths()),
                    lang("gui.clandetails.clan.lore.fee", viewer, clan.isMemberFeeEnabled()
                            ? lang("fee.enabled", viewer) : lang("fee.disabled", viewer), clan.getMemberFee()),
                    lang("gui.clandetails.clan.lore.allies", viewer, clan.getAllies().isEmpty() ? lang("none", viewer) : clan.getAllyString(lang("gui.clandetails.clan.lore.allies.separator", viewer), viewer)),
                    lang("gui.clandetails.clan.lore.rivals", viewer, clan.getRivals().isEmpty() ? lang("none", viewer) : clan.getRivalString(lang("gui.clandetails.clan.lore.rivals.separator", viewer), viewer)),
                    lang("gui.clandetails.clan.lore.founded", viewer, clan.getFoundedString()),
                    lang("gui.clandetails.clan.lore.inactive", viewer, clan.getInactiveDays(), Helper.formatMaxInactiveDays(clan.getMaxInactiveDays())));
        } else {
            name = lang("gui.clandetails.free.agent.title", viewer);
            double price = pl.getSettingsManager().is(ECONOMY_PURCHASE_CLAN_CREATE) ? pl.getSettingsManager().getDouble(ECONOMY_CREATION_PRICE) : 0;
            lore = new ArrayList<>();
            if (price != 0) {
                lore.add(lang("gui.clandetails.free.agent.create.clan.price.lore", frame.getViewer(), CurrencyFormat.format(price)));
            }
            lore.add(lang("gui.clandetails.free.agent.create.clan.lore", frame.getViewer()));
        }

        ItemStack item;
        if (clan != null && clan.getBanner() != null) {
            item = clan.getBanner();
        } else {
            item = XMaterial.GREEN_BANNER.parseItem();
        }
        SCComponent c = new SCComponentImpl.Builder(item).withLore(lore).withDisplayName(name).withSlot(slot).build();
        if (openDetails && clan != null) {
            c.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanDetailsFrame(frame, viewer, clan)));
        }
        if (clan == null) {
            c.setPermission(ClickType.LEFT, "simpleclans.leader.create");
            c.setListener(ClickType.LEFT, () -> InventoryController.runSubcommand(viewer, "create", false));
        }

        if (clan != null && clan.isMember(viewer)) {
            c.setLorePermission("simpleclans.member.profile");
        } else {
            c.setLorePermission("simpleclans.anyone.profile");
        }

        return c;
    }

    public static SCComponent getBackComponent(@Nullable SCFrame parent, int slot, Player viewer) {
        SCComponent back = new SCComponentImpl(lang("gui.back.title", viewer), null,
                XMaterial.ARROW, slot);
        back.setListener(ClickType.LEFT, () -> InventoryDrawer.open(parent));
        return back;
    }

    public static SCComponent getPanelComponent(int slot) {
        return new SCComponentImpl(" ", null, XMaterial.GRAY_STAINED_GLASS_PANE, slot);
    }

    public static @NotNull SCComponent getPreviousPageComponent(int slot, @Nullable Runnable listener, @NotNull Paginator paginator, @NotNull Player viewer) {
        if (!paginator.hasPreviousPage()) {
            return getPanelComponent(slot);
        }
        SCComponent c = new SCComponentImpl(lang("gui.previous.page.title", viewer), null,
                XMaterial.STONE_BUTTON, slot);
        setOneTimeUseListener(c, listener);
        return c;
    }

    public static @NotNull SCComponent getNextPageComponent(int slot, @Nullable Runnable listener, @NotNull Paginator paginator, @NotNull Player viewer) {
        if (!paginator.hasNextPage()) {
            return getPanelComponent(slot);
        }
        SCComponent c = new SCComponentImpl(lang("gui.next.page.title", viewer), null,
                XMaterial.STONE_BUTTON, slot);
        setOneTimeUseListener(c, listener);
        return c;
    }

    private static void setOneTimeUseListener(SCComponent c, @Nullable Runnable listener) {
        c.setListener(ClickType.LEFT, () -> {
            if (listener != null) {
                listener.run();
            }
            c.setListener(ClickType.LEFT, null);
        });
    }

    @SuppressWarnings("deprecation")
    public static void setOwningPlayer(@NotNull ItemStack item, @NotNull OfflinePlayer player) {
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        if (itemMeta == null || player.getName() == null) {
            return;
        }
        try {
            itemMeta.setOwningPlayer(player);
        } catch (NoSuchMethodError e) {
            itemMeta.setOwner(player.getName());
        }

        item.setItemMeta(itemMeta);
    }
}
