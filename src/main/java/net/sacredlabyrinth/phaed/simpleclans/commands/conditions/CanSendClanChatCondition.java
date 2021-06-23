package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class CanSendClanChatCondition extends AbstractCommandCondition {
    public CanSendClanChatCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        if (!settingsManager.getClanChatEnable()) {
            throw new ConditionFailedException(lang("clan.chat.disabled", context.getIssuer()));
        }
    }

    @Override
    public @NotNull String getId() {
        return "clan_chat";
    }
}
