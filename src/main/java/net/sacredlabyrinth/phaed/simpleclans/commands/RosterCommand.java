package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author phaed
 */
public class RosterCommand {

    public RosterCommand() {
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

        Clan clan = null;

        if (arg.length == 0) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.roster")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
                } else {
                    clan = cp.getClan();
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            }
        } else if (arg.length == 1) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster")) {
                clan = plugin.getClanManager().getClan(arg[0]);

                if (clan == null) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clan.matched",player));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.roster.tag",player), plugin.getSettingsManager().getCommandClan()));
        }

        if (clan != null) {
            if (clan.isVerified()) {
                ChatBlock chatBlock = new ChatBlock();

                ChatBlock.sendBlank(player);
                ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " " + lang("roster",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
                ChatBlock.sendBlank(player);
                ChatBlock.sendMessage(player, headColor + lang("legend",player) + " " + plugin.getSettingsManager().getPageLeaderColor() + lang("leader",player) + headColor + ", " + plugin.getSettingsManager().getPageTrustedColor() + lang("trusted",player) + headColor + ", " + plugin.getSettingsManager().getPageUnTrustedColor() + lang("untrusted",player));
                ChatBlock.sendBlank(player);

                chatBlock.setFlexibility(false, true, false, true);
                chatBlock.addRow("  " + headColor + lang("player",player), lang("rank",player), lang("seen",player));

                List<ClanPlayer> leaders = clan.getLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(leaders);

                List<ClanPlayer> members = clan.getNonLeaders();
                plugin.getClanManager().sortClanPlayersByLastSeen(members);

                for (ClanPlayer cp : leaders) {

                    Player p = cp.toPlayer();

                    String name = plugin.getSettingsManager().getPageLeaderColor() + cp.getName();
                    String lastSeen = p != null && p.isOnline() && !Helper.isVanished(p) ? ChatColor.GREEN + lang("online",player) : ChatColor.WHITE + cp.getLastSeenDaysString(player);

                    chatBlock.addRow("  " + name, ChatColor.YELLOW + Helper.parseColors(cp.getRankDisplayName()) + ChatColor.RESET, lastSeen);

                }

                for (ClanPlayer cp : members) {
                    Player p = cp.toPlayer();

                    String name = (cp.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor()) + cp.getName();
                    String lastSeen = p != null && p.isOnline() && !Helper.isVanished(p) ? ChatColor.GREEN + lang("online",player) : ChatColor.WHITE + cp.getLastSeenDaysString(player);

                    chatBlock.addRow("  " + name, ChatColor.YELLOW + Helper.parseColors(cp.getRankDisplayName()) + ChatColor.RESET, lastSeen);
                }

                boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

                if (more) {
                    plugin.getStorageManager().addChatBlock(player, chatBlock);
                    ChatBlock.sendBlank(player);
                    ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
                }

                ChatBlock.sendBlank(player);
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            }
        } else {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.roster.tag",player), plugin.getSettingsManager().getCommandClan()));
        }
    }
}
