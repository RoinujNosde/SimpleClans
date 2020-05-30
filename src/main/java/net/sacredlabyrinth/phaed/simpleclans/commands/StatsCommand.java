package net.sacredlabyrinth.phaed.simpleclans.commands;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class StatsCommand {
    public StatsCommand() {
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

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.stats")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }

        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, RankPermission.STATS, PermissionLevel.TRUSTED, true)) {
        	return;
        }

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.stats",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();
        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " " + lang("stats",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + lang("kdr",player) + " = " + subColor + lang("kill.death.ratio",player));
        ChatBlock.sendMessage(player, headColor + lang("weights",player) + " = " + lang("rival",player) + ": " + subColor + plugin.getSettingsManager().getKwRival() + headColor + " " + lang("neutral",player) + ": " + subColor + plugin.getSettingsManager().getKwNeutral() + headColor + " " + lang("civilian",player) + ": " + subColor + plugin.getSettingsManager().getKwCivilian());
        ChatBlock.sendBlank(player);

        chatBlock.setFlexibility(true, false, false, false, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c", "c", "c", "c");
        chatBlock.addRow("  " + headColor + lang("name", player), lang("kdr", player), lang("rival",player), lang("neutral",player), lang("civilian.abbreviation",player), lang("deaths",player));

        List<ClanPlayer> leaders = clan.getLeaders();
        plugin.getClanManager().sortClanPlayersByKDR(leaders);

        List<ClanPlayer> members = clan.getNonLeaders();
        plugin.getClanManager().sortClanPlayersByKDR(members);

        addRows(leaders, chatBlock);
        addRows(members, chatBlock);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }

    private void addRows(List<ClanPlayer> leaders, ChatBlock chatBlock) {
        SimpleClans plugin = SimpleClans.getInstance();
        NumberFormat formatter = new DecimalFormat("#.#");

        for (ClanPlayer cpm : leaders) {
            String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : (cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor())) + cpm.getName();
            String rival = NumberFormat.getInstance().format(cpm.getRivalKills());
            String neutral = NumberFormat.getInstance().format(cpm.getNeutralKills());
            String civilian = NumberFormat.getInstance().format(cpm.getCivilianKills());
            String deaths = NumberFormat.getInstance().format(cpm.getDeaths());
            String kdr = formatter.format(cpm.getKDR());

            chatBlock.addRow("  " + name, ChatColor.YELLOW + kdr, ChatColor.WHITE + rival, ChatColor.GRAY + neutral, ChatColor.DARK_GRAY + civilian, ChatColor.DARK_RED + deaths);
        }
    }
}
