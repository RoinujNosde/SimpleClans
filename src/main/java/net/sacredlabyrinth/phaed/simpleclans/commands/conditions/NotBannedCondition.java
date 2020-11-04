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
public class NotBannedCondition extends AbstractCommandCondition {
    public NotBannedCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        if (!context.getIssuer().isPlayer()) {
            return;
        }
        Player player = context.getIssuer().getPlayer();
        if (settingsManager.isBanned(context.getIssuer().getUniqueId())) {
            throw new ConditionFailedException(lang("banned", player));
        }
    }

    @Override
    public @NotNull String getId() {
        return "not_banned";
    }
}
