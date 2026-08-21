package net.sacredlabyrinth.phaed.simpleclans.hooks.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Registers the {@link ClanContextCalculator} in LuckPerms and signals it,
 * when clan data of an online player changes.
 */
public class LuckPermsContextHook {

    private final LuckPerms luckPerms;
    private final ClanContextCalculator calculator;

    public LuckPermsContextHook(@NotNull SimpleClans plugin) {
        luckPerms = LuckPermsProvider.get();
        calculator = new ClanContextCalculator(plugin.getClanManager());
    }

    public void register() {
        luckPerms.getContextManager().registerCalculator(calculator);
    }

    public void unregister() {
        luckPerms.getContextManager().unregisterCalculator(calculator);
    }

    /**
     * Invalidates the cached contexts of a player
     */
    public void signalContextUpdate(@NotNull Player player) {
        luckPerms.getContextManager().signalContextUpdate(player);
    }

    /**
     * Invalidates the cached contexts of all online members of a clan
     */
    public void signalContextUpdate(@NotNull Clan clan) {
        for (ClanPlayer cp : clan.getOnlineMembers()) {
            Player player = cp.toPlayer();
            if (player != null) {
                signalContextUpdate(player);
            }
        }
    }
}
