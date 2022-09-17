package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.Flags;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class TeleportCondition extends AbstractParameterCondition<Clan> {

    public TeleportCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<Clan> getType() {
        return Clan.class;
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context,
                                  BukkitCommandExecutionContext execContext,
                                  Clan value) throws InvalidCommandArgument {
        Player player = execContext.getPlayer();
        if (value.getHomeLocation() == null) {
            throw new ConditionFailedException(lang("hombase.not.set", player));
        }
        Flags flags = new Flags(value.getFlags());
        String homeServer = flags.getString("homeServer", "");
        if (!homeServer.isEmpty() && !plugin.getProxyManager().getServerName().equals(homeServer)) {
            throw new ConditionFailedException(lang("home.set.in.different.server"));
        }
    }

    @Override
    public @NotNull String getId() {
        return "can_teleport";
    }
}
