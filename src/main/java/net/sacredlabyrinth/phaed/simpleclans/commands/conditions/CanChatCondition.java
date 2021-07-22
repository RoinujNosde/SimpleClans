package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.ALLYCHAT_ENABLE;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.CLANCHAT_ENABLE;

public class CanChatCondition extends AbstractCommandCondition {
    public CanChatCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        String type = context.getConfigValue("type", (String) null);

        switch (ClanPlayer.Channel.valueOf(type)) {
            case CLAN:
                if (!settingsManager.is(CLANCHAT_ENABLE)) {
                    throw new ConditionFailedException(lang("clan.chat.disabled", context.getIssuer()));
                }
            case ALLY:
                if (!settingsManager.is(ALLYCHAT_ENABLE)) {
                    throw new ConditionFailedException(lang("ally.chat.disabled", context.getIssuer()));
                }
        }
    }

    @Override
    public @NotNull String getId() {
        return "can_chat";
    }
}
