package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class ChatSubcommandsCompletion extends AbstractSyncCompletion {

    public ChatSubcommandsCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull String getId() {
        return "chat_subcommands";
    }

    @Override
    public Collection<String> getCompletions(BukkitCommandCompletionContext context) throws InvalidCommandArgument {
        Player player = context.getPlayer();
        if (player == null) {
            return Collections.emptyList();
        }
        String leave = lang("leave", player);
        List<String> subcommands = new ArrayList<>(Arrays.asList(lang("join", player), leave, lang("mute",
                player)));
        ClanPlayer cp = clanManager.getAnyClanPlayer(player.getUniqueId());
        if (cp != null) {
            if (cp.getChannel() == ClanPlayer.Channel.NONE) {
                subcommands.remove(leave);
            }
        }

        return subcommands;
    }
}
