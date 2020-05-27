package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.uuid.UUIDMigration;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.UUID;

/**
 * @author phaed
 */
public class LookupCommand {
    public LookupCommand() {
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
        NumberFormat formatter = new DecimalFormat("#.#");

        String playerName = null;

        if (arg.length == 0) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.lookup")) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                return;
            }
            playerName = player.getName();
        } else if (arg.length == 1) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup")) {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
                return;
            }
            playerName = arg[0];
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.lookup.tag",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        UUID targetUuid = UUIDMigration.getForcedPlayerUUID(playerName);
        if (targetUuid == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.matched",player));
            return;
        }
        ClanPlayer targetCp = plugin.getClanManager().getAnyClanPlayer(targetUuid);
        ClanPlayer myCp = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        Clan myClan = myCp == null ? null : myCp.getClan();

        if (targetCp != null) {
            Clan targetClan = targetCp.getClan();

            ChatBlock.sendBlank(player);
            ChatBlock.saySingle(player, MessageFormat.format(lang("s.player.info",player), plugin.getSettingsManager().getPageClanNameColor() + targetCp.getName() + subColor) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
            ChatBlock.sendBlank(player);

            String clanName = ChatColor.WHITE + lang("none",player);

            if (targetClan != null) {
                clanName = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + targetClan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + targetClan.getName();
            }

            String status = targetClan == null ? ChatColor.WHITE + lang("free.agent",player) : (targetCp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() + lang("leader",player) : (targetCp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() + lang("trusted",player) : plugin.getSettingsManager().getPageUnTrustedColor() + lang("untrusted",player)));
            String rank = ChatColor.WHITE + "" + Helper.parseColors(targetCp.getRankDisplayName());
            String joinDate = ChatColor.WHITE + "" + targetCp.getJoinDateString();
            String lastSeen = ChatColor.WHITE + "" + targetCp.getLastSeenString();
            String inactive = ChatColor.WHITE + "" + targetCp.getInactiveDays() + subColor + "/" + ChatColor.WHITE + plugin.getSettingsManager().getPurgePlayers() + " days";
            String rival = ChatColor.WHITE + "" + targetCp.getRivalKills();
            String neutral = ChatColor.WHITE + "" + targetCp.getNeutralKills();
            String civilian = ChatColor.WHITE + "" + targetCp.getCivilianKills();
            String deaths = ChatColor.WHITE + "" + targetCp.getDeaths();
            String kdr = ChatColor.YELLOW + "" + formatter.format(targetCp.getKDR());
            String pastClans = ChatColor.WHITE + "" + targetCp.getPastClansString(headColor + ", ");

            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("clan.0",player), clanName));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("rank.0",player), rank));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("status.0",player), status));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("kdr.0",player), kdr));
            ChatBlock.sendMessage(player, "  " + subColor + lang("kill.totals",player) + " " + headColor + "[" + lang("rival",player) + ":" + rival + " " + headColor + "" + lang("neutral",player) + ":" + neutral + " " + headColor + "" + lang("civilian",player) + ":" + civilian + headColor + "]");
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("deaths.0",player), deaths));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("join.date.0",player), joinDate));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("last.seen.0",player), lastSeen));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("past.clans.0",player), pastClans));
            ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("inactive.0",player), inactive));

            if (arg.length == 1 && !targetCp.equals(myCp)) {
                String killType = ChatColor.GRAY + lang("neutral",player);

                if (targetClan == null) {
                    killType = ChatColor.DARK_GRAY + lang("civilian",player);
                } else if (myClan != null && myClan.isRival(targetClan.getTag())) {
                    killType = ChatColor.WHITE + lang("rival",player);
                }

                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("kill.type.0",player), killType));
            }

            ChatBlock.sendBlank(player);
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.player.data.found",player));

            if (arg.length == 1 && myClan != null) {
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, MessageFormat.format(lang("kill.type.civilian",player), ChatColor.DARK_GRAY));
            }
        }
    }
}
