package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.LAND_SHARING;

@SuppressWarnings("unused")
public class LandSharingCondition extends AbstractCommandCondition {

    public LandSharingCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        if (!settingsManager.is(LAND_SHARING)) {
            throw new ConditionFailedException(lang("land.sharing.disabled", context.getIssuer()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "land_sharing";
    }
}
