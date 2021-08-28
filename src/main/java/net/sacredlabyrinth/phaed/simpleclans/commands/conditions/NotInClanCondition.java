package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class NotInClanCondition extends AbstractParameterCondition<ClanPlayerInput> {

    public NotInClanCondition(@NotNull SimpleClans plugin) {
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
        ClanPlayer clanPlayer = value.getClanPlayer();
        debug(String.format("NotInClanCondition -> %s %s", clanPlayer.getName(), clanPlayer.getUniqueId()));
        if (clanPlayer.getClan() != null) {
            throw new ConditionFailedException(RED + lang("the.player.is.already.member.of.another.clan",
                    execContext.getSender()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "not_in_clan";
    }
}
