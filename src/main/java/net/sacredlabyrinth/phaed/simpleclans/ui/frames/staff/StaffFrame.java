package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.GLOBAL_FRIENDLY_FIRE;
import static net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff.ClanListFrame.Type;

public class StaffFrame extends SCFrame {

    public StaffFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
        super(parent, viewer);
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.staff.title", getViewer());
    }

    @Override
    public void createComponents() {
        super.createComponents();
        addClans();
        addPlayers();
        addGlobalFf();
        addReload();
    }

    private void addClans() {
        SCComponent clanList = new SCComponentImpl.Builder(getConfig(), "clans")
                .withViewer(getViewer())
                .withDisplayNameKey("gui.main.clan.list.title")
                .withLoreKey("gui.staff.clan.list.lore.left.click")
                .withLoreKey("gui.staff.clan.list.lore.right.click").build();
        clanList.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer(),
                Type.ALL, null)));
        clanList.setListener(ClickType.RIGHT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer(),
                Type.UNVERIFIED, null)));
        add(clanList);
    }

    private void addPlayers() {
        SCComponent players = new SCComponentImpl.Builder(getConfig(), "players")
                .withViewer(getViewer())
                .withDisplayNameKey("gui.staff.player.list.title")
                .withLoreKey("gui.staff.player.list.lore.left.click")
                .withLoreKey("gui.staff.player.list.lore.right.click").build();
        players.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new PlayerListFrame(getViewer(), this, false)));
        players.setListener(ClickType.RIGHT, () -> InventoryDrawer.open(new PlayerListFrame(getViewer(), this, true)));
        add(players);
    }

    private void addReload() {
        SCComponent reload = new SCComponentImpl.Builder(getConfig(), "reload").withViewer(getViewer())
                .withDisplayNameKey("gui.staff.reload.title").withLoreKey("gui.staff.reload.lore").build();
        reload.setPermission(ClickType.LEFT, "simpleclans.admin.reload");
        reload.setConfirmationRequired(ClickType.LEFT);
        reload.setListener(ClickType.LEFT, () ->
                InventoryController.runSubcommand(getViewer(), "admin reload", false));
        add(reload);
    }

    private void addGlobalFf() {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();
        boolean globalffAllowed = sm.is(GLOBAL_FRIENDLY_FIRE);
        String status = globalffAllowed ? lang("allowed", getViewer()) : lang("auto", getViewer());

        SCComponent globalFf = new SCComponentImpl.Builder(getConfig(), "global_ff").withViewer(getViewer())
                .withDisplayNameKey("gui.staff.global.ff.title")
                .withLoreKey("gui.staff.global.ff.lore.status", status)
                .withLoreKey("gui.staff.global.ff.lore.toggle").build();
        globalFf.setPermission(ClickType.LEFT, "simpleclans.mod.globalff");
        globalFf.setListener(ClickType.LEFT, () -> {
            String arg = globalffAllowed ? "auto" : "allow";
            InventoryController.runSubcommand(getViewer(), "mod globalff", true, arg);
        });
        add(globalFf);
    }
}
