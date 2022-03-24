package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import java.util.UUID;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ResetKDRCommand {

    public ResetKDRCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (arg == null || arg.length == 0) {
            if (!plugin.getSettingsManager().isAllowResetKdr()) {
                ChatBlock.sendMessage(player, ChatColor.RED
                        + MessageFormat.format(plugin.getLang("usage.resetkdr"),
                                plugin.getSettingsManager().getCommandClan()));
                return;
            }
            if (!plugin.getPermissionsManager().has(player, "simpleclans.vip.resetkdr")) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp == null) {
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("not.a.member.of.any.clan"));
                return;
            }
            if (plugin.getClanManager().purchaseResetKdr(player)) {
                plugin.getClanManager().resetKdr(cp);
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.have.reseted.your.kdr"));
            }
            return;
        }
        if (arg.length == 1 && plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
            if (arg[0].equalsIgnoreCase("all")) {
                for (ClanPlayer cp : SimpleClans.getInstance().getClanManager().getAllClanPlayers()) {
                    plugin.getClanManager().resetKdr(cp);
                }
                ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("you.have.reseted.kdr.of.all.players"));
            } else {                
            	UUID uuid = UUIDMigration.getForcedPlayerUUID(arg[0]);
                if (uuid == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.matched"));
                    return;
                }
                ClanPlayer trcp = plugin.getClanManager().getClanPlayer(uuid);
                if (trcp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("no.player.data.found"));
                    return;
                }
                plugin.getClanManager().resetKdr(trcp);
                ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(plugin.getLang("you.have.reseted.0.kdr"), trcp.getName()));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + plugin.getLang("insufficient.permissions"));
        }
    }
}
