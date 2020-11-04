package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class CanVoteCondition extends AbstractCommandCondition {

    public CanVoteCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = Conditions.assertPlayer(context.getIssuer());
        ClanPlayer cp = clanManager.getCreateClanPlayer(player.getUniqueId());
        Clan clan = cp.getClan();
        if (clan != null) {
            if (!clan.isLeader(player)) {
                throw new ConditionFailedException(RED + lang("no.leader.permissions", player));
            }
            if (!requestManager.hasRequest(clan.getTag())) {
                throw new ConditionFailedException(lang("nothing.to.vote", player));
            }
            if (cp.getVote() != null) {
                throw new ConditionFailedException(RED + lang("you.have.already.voted", player));
            }
        } else {
            if (!requestManager.hasRequest(player.getName().toLowerCase())) {
                throw new ConditionFailedException(lang("nothing.to.vote", player));
            }
        }
    }

    @Override
    public @NotNull String getId() {
        return "can_vote";
    }
}
