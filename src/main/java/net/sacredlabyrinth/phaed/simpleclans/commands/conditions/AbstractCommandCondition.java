package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.CommandConditions;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommandCondition extends AbstractCondition implements CommandConditions.Condition<BukkitCommandIssuer> {
    public AbstractCommandCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }
}
