package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class ClanProfile extends Sendable {

    private final Clan clan;

    public ClanProfile(@NotNull SimpleClans plugin, @NotNull CommandSender sender, @NotNull Clan clan) {
        super(plugin, sender);
        this.clan = clan;
    }

    @Override
    public void send() {
        String message = lang("clan.profile").replace("%clan_name%", clan.getName())
                .replace("%clan_color_tag%", clan.getColorTag())
                .replace("%clan_description%", getDescription())
                .replace("%clan_verified%", getVerifiedStatus())
                .replace("%clan_leaders%", clan.getLeadersString(sm.getPageLeaderColor(), subColor + ", "))
                .replace("%clan_online_count%", String.valueOf(clan.getOnlineMembers().size()))
                .replace("%clan_size%", String.valueOf(clan.getSize()))
                .replace("%clan_kdr%", KDRFormat.format(clan.getTotalKDR()))
                .replace("%clan_rival_kills%", String.valueOf(clan.getTotalRival()))
                .replace("%clan_neutral_kills%", String.valueOf(clan.getTotalNeutral()))
                .replace("%clan_civilian_kills%", String.valueOf(clan.getTotalCivilian()))
                .replace("%clan_deaths%", String.valueOf(clan.getTotalDeaths()))
                .replace("%clan_fee_enabled%", getFeeEnabled())
                .replace("%clan_fee_value%", String.valueOf(clan.getMemberFee()))
                .replace("%clan_allies%", clan.getAllyString(subColor + ", "))
                .replace("%clan_rivals%", clan.getRivalString(subColor + ", "))
                .replace("%clan_founded%", clan.getFoundedString())
                .replace("%clan_inactive_days%", String.valueOf(clan.getInactiveDays()))
                .replace("%clan_max_inactive_days%", getMaxInactiveDays());
        sender.sendMessage(message);
    }

    @NotNull
    private String getMaxInactiveDays() {
        return String.valueOf(clan.isVerified() ?
                sm.getPurgeClan() : sm.getPurgeUnverified());
    }

    @NotNull
    private String getFeeEnabled() {
        return clan.isMemberFeeEnabled() ? lang("fee.enabled", sender) : lang("fee.disabled", sender);
    }

    @NotNull
    private String getVerifiedStatus() {
        return clan.isVerified() ? sm.getPageTrustedColor() + lang("verified", sender) :
                sm.getPageUnTrustedColor() + lang("unverified", sender);
    }

    @NotNull
    private String getDescription() {
        return clan.getDescription() != null && !clan.getDescription().isEmpty() ? clan.getDescription() :
                lang("no.description", sender);
    }
}
