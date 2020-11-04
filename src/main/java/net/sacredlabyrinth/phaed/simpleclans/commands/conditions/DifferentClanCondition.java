package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class DifferentClanCondition extends AbstractParameterCondition<ClanInput> {

    public DifferentClanCondition(@NotNull SimpleClans plugin) {
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
        if (execContext.getIssuer().isPlayer()) {
            Player player = execContext.getPlayer();
            ClanPlayer cp = clanManager.getAnyClanPlayer(player.getUniqueId());
            if (cp != null && cp.getClan() != null) {
                if (value.getClan().equals(cp.getClan())) {
                    throw new ConditionFailedException(lang("cannot.be.same.clan", player));
                }
            }
        }
    }

    @Override
    public @NotNull String getId() {
        return "different";
    }
}
