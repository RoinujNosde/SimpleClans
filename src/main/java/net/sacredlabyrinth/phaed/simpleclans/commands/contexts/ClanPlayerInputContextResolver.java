package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class ClanPlayerInputContextResolver extends AbstractInputOnlyContextResolver<ClanPlayerInput> {
    public ClanPlayerInputContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public ClanPlayerInput getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String arg = context.popFirstArg();
        ClanPlayer cp = clanManager.getAnyClanPlayer(arg);
        if (cp == null) {
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);
            if (!offlinePlayer.hasPlayedBefore()) {
                throw new InvalidCommandArgument(lang("user.hasnt.played.before", context.getSender()));
            }
            cp = clanManager.getCreateClanPlayer(offlinePlayer.getUniqueId());
        }

        return new ClanPlayerInput(cp);
    }

    @Override
    public Class<ClanPlayerInput> getType() {
        return ClanPlayerInput.class;
    }
}
