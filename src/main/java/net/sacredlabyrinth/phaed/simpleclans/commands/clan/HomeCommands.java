package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.events.HomeRegroupEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerHomeSetEvent;
import net.sacredlabyrinth.phaed.simpleclans.hooks.protection.Land;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

@CommandAlias("%clan")
@Conditions("%basic_conditions|verified")
public class HomeCommands extends BaseCommand {
    @Dependency
    private SimpleClans plugin;
    @Dependency
    private PermissionsManager permissions;
    @Dependency
    private SettingsManager settings;
    @Dependency
    private ProtectionManager protection;
    @Dependency
    private ClanManager cm;

    @Subcommand("%regroup %me")
    @CommandPermission("simpleclans.leader.regroup.me")
    @Conditions("rank:name=REGROUP_ME")
    @Description("{@@command.description.regroup.me}")
    public void regroupMe(Player player, ClanPlayer cp, Clan clan) {
        if (!settings.getAllowReGroupCommand()) {
            ChatBlock.sendMessage(player, RED + lang("insufficient.permissions", player));
            return;
        }

        processTeleport(player, cp, clan, player.getLocation());
    }

    private void processTeleport(Player player, ClanPlayer cp, Clan clan, Location location) {
        HomeRegroupEvent homeRegroupEvent = new HomeRegroupEvent(clan, cp, VanishUtils.getNonVanished(player, clan), location);
        plugin.getServer().getPluginManager().callEvent(homeRegroupEvent);

        if (homeRegroupEvent.isCancelled() || !cm.purchaseHomeRegroup(player)) {
            return;
        }

        plugin.getTeleportManager().teleport(player, clan, location);
    }

    @Subcommand("%regroup %home")
    @CommandPermission("simpleclans.leader.regroup.home")
    @Conditions("rank:name=REGROUP_HOME")
    @Description("{@@command.description.regroup.home}")
    public void regroupHome(Player player, ClanPlayer cp, Clan clan) {
        if (!settings.getAllowReGroupCommand()) {
            ChatBlock.sendMessage(player, RED + lang("insufficient.permissions", player));
            return;
        }
        Location location = clan.getHomeLocation();
        if (location == null) {
            ChatBlock.sendMessage(player, RED + lang("hombase.not.set", player));
            return;
        }

        processTeleport(player, cp, clan, location);
    }

    @Subcommand("%home")
    @CommandPermission("simpleclans.member.home")
    @Conditions("rank:name=HOME_TP")
    @Description("{@@command.description.home.tp}")
    public void teleport(Player player, Clan clan, ClanPlayer cp) {
        Location loc = clan.getHomeLocation();

        if (loc == null) {
            ChatBlock.sendMessage(player, RED + lang("hombase.not.set", player));
            return;
        }

        if (cm.purchaseHomeTeleport(player)) {
            plugin.getTeleportManager().addPlayer(player, clan.getHomeLocation(), clan.getName());
        }
    }

    @Subcommand("%home %clear")
    @CommandPermission("simpleclans.leader.home-set")
    @Conditions("rank:name=HOME_SET")
    @Description("{@@command.description.home.clear}")
    public void clear(Player player, Clan clan) {
        if (settings.isHomebaseSetOnce() && clan.getHomeLocation() != null &&
                !permissions.has(player, "simpleclans.mod.home")) {
            ChatBlock.sendMessage(player, RED + lang("home.base.only.once", player));
            return;
        }

        clan.setHomeLocation(null);
        ChatBlock.sendMessage(player, AQUA + lang("hombase.cleared", player));
    }

    @Subcommand("%home %set")
    @Conditions("rank:name=HOME_SET")
    @CommandPermission("simpleclans.leader.home-set")
    @Description("{@@command.description.home.set}")
    public void set(Player player, ClanPlayer cp, Clan clan) {
        if (settings.isHomebaseSetOnce() && clan.getHomeLocation() != null &&
                !permissions.has(player, "simpleclans.mod.home")) {
            ChatBlock.sendMessage(player, RED + lang("home.base.only.once", player));
            return;
        }
        if (settings.isSetBaseOnlyInLand()) {
            Land land = protection.getLandAt(player.getLocation());
            if (land == null || !land.getOwners().contains(player.getUniqueId())) {
                ChatBlock.sendMessageKey(player, "you.can.only.set.base.in.your.land");
                return;
            }
        }
        PlayerHomeSetEvent homeSetEvent = new PlayerHomeSetEvent(clan, cp, player.getLocation());
        plugin.getServer().getPluginManager().callEvent(homeSetEvent);

        if (homeSetEvent.isCancelled() || !cm.purchaseHomeTeleportSet(player)) {
            return;
        }

        clan.setHomeLocation(player.getLocation());
        ChatBlock.sendMessage(player, AQUA + lang("hombase.set", player, YELLOW +
                Helper.toLocationString(player.getLocation())));
    }
}
