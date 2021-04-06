package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public class NonVanishedMembersCompletion extends AbstractSyncCompletion {
    public NonVanishedMembersCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext c) throws InvalidCommandArgument {
        return Bukkit.getOnlinePlayers().stream()
                .filter(p -> clanManager.getClanByPlayerUniqueId(p.getUniqueId()) == null || VanishUtils.isVanished(c.getSender(), p)).map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public @NotNull String getId() {
        return "non_vanished_members";
    }
}
