package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class NotBannedInputCondition extends AbstractParameterCondition<ClanPlayerInput> {

    public NotBannedInputCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanPlayerInput> getType() {
        return ClanPlayerInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context,
                                  BukkitCommandExecutionContext execContext, ClanPlayerInput value)
            throws InvalidCommandArgument {
        UUID uniqueId = value.getClanPlayer().getUniqueId();
        if (settingsManager.isBanned(uniqueId)) {
            throw new ConditionFailedException(RED + lang("this.player.is.banned.from.using.clan.commands",
                    execContext.getSender()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "not_banned";
    }
}
