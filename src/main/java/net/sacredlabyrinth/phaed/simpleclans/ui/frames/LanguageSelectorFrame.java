package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponent;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCComponentImpl;
import net.sacredlabyrinth.phaed.simpleclans.ui.SCFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class LanguageSelectorFrame extends SCFrame {

    private final ClanPlayer clanPlayer;
    private final SimpleClans plugin;
    private Paginator paginator;

    public LanguageSelectorFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
        super(parent, viewer);
        plugin = SimpleClans.getInstance();
        clanPlayer = plugin.getClanManager().getCreateClanPlayer(viewer.getUniqueId());
    }

    @Override
    public @NotNull String getTitle() {
        return lang("gui.languageselector.title", clanPlayer.getLocale().getDisplayName());
    }

    @Override
    public int getSize() {
        return 3 * 9;
    }

    @Override
    public void createComponents() {
        List<Locale> languages = LanguageResource.getAvailableLocales();
        paginator = new Paginator(getSize() - 9, languages.size());
        addHeader();
        int slot = 9;
        for (int i = paginator.getMinIndex(); paginator.isValidIndex(i); i++) {
            Locale locale = languages.get(i);
            // TODO Display translation status
            SCComponent c = new SCComponentImpl.Builder(Material.PAPER)
                    .withDisplayName(lang("gui.languageselector.language.title", locale.getDisplayName()))
                    .withSlot(slot).withLore(Collections.singletonList(lang("gui.languageselector.language.lore")))
                    .build();
            c.setListener(ClickType.LEFT, () -> {
                clanPlayer.setLocale(locale);
                plugin.getStorageManager().updateClanPlayer(clanPlayer);
            });
            add(c);
            slot++;
        }
    }

    public void addHeader() {
        for (int slot = 0; slot < 9; slot++) {
            if (slot == 2 || slot == 6 || slot == 7)
                continue;
            add(Components.getPanelComponent(slot));
        }
        add(Components.getBackComponent(getParent(), 2, getViewer()));

        add(Components.getPreviousPageComponent(6, this::previousPage, paginator, getViewer()));
        add(Components.getNextPageComponent(7, this::nextPage, paginator, getViewer()));
    }

    private void previousPage() {
        if (paginator.previousPage()) {
            updateFrame();
        }
    }

    private void nextPage() {
        if (paginator.nextPage()) {
            updateFrame();
        }
    }

    private void updateFrame() {
        InventoryDrawer.open(this);
    }
}
