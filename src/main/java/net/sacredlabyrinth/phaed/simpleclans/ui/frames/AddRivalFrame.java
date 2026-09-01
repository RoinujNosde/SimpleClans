package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class AddRivalFrame extends PageableFrame<Clan> {

    private final Paginator<Clan> paginator;

    public AddRivalFrame(SCFrame parent, Player viewer, Clan subject) {
        super(parent, viewer);
        SimpleClans plugin = SimpleClans.getInstance();
        List<Clan> notRivals = plugin.getClanManager().getClans().stream().filter(c -> !c.equals(subject) && !c.isRival(subject.getTag()) && !c.isAlly(subject.getTag())).collect(Collectors.toList());
        paginator = new Paginator<>(getPageSize(), notRivals);
    }

    @Override
    public void createComponents() {
        super.createComponents();
        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.clanlist.clan.title", Clan::getColorTag, Clan::getName)
                .withLoreKey("gui.add.rival.clan.lore")
                .withListener(ClickType.LEFT, this::addRival, RankPermission.RIVAL_ADD)
                .withConfirmationRequired(ClickType.LEFT).build();
        addAll(list);
    }

    private Runnable addRival(Clan clan) {
        return () -> InventoryController.runSubcommand(getViewer(), "rival add", false, clan.getTag());
    }

    @Override
    public Paginator<Clan> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.add.rival.title", getViewer());
    }
}
