package net.sacredlabyrinth.phaed.simpleclans.ui.frames.staff;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.Components;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
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
    public int getSize() {
        return 2 * 9;
    }

    @Override
    public void createComponents() {
        for (int slot = 0; slot < 9; slot++) {
            if (slot == 4)
                continue;
            add(Components.getPanelComponent(slot));
        }

        add(Components.getBackComponent(getParent(), 4, getViewer()));
        addClans();
        addPlayers();
        addGlobalFf();
        addReload();
    }

    private void addClans() {
        ItemStack item = new ItemStack(Material.BANNER);
        BannerMeta bannerMeta = (BannerMeta) Objects.requireNonNull(item.getItemMeta());
        bannerMeta.setBaseColor(DyeColor.PURPLE);
        item.setItemMeta(bannerMeta);
        SCComponent clanList = new SCComponentImpl.Builder(item)
                .withDisplayName(lang("gui.main.clan.list.title", getViewer())).withSlot(9)
                .withLoreLine(lang("gui.staff.clan.list.lore.left.click", getViewer()))
                .withLoreLine(lang("gui.staff.clan.list.lore.right.click", getViewer())).build();
        clanList.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer(),
                Type.ALL, null)));
        clanList.setListener(ClickType.RIGHT, () -> InventoryDrawer.open(new ClanListFrame(this, getViewer(),
                Type.UNVERIFIED, null)));
        add(clanList);
    }

    private void addPlayers() {
        ItemStack item = new ItemStack(Material.BANNER);
        BannerMeta bannerMeta = (BannerMeta) Objects.requireNonNull(item.getItemMeta());
        bannerMeta.setBaseColor(DyeColor.WHITE);
        item.setItemMeta(bannerMeta);
        SCComponent players = new SCComponentImpl.Builder(item)
                .withDisplayName(lang("gui.staff.player.list.title", getViewer())).withSlot(10)
                .withLoreLine(lang("gui.staff.player.list.lore.left.click", getViewer()))
                .withLoreLine(lang("gui.staff.player.list.lore.right.click", getViewer())).build();
        players.setListener(ClickType.LEFT, () -> InventoryDrawer.open(new PlayerListFrame(getViewer(), this, false)));
        players.setListener(ClickType.RIGHT, () -> InventoryDrawer.open(new PlayerListFrame(getViewer(), this, true)));
        add(players);
    }

    private void addReload() {
        SCComponent reload = new SCComponentImpl.Builder(Material.MOB_SPAWNER)
                .withDisplayName(lang("gui.staff.reload.title", getViewer())).withSlot(17)
                .withLoreLine(lang("gui.staff.reload.lore", getViewer())).build();
        reload.setPermission(ClickType.LEFT, "simpleclans.admin.reload");
        reload.setConfirmationRequired(ClickType.LEFT);
        reload.setListener(ClickType.LEFT, () ->
                InventoryController.runSubcommand(getViewer(), "reload", false));
        add(reload);
    }

    private void addGlobalFf() {
        SettingsManager sm = SimpleClans.getInstance().getSettingsManager();
        boolean globalffAllowed = sm.isGlobalff();
        String status = globalffAllowed ? lang("allowed", getViewer()) : lang("auto", getViewer());
        SCComponent globalFf = new SCComponentImpl.Builder(Material.DIAMOND_SWORD).withSlot(12)
                .withDisplayName(lang("gui.staff.global.ff.title", getViewer()))
                .withLoreLine(lang("gui.staff.global.ff.lore.status", getViewer(), status))
                .withLoreLine(lang("gui.staff.global.ff.lore.toggle", getViewer())).build();
        globalFf.setPermission(ClickType.LEFT, "simpleclans.mod.globalff");
        globalFf.setListener(ClickType.LEFT, () -> {
            String subcommand = "globalff ";
            if (globalffAllowed) {
                subcommand += lang("auto", getViewer());
            } else {
                subcommand += lang("allow", getViewer());
            }
            InventoryController.runSubcommand(getViewer(), subcommand, true);
        });
        add(globalFf);
    }
}
