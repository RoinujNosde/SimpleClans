package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType.ORDINAL;
import static org.bukkit.ChatColor.*;

public class ClanList extends Sendable {

    private final @Nullable String type;
    private final @Nullable String order;

    public ClanList(@NotNull SimpleClans plugin,
                    @NotNull CommandSender sender,
                    @Nullable String type,
                    @Nullable String order) {
        super(plugin, sender);
        this.type = type;
        this.order = order;
    }

    @Override
    public void send() {
        List<Clan> clans = getListableClans();
        if (clans.isEmpty()) {
            ChatBlock.sendMessage(sender, RED + lang("no.clans.have.been.created", sender));
            return;
        }
        RankingNumberResolver<Clan, ? extends Comparable<?>> ranking = getRankingResolver(clans, type, order);
        sendHeader(clans);
        for (Clan clan : clans) {
            addLine(ranking, clan);
        }

        sendBlock();
    }

    private RankingNumberResolver<Clan, ? extends Comparable<?>> getRankingResolver(List<Clan> clans,
                                                                                    @Nullable String type,
                                                                                    @Nullable String order) {
        boolean ascending = order == null || lang("list.order.asc").equalsIgnoreCase(order);
        if (type == null) {
            type = sm.getListDefaultOrderBy();
        }
        if (type.equalsIgnoreCase(lang("list.type.size"))) {
            return new RankingNumberResolver<>(clans, Clan::getSize, order != null && ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.active"))) {
            return new RankingNumberResolver<>(clans, Clan::getLastUsed, order != null && ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.founded"))) {
            return new RankingNumberResolver<>(clans, Clan::getFounded, ascending, ORDINAL);
        }
        if (type.equalsIgnoreCase(lang("list.type.name"))) {
            return new RankingNumberResolver<>(clans, Clan::getName, ascending, ORDINAL);
        }
        return new RankingNumberResolver<>(clans, clan -> KDRFormat.toBigDecimal(clan.getTotalKDR()),
                order != null && ascending, sm.getRankingType());
    }

    @NotNull
    private List<Clan> getListableClans() {
        List<Clan> clans = plugin.getClanManager().getClans();
        clans = clans.stream().filter(clan -> clan.isVerified() || sm.isShowUnverifiedOnList())
                .collect(Collectors.toList());
        return clans;
    }

    private void sendHeader(List<Clan> clans) {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.getServerName() + subColor + " " + lang("clans.lower", sender)
                + " " + headColor + Helper.generatePageSeparator(sm.getPageSep()));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("total.clans", sender) + " " + subColor + clans.size());
        ChatBlock.sendBlank(sender);
        chatBlock.setAlignment("c", "l", "c", "c");
        chatBlock.setFlexibility(false, true, false, false);
        chatBlock.addRow("  " + headColor + lang("rank", sender), lang("name", sender),
                lang("kdr", sender), lang("members", sender));
    }

    @SuppressWarnings("deprecation")
    private void addLine(RankingNumberResolver<Clan, ? extends Comparable<?>> ranking, Clan clan) {
        String tag = sm.getClanChatBracketColor() + sm.getClanChatTagBracketLeft()
                + sm.getTagDefaultColor() + clan.getColorTag() + sm.getClanChatBracketColor()
                + sm.getClanChatTagBracketRight();
        String name = (clan.isVerified() ? sm.getPageClanNameColor() : GRAY) + clan.getName();
        String fullname = tag + " " + name;
        String size = WHITE + "" + clan.getSize();
        String kdr = clan.isVerified() ? YELLOW + "" + KDRFormat.format(clan.getTotalKDR()) : "";

        chatBlock.addRow("  " + ranking.getRankingNumber(clan), fullname, kdr, size);
    }
}
