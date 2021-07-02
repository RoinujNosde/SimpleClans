package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.WHITE;

public class ClanCoords extends Sendable {

    private final Player player;
    private final Clan clan;

    public ClanCoords(@NotNull SimpleClans plugin, @NotNull Player player, @NotNull Clan clan) {
        super(plugin, player);
        this.player = player;
        this.clan = clan;
    }

    private void populateRows() {
        Map<Integer, List<String>> rows = new TreeMap<>();
        for (ClanPlayer cpm : VanishUtils.getNonVanished(player, clan)) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? sm.get(PAGE_LEADER_COLOR) : (cpm.isTrusted() ?
                        sm.get(PAGE_TRUSTED_COLOR) : sm.get(PAGE_UNTRUSTED_COLOR))) + cpm.getName();
                Location loc = p.getLocation();
                int distance = (int) Math.ceil(loc.toVector().distance(player.getLocation().toVector()));
                String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                String world = loc.getWorld() == null ? "-" : loc.getWorld().getName();

                List<String> cols = new ArrayList<>();
                cols.add("  " + name);
                cols.add(AQUA + "" + distance);
                cols.add(WHITE + "" + coords);
                cols.add(world);
                rows.put(distance, cols);
            }
        }
        for (List<String> col : rows.values()) {
            chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
        }
    }

    private void configureAndSendHeader() {
        chatBlock.setFlexibility(true, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c");

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, sm.get(PAGE_CLAN_NAME_COLOR) + clan.getName() + subColor + " " +
                lang("coords", player) + " " + headColor + Helper.generatePageSeparator(sm.get(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(player);

        chatBlock.addRow("  " + headColor + lang("name", player), lang("distance", player),
                lang("coords.upper", player), lang("world", player));
    }

    @Override
    public void send() {
        configureAndSendHeader();
        populateRows();

        sendBlock();
    }
}
