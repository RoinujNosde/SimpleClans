package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

/**
 * @author phaed
 */
public class ProfileCommand {
    public ProfileCommand() {
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

        Clan clan = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.profile")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
                } else {
                    if (cp.getClan().isVerified()) {
                        clan = cp.getClan();
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
                    }
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            }
        } else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile")) {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clan.matched",player));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.profile.tag",player), plugin.getSettingsManager().getCommandClan()));
        }

        if (clan != null) {
            if (clan.isVerified()) {
                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " " + lang("profile",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);

                String name = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight() + " " + plugin.getSettingsManager().getPageClanNameColor() + clan.getName();
                String description = ChatColor.WHITE + (clan.getDescription() != null && !clan.getDescription().isEmpty() ? clan.getDescription() : lang("no.description",player));
                String leaders = clan.getLeadersString(plugin.getSettingsManager().getPageLeaderColor(), subColor + ", ");
                String onlineCount = ChatColor.WHITE + "" + Helper.stripOffLinePlayers(clan.getMembers()).size();
                String membersOnline = onlineCount + subColor + "/" + ChatColor.WHITE + clan.getSize();
                String inactive = ChatColor.WHITE + "" + clan.getInactiveDays() + subColor + "/" + ChatColor.WHITE + (clan.isVerified() ? plugin.getSettingsManager().getPurgeClan() : plugin.getSettingsManager().getPurgeUnverified()) + " " + lang("days",player);
                String founded = ChatColor.WHITE + "" + clan.getFoundedString();
                String allies = ChatColor.WHITE + "" + clan.getAllyString(subColor + ", ");
                String rivals = ChatColor.WHITE + "" + clan.getRivalString(subColor + ", ");
                String kdr = ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR());
                String deaths = ChatColor.WHITE + "" + clan.getTotalDeaths();
                String rival = ChatColor.WHITE + "" + clan.getTotalRival();
                String neutral = ChatColor.WHITE + "" + clan.getTotalNeutral();
                String civ = ChatColor.WHITE + "" + clan.getTotalCivilian();
                String status = ChatColor.WHITE + "" + (clan.isVerified() ? plugin.getSettingsManager().getPageTrustedColor() + lang("verified",player) : plugin.getSettingsManager().getPageUnTrustedColor() + lang("unverified",player));
                String feeEnabled = ChatColor.WHITE + (clan.isMemberFeeEnabled() ? lang("fee.enabled",player) : lang("fee.disabled",player));
                String feeValue = ChatColor.WHITE + "" + clan.getMemberFee();
                
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("name.0",player), name));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("description.0",player), description));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("status.0",player), status));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("leaders.0",player), leaders));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("members.online.0",player), membersOnline));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("kdr.0",player), kdr));
                ChatBlock.sendMessage(player, "  " + subColor + lang("kill.totals",player) + " " + headColor + "[" + lang("rival",player) + ":" + rival + " " + headColor + "" + lang("neutral",player) + ":" + neutral + " " + headColor + "" + lang("civilian",player) + ":" + civ + headColor + "]");
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("deaths.0",player), deaths));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("fee.0.value.1",player), feeEnabled, feeValue));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("allies.0",player), allies));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("rivals.0",player), rivals));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("founded.0",player), founded));
                ChatBlock.sendMessage(player, "  " + subColor + MessageFormat.format(lang("inactive.0",player), inactive));

                ChatBlock.sendBlank(player);
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            }
        }
    }
}
