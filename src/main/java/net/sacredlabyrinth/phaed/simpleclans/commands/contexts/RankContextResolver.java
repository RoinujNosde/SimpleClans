package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class RankContextResolver extends AbstractInputOnlyContextResolver<Rank> {
    public RankContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Rank getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        Clan clan = Contexts.assertClanMember(clanManager, context.getIssuer());
        String rankName = context.isLastArg() ? context.joinArgs() : context.popFirstArg();
        Rank rank = clan.getRank(rankName);
        if (rank == null) {
            throw new InvalidCommandArgument(RED + lang("rank.0.does.not.exist", context.getIssuer(), rankName),
                    false);
        }
        return rank;
    }

    @Override
    public Class<Rank> getType() {
        return Rank.class;
    }
}
