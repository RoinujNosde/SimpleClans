package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CanWarTargetCondition extends AbstractParameterCondition<ClanInput> {
    public CanWarTargetCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanInput> getType() {
        return ClanInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, ClanInput target) throws InvalidCommandArgument {
        BukkitCommandIssuer issuer = execContext.getIssuer();
        Clan issuerClan = Conditions.assertClanMember(clanManager, issuer);
        Clan targetClan = target.getClan();

        if (!issuerClan.isRival(targetClan.getTag())) {
            throw new ConditionFailedException(lang("you.can.only.start.war.with.rivals", issuer));
        }

        if (issuerClan.isWarring(targetClan)) {
            throw new ConditionFailedException(lang("clans.already.at.war", issuer));
        }

        boolean isRequestEnabled = settingsManager.isRequestEnabled();
        int maxDifference = settingsManager.getMembersOnlineMaxDifference();

        if (isRequestEnabled && maxDifference >= 0) {
            int difference = Math.abs(issuerClan.getOnlineMembers().size() - targetClan.getOnlineMembers().size());
            if (difference > maxDifference)
                throw new ConditionFailedException(lang("you.cant.start.war.online.members.difference", issuer));
        }
    }

    @Override
    public @NotNull String getId() {
        return "can_war_target";
    }
}
