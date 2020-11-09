package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import org.bukkit.entity.Player;

import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

@CommandAlias("%clan")
@Conditions("%basic_conditions|verified")
public class BbCommand extends BaseCommand {

    @Dependency
    private ClanManager cm;
    @Dependency
    private StorageManager storage;

    @Subcommand("%bb")
    @CommandPermission("simpleclans.member.bb")
    @Description("{@@command.description.bb.display}")
    public void display(Player sender) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(sender.getUniqueId()));
        clan.displayBb(sender);
    }

    @Subcommand("%bb %clear")
    @CommandPermission("simpleclans.leader.bb-clear")
    @Conditions("rank:name=BB_CLEAR")
    @Description("{@@command.description.bb.clear}")
    public void clear(Player player) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.clearBb();
        ChatBlock.sendMessage(player, RED + lang("cleared.bb", player));
    }

    @Subcommand("%bb %add")
    @CommandPermission("simpleclans.member.bb-add")
    @Conditions("rank:name=BB_ADD")
    @Description("{@@command.description.bb.post}")
    public void postMessage(Player player, @Name("message") String msg) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.addBb(player.getName(), AQUA + player.getName() + ": " + WHITE + msg);
        storage.updateClan(clan);
    }
}
