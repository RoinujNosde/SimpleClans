package net.sacredlabyrinth.phaed.simpleclans;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class PlayerNameTabCompleter implements TabCompleter {
    private final SimpleClans plugin;

    public PlayerNameTabCompleter() {
        plugin = SimpleClans.getInstance();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, Command command, @NotNull String s, String[] strings) {
        if (command.getName().equalsIgnoreCase(plugin.getSettingsManager().getCommandClan())) {
            if (strings.length < 2) {
                return null;
            }
            Player player = null;
            if (commandSender instanceof Player) {
                player = (Player) commandSender;
            }

            if (strings[0].equalsIgnoreCase(lang("lookup.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("ban.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("unban.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("kick.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("trust.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("untrust.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("promote.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("demote.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("setrank.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("place.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("invite.command", player)) ||
                    strings[0].equalsIgnoreCase(lang("kills.command", player))) {
                List<String> list = new ArrayList<>();

                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (p.getName().startsWith(strings[1])) {
                        list.add(p.getName());
                    }
                }

                Collections.sort(list);
                return list;
            }
        }

        return null;
    }
}
