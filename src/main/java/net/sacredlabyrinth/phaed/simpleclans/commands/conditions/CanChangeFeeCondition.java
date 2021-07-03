package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;

@SuppressWarnings("unused")
public class CanChangeFeeCondition extends AbstractCommandCondition {

    private static final float HOUR_IN_MINUTES = 60;

    public CanChangeFeeCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime collectTime = getCollectTime();
        long interval = now.until(collectTime, MINUTES);
        if (interval <= settingsManager.getInt(ECONOMY_MEMBER_FEE_LAST_MINUTE_CHANGE_INTERVAL) * HOUR_IN_MINUTES) {
            BukkitCommandIssuer issuer = context.getIssuer();
            String error = lang("cannot.change.member.fee.now", issuer);
            if (interval <= 60) {
                error += lang("cannot.change.member.fee.minutes", issuer, interval);
            } else {
                error += lang("cannot.change.member.fee.hours", issuer, Math.ceil(interval / HOUR_IN_MINUTES));
            }
            throw new ConditionFailedException(error);
        }
    }

    private LocalDateTime getCollectTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime collectTime = now.withHour(settingsManager.get(TASKS_COLLECT_FEE_HOUR))
                .withMinute(settingsManager.get(TASKS_COLLECT_FEE_MINUTE));
        if (collectTime.isBefore(now)) {
            collectTime = collectTime.plusDays(1);
        }
        return collectTime;
    }

    @Override
    public @NotNull String getId() {
        return "change_fee";
    }
}
