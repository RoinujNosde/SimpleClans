package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import io.papermc.lib.PaperLib;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
public class StaffCommands extends BaseCommand {

    @Dependency
    private SimpleClans plugin;
    @Dependency
    private ClanManager cm;
    @Dependency
    private PermissionsManager permissions;
    @Dependency
    private SettingsManager settings;
    @Dependency
    private StorageManager storage;

    @Subcommand("%place")
    @CommandPermission("simpleclans.mod.place")
    @CommandCompletion("@players @clans")
    @HelpSearchTags("move put")
    @Description("{@@command.description.place}")
    public void place(CommandSender sender, OnlinePlayer onlinePlayer, ClanInput clan) {
        Player player = onlinePlayer.getPlayer();
        ClanPlayer oldCp = cm.getClanPlayer(player);
        Clan newClanInput = clan.getClan();
        if (oldCp != null) {
            Clan oldClan = Objects.requireNonNull(oldCp.getClan());

            if (oldClan.equals(newClanInput)) {
                ChatBlock.sendMessage(sender, lang("player.already.in.this.clan", sender));
                return;
            }

            if (oldClan.isLeader(player) && oldClan.getLeaders().size() <= 1) {
                oldClan.clanAnnounce(player.getName(), AQUA + lang("clan.has.been.disbanded",
                        oldClan.getName()));
                oldClan.disband();
            } else {
                oldClan.addBb(player.getName(), AQUA + lang("0.has.resigned", player.getName()));
                oldClan.removePlayerFromClan(player.getUniqueId());
            }
        }

        ClanPlayer cp = Objects.requireNonNull(cm.getCreateClanPlayerUUID(player.getName()));

        newClanInput.addBb(AQUA + lang("joined.the.clan", player.getName()));
        cm.serverAnnounce(lang("has.joined", player.getName(), newClanInput.getName()));
        newClanInput.addPlayerToClan(cp);
    }

    @Subcommand("%reload")
    @CommandPermission("simpleclans.admin.reload")
    @Description("{@@command.description.reload}")
    public void reload(CommandSender sender) {
        storage.saveModified();
        plugin.reloadConfig();
        LanguageResource.clearCache();
        settings.load();
        storage.importFromDatabase();
        permissions.loadPermissions();

        for (Clan clan : cm.getClans()) {
            permissions.updateClanPermissions(clan);
        }
        ChatBlock.sendMessage(sender, AQUA + lang("configuration.reloaded", sender));
    }

    @Subcommand("%home %set")
    @CommandPermission("simpleclans.mod.home")
    @CommandCompletion("@clans")
    @Description("{@@command.description.mod.home.set}")
    public void homeSet(Player player, ClanInput clan) {
        Location loc = player.getLocation();

        clan.getClan().setHomeLocation(loc);
        ChatBlock.sendMessage(player, AQUA + lang("hombase.mod.set", player, clan.getClan().getName()) + " " +
                ChatColor.YELLOW + Helper.toLocationString(loc));
    }

    @Subcommand("%home %tp")
    @CommandCompletion("@clans:has_home")
    @CommandPermission("simpleclans.mod.hometp")
    @Description("{@@command.description.mod.home.tp}")
    public void homeTp(Player player, ClanInput clan) {
        Location loc = clan.getClan().getHomeLocation();

        if (loc == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("hombase.not.set", player));
            return;
        }

