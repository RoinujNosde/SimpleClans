package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

public class InputClanMemberCondition extends AbstractParameterCondition<ClanPlayerInput> {
    public InputClanMemberCondition(@NotNull SimpleClans plugin) {
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
        if (value.getClanPlayer().getClan() == null) {
            throw new ConditionFailedException(RED + lang("player.not.a.member.of.any.clan",
                    execContext.getSender()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "clan_member";
    }
}
