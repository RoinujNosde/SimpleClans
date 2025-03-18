package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CoordsFrame extends PageableFrame<ClanPlayer> {

    private final List<ClanPlayer> allMembers;
    private final Paginator<ClanPlayer> paginator;

    public CoordsFrame(Player viewer, SCFrame parent, Clan subject) {
        super(parent, viewer);
        allMembers = VanishUtils.getNonVanished(getViewer(), subject);
        allMembers.sort((cp1, cp2) -> Boolean.compare(cp1.isLeader(), cp2.isLeader()));

        paginator = new Paginator<>(getPageSize(), allMembers);
    }

    @Override
    public void createComponents() {
        super.createComponents();

        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.playerdetails.player.title", ClanPlayer::getName)
                .withLoreKey("gui.coords.player.lore.distance", cp -> {
                    Location cpLoc = Objects.requireNonNull(cp.toPlayer()).getLocation();
                    return (int) Math.ceil(cpLoc.toVector().distance(getViewer().getLocation().toVector()));
                }).withLoreKey("gui.coords.player.lore.coords", this::getBlocks)
                .withLoreKey("gui.coords.player.lore.world", cp ->
                        Objects.requireNonNull(cp.toPlayer()).getWorld().getName())
                .withOwningPlayer(ClanPlayer::toPlayer)
                .withListener(ClickType.LEFT, cp -> () -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(cp.getUniqueId());
                    InventoryDrawer.open(new PlayerDetailsFrame(getViewer(), this, offlinePlayer));
                }, RankPermission.COORDS).build();
        addAll(list);
    }

    private int[] getBlocks(ClanPlayer cp) {
        int[] blocks = new int[3];
        Location location = Objects.requireNonNull(cp.toPlayer()).getLocation();
        blocks[0] = location.getBlockX();
        blocks[1] = location.getBlockY();
        blocks[2] = location.getBlockZ();
        return blocks;
    }

    @Override
    public Paginator<ClanPlayer> getPaginator() {
        return paginator;
    }

    @Override
    @NotNull
    public String getTitle() {
        return lang("gui.coords.title", getViewer());
    }

}
