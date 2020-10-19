package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class InputVerifiedCondition extends AbstractParameterCondition<ClanInput> {

    public InputVerifiedCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanInput> getType() {
        return ClanInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context,
                                  BukkitCommandExecutionContext execContext,
                                  ClanInput value)
            throws InvalidCommandArgument {
        if (!value.getClan().isVerified()) {
            throw new ConditionFailedException(lang("other.clan.not.verified", execContext.getSender()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "verified";
    }
}
