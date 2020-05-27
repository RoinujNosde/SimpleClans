package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.MessageFormat;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.PermissionLevel;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author roinujnosde
 */
public class FeeCommand {

    /**
     * Executes the command
     *
     * @param player
     * @param args
     */
    public void execute(Player player, String[] args) {
        SimpleClans plugin = SimpleClans.getInstance();
        if (!plugin.getSettingsManager().isMemberFee()) {
        	ChatBlock.sendMessage(player, ChatColor.RED + lang("disabled.command",player));
            return;
        }
        
        if (args.length >= 1) {
            ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
            Clan clan = cp.getClan();
            if (!clan.isVerified()) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
                return;
            }

            if (args[0].equalsIgnoreCase("check")) {
                if (!plugin.getPermissionsManager().has(player, "simpleclans.member.fee-check")) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                    return;
                }
                ChatBlock.sendMessage(player, ChatColor.AQUA
                        + MessageFormat.format(
                        		lang("the.fee.is.0.and.its.current.value.is.1",player),
                                clan.isMemberFeeEnabled() ? lang("fee.enabled",player) : lang("fee.disabled",player),
                                clan.getMemberFee()
                        ));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
                if (!plugin.getPermissionsManager().has(player, RankPermission.FEE_SET, PermissionLevel.LEADER, true)) {
                    return;
                }
                if (!cp.isLeader()) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
                    return;
                }
                double newFee = 0;
                try {
                    newFee = Double.parseDouble(args[1]);
                } catch (NumberFormatException ignored) {
                    player.sendMessage(ChatColor.RED + lang("invalid.fee",player));
                    return;
                }
                double maxFee = plugin.getSettingsManager().getMaxMemberFee();
                if (newFee > maxFee) {
                    ChatBlock.sendMessage(player, ChatColor.RED
                            + MessageFormat.format(lang("max.fee.allowed.is.0",player), maxFee));
                    return;
                }
                
                if (plugin.getClanManager().purchaseMemberFeeSet(player)) {
                    clan.setMemberFee(newFee);
                    clan.addBb(player.getName(), ChatColor.AQUA + MessageFormat.format(lang("bb.fee.set",player), newFee));
                    ChatBlock.sendMessage(player, ChatColor.AQUA + lang("fee.set",player));
                    plugin.getStorageManager().updateClan(clan);                    
                }
                return;
            }
        }
        ChatBlock.sendMessage(player, ChatColor.RED
                + MessageFormat.format(lang("usage.fee",player), plugin.getSettingsManager().getCommandClan()));
    }
}
