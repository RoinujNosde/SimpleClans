package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClansCompletion extends AbstractSyncCompletion {
    public ClansCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext c) throws InvalidCommandArgument {
        Stream<Clan> clans = clanManager.getClans().stream();
        if (c.hasConfig("has_home")) {
            clans = clans.filter(clan -> clan.getHomeLocation() != null);
        }
        if (c.hasConfig("unverified")) {
            clans = clans.filter(clan -> !clan.isVerified());
        }
        return clans.map(Clan::getTag).collect(Collectors.toList());
    }

    @Override
    public @NotNull String getId() {
        return "clans";
    }
}
