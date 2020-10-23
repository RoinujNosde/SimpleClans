package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SenderClanMemberCondition extends AbstractCommandCondition {

    public SenderClanMemberCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Conditions.assertClanMember(clanManager, context.getIssuer());
    }

    @Override
    public @NotNull String getId() {
        return "clan_member";
    }
}
