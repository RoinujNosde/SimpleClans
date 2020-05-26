package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author roinujnosde
 */
public class PurgeCommand {

    /**
     * Executes the command
     *
     * @param sender
     * @param args
     */
    public void execute(CommandSender sender, String args[]) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (sender instanceof Player) {
            if (!plugin.getPermissionsManager().has((Player) sender, "simpleclans.admin.purge")) {
                ChatBlock.sendMessage(sender, ChatColor.RED + lang("insufficient.permissions",sender));
                return;
            }
        }

        if (args.length == 1) {
            UUID uuid = UUIDMigration.getForcedPlayerUUID(args[0]);
            if (uuid == null || plugin.getClanManager().getAnyClanPlayer(uuid) == null) {
                ChatBlock.sendMessage(sender, ChatColor.RED + lang("no.player.matched",sender));
                return;
            }
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && InventoryController.isRegistered(player)) {
                player.closeInventory();
            }

            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(uuid);

            Clan clan = cp.getClan();
            if (clan != null && clan.getMembers().size() == 1) {
                clan.disband();
            }
            plugin.getClanManager().deleteClanPlayer(cp);
            ChatBlock.sendMessage(sender, ChatColor.AQUA + lang("player.purged",sender));
            return;
        }

        ChatBlock.sendMessage(sender, ChatColor.RED
                + MessageFormat.format(lang("usage.purge",sender), plugin.getSettingsManager().getCommandClan()));
    }
}
