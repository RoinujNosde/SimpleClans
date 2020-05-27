package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class AlliancesCommand {
    public AlliancesCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.clan.alliances",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.alliances")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        List<Clan> clans = plugin.getClanManager().getClans();
        plugin.getClanManager().sortClansByKDR(clans);

        ChatBlock chatBlock = new ChatBlock();

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + lang("alliances",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        chatBlock.setAlignment("l", "l");
        chatBlock.addRow("  " + headColor + lang("clan",player), lang("allies",player));

        for (Clan clan : clans) {
            if (!clan.isVerified()) {
                continue;
            }

            chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getAllyString(ChatColor.DARK_GRAY + ", "));
        }

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }
}
