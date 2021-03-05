package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class OwnLandCondition extends AbstractCommandCondition {
    public OwnLandCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        Player player = Conditions.assertPlayer(context.getIssuer());
        if (protectionManager.getLands(player, player.getLocation()).isEmpty()) {
            throw new ConditionFailedException(lang("you.do.not.own.lands", player));
        }
    }

    @Override
    public @NotNull String getId() {
        return "own_land";
    }
}
