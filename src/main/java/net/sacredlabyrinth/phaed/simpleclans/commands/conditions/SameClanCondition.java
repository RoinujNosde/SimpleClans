package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class SameClanCondition extends AbstractParameterCondition<ClanPlayerInput> {
    public SameClanCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanPlayerInput> getType() {
        return ClanPlayerInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context,
                                  BukkitCommandExecutionContext execContext,
                                  ClanPlayerInput value) throws InvalidCommandArgument {
        BukkitCommandIssuer issuer = context.getIssuer();
        Clan clan = Conditions.assertClanMember(clanManager, issuer);
        if (value == null || value.getClanPlayer().getClan() == null ||
                !value.getClanPlayer().getClan().equals(clan)) {
            throw new ConditionFailedException(lang("the.player.is.not.a.member.of.your.clan", issuer));
        }
    }

    @Override
    public @NotNull String getId() {
        return "same_clan";
    }
}
