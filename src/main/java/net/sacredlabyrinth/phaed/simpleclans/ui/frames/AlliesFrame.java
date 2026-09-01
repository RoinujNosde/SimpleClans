package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AlliesFrame extends PageableFrame<Clan> {
    private final Paginator<Clan> paginator;
    private final Clan subject;

    public AlliesFrame(Player viewer, SCFrame parent, Clan subject) {
        super(parent, viewer);
        this.subject = subject;
        ClanManager manager = SimpleClans.getInstance().getClanManager();
        List<Clan> allies = subject.getAllies().stream().map(manager::getClan).filter(Objects::nonNull).collect(Collectors.toList());
        paginator = new Paginator<>(getPageSize(), allies);
    }

    @Override
    public void createComponents() {
        super.createComponents();

        SCComponent add = new SCComponentImpl.Builder(getConfig(), "add")
                .withViewer(getViewer())
                .withDisplayNameKey("gui.allies.add.title")
                .build();
        add.setVerifiedOnly(ClickType.LEFT);
        add.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AddAllyFrame(this, getViewer(), subject)));
        add.setPermission(ClickType.LEFT, RankPermission.ALLY_ADD);
        add(add);

        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.clanlist.clan.title", Clan::getColorTag, Clan::getName)
                .withLoreKey("gui.allies.clan.lore")
                .withListener(ClickType.RIGHT, (clan) -> () -> InventoryController.runSubcommand(getViewer(),
                        "ally remove", false, clan.getTag()), RankPermission.ALLY_REMOVE)
                .build();
        addAll(list);
    }

    @Override
    public Paginator<Clan> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.allies.title", getViewer());
    }

}
