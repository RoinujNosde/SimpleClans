package net.sacredlabyrinth.phaed.simpleclans.ui;

import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class PageableFrame<T> extends SCFrame {

    public PageableFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
        super(parent, viewer);
    }

    @Override
    @OverridingMethodsMustInvokeSuper
    public void createComponents() {
        super.createComponents();
        SCComponent nextPage = new SCComponentImpl.Builder(getConfig(), "next_page")
                .withDisplayNameKey("gui.next.page.title").build();
        setOneTimeListener(nextPage, this::nextPage);
        add(nextPage);

        SCComponent previousPage = new SCComponentImpl.Builder(getConfig(), "previous_page")
                .withDisplayNameKey("gui.previous.page.title").build();
        setOneTimeListener(previousPage, this::previousPage);
        add(previousPage);
    }

    public abstract Paginator<T> getPaginator();

    private void nextPage() {
        if (getPaginator().nextPage()) {
            update();
        }
    }

    private void previousPage() {
        if (getPaginator().previousPage()) {
            update();
        }
    }

    private void setOneTimeListener(final SCComponent component, final Runnable runnable) {
        component.setListener(ClickType.LEFT, () -> {
            component.setListener(ClickType.LEFT, null);
            if (runnable == null) {
                return;
            }
            runnable.run();
        });
    }

}
