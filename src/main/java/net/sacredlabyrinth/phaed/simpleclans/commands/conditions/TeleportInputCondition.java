package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.jetbrains.annotations.NotNull;

public class TeleportInputCondition extends AbstractParameterCondition<ClanInput> {

    public TeleportInputCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanInput> getType() {
        return ClanInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, ClanInput value) throws InvalidCommandArgument {
        new TeleportCondition(plugin).validateCondition(context, execContext, value.getClan());
    }

    @Override
    public @NotNull String getId() {
        return "can_teleport";
    }
}
