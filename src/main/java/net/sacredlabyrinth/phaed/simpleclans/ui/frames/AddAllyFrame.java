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

public class AddAllyFrame extends PageableFrame<Clan> {
    private final Paginator<Clan> paginator;

    public AddAllyFrame(SCFrame parent, Player viewer, Clan subject) {
        super(parent, viewer);
        SimpleClans plugin = SimpleClans.getInstance();
        List<Clan> notAllies = plugin.getClanManager().getClans().stream()
                .filter(c -> !c.equals(subject) && !c.isRival(subject.getTag()) && !c.isAlly(subject.getTag()))
                .collect(Collectors.toList());
        paginator = new Paginator<>(getSize() - 9, notAllies);
    }

    @Override
    public void createComponents() {
        super.createComponents();
        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withLoreKey("gui.add.ally.clan.lore")
                .withListener(ClickType.LEFT, this::addAlly, RankPermission.ALLY_ADD)
                .build();
        addAll(list);
    }

    private Runnable addAlly(Clan clan) {
        return () -> InventoryController.runSubcommand(getViewer(), "ally add", false, clan.getTag());
    }

    @Override
    public Paginator<Clan> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.add.ally.title", getViewer());
    }

}
