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
    private final ProfileCommand profileCommand;
    private final RosterCommand rosterCommand;
    private final LookupCommand lookupCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final RivalriesCommand rivalriesCommand;
    private final VitalsCommand vitalsCommand;
    private final StatsCommand statsCommand;
    private final KillsCommand killsCommand;
    private final MostKilledCommand mostKilledCommand;

    /**
     *
     */
    public ClanCommandExecutor() {
        plugin = SimpleClans.getInstance();
        //table commands
        vitalsCommand = new VitalsCommand();
        lookupCommand = new LookupCommand();
        mostKilledCommand = new MostKilledCommand();
        killsCommand = new KillsCommand();
        statsCommand = new StatsCommand();
        rivalriesCommand = new RivalriesCommand();
        leaderboardCommand = new LeaderboardCommand();
        rosterCommand = new RosterCommand();
        profileCommand = new ProfileCommand();

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
                    
                    if (subcommand.equalsIgnoreCase(lang("profile.command",player)) || subcommand.equalsIgnoreCase("profile")) {
                        profileCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("roster.command",player)) || subcommand.equalsIgnoreCase("roster")) {
                        rosterCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("lookup.command",player)) || subcommand.equalsIgnoreCase("lookup")) {
                        lookupCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("leaderboard.command",player)) || subcommand.equalsIgnoreCase("leaderboard")) {
                        leaderboardCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("rivalries.command",player)) || subcommand.equalsIgnoreCase("rivalries")) {
                        rivalriesCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("vitals.command",player)) || subcommand.equalsIgnoreCase("vitals")) {
                        vitalsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("stats.command", player)) || subcommand.equalsIgnoreCase("stats")) {
                        statsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("kills.command", player)) || subcommand.equalsIgnoreCase("kills")) {
                        killsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("mostkilled.command", player)) || subcommand.equalsIgnoreCase("mostkilled")) {
                        mostKilledCommand.execute(player, subargs);
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
