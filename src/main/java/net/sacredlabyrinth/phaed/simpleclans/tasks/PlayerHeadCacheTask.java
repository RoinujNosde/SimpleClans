package net.sacredlabyrinth.phaed.simpleclans.tasks;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

public class PlayerHeadCacheTask extends BukkitRunnable {

    private final SimpleClans plugin;

    public PlayerHeadCacheTask(@NotNull SimpleClans plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts the repetitive task
     */
    public void start() {
        int hour = 3600;
        this.runTaskTimerAsynchronously(plugin, 0, (hour + 60) * 20);
    }

    @Override
    public void run() {
        plugin.getLogger().info("Caching player heads...");
        long begin = System.currentTimeMillis();
        Inventory inventory = Bukkit.createInventory(null, 9);
        List<ClanPlayer> players = plugin.getClanManager().getAllClanPlayers();
        players.sort(Comparator.comparing(ClanPlayer::getName));

        for (ClanPlayer player : players) {
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta itemMeta = (SkullMeta) itemStack.getItemMeta();
            if (itemMeta != null) {
                itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
            }
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(0, itemStack);
        }
        plugin.getLogger().info(String.format("Finished caching heads! It took %d milliseconds...",
                System.currentTimeMillis() - begin));
    }
}
