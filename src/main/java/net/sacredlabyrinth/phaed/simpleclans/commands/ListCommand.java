package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.List;


/**
 * @author phaed
 */
public class ListCommand {
    public ListCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param args
     */
    public void execute(Player player, String[] args) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();
        NumberFormat formatter = new DecimalFormat("#.#");

        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.list")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }
        if (args.length > 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.list",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }
        List<Clan> clans = plugin.getClanManager().getClans();

        sort(plugin, clans, args);

        if (clans.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clans.have.been.created",player));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + lang("clans.lower",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + lang("total.clans",player) + " " + subColor + clans.size());
        ChatBlock.sendBlank(player);
        chatBlock.setAlignment("c", "l", "c", "c");
        chatBlock.setFlexibility(false, true, false, false);
        chatBlock.addRow("  " + headColor + lang("rank",player), lang("name",player), lang("kdr",player), lang("members",player));

        int rank = 1;

        for (Clan clan : clans) {
            if (!plugin.getSettingsManager().isShowUnverifiedOnList() && !clan.isVerified()) {
                continue;
            }

            String tag = plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketLeft() + plugin.getSettingsManager().getTagDefaultColor() + clan.getColorTag() + plugin.getSettingsManager().getClanChatBracketColor() + plugin.getSettingsManager().getClanChatTagBracketRight();
            String name = (clan.isVerified() ? plugin.getSettingsManager().getPageClanNameColor() : ChatColor.GRAY) + clan.getName();
            String fullname = tag + " " + name;
            String size = ChatColor.WHITE + "" + clan.getSize();
            String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + formatter.format(clan.getTotalKDR()) : "";

            chatBlock.addRow("  " + rank, fullname, kdr, size);
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

	private void sort(SimpleClans plugin, List<Clan> clans, String[] args) {
		SettingsManager sm = plugin.getSettingsManager();
		String type = sm.getListDefault();
        String order = null;
        if (args.length > 0) {
        	type = args[0];
        }
        String asc = sm.getListAsc();
        String desc = sm.getListDesc();
        if (args.length == 2) {
        	order = args[1];
        	if (!order.equalsIgnoreCase(asc) && !order.equalsIgnoreCase(desc)) {
        		order = null;
        	}
        }
        ClanManager cm = plugin.getClanManager();
        //in case the default type is invalid
        cm.sortClansByKDR(clans);
        
		if (type.equalsIgnoreCase(sm.getListActive())) {
        	cm.sortClansByActive(clans, asc.equalsIgnoreCase(order));
        }
        if (type.equalsIgnoreCase(sm.getListFounded())) {
        	if (order == null) {
        		order = asc;
        	}
        	cm.sortClansByFounded(clans, asc.equalsIgnoreCase(order));
        }
        if (type.equalsIgnoreCase(sm.getListKdr())) {
        	cm.sortClansByKDR(clans, asc.equalsIgnoreCase(order));
        }
        if (type.equalsIgnoreCase(sm.getListName())) {
        	if (order == null) {
        		order = asc;
        	}
        	cm.sortClansByName(clans, asc.equalsIgnoreCase(order));
        }
        if (type.equalsIgnoreCase(sm.getListSize())) {
        	cm.sortClansBySize(clans, asc.equalsIgnoreCase(order));
        }
	}
}



