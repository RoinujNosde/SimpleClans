package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ClanMembersCompletion extends AbstractSyncCompletion {
    public ClanMembersCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext c) throws InvalidCommandArgument {
        Player player = c.getPlayer();
        if (player != null) {
            Clan clan = clanManager.getClanByPlayerUniqueId(player.getUniqueId());
            if (clan != null) {
                List<String> list = clan.getMembers().stream().map(ClanPlayer::getName).collect(Collectors.toList());
                if (c.hasConfig("hide_own")) {
                    list.remove(player.getName());
                }
                return list;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public @NotNull String getId() {
        return "clan_members";
    }
}
