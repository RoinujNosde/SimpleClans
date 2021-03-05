package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@SuppressWarnings("unused")
public class LandSharingCondition extends AbstractCommandCondition {

    public LandSharingCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        if (!settingsManager.isLandSharing()) {
            throw new ConditionFailedException(lang("land.sharing.disabled", context.getIssuer()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "land_sharing";
    }
}
