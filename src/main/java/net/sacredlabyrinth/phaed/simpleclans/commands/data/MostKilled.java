package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.PAGE_SEPARATOR;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.SERVER_NAME;
import static org.bukkit.ChatColor.*;

public class MostKilled extends Sendable {

    private final Player player;

    public MostKilled(@NotNull SimpleClans plugin, @NotNull Player player) {
        super(plugin, player);
        this.player = player;
    }

    @Override
    public void send() {
        plugin.getStorageManager().getMostKilled(data -> plugin.getScheduler().runAtEntity(player, task -> {
            if (data.isEmpty()) {
                ChatBlock.sendMessage(player, RED + lang("nokillsfound", player));
                return;
            }

            sendHeader();

            Map<String, Integer> killsPerPlayer = Helper.sortByValue(data);

            for (Map.Entry<String, Integer> attackerVictim : killsPerPlayer.entrySet()) {
                addLine(attackerVictim);
            }

            sendBlock();
        }));
    }

    private void addLine(Map.Entry<String, Integer> attackerVictim) {
        String[] split = attackerVictim.getKey().split(" ");

        if (split.length < 2) {
            return;
        }

        int count = attackerVictim.getValue();
        String attacker = split[0];
        String victim = split[1];

        chatBlock.addRow("  " + WHITE + victim, AQUA + "" + count, YELLOW + attacker);
    }

    private void sendHeader() {
        chatBlock.setFlexibility(true, false, false);
        chatBlock.setAlignment("l", "c", "l");
        chatBlock.addRow("  " + headColor + lang("victim", player), headColor +
                lang("killcount", player), headColor + lang("attacker", player));

        ChatBlock.saySingle(player, sm.getColored(SERVER_NAME) + subColor + " " + lang("mostkilled",
                player) + " " + headColor + Helper.generatePageSeparator(sm.getString(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(player);
    }
}
