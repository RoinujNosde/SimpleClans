package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ClanPlayerContextResolver extends AbstractIssuerOnlyContextResolver<ClanPlayer> {
    public ClanPlayerContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public ClanPlayer getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        Player player = Contexts.assertPlayer(context.getIssuer());
        return clanManager.getCreateClanPlayer(player.getUniqueId());
    }

    @Override
    public Class<ClanPlayer> getType() {
        return ClanPlayer.class;
    }
}
