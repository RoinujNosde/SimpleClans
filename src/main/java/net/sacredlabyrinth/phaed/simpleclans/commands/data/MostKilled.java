package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.*;

public class MostKilled extends Sendable {

    private final Player player;

    public MostKilled(@NotNull SimpleClans plugin, @NotNull Player player) {
        super(plugin, player);
        this.player = player;
    }

    @Override
    public void send() {
        plugin.getStorageManager().getMostKilled(data -> new BukkitRunnable() {
            @Override
            public void run() {
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
            }
        }.runTask(plugin));
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

    protected void sendBlock() {
        SettingsManager sm = plugin.getSettingsManager();
        boolean more = chatBlock.sendBlock(sender, sm.getInt(PAGE_SIZE));

        if (more) {
            plugin.getStorageManager().addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, sm.getColored(PAGE_HEADINGS_COLOR) + lang("view.next.page", sender,
                    sm.getString(COMMANDS_MORE)));
        }
        ChatBlock.sendBlank(sender);
    }
}
