package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class OnlineCondition extends AbstractParameterCondition<ClanPlayerInput> {

    public OnlineCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<ClanPlayerInput> getType() {
        return ClanPlayerInput.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context, BukkitCommandExecutionContext execContext, ClanPlayerInput value) throws InvalidCommandArgument {
        if (value.getClanPlayer().toPlayer() == null || (context.hasConfig("ignore_vanished") && VanishUtils.isVanished(execContext.getSender(), value.getClanPlayer().toPlayer()))) {
            throw new ConditionFailedException(lang("other.player.must.be.online", execContext.getSender()));
        }
    }

    @Override
    @NotNull
    public String getId() {
        return "online";
    }
}
