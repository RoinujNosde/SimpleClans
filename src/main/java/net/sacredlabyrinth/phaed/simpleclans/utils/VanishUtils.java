package net.sacredlabyrinth.phaed.simpleclans.utils;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class VanishUtils {

    private VanishUtils() {}

    public static @NotNull List<ClanPlayer> getNonVanished(@Nullable CommandSender viewer, @NotNull Clan clan) {
        return getNonVanished(viewer, clan.getMembers());
    }

    public static @NotNull List<ClanPlayer> getNonVanished(@Nullable CommandSender viewer,
                                                        @NotNull List<ClanPlayer> clanPlayers) {
        ArrayList<ClanPlayer> nonVanished = new ArrayList<>();
        for (ClanPlayer cp : clanPlayers) {
            if (!isVanished(viewer, cp)) {
                nonVanished.add(cp);
            }
        }
        return nonVanished;
    }

    public static boolean isVanished(@Nullable CommandSender viewer, @NotNull ClanPlayer cp) {
        if (isVanished(cp)) {
            return true;
        }
        Player player = cp.toPlayer();
        if (viewer instanceof Player && player != null) {
            return !((Player) viewer).canSee(player);
        }

        return false;
    }

    public static boolean isVanished(@Nullable CommandSender viewer, @NotNull Player player) {
        if (viewer instanceof Player) {
            return !((Player) viewer).canSee(player);
        }
        return checkMetadata(player);
    }

    public static boolean isVanished(@NotNull ClanPlayer cp) {
        if (!isOnline(cp)) {
            return true;
        }

        Player player = cp.toPlayer();
        return player != null && checkMetadata(player);
    }

    private static boolean checkMetadata(Player player) {
        if (player.hasMetadata("vanished") && !player.getMetadata("vanished").isEmpty()) {
            return player.getMetadata("vanished").get(0).asBoolean();
        }
        return false;
    }

    public static boolean isOnline(@NotNull ClanPlayer player) {
        return SimpleClans.getInstance().getProxyManager().isOnline(player.getName());
    }
}
