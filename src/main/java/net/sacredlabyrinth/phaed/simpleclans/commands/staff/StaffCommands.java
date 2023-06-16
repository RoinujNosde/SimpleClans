package net.sacredlabyrinth.phaed.simpleclans.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerHomeSetEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerResetKdrEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.TagChangeEvent;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryController;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import net.sacredlabyrinth.phaed.simpleclans.utils.TagValidator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.GLOBAL_FRIENDLY_FIRE;
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

    @Subcommand("%mod %place")
    @CommandPermission("simpleclans.mod.place")
    @CommandCompletion("@players @clans")
    @HelpSearchTags("move put")
    @Description("{@@command.description.place}")
    public void place(CommandSender sender, @Name("player") ClanPlayerInput cpInput, @Name("clan") ClanInput clanInput) {
        UUID uuid = cpInput.getClanPlayer().getUniqueId();
        ClanPlayer oldCp = cm.getClanPlayer(uuid);
        Clan newClan = clanInput.getClan();

        if (oldCp != null) {
            Clan oldClan = Objects.requireNonNull(oldCp.getClan());

            if (oldClan.equals(newClan)) {
                ChatBlock.sendMessage(sender, lang("player.already.in.this.clan", sender));
                return;
            }
            if (!oldClan.isPermanent() && oldClan.isLeader(uuid) && oldClan.getLeaders().size() <= 1) {
                ChatBlock.sendMessage(sender, RED + lang("you.cannot.move.the.last.leader", sender));
                return;
            } else {
                oldClan.addBb(oldCp.getName(), lang("0.has.resigned", oldCp.getName()));
                oldClan.removePlayerFromClan(uuid);
            }
        }

        ClanPlayer cp = cm.getCreateClanPlayer(uuid);

        newClan.addBb(lang("joined.the.clan", cp.getName()));
        cm.serverAnnounce(lang("has.joined", cp.getName(), newClan.getName()));
        newClan.addPlayerToClan(cp);
    }

    @Subcommand("%mod %modtag")
    @CommandPermission("simpleclans.mod.modtag")
    @Description("{@@command.description.modtag.other}")
    public void modtag(Player player, @Name("clan") ClanInput clanInput, @Single @Name("tag") String tag) {
        Clan clan = clanInput.getClan();
        TagChangeEvent event = new TagChangeEvent(player, clan, tag);
        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        tag = event.getNewTag();
        String cleanTag = Helper.cleanTag(tag);

        TagValidator validator = new TagValidator(plugin, player, tag);
        if (validator.getErrorMessage() != null) {
            ChatBlock.sendMessage(player, validator.getErrorMessage());
            return;
        }

        if (!cleanTag.equals(clan.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("you.can.only.modify.the.color.and.case.of.the.tag",
                    player));
            return;
        }

        clan.addBb(player.getName(), lang("tag.changed.to.0", ChatUtils.parseColors(tag)));
        clan.changeClanTag(tag);
        player.sendMessage(lang("0.tag.changed.to.1", player, clan.getTag(), tag));
    }

    @Subcommand("%admin %reload")
    @CommandPermission("simpleclans.admin.reload")
    @Description("{@@command.description.reload}")
    public void reload(CommandSender sender) {
        storage.saveModified();
        plugin.reloadConfig();
        LanguageResource.clearCache();
        settings.loadAndSave();
        storage.importFromDatabase();
        permissions.loadPermissions();

        for (Clan clan : cm.getClans()) {
            permissions.updateClanPermissions(clan);
        }
        ChatBlock.sendMessage(sender, AQUA + lang("configuration.reloaded", sender));
    }

    @Subcommand("%mod %home %set")
    @CommandPermission("simpleclans.mod.home")
    @CommandCompletion("@clans")
    @Description("{@@command.description.mod.home.set}")
    public void homeSet(Player player, ClanPlayer cp, @Name("clan") ClanInput clanInput) {
        Location loc = player.getLocation();
        Clan clan = clanInput.getClan();

        PlayerHomeSetEvent event = new PlayerHomeSetEvent(clan, cp, loc);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        clan.setHomeLocation(loc);
        ChatBlock.sendMessage(player, AQUA + lang("hombase.mod.set", player, clan.getName()) + " " +
                ChatColor.YELLOW + Helper.toLocationString(loc));
    }

    @Subcommand("%mod %home %tp")
    @CommandCompletion("@clans:has_home")
    @CommandPermission("simpleclans.mod.hometp")
    @Description("{@@command.description.mod.home.tp}")
    public void homeTp(Player player, @Name("clan") @Conditions("can_teleport") ClanInput clan) {
        plugin.getTeleportManager().teleportToHome(player, clan.getClan());
    }

    @Subcommand("%mod %ban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    @Description("{@@command.description.ban}")
    public void ban(CommandSender sender, @Name("player") ClanPlayerInput player) {
        UUID uuid = player.getClanPlayer().getUniqueId();
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

    @Subcommand("%mod %unban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    @Description("{@@command.description.unban}")
    public void unban(CommandSender sender, @Name("player") ClanPlayerInput player) {
        UUID uuid = player.getClanPlayer().getUniqueId();
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

    @Subcommand("%mod %globalff %allow")
    @CommandPermission("simpleclans.mod.globalff")
    @Description("{@@command.description.globalff.allow}")
    public void allowGlobalFf(CommandSender sender) {
        if (settings.is(GLOBAL_FRIENDLY_FIRE)) {
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendly.fire.is.already.being.allowed", sender));
        } else {
            settings.set(GLOBAL_FRIENDLY_FIRE, true);
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendly.fire.is.set.to.allowed", sender));
        }
    }

    @Subcommand("%mod %globalff %auto")
    @CommandPermission("simpleclans.mod.globalff")
    @Description("{@@command.description.globalff.auto}")
    public void autoGlobalFf(CommandSender sender) {
        if (!settings.is(GLOBAL_FRIENDLY_FIRE)) {
            ChatBlock.sendMessage(sender, AQUA +
                    lang("global.friendy.fire.is.already.being.managed.by.each.clan", sender));
        } else {
            settings.set(GLOBAL_FRIENDLY_FIRE, false);
            ChatBlock.sendMessage(sender, AQUA + lang("global.friendy.fire.is.now.managed.by.each.clan",
                    sender));
        }
    }

    @Subcommand("%mod %verify")
    @CommandPermission("simpleclans.mod.verify")
    @CommandCompletion("@clans:unverified")
    @Description("{@@command.description.mod.verify}")
    public void verify(CommandSender sender, @Name("clan") ClanInput clan) {
        Clan clanInput = clan.getClan();

        if (!clanInput.isVerified()) {
            clanInput.verifyClan();
            clanInput.addBb(sender.getName(), lang("clan.0.has.been.verified", clanInput.getName()));
            ChatBlock.sendMessage(sender, AQUA + lang("the.clan.has.been.verified", sender));
        } else {
            ChatBlock.sendMessage(sender, RED + lang("the.clan.is.already.verified", sender));
        }
    }

    @Subcommand("%admin %purge")
    @CommandPermission("simpleclans.admin.purge")
    @CommandCompletion("@players")
    @Description("{@@command.description.purge}")
    public void purge(CommandSender sender, @Name("player") ClanPlayerInput player) {
        Player onlinePlayer = player.getClanPlayer().toPlayer();
        if (onlinePlayer != null && InventoryController.isRegistered(onlinePlayer)) {
            onlinePlayer.closeInventory();
        }

        Clan clan = player.getClanPlayer().getClan();
        if (clan != null && clan.getMembers().size() == 1) {
            clan.disband(sender, false, false);
        }
        cm.deleteClanPlayer(player.getClanPlayer());
        ChatBlock.sendMessage(sender, AQUA + lang("player.purged", sender));
    }

    @Subcommand("%mod %kick")
    @Description("{@@command.description.mod.kick}")
    @CommandPermission("simpleclans.mod.kick")
    @CommandCompletion("@all_non_leaders|@all_leaders")
    public void kick(CommandSender sender, @Conditions("clan_member") @Name("player") ClanPlayerInput cp) {
        ClanPlayer clanPlayer = cp.getClanPlayer();
        Clan clan = Objects.requireNonNull(clanPlayer.getClan());
        if (clanPlayer.isLeader() && clan.getLeaders().size() == 1) {
            ChatBlock.sendMessageKey(sender, "cannot.kick.last.leader");
            return;
        }

        clan.addBb(sender.getName(), lang("has.been.kicked.by", clanPlayer.getName(),
                sender.getName(), sender));
        clan.removePlayerFromClan(clanPlayer.getUniqueId());
    }

    @Subcommand("%mod %disband")
    @CommandCompletion("@clans")
    @CommandPermission("simpleclans.mod.disband")
    @Description("{@@command.description.mod.disband}")
    public void disband(CommandSender sender, @Name("clan") ClanInput clan) {
        clan.getClan().disband(sender, true, true);
    }

    @Subcommand("%admin %promote")
    @CommandCompletion("@all_non_leaders")
    @CommandPermission("simpleclans.admin.promote")
    @Description("{@@command.description.admin.promote}")
    public void promote(CommandSender sender, @Conditions("online|clan_member") @Name("player") ClanPlayerInput promote) {
        ClanPlayer clanPlayer = promote.getClanPlayer();
        Player promotePl = Objects.requireNonNull(clanPlayer.toPlayer());
        if (!permissions.has(promotePl, "simpleclans.leader.promotable")) {
            ChatBlock.sendMessage(sender, RED + lang("the.player.does.not.have.the.permissions.to.lead.a.clan",
                    sender));
            return;
        }
        Clan clan = Objects.requireNonNull(clanPlayer.getClan());
        if (clan.isLeader(promotePl)) {
            ChatBlock.sendMessage(sender, RED + lang("the.player.is.already.a.leader", sender));
            return;
        }

        clan.addBb(sender.getName(), lang("promoted.to.leader", promotePl.getName()));
        clan.promote(promotePl.getUniqueId());
        ChatBlock.sendMessage(sender, AQUA + lang("player.successfully.promoted", sender));
    }

    @Subcommand("%admin %demote")
    @CommandCompletion("@all_leaders")
    @CommandPermission("simpleclans.admin.demote")
    @Description("{@@command.description.admin.demote}")
    public void demote(CommandSender sender, @Conditions("clan_member") @Name("leader") ClanPlayerInput other) {
        ClanPlayer otherCp = other.getClanPlayer();
        Clan clan = Objects.requireNonNull(otherCp.getClan());

        if (!otherCp.isLeader()) {
            ChatBlock.sendMessage(sender, RED + lang("player.is.not.a.leader", sender));
            return;
        }

        if (clan.getLeaders().size() == 1 && !clan.isPermanent()) {
            ChatBlock.sendMessage(sender, RED + lang("you.cannot.demote.the.last.leader", sender));
            return;
        }
        clan.demote(otherCp.getUniqueId());
        clan.addBb(sender.getName(), lang("demoted.back.to.member", otherCp.getName()));
        ChatBlock.sendMessage(sender, AQUA + lang("player.successfully.demoted", sender));
    }

    @Subcommand("%admin %resetkdr %everyone")
    @CommandPermission("simpleclans.admin.resetkdr")
    @Description("{@@command.description.resetkdr.everyone}")
    public void resetKdr(CommandSender sender) {
        for (ClanPlayer cp : cm.getAllClanPlayers()) {
            PlayerResetKdrEvent event = new PlayerResetKdrEvent(cp);
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                cm.resetKdr(cp);
            }
        }
        ChatBlock.sendMessage(sender, RED + lang("you.have.reseted.kdr.of.all.players", sender));
    }

    @Subcommand("%admin %resetkdr")
    @CommandCompletion("@players")
    @CommandPermission("simpleclans.admin.resetkdr")
    @Description("{@@command.description.resetkdr.player}")
    public void resetKdr(CommandSender sender, @Name("player") ClanPlayerInput clanPlayer) {
        ClanPlayer cp = clanPlayer.getClanPlayer();
        PlayerResetKdrEvent event = new PlayerResetKdrEvent(cp);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            cm.resetKdr(cp);
            ChatBlock.sendMessage(sender, RED + lang("you.have.reseted.0.kdr", sender, cp.getName()));
        }
    }

    @Subcommand("%admin %permanent")
    @CommandCompletion("@clans")
    @CommandPermission("simpleclans.admin.permanent")
    @Description("{@@command.description.admin.permanent}")
    public void togglePermanent(CommandSender sender, @Name("clan") ClanInput clanInput) {
        Clan clan = clanInput.getClan();
        boolean permanent = !clan.isPermanent();
        clan.setPermanent(permanent);
        clan.addBb(sender.getName(), lang((permanent) ? "permanent.status.enabled" : "permanent.status.disabled", sender.getName()));
        ChatBlock.sendMessage(sender, AQUA + lang("you.have.toggled.permanent.status", sender, clan.getName()));
    }

    @Subcommand("%mod %rename")
    @CommandCompletion("@clans @nothing")
    @CommandPermission("simpleclans.mod.rename")
    @Description("{@@command.description.mod.rename}")
    public void rename(CommandSender sender, @Name("clan") ClanInput clanInput, @Name("name") String clanName) {
        Clan clan = clanInput.getClan();
        clan.setName(clanName);
        storage.updateClan(clan);

        ChatBlock.sendMessageKey(sender, "you.have.successfully.renamed.the.clan", clanName);
    }

    @Subcommand("%mod %locale")
    @CommandPermission("simpleclans.mod.locale")
    @Description("{@@command.description.mod.locale}")
    @CommandCompletion("@locales")
    public void locale(CommandSender sender, @Name("player") ClanPlayerInput input, @Values("@locales") @Name("locale") String locale) {
        ClanPlayer cp = input.getClanPlayer();
        cp.setLocale(Helper.forLanguageTag(locale.replace("_", "-")));
        plugin.getStorageManager().updateClanPlayer(cp);

        ChatBlock.sendMessage(sender, lang("locale.has.been.changed"));
    }
}
