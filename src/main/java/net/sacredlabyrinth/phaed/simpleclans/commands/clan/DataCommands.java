package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.data.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@CommandAlias("%clan")
@Conditions("%basic_conditions|verified")
public class DataCommands extends BaseCommand {
    @Dependency
    private SimpleClans plugin;

    @Subcommand("%vitals")
    @CommandPermission("simpleclans.member.vitals")
    @Conditions("rank:name=VITALS")
    @Description("{@@command.description.vitals}")
    public void vitals(CommandSender sender, Clan clan) {
        Vitals vitals = new Vitals(plugin, sender, clan);
        vitals.send();
    }

    @Subcommand("%stats")
    @CommandPermission("simpleclans.member.stats")
    @Conditions("rank:name=STATS")
    @Description("{@@command.description.stats}")
    public void stats(Player player, Clan clan) {
        ClanStats stats = new ClanStats(plugin, player, clan);
        stats.send();
    }

    @Subcommand("%profile")
    @CommandPermission("simpleclans.member.profile")
    @Description("{@@command.description.profile}")
    public void profile(CommandSender sender, Clan clan) {
        ClanProfile p = new ClanProfile(plugin, sender, clan);
        p.send();
    }

    @Subcommand("%roster")
    @CommandPermission("simpleclans.member.roster")
    @Description("{@@command.description.roster}")
    public void roster(Player player, Clan clan) {
        ClanRoster r = new ClanRoster(plugin, player, clan);
        r.send();
    }

    @Subcommand("%coords")
    @CommandPermission("simpleclans.member.coords")
    @Conditions("rank:name=COORDS")
    @HelpSearchTags("local location")
    @Description("{@@command.description.coords}")
    public void coords(Player player, Clan clan) {
        if (VanishUtils.getNonVanished(player, clan).size() == 1) {
            ChatBlock.sendMessage(player, RED + lang("you.are.the.only.member.online", player));
            return;
        }
        ClanCoords c = new ClanCoords(plugin, player, clan);
        c.send();
    }
}
