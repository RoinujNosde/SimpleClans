package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MinecraftMessageKeys;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.USERNAME_REGEX;

@SuppressWarnings("unused")
public class ClanPlayerInputContextResolver extends AbstractInputOnlyContextResolver<ClanPlayerInput> {

    private final Pattern validUsername;

    public ClanPlayerInputContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
        validUsername = Pattern.compile(plugin.getSettingsManager().getString(USERNAME_REGEX));
    }

    @Override
    public ClanPlayerInput getContext(BukkitCommandExecutionContext context) throws InvalidCommandArgument {
        String arg = context.popFirstArg();
        if (!validUsername.matcher(arg).matches()) {
            throw new InvalidCommandArgument(MinecraftMessageKeys.IS_NOT_A_VALID_NAME, "{name}", arg);
        }

        ClanPlayer cp = clanManager.getAnyClanPlayer(arg);
        if (cp == null) {
            @SuppressWarnings("deprecation")
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(arg);
            if (offlinePlayer.getPlayer() == null && !offlinePlayer.hasPlayedBefore()) {
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
