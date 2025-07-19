package net.sacredlabyrinth.phaed.simpleclans.tasks;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
        plugin.getScheduler().runTimerAsync(this, 0, (hour + 60) * 20);
    }

    @Override
    public void run() {
        plugin.getLogger().info("Caching player heads...");
        long begin = System.currentTimeMillis();
        Inventory inventory = Bukkit.createInventory(null, 9);
        List<ClanPlayer> players = plugin.getClanManager().getAllClanPlayers();
        players.sort(Comparator.comparing(ClanPlayer::getName));

        for (ClanPlayer player : players) {
            if (isCancelled()) {
                plugin.getLogger().info("Player heads caching task cancelled!");
                return;
            }
            ItemStack itemStack = XMaterial.PLAYER_HEAD.parseItem();
            SkullMeta itemMeta = (SkullMeta) Objects.requireNonNull(itemStack).getItemMeta();
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
