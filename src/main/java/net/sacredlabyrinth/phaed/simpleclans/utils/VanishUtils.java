package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VanishUtils {

    private VanishUtils() {}

    public static @NotNull List<ClanPlayer> getNonVanished(@Nullable CommandSender viewer, @NotNull Clan clan) {
        return getNonVanished(viewer, clan.getAllMembers());
    }

    public static @NotNull List<ClanPlayer> getNonVanished(@Nullable CommandSender viewer,
                                                        @NotNull List<ClanPlayer> clanPlayers) {
        ArrayList<ClanPlayer> nonVanished = new ArrayList<>();
        for (ClanPlayer cp : clanPlayers) {
            Player player = cp.toPlayer();
            if (!isVanished(viewer, player)) {
                nonVanished.add(cp);
            }
        }
        return nonVanished;
    }

    @Contract("_, null -> true")
    public static boolean isVanished(@Nullable CommandSender viewer, @Nullable Player player) {
        if (isVanished(player)) {
            return true;
        }
        if (viewer instanceof Player) {
            return !((Player) viewer).canSee(player);
        }

        return false;
    }

    @Contract("null -> true")
    public static boolean isVanished(@Nullable Player player) {
        if (player == null || !player.isOnline()) {
            return true;
        }
        if (player.hasMetadata("vanished") && !player.getMetadata("vanished").isEmpty()) {
            return player.getMetadata("vanished").get(0).asBoolean();
        }
        return false;
    }
}
