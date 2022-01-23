package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class DoubleContextResolver extends DoublePrimitiveContextResolver {
    public DoubleContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Class<Double> getType() {
        return Double.class;
    }
}
