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
        headColor = sm.get(PAGE_HEADINGS_COLOR);
        subColor = sm.get(PAGE_SUBTITLE_COLOR);
    }

    protected void sendBlock() {
        SettingsManager sm = plugin.getSettingsManager();
        boolean more = chatBlock.sendBlock(sender, sm.getInt(PAGE_SIZE));

        if (more) {
            plugin.getStorageManager().addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, sm.get(PAGE_HEADINGS_COLOR) + lang("view.next.page", sender,
                    sm.get(COMMANDS_MORE)));
        }
        ChatBlock.sendBlank(sender);
    }

    public abstract void send();
}
