package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ECONOMY_MEMBER_FEE_ENABLED;

public class MemberFeeEnabledCondition extends AbstractCommandCondition {
    public MemberFeeEnabledCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        if (!settingsManager.is(ECONOMY_MEMBER_FEE_ENABLED)) {
            throw new ConditionFailedException(ChatColor.RED + lang("disabled.command",
                    context.getIssuer().getIssuer()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "member_fee_enabled";
    }
}
