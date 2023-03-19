package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.RED;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
@Subcommand("%mod %bb")
public class BbCommand extends BaseCommand {

    @Dependency
    private StorageManager storage;
    @Dependency
    private ClanManager cm;

    @Dependency
    private SettingsManager settings;

    @Default
    @CommandPermission("simpleclans.mod.bb")
    @Description("{@@command.description.mod.bb.display}")
    public void display(Player sender, @Name("clan") ClanInput input) {
        input.getClan().displayBb(sender);
    }

    @Subcommand("%clear")
    @CommandPermission("simpleclans.mod.bb-clear")
    @Description("{@@command.description.mod.bb.clear}")
    public void clear(Player player, @Name("clan") ClanInput input) {
        input.getClan().clearBb();
        ChatBlock.sendMessage(player, RED + lang("cleared.bb", player));
    }

    @Subcommand("%add")
    @CommandPermission("simpleclans.mod.bb-add")
    @Description("{@@command.description.mod.bb.post}")
    public void postMessage(Player player, @Name("clan") ClanInput input, @Name("message") String msg) {
        Clan clan = input.getClan();
        clan.addBb(lang("bulletin.board.message", player.getName(), msg));
        clan.displayBb(player);
        storage.updateClan(clan);
    }

}
