package net.sacredlabyrinth.phaed.simpleclans.listeners;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.chest.ClanChest;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;


public class ClanChestListener extends SCListener{

    public ClanChestListener(SimpleClans plugin) {
        super(plugin);
    }

    @EventHandler
    public void close(InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        ClanPlayer clanPlayer = plugin.getClanManager().getClanPlayer(player.getUniqueId());
        if (clanPlayer == null || clanPlayer.getClan() == null) {
            return;
        }

        var clan = clanPlayer.getClan();
        var storage = plugin.getStorageManager();

        if (event.getInventory().getHolder() instanceof ClanChest) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                boolean success = storage.runWithTransaction(() -> storage.unlockChest(clan.getTag(), clanPlayer.getUniqueId()));
                if (success) {
                    storage.updateClan(clan, true);
                }
            });
        }
    }
}
