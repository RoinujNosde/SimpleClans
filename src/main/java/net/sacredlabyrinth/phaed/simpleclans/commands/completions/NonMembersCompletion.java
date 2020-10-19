package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class NonMembersCompletion extends AbstractSyncCompletion {
    public NonMembersCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "non_members";
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext c) throws InvalidCommandArgument {
        Player player = c.getPlayer();
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> clanManager.getClanByPlayerUniqueId(p.getUniqueId()) == null
                        && (player == null || player.canSee(p)))
                .map(Player::getName).collect(Collectors.toList());
    }
}
