package net.sacredlabyrinth.phaed.simpleclans.executors;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public final class ClanCommandExecutor implements CommandExecutor {
    private final SimpleClans plugin;
    private final RivalriesCommand rivalriesCommand;
    private final VitalsCommand vitalsCommand;
    private final StatsCommand statsCommand;

    /**
     *
     */
    public ClanCommandExecutor() {
        plugin = SimpleClans.getInstance();
        //table commands
        vitalsCommand = new VitalsCommand();
        statsCommand = new StatsCommand();
        rivalriesCommand = new RivalriesCommand();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (args.length == 0) {
                    if (plugin.getSettingsManager().isEnableGUI()) {
                        InventoryDrawer.open(new MainFrame(player));
                    }
                } else {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);
                    
                    if (subcommand.equalsIgnoreCase(lang("rivalries.command",player)) || subcommand.equalsIgnoreCase("rivalries")) {
                        rivalriesCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("vitals.command",player)) || subcommand.equalsIgnoreCase("vitals")) {
                        vitalsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("stats.command", player)) || subcommand.equalsIgnoreCase("stats")) {
                        statsCommand.execute(player, subargs);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("does.not.match", player));
                    }
                }
            }
        } catch (Exception ex) {
            SimpleClans.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + MessageFormat.format(lang("simpleclans.command.failure"), ex.getMessage()));
            for (StackTraceElement el : ex.getStackTrace()) {
                System.out.print(el.toString());
            }
        }

        return false;
    }
}