        PaperLib.teleportAsync(player, loc, PlayerTeleportEvent.TeleportCause.COMMAND).thenAccept(result -> {
            if (result) {
                ChatBlock.sendMessage(player, AQUA + lang("now.at.homebase", player, clan.getClan().getName()));
            } else {
                plugin.getLogger().log(Level.WARNING, "An error occurred while teleporting a player");
            }
        });
    }

    @Subcommand("%ban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    @Description("{@@command.description.ban}")
    public void ban(CommandSender sender, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (settings.isBanned(uuid)) {
            ChatBlock.sendMessage(sender, RED + lang("this.player.is.already.banned", sender));
            return;
        }

        cm.ban(uuid);
        ChatBlock.sendMessage(sender, AQUA + lang("player.added.to.banned.list", sender));

        Player pl = sender.getServer().getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, AQUA + lang("you.banned", sender));
        }
    }

    @Subcommand("%unban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    @Description("{@@command.description.unban}")
    public void unban(CommandSender sender, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (!settings.isBanned(uuid)) {
            ChatBlock.sendMessage(sender, RED + lang("this.player.is.not.banned", sender));
            return;
        }

        Player pl = Bukkit.getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, AQUA + lang("you.have.been.unbanned.from.clan.commands", sender));
        }

        settings.removeBanned(uuid);
        ChatBlock.sendMessage(sender, AQUA + lang("player.removed.from.the.banned.list", sender));
    }

    @Subcommand("%globalff %allow")
    @CommandPermission("simpleclans.mod.globalff")
    @Description("{@@command.description.globalff.allow}")
    public void allowGlobalFf(CommandSender sender) {
        if (settings.isGlobalff()) {
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendly.fire.is.already.being.allowed", sender));
        } else {
            settings.setGlobalff(true);
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendly.fire.is.set.to.allowed", sender));
        }
    }

    @Subcommand("%globalff %auto")
    @CommandPermission("simpleclans.mod.globalff")
    @Description("{@@command.description.globalff.auto}")
    public void autoGlobalFf(CommandSender sender) {
        if (!settings.isGlobalff()) {
            ChatBlock.sendMessage(sender, AQUA +
                    lang("global.friendy.fire.is.already.being.managed.by.each.clan", sender));
        } else {
            settings.setGlobalff(false);
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendy.fire.is.now.managed.by.each.clan",
                    sender));
        }
    }


    @Subcommand("%verify")
    @CommandPermission("simpleclans.mod.verify")
    @CommandCompletion("@clans:unverified")
    @Description("{@@command.description.mod.verify}")
    public void verify(CommandSender sender, ClanInput clan) {
        Clan clanInput = clan.getClan();

        if (!clanInput.isVerified()) {
            clanInput.verifyClan();
            clanInput.addBb(sender.getName(), AQUA + lang("clan.0.has.been.verified", clanInput.getName()));
            ChatBlock.sendMessage(sender, AQUA + lang("the.clan.has.been.verified", sender));
        } else {
            ChatBlock.sendMessage(sender, RED + lang("the.clan.is.already.verified", sender));
        }
    }

    @Subcommand("%purge")
    @CommandPermission("simpleclans.admin.purge")
    @CommandCompletion("@players")
    @Description("{@@command.description.purge}")
    public void purge(CommandSender sender, ClanPlayerInput player) {
        Player onlinePlayer = player.getClanPlayer().toPlayer();
        if (onlinePlayer != null && InventoryController.isRegistered(onlinePlayer)) {
            onlinePlayer.closeInventory();
        }

        Clan clan = player.getClanPlayer().getClan();
        if (clan != null && clan.getMembers().size() == 1) {
            clan.disband();
        }
        cm.deleteClanPlayer(player.getClanPlayer());
        ChatBlock.sendMessage(sender, AQUA + lang("player.purged", sender));
    }

    @Subcommand("%disband")
    @CommandCompletion("@clans")
    @CommandPermission("simpleclans.mod.disband")
    @Description("{@@command.description.mod.disband}")
    public void disband(CommandSender sender, ClanInput clan) {
        cm.serverAnnounce(AQUA + lang("clan.has.been.disbanded", clan.getClan().getName()));
        clan.getClan().disband();
    }

    @Subcommand("%admin %promote")
    @CommandCompletion("@all_non_leaders")
    @CommandPermission("simpleclans.admin.promote")
    @Description("{@@command.description.admin.promote}")
    public void promote(CommandSender sender, @Conditions("online|clan_member") ClanPlayerInput promote) {
        Player promotePl = Objects.requireNonNull(promote.getClanPlayer().toPlayer());
        if (!permissions.has(promotePl, "simpleclans.leader.promotable")) {
            ChatBlock.sendMessage(sender, RED + lang("the.player.does.not.have.the.permissions.to.lead.a.clan",
                    sender));
            return;
        }
        Clan clan = Objects.requireNonNull(promote.getClanPlayer().getClan());
        if (clan.isLeader(promotePl)) {
            ChatBlock.sendMessage(sender, RED + lang("the.player.is.already.a.leader", sender));
            return;
        }

        clan.addBb(sender.getName(), AQUA + lang("promoted.to.leader", promotePl.getName()));
        clan.promote(promotePl.getUniqueId());
    }

    @Subcommand("%admin %demote")
    @CommandCompletion("@all_leaders")
    @CommandPermission("simpleclans.admin.demote")
    @Description("{@@command.description.admin.demote}")
    public void demote(CommandSender sender, @Conditions("clan_member") ClanPlayerInput other) {
        ClanPlayer otherCp = other.getClanPlayer();
        Clan clan = Objects.requireNonNull(otherCp.getClan());
        if (clan.getLeaders().size() == 1) {
            ChatBlock.sendMessage(sender, RED + lang("you.cannot.demote.the.last.leader", sender));
            return;
        }
        clan.demote(otherCp.getUniqueId());

        clan.addBb(sender.getName(), AQUA + lang("demoted.back.to.member", otherCp.getName()));
        ChatBlock.sendMessage(sender, AQUA + lang("player.successfully.demoted", sender));
    }

    @Subcommand("%resetkdr %everyone")
    @CommandPermission("simpleclans.admin.resetkdr")
    @Description("{@@command.description.resetkdr.everyone}")
    public void resetKdr(CommandSender sender) {
        for (ClanPlayer cp : cm.getAllClanPlayers()) {
            cm.resetKdr(cp);
        }
        ChatBlock.sendMessage(sender, RED + lang("you.have.reseted.kdr.of.all.players", sender));
    }

    @Subcommand("%resetkdr")
    @CommandCompletion("@players")
    @CommandPermission("simpleclans.admin.resetkdr")
    @Description("{@@command.description.resetkdr.player}")
    public void resetKdr(CommandSender sender, ClanPlayerInput player) {
        ClanPlayer cp = player.getClanPlayer();
        cm.resetKdr(cp);
        ChatBlock.sendMessage(sender, RED + lang("you.have.reseted.0.kdr", sender, cp.getName()));
    }
}
