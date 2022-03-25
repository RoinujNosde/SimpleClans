package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

public abstract class Sendable {

    protected final SimpleClans plugin;
    protected final SettingsManager sm;
    protected final ClanManager cm;
    protected final CommandSender sender;
    protected final ChatBlock chatBlock = new ChatBlock();
    protected final String headColor;
    protected final String subColor;


    public Sendable(@NotNull SimpleClans plugin, @NotNull CommandSender sender) {
        this.plugin = plugin;
        sm = plugin.getSettingsManager();
        cm = plugin.getClanManager();
        this.sender = sender;
        headColor = sm.getColored(PAGE_HEADINGS_COLOR);
        subColor = sm.getColored(PAGE_SUBTITLE_COLOR);
    }

    public abstract void send();
}
