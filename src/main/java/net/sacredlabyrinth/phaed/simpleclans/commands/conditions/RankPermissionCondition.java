package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class RankPermissionCondition extends AbstractCommandCondition {

    public RankPermissionCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        String name = context.getConfigValue("name", (String) null);
        Player player = context.getIssuer().getPlayer();
        if (player == null || name == null) {
            return;
        }
        RankPermission rankPermission = net.sacredlabyrinth.phaed.simpleclans.RankPermission.valueOf(name);

        if (!permissionsManager.has(player, rankPermission, true)) {
            throw new ConditionFailedException();
        }
    }

    @Override
    public @NotNull String getId() {
        return "rank";
    }
}
