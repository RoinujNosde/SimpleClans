package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class AllLeadersCompletion extends AbstractSyncCompletion {
    public AllLeadersCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        return clanManager
                .getAllClanPlayers().stream().filter(ClanPlayer::isLeader).map(ClanPlayer::getName)
                .collect(Collectors.toList());
    }

    @Override
    public @NotNull String getId() {
        return "all_leaders";
    }
}
