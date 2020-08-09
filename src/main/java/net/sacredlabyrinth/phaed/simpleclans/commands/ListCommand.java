package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType.ORDINAL;


/**
 * @author phaed
 */
public class ListCommand {

    private final @NotNull SettingsManager sm;
    private RankingNumberResolver<Clan, ? extends Comparable<?>> ranking;
    private @Nullable String order;
    private final @NotNull String asc;

    public ListCommand() {
        sm = SimpleClans.getInstance().getSettingsManager();
        asc = sm.getListAsc();
    }

    /**
     * Execute the command
     *
     * @param player
     * @param args
     */
    public void execute(Player player, String[] args) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = sm.getPageHeadingsColor();
        String subColor = sm.getPageSubTitleColor();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.anyone.list")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }
        if (args.length > 2) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.list",player), sm.getCommandClan()));
            return;
        }
        List<Clan> clans = plugin.getClanManager().getClans();
        clans = clans.stream().filter(clan -> clan.isVerified() || sm.isShowUnverifiedOnList())
                .collect(Collectors.toList());
        sort(clans, args);

        if (clans.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("no.clans.have.been.created",player));
            return;
        }

        ChatBlock chatBlock = new ChatBlock();
        sendHeader(player, headColor, subColor, clans, chatBlock);

        for (Clan clan : clans) {
            addLine(chatBlock, clan);
        }

        boolean more = chatBlock.sendBlock(player, sm.getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), sm.getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }

    private void addLine(ChatBlock chatBlock, Clan clan) {
        String tag = sm.getClanChatBracketColor() + sm.getClanChatTagBracketLeft() + sm.getTagDefaultColor() + clan.getColorTag() + sm.getClanChatBracketColor() + sm.getClanChatTagBracketRight();
        String name = (clan.isVerified() ? sm.getPageClanNameColor() : ChatColor.GRAY) + clan.getName();
        String fullname = tag + " " + name;
        String size = ChatColor.WHITE + "" + clan.getSize();
        String kdr = clan.isVerified() ? ChatColor.YELLOW + "" + KDRFormat.format(clan.getTotalKDR()) : "";

        chatBlock.addRow("  " + ranking.getRankingNumber(clan), fullname, kdr, size);
    }

    private void sendHeader(Player player, String headColor, String subColor, List<Clan> clans, ChatBlock chatBlock) {
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, sm.getServerName() + subColor + " " + lang("clans.lower", player) + " " + headColor + Helper.generatePageSeparator(sm.getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + lang("total.clans", player) + " " + subColor + clans.size());
        ChatBlock.sendBlank(player);
        chatBlock.setAlignment("c", "l", "c", "c");
        chatBlock.setFlexibility(false, true, false, false);
        chatBlock.addRow("  " + headColor + lang("rank", player), lang("name", player), lang("kdr", player), lang("members", player));
    }

    private void sort(@NotNull List<Clan> clans, @NotNull String[] args) {
        String type = sm.getListDefault();
        if (args.length > 0) {
            type = args[0];
        }
        String desc = sm.getListDesc();
        if (args.length == 2) {
        	order = args[1];
        	if (!order.equalsIgnoreCase(asc) && !order.equalsIgnoreCase(desc)) {
        		order = null;
        	}
        }

		if (type.equalsIgnoreCase(sm.getListActive())) {
		    ranking = new RankingNumberResolver<>(clans, Clan::getLastUsed, isAsc(), ORDINAL);
        }
        if (type.equalsIgnoreCase(sm.getListFounded())) {
        	if (order == null) {
        		order = asc;
        	}
        	ranking = new RankingNumberResolver<>(clans, Clan::getFounded, isAsc(), ORDINAL);
        }
        if (type.equalsIgnoreCase(sm.getListKdr())) {
            ranking = new RankingNumberResolver<>(clans, clan -> KDRFormat.toBigDecimal(clan.getTotalKDR()),
                    isAsc(), sm.getRankingType());
        }
        if (type.equalsIgnoreCase(sm.getListName())) {
        	if (order == null) {
        		order = asc;
        	}
        	ranking = new RankingNumberResolver<>(clans, Clan::getName, isAsc(), ORDINAL);
        }
        if (type.equalsIgnoreCase(sm.getListSize())) {
            ranking = new RankingNumberResolver<>(clans, Clan::getSize, isAsc(), ORDINAL);
        }

        //in case the default type is invalid
        if (ranking == null) {
            ranking = new RankingNumberResolver<>(clans, clan -> KDRFormat.toBigDecimal(clan.getTotalKDR()), false,
                    sm.getRankingType());
        }
	}

    private boolean isAsc() {
        return asc.equalsIgnoreCase(order);
    }
}



