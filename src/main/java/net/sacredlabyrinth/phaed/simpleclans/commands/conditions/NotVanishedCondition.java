package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class NotVanishedCondition extends AbstractParameterCondition<Player> {

    public NotVanishedCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<Player> getType() {
        return Player.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, Player target) throws InvalidCommandArgument {
        if (VanishUtils.isVanished(execContext.getSender(), target)) throw new ConditionFailedException(lang("other.player.must.be.online", execContext.getSender()));
    }

    @Override
    public @NotNull String getId() {
        return "not_in_vanish";
    }
}
