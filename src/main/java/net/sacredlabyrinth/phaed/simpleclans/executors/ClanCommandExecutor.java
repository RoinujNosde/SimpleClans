package net.sacredlabyrinth.phaed.simpleclans.executors;

import java.text.MessageFormat;

import net.sacredlabyrinth.phaed.simpleclans.commands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Helper;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public final class ClanCommandExecutor implements CommandExecutor {
    private final SimpleClans plugin;
    private final ListCommand listCommand;
    private final ProfileCommand profileCommand;
    private final RosterCommand rosterCommand;
    private final LookupCommand lookupCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final RivalriesCommand rivalriesCommand;
    private final VitalsCommand vitalsCommand;
    private final StatsCommand statsCommand;
    private final AllyCommand allyCommand;
    private final RivalCommand rivalCommand;
    private final ModtagCommand modtagCommand;
    private final ToggleCommand toggleCommand;
    private final InviteCommand inviteCommand;
    private final TrustCommand trustCommand;
    private final UntrustCommand untrustCommand;
    private final PromoteCommand promoteCommand;
    private final DemoteCommand demoteCommand;
    private final ClanffCommand clanffCommand;
    private final FfCommand ffCommand;
    private final ResignCommand resignCommand;
    private final DisbandCommand disbandCommand;
    private final VerifyCommand verifyCommand;
    private final BanCommand banCommand;
    private final FeeCommand feeCommand;
    private final UnbanCommand unbanCommand;
    private final ReloadCommand reloadCommand;
    private final GlobalffCommand globalffCommand;
    private final MenuCommand menuCommand;
    private final WarCommand warCommand;
    private final HomeCommand homeCommand;
    private final KillsCommand killsCommand;
    private final MostKilledCommand mostKilledCommand;
    private final SetRankCommand setRankCommand;
    private final BankCommand bankCommand;
    private final ResetKDRCommand resetKDRCommand;
    private final PurgeCommand purgeCommand;
    private final DescriptionCommand descriptionCommand;
    private final RankCommand rankCommand;

    /**
     *
     */
    public ClanCommandExecutor() {
        plugin = SimpleClans.getInstance();
        menuCommand = new MenuCommand();
        listCommand = new ListCommand();
        profileCommand = new ProfileCommand();
        rosterCommand = new RosterCommand();
        lookupCommand = new LookupCommand();
        leaderboardCommand = new LeaderboardCommand();
        rivalriesCommand = new RivalriesCommand();
        vitalsCommand = new VitalsCommand();
        statsCommand = new StatsCommand();
        allyCommand = new AllyCommand();
        rivalCommand = new RivalCommand();
        modtagCommand = new ModtagCommand();
        toggleCommand = new ToggleCommand();
        inviteCommand = new InviteCommand();
        trustCommand = new TrustCommand();
        untrustCommand = new UntrustCommand();
        promoteCommand = new PromoteCommand();
        demoteCommand = new DemoteCommand();
        clanffCommand = new ClanffCommand();
        ffCommand = new FfCommand();
        resignCommand = new ResignCommand();
        disbandCommand = new DisbandCommand();
        verifyCommand = new VerifyCommand();
        banCommand = new BanCommand();
        unbanCommand = new UnbanCommand();
        reloadCommand = new ReloadCommand();
        globalffCommand = new GlobalffCommand();
        warCommand = new WarCommand();
        homeCommand = new HomeCommand();
        killsCommand = new KillsCommand();
        mostKilledCommand = new MostKilledCommand();
        setRankCommand = new SetRankCommand();
        bankCommand = new BankCommand();
        resetKDRCommand = new ResetKDRCommand();
        feeCommand = new FeeCommand();
        purgeCommand = new PurgeCommand();
        descriptionCommand = new DescriptionCommand();
        rankCommand = new RankCommand();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String s, String[] args) {
        try {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (plugin.getSettingsManager().isBlacklistedWorld(player.getLocation().getWorld().getName())) {
                    return false;
                }

                if (plugin.getSettingsManager().isBanned(player.getUniqueId())) {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("banned",player));
                    return false;
                }

                if (args.length == 0) {
                	if (plugin.getSettingsManager().isEnableGUI()) {
                		 InventoryDrawer.open(new MainFrame(player));
                	} else {
                        menuCommand.execute(player);
                	}
                } else {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);
                    
                    if (subcommand.equalsIgnoreCase(lang("list.command",player)) || subcommand.equalsIgnoreCase("list")) {
                        listCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("bank.command",player)) || subcommand.equalsIgnoreCase("bank")) {
                        bankCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("profile.command",player)) || subcommand.equalsIgnoreCase("profile")) {
                        profileCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("roster.command",player)) || subcommand.equalsIgnoreCase("roster")) {
                        rosterCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("lookup.command",player)) || subcommand.equalsIgnoreCase("lookup")) {
                        lookupCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("home.command",player)) || subcommand.equalsIgnoreCase("home")) {
                        homeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("leaderboard.command",player)) || subcommand.equalsIgnoreCase("leaderboard")) {
                        leaderboardCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("rivalries.command",player)) || subcommand.equalsIgnoreCase("rivalries")) {
                        rivalriesCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("vitals.command",player)) || subcommand.equalsIgnoreCase("vitals")) {
                        vitalsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("stats.command",player)) || subcommand.equalsIgnoreCase("stats")) {
                        statsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("ally.command",player)) || subcommand.equalsIgnoreCase("ally")) {
                        allyCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("rival.command",player)) || subcommand.equalsIgnoreCase("rival")) {
                        rivalCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("modtag.command",player)) || subcommand.equalsIgnoreCase("modtag")) {
                        modtagCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("toggle.command",player)) || subcommand.equalsIgnoreCase("toggle")) {
                        toggleCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("invite.command",player)) || subcommand.equalsIgnoreCase("invite")) {
                        inviteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("trust.command",player)) || subcommand.equalsIgnoreCase("trust")) {
                        trustCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("untrust.command",player)) || subcommand.equalsIgnoreCase("unstrust")) {
                        untrustCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("promote.command",player)) || subcommand.equalsIgnoreCase("promote")) {
                        promoteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("demote.command",player)) || subcommand.equalsIgnoreCase("demote")) {
                        demoteCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("clanff.command",player)) || subcommand.equalsIgnoreCase("canff")) {
                        clanffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("ff.command",player)) || subcommand.equalsIgnoreCase("ff")) {
                        ffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("resign.command",player)) || subcommand.equalsIgnoreCase("resign")) {
                        resignCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("disband.command",player)) || subcommand.equalsIgnoreCase("disband")) {
                        disbandCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("verify.command",player)) || subcommand.equalsIgnoreCase("verify")) {
                        verifyCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("ban.command",player)) || subcommand.equalsIgnoreCase("ban")) {
                        banCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("unban.command",player)) || subcommand.equalsIgnoreCase("unban")) {
                        unbanCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("reload.command",player)) || subcommand.equalsIgnoreCase("reload")) {
                        reloadCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("globalff.command",player)) || subcommand.equalsIgnoreCase("globalff")) {
                        globalffCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("war.command",player)) || subcommand.equalsIgnoreCase("war")) {
                        warCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("kills.command",player)) || subcommand.equalsIgnoreCase("kills")) {
                        killsCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("mostkilled.command",player)) || subcommand.equalsIgnoreCase("mostkilled")) {
                        mostKilledCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("setrank.command",player)) || subcommand.equalsIgnoreCase("setrank")) {
                        setRankCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("resetkdr.command",player)) || subcommand.equalsIgnoreCase("resetkdr")) {
                        resetKDRCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("fee.command",player)) || subcommand.equalsIgnoreCase("fee")) {
                        feeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("purge.command",player)) || subcommand.equalsIgnoreCase("purge")){
                        purgeCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("description.command",player)) || subcommand.equalsIgnoreCase("description")) {
                    	descriptionCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("rank.command",player)) || subcommand.equalsIgnoreCase("rank")) {
                    	rankCommand.execute(player, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("help.command",player)) || subcommand.equalsIgnoreCase("help")) {
                    	menuCommand.execute(player);
                    } else if (subcommand.equalsIgnoreCase(lang("setbanner.command", player)) || subcommand.equalsIgnoreCase("setbanner")) {
                        new SetBannerCommand().execute(player);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("does.not.match",player));
                    }
                }
            } else {
                if (args.length == 0) {
                    menuCommand.executeSender(sender);
                } else {
                    String subcommand = args[0];
                    String[] subargs = Helper.removeFirst(args);

                    if (subcommand.equalsIgnoreCase(lang("verify.command", sender)) || subcommand.equalsIgnoreCase("verify")) {
                        verifyCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("reload.command", sender)) || subcommand.equalsIgnoreCase("reload")) {
                        reloadCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("globalff.command", sender)) || subcommand.equalsIgnoreCase("globalff")) {
                        globalffCommand.execute(sender, subargs);
                    } else if (subcommand.equalsIgnoreCase(lang("purge.command", sender)) || subcommand.equalsIgnoreCase("purge")) {
                        purgeCommand.execute(sender, subargs);
                    } else {
                        ChatBlock.sendMessage(sender, ChatColor.RED + lang("does.not.match", sender));
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
