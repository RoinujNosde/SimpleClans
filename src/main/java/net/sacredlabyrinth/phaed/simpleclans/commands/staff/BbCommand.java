package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Conditions;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;

import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.WHITE;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
@Subcommand("%mod %bb")
public class BbCommand extends BaseCommand {

    @Dependency
    private StorageManager storage;
    @Dependency
    private ClanManager cm;
    
    @Default
    @CommandPermission("simpleclans.mod.bb")
    @CommandCompletion("@clans")
    @Description("{@@command.description.mod.bb.display}")
    public void display(Player sender, @Name("clan") ClanInput input) {
        input.getClan().displayBb(sender);
    }

    @Subcommand("%clear")
    @CommandPermission("simpleclans.mod.bb-clear")
    @CommandCompletion("@clans")
    @Description("{@@command.description.mod.bb.clear}")
    public void clear(Player player, @Name("clan") ClanInput input) {
        input.getClan().clearBb();
        ChatBlock.sendMessage(player, RED + lang("cleared.bb", player));
    }

    @Subcommand("%add")
    @CommandPermission("simpleclans.mod.bb-add")
    @CommandCompletion("@clans @nothing")
    @Description("{@@command.description.mod.bb.post}")
    public void postMessage(Player player, @Name("clan") ClanInput input, @Name("message") String msg) {
        Clan clan = input.getClan();
        clan.addBb(player.getName(), AQUA + player.getName() + ": " + WHITE + msg);
        storage.updateClan(clan);
    }

}
