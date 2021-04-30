package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

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
        Collection<String> onlinePlayers = new ArrayList<>();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            boolean vanished = VanishUtils.isVanished(c.getSender(), onlinePlayer);
            if (clanManager.getClanByPlayerUniqueId(onlinePlayer.getUniqueId()) != null || (c.hasConfig("ignore_vanished") && vanished)) {
                continue;
            }
            onlinePlayers.add(onlinePlayer.getName());
        }

        return onlinePlayers;
    }
}
