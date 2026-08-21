package net.sacredlabyrinth.phaed.simpleclans.hooks.luckperms;

import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Exposes clan membership as LuckPerms contexts, allowing permissions to be
 * granted by clan facts, without SimpleClans creating groups or nodes.
 * <p>
 * Provided contexts:
 * </p>
 * <ul>
 *     <li>{@code simpleclans:in-clan} - true/false</li>
 *     <li>{@code simpleclans:rank} - leader/trusted/member</li>
 *     <li>{@code simpleclans:tag} - the clan tag, without colors, lowercased</li>
 * </ul>
 * Contexts are resolved for online players only.
 */
public class ClanContextCalculator implements ContextCalculator<Player> {

    public static final String IN_CLAN_KEY = "simpleclans:in-clan";
    public static final String RANK_KEY = "simpleclans:rank";
    public static final String TAG_KEY = "simpleclans:tag";

    private static final String LEADER = "leader";
    private static final String TRUSTED = "trusted";
    private static final String MEMBER = "member";

    private final ClanManager clanManager;

    public ClanContextCalculator(@NotNull ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    @Override
    public void calculate(@NotNull Player target, @NotNull ContextConsumer consumer) {
        ClanPlayer cp = clanManager.getClanPlayer(target.getUniqueId());
        Clan clan = cp != null ? cp.getClan() : null;

        if (clan == null) {
            consumer.accept(IN_CLAN_KEY, "false");
            return;
        }

        consumer.accept(IN_CLAN_KEY, "true");
        consumer.accept(RANK_KEY, getRank(cp));
        consumer.accept(TAG_KEY, Helper.cleanTag(clan.getTag()));
    }

    @Override
    public @NotNull ContextSet estimatePotentialContexts() {
        ImmutableContextSet.Builder builder = ImmutableContextSet.builder();
        builder.add(IN_CLAN_KEY, "true");
        builder.add(IN_CLAN_KEY, "false");
        builder.add(RANK_KEY, LEADER);
        builder.add(RANK_KEY, TRUSTED);
        builder.add(RANK_KEY, MEMBER);

        for (Clan clan : clanManager.getClans()) {
            builder.add(TAG_KEY, Helper.cleanTag(clan.getTag()));
        }

        return builder.build();
    }

    @NotNull
    private String getRank(@NotNull ClanPlayer cp) {
        if (cp.isLeader()) {
            return LEADER;
        }
        return cp.isTrusted() ? TRUSTED : MEMBER;
    }
}
