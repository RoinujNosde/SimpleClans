package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author phaed
 */
public class CoordsCommand {
    public CoordsCommand() {
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

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.coords")) {
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
        if (!plugin.getPermissionsManager().has(player, RankPermission.COORDS, PermissionLevel.TRUSTED, true)) {
        	return;
        }

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.coords",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();

        chatBlock.setFlexibility(true, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c");

        chatBlock.addRow("  " + headColor + lang("name",player), lang("distance",player), lang("coords.upper",player), lang("world",player));

        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getMembers());

        Map<Integer, List<String>> rows = new TreeMap<>();

        for (ClanPlayer cpm : members) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : (cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor())) + cpm.getName();
                Location loc = p.getLocation();
                int distance = (int) Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                String world = loc.getWorld().getName();

                List<String> cols = new ArrayList<>();
                cols.add("  " + name);
                cols.add(ChatColor.AQUA + "" + distance);
                cols.add(ChatColor.WHITE + "" + coords);
                cols.add(world);
                rows.put(distance, cols);
            }
        }

        if (rows.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("you.are.the.only.member.online",player));
            return;
        }

        for (List<String> col : rows.values()) {
            chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
        }

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " " + lang("coords",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }
}
