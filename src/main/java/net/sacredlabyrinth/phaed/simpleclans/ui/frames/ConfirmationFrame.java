package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ConfirmationFrame extends SCFrame {

    private final Runnable listener;

    public ConfirmationFrame(@Nullable SCFrame parent, @NotNull Player viewer, @Nullable Runnable listener) {
        super(parent, viewer);
        this.listener = listener;
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.confirmation.title", getViewer());
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    @Override
    public void createComponents() {
        SCComponent confirm = new SCComponentImpl.Builder(XMaterial.LIME_WOOL.parseItem())
                .withDisplayName(lang("gui.confirmation.confirm", getViewer())).withSlot(12).build();
        confirm.setListener(ClickType.LEFT, listener);
        add(confirm);

        SCComponent returnC = new SCComponentImpl.Builder(XMaterial.RED_WOOL.parseItem())
                .withDisplayName(lang("gui.confirmation.return", getViewer())).withSlot(14).build();
        returnC.setListener(ClickType.LEFT, () -> InventoryDrawer.open(getParent()));
        add(returnC);
    }
}
