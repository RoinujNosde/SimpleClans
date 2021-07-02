package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.PAGE_SEPARATOR;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.SERVER_NAME;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.DARK_GRAY;

public class Alliances extends Sendable {

    public Alliances(@NotNull SimpleClans plugin, @NotNull CommandSender sender) {
        super(plugin, sender);
    }

    @Override
    public void send() {
        List<Clan> clans = cm.getClans();
        cm.sortClansByKDR(clans);
        sendHeader();

        for (Clan clan : clans) {
            if (!clan.isVerified()) {
                continue;
            }

            chatBlock.addRow("  " + AQUA + clan.getName(), clan.getAllyString(DARK_GRAY + ", ", sender));
        }

        sendBlock();
    }

    private void sendHeader() {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.get(SERVER_NAME) + subColor + " " +
                lang("alliances", sender) + " " + headColor +
                Helper.generatePageSeparator(sm.get(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);

        chatBlock.setAlignment("l", "l");
        chatBlock.addRow("  " + headColor + lang("clan", sender), lang("allies", sender));
    }
}
