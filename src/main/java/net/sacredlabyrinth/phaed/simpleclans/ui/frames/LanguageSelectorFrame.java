package net.sacredlabyrinth.phaed.simpleclans.ui.frames;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.ui.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.Paginator;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class LanguageSelectorFrame extends PageableFrame<Locale> {

    private final ClanPlayer clanPlayer;
    private final SimpleClans plugin;
    private final Paginator<Locale> paginator;

    public LanguageSelectorFrame(@Nullable SCFrame parent, @NotNull Player viewer) {
        super(parent, viewer);
        plugin = SimpleClans.getInstance();
        clanPlayer = plugin.getClanManager().getCreateClanPlayer(viewer.getUniqueId());
        List<Locale> languages = LanguageResource.getAvailableLocales();
        paginator = new Paginator<>(getPageSize(), languages);
    }

    @Override
    public @NotNull String getTitle() {
        Locale locale = clanPlayer.getLocale();
        if (locale == null) {
            locale = plugin.getSettingsManager().getLanguage();
        }
        return lang("gui.languageselector.title", getViewer(), locale.toLanguageTag());
    }

    @Override
    public void createComponents() {
        super.createComponents();
        List<SCComponent> list = new SCComponentImpl.ListBuilder<>(getConfig(), "list", paginator.getCurrentElements())
                .withViewer(getViewer())
                .withDisplayNameKey("gui.languageselector.language.title",
                        loc -> loc.toLanguageTag().replace("-", "_"))
                .withLoreKey("gui.languageselector.language.lore.left.click")
                .withLoreKey("gui.languageselector.language.lore.translation.status", LanguageResource::getTranslationStatus)
                .withLoreLine(loc -> !loc.equals(Locale.ENGLISH)
                        ? lang("gui.languageselector.language.lore.right.click", getViewer()) : "")
                .withListener(ClickType.LEFT, this::setLocale, (String) null)
                .withListener(ClickType.RIGHT, this::translate, (String) null).build();
        addAll(list);

    }

    private Runnable setLocale(Locale locale) {
        return () -> {
            clanPlayer.setLocale(locale);
            plugin.getStorageManager().updateClanPlayer(clanPlayer);
            InventoryDrawer.open(this);
        };
    }

    private Runnable translate(Locale locale) {
        return () -> {
            if (!locale.equals(Locale.ENGLISH)) {
                getViewer().sendMessage(lang("click.to.help.translating", getViewer(), getCrowdinLink(locale)));
                getViewer().closeInventory();
            }
        };
    }

    private String getCrowdinLink(@NotNull Locale locale) {
        String base = "https://crowdin.com/project/simpleclans/";
        //only known exception
        if (locale.equals(new Locale("uk", "UA"))) {
            return base + "uk";
        }
        if (locale.getLanguage().equalsIgnoreCase(locale.getCountry())) {
            return base + locale.getLanguage();
        }

        return base + locale.toLanguageTag();
    }

    @Override
    public Paginator<Locale> getPaginator() {
        return paginator;
    }

}
