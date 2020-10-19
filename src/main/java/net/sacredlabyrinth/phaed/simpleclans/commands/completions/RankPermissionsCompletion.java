package net.sacredlabyrinth.phaed.simpleclans.commands.completions;

import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;

@SuppressWarnings("unused")
public class RankPermissionsCompletion extends AbstractStaticCompletion {
    public RankPermissionsCompletion(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public @NotNull Collection<String> getCompletions() {
        return Arrays.asList(Helper.fromPermissionArray());
    }

    @Override
    public @NotNull String getId() {
        return "rank_permissions";
    }
}
