package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

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

        String status = cp.getClan() == null ? lang("free.agent",viewer)
                : (cp.isLeader() ? lang("leader",viewer)
                : (cp.isTrusted() ? lang("trusted",viewer) : lang("untrusted",viewer)));
        SCComponent c = new SCComponentImpl(lang("gui.playerdetails.player.title",viewer, cp.getName()),
                Arrays.asList(
                        cp.getClan() == null ? lang("gui.playerdetails.player.lore.noclan",viewer)
                                : lang("gui.playerdetails.player.lore.clan",viewer, cp.getClan().getColorTag(),
                                cp.getClan().getName()),
                        lang("gui.playerdetails.player.lore.rank",viewer,
                                Helper.parseColors(cp.getRankDisplayName())),
                        lang("gui.playerdetails.player.lore.status",viewer, status),
                        lang("gui.playerdetails.player.lore.kdr",viewer,
                                new DecimalFormat("#.#").format(cp.getKDR())),
                        lang("gui.playerdetails.player.lore.kill.totals",viewer, cp.getRivalKills(),
                                cp.getNeutralKills(), cp.getCivilianKills()),
                        lang("gui.playerdetails.player.lore.deaths",viewer, cp.getDeaths()),
                        lang("gui.playerdetails.player.lore.join.date",viewer, cp.getJoinDateString()),
                        lang("gui.playerdetails.player.lore.last.seen",viewer, cp.getLastSeenString()),
                        lang("gui.playerdetails.player.lore.past.clans",viewer, cp.getPastClansString(
                                lang("gui.playerdetails.player.lore.past.clans.separator",viewer))),
                        lang("gui.playerdetails.player.lore.inactive",viewer, cp.getInactiveDays(),
                                pl.getSettingsManager().getPurgePlayers())),
                Material.PLAYER_HEAD, slot);
        SkullMeta itemMeta = (SkullMeta) c.getItemMeta();
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cp.getUniqueId());
        if (itemMeta != null) {
            itemMeta.setOwningPlayer(offlinePlayer);
            c.setItemMeta(itemMeta);
        }
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

    public static SCComponent getClanComponent(@NotNull SCFrame frame, @NotNull Player viewer,
                                               @Nullable Clan clan, int slot, boolean openDetails) {
        SimpleClans pl = SimpleClans.getInstance();
        String name;
        List<String> lore;
        if (clan != null) {
            name = lang("gui.clandetails.clan.title",viewer, clan.getColorTag(), clan.getName());
            lore = Arrays.asList(
                    lang("gui.clandetails.clan.lore.description",viewer,
                            clan.getDescription() != null && !clan.getDescription().isEmpty() ? clan.getDescription() : lang("no.description",viewer)),
                    lang("gui.clandetails.clan.lore.status",viewer, clan.isVerified() ? lang("verified",viewer) : lang("unverified",viewer)),
                    lang("gui.clandetails.clan.lore.leaders",viewer, clan.getLeadersString("", ", ")),
                    lang("gui.clandetails.clan.lore.online.members",viewer, clan.getOnlineMembers().size(), clan.getMembers().size()),
                    lang("gui.clandetails.clan.lore.kdr",viewer, Helper.formatKDR(clan.getTotalKDR())),
                    lang("gui.clandetails.clan.lore.kill.totals",viewer, clan.getTotalRival(), clan.getTotalNeutral(), clan.getTotalCivilian()),
                    lang("gui.clandetails.clan.lore.deaths",viewer, clan.getTotalDeaths()),
                    lang("gui.clandetails.clan.lore.fee",viewer, clan.isMemberFeeEnabled()
                            ? lang("fee.enabled",viewer) : lang("fee.disabled",viewer), clan.getMemberFee()),
                    lang("gui.clandetails.clan.lore.allies",viewer, clan.getAllies().isEmpty() ? lang("none",viewer) : clan.getAllyString(lang("gui.clandetails.clan.lore.allies.separator",viewer))),
                    lang("gui.clandetails.clan.lore.rivals",viewer, clan.getRivals().isEmpty() ? lang("none",viewer) : clan.getRivalString(lang("gui.clandetails.clan.lore.rivals.separator",viewer))),
                    lang("gui.clandetails.clan.lore.founded",viewer, clan.getFoundedString()),
                    lang("gui.clandetails.clan.lore.inactive",viewer, clan.getInactiveDays(), (clan.isVerified() ?
                            pl.getSettingsManager().getPurgeClan() : pl.getSettingsManager().getPurgeUnverified())
                    ));
        } else {
            name = lang("gui.clandetails.free.agent.title",viewer);
            lore = null;
        }

        SCComponent c = new SCComponentImpl(name, lore, Material.GREEN_BANNER, slot);
        if (openDetails && clan != null) {
            c.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanDetailsFrame(frame, viewer, clan)));
        }

        if (clan != null && clan.isMember(viewer)) {
            c.setLorePermission("simpleclans.member.profile");
        } else {
            c.setLorePermission("simpleclans.anyone.profile");
        }

        return c;
    }

    public static SCComponent getBackComponent(SCFrame parent, int slot, Player viewer) {
        SCComponent back = new SCComponentImpl(lang("gui.back.title",viewer), null,
                Material.ARROW, slot);
        back.setListener(ClickType.LEFT, () -> InventoryDrawer.open(parent));
        return back;
    }

    public static SCComponent getPanelComponent(int slot) {
        return new SCComponentImpl(" ", null, Material.GRAY_STAINED_GLASS_PANE, slot);
    }

    public static SCComponent getPreviousPageComponent(int slot, Runnable listener, Player viewer) {
        SCComponent c = new SCComponentImpl(lang("gui.previous.page.title",viewer), null,
                Material.STONE_BUTTON, slot);
        c.setListener(ClickType.LEFT, listener);
        return c;
    }

    public static SCComponent getNextPageComponent(int slot, Runnable listener, Player viewer) {
        SCComponent c = new SCComponentImpl(lang("gui.next.page.title",viewer), null,
                Material.STONE_BUTTON, slot);
        c.setListener(ClickType.LEFT, listener);
        return c;
    }
}
