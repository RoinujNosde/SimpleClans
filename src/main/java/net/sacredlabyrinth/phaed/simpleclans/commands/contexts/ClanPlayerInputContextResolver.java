package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ClanPlayerInputContextResolver extends AbstractInputOnlyContextResolver<ClanPlayerInput> {
    public ClanPlayerInputContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public ClanPlayerInput getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String arg = context.popFirstArg();
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);
        ClanPlayer cp = clanManager.getCreateClanPlayer(offlinePlayer.getUniqueId());

        return new ClanPlayerInput(cp);
    }

    @Override
    public Class<ClanPlayerInput> getType() {
        return ClanPlayerInput.class;
    }
}
