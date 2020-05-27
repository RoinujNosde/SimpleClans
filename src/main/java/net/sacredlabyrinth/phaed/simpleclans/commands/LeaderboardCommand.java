package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * @author phaed
 */
public class LeaderboardCommand {
    public LeaderboardCommand() {
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

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.leaderboard",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }
        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        List<ClanPlayer> clanPlayers = plugin.getClanManager().getAllClanPlayers();
        plugin.getClanManager().sortClanPlayersByKDR(clanPlayers);

        ChatBlock chatBlock = new ChatBlock();

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + lang("leaderboard.command",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("total.clan.players.0",player), subColor + clanPlayers.size()));
        ChatBlock.sendBlank(player);

        chatBlock.setAlignment("c", "l", "c", "c", "c", "c");
        chatBlock.addRow("  " + headColor + lang("rank",player), lang("player",player), lang("kdr",player), lang("clan",player), lang("seen",player));

        int rank = 1;

        for (ClanPlayer cp : clanPlayers) {
            Player p = cp.toPlayer();

            boolean isOnline = false;

            if (p != null) {
                isOnline = true;
            }

            String name = (cp.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : (cp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor())) + cp.getName();
            String lastSeen = isOnline ? ChatColor.GREEN + lang("online", player) : ChatColor.WHITE + cp.getLastSeenDaysString(player);

            String clanTag = ChatColor.WHITE + lang("free.agent", player);

            if (cp.getClan() != null) {
                clanTag = cp.getClan().getColorTag();
            }

            chatBlock.addRow("  " + rank, name, ChatColor.YELLOW + "" + formatter.format(cp.getKDR()), ChatColor.WHITE + clanTag, lastSeen);
            rank++;
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
