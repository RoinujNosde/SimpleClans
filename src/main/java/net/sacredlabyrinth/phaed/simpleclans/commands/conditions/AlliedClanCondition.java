package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class AlliedClanCondition extends AbstractParameterCondition<ClanInput> {
    public AlliedClanCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanInput> getType() {
        return ClanInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context,
                                  BukkitCommandExecutionContext execContext,
                                  ClanInput value) throws InvalidCommandArgument {
        BukkitCommandIssuer issuer = context.getIssuer();
        Clan clan = Conditions.assertClanMember(clanManager, issuer);
        if (!clan.isAlly(value.getClan().getTag())) {
            throw new ConditionFailedException(RED + lang("your.clans.are.not.allies", issuer));
        }
    }

    @Override
    public @NotNull String getId() {
        return "allied_clan";
    }
}
