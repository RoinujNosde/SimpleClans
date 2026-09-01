package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class PermissionsFrame extends PageableFrame<String> {
    private final Rank rank;
    private final Paginator<String> paginator;

    public PermissionsFrame(SCFrame parent, Player viewer, Rank rank) {
        super(parent, viewer);
        this.rank = rank;
        paginator = new Paginator<>(getPageSize(), new ArrayList<>(rank.getPermissions()));
    }

    @Override
    public void createComponents() {
        super.createComponents();

        SCComponent add = new SCComponentImpl.Builder(getConfig(), "add")
                .withViewer(getViewer())
                .withDisplayNameKey("gui.permissions.add.title")
                .build();
        add.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new AddPermissionFrame(this, getViewer(), rank)));
        add.setPermission(ClickType.LEFT, "simpleclans.leader.rank.permissions.add");
        add(add);

        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.permissions.permission.title", p -> p)
                .withLoreKey("gui.permissions.permission.lore")
                .withListener(ClickType.RIGHT, this::removePermission, "simpleclans.leader.rank.permissions.remove")
                .build();
        addAll(list);
    }

    private Runnable removePermission(String permission) {
        return () -> InventoryController.runSubcommand(getViewer(),
                "rank permissions remove", true, rank.getName(), permission);
    }

    @Override
    public Paginator<String> getPaginator() {
        return paginator;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.permissions.title", getViewer(), rank.getName());
    }

}
