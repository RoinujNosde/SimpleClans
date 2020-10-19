package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Rank;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class RanksCompletion extends AbstractSyncCompletion {
    public RanksCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        Player player = context.getPlayer();
        if (player != null) {
            Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
            if (clan != null) {
                return clan.getRanks().stream().map(Rank::getName).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();    }

    @Override
    public @NotNull String getId() {
        return "ranks";
    }
}
