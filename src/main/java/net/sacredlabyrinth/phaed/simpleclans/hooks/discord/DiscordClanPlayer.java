package net.sacredlabyrinth.phaed.simpleclans.hooks.discord;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import github.scarsz.discordsrv.util.DiscordUtil;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

// TODO: Make a diagram to make sure it works normally
public class DiscordClanPlayer {

    private final AccountLinkManager accountManager = DiscordSRV.getPlugin().getAccountLinkManager();
    private final ClanManager clanManager = SimpleClans.getInstance().getClanManager();

    private final Member member;
    private final ClanPlayer clanPlayer;

    public DiscordClanPlayer(@NotNull ClanPlayer clanPlayer, @NotNull Member member) {
        this.clanPlayer = clanPlayer;
        this.member = member;
    }

    public DiscordClanPlayer(@NotNull ClanPlayer clanPlayer) {
        this.clanPlayer = clanPlayer;
        this.member = getMember();
    }

    @Nullable
    public Member getMember() {
        if (member == null) {
            String discordId = accountManager.getDiscordId(clanPlayer.getUniqueId());
            return DiscordUtil.getMemberById(discordId);
        }

        return member;
    }

    @Nullable
    public ClanPlayer getClanPlayer() {
        if (clanPlayer == null) {
            UUID playerUuid = accountManager.getUuid(member.getId());
            return clanManager.getAnyClanPlayer(playerUuid);
        }

        return clanPlayer;
    }
}
