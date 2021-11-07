package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ClansCompletion extends AbstractSyncCompletion {
    public ClansCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext c) throws InvalidCommandArgument {
        List<Clan> clans = clanManager.getClans();
        if (c.hasConfig("has_home")) {
            clans.removeIf(clan -> clan.getHomeLocation() == null);
        }
        if (c.hasConfig("unverified")) {
            clans.removeIf(clan -> clan.isVerified());
        }
        if (c.hasConfig("hide_own")) {
            Clan clan = getClan(c.getIssuer());
            if (clan != null) {
                clans.remove(clan);
            }
        }
        return clans.stream().map(Clan::getTag).collect(Collectors.toList());
    }

    @Override
    public @NotNull String getId() {
        return "clans";
    }

    private @Nullable Clan getClan(CommandIssuer issuer) {
        return clanManager.getClanByPlayerUniqueId(issuer.getUniqueId());
    }
}
