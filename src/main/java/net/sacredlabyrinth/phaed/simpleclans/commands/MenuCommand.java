package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

/**
 * @author phaed
 */
public class MenuCommand {
    private List<String> menuItems = new LinkedList<>();

    public MenuCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     */
    public void execute(Player player) {
        SimpleClans plugin = SimpleClans.getInstance();
                
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
        Clan clan = cp == null ? null : cp.getClan();

        boolean isLeader = cp != null && cp.isLeader();
        boolean isVerified = clan != null && clan.isVerified();
        boolean isNonVerified = clan != null && !clan.isVerified();

        String clanCommand = plugin.getSettingsManager().getCommandClan();

        ChatBlock chatBlock = new ChatBlock();
        chatBlock.setCropRight(false);
        chatBlock.setPadRight(false);

        if (clan == null && plugin.getPermissionsManager().has(player, "simpleclans.leader.create")) {
            if (plugin.getSettingsManager().isePurchaseCreation()) {
                chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.create.1.purchase.a.new.clan", player), clanCommand, ChatColor.WHITE));
            } else {
                chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.create.1.create.a.new.clan", player), clanCommand, ChatColor.WHITE));
            }
        }
        if (clan != null && plugin.getPermissionsManager().has(player, "simpleclans.member.chat")) {
        	String chatCommand = plugin.getSettingsManager().isTagBasedClanChat() ? clan.getTag() : plugin.getSettingsManager().getCommandClanChat();
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.message.1.send.a.message.to.clan.chat", player), chatCommand, ChatColor.WHITE));
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.join.leave.3.join.leave.the.clan.chat", player), chatCommand, lang("join", player), lang("leave", player), ChatColor.WHITE));
        }
        if (clan != null && plugin.getPermissionsManager().has(player, "simpleclans.member.ally")) {
            String allyCommand = plugin.getSettingsManager().getCommandAlly();
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(plugin.getLang("0.message.1.send.a.message.to.ally.chat"), allyCommand, ChatColor.WHITE));
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(plugin.getLang("0.join.leave.3.join.leave.the.ally.chat"), allyCommand, lang("join", player), lang("leave", player), ChatColor.WHITE));
        }
        if (isNonVerified && plugin.getSettingsManager().isRequireVerification() && plugin.getSettingsManager().isePurchaseVerification()) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.verify.1.purchase.verification.of.your.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.list")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.list.1.lists.all.clans",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.profile")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.profile.1.view.your.clan.s.profile",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.profile")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.profile.tag.1.view.a.clan.s.profile",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.lookup")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.lookup.1.lookup.your.info",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.lookup")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.lookup.player.1.lookup.a.player.s.info",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.leaderboard")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.leaderboard.1.view.leaderboard",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.alliances")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.alliances.1.view.all.clan.alliances",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.rivalries")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rivalries.1.view.all.clan.rivalries",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.roster")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.roster.1.view.your.clan.s.member.list",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.anyone.roster")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.roster.tag.1.view.a.clan.s.member.list",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.VITALS, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.vitals.1.view.your.clan.member.s.vitals",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.COORDS, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.coords.1.view.your.clan.member.s.coordinates",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.STATS, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.stats.1.view.your.clan.member.s.stats",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.KILLS, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.kills",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.KILLS, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.killsplayer",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getSettingsManager().isMemberFee() && isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.fee-check")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.fee.check",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getSettingsManager().isMemberFee() && isVerified && plugin.getPermissionsManager().has(player, RankPermission.FEE_SET, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.fee.set",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && (plugin.getPermissionsManager().has(player, RankPermission.ALLY_ADD, PermissionLevel.LEADER, false) 
        		|| plugin.getPermissionsManager().has(player, RankPermission.ALLY_REMOVE, PermissionLevel.LEADER, false))) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.ally.add.remove.tag.1.add.remove.an.ally.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && (plugin.getPermissionsManager().has(player, RankPermission.RIVAL_ADD, PermissionLevel.LEADER, false) 
        		|| plugin.getPermissionsManager().has(player, RankPermission.RIVAL_REMOVE, PermissionLevel.LEADER, false))) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rival.add.remove.tag.1.add.remove.a.rival.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.HOME_TP, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("home-menu",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.HOME_SET, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("home-set-menu",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.HOME_SET, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("home-clear-menu",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.HOME_REGROUP, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("home-regroup-menu",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && (plugin.getPermissionsManager().has(player, RankPermission.WAR_END, PermissionLevel.LEADER, false) 
        		|| plugin.getPermissionsManager().has(player, RankPermission.WAR_START, PermissionLevel.LEADER, false))) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.war",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resetkdr")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.resetkdr.1.resets.your.kdr",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.bb")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.bb.1.display.bulletin.board",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.BB_ADD, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.bb.msg.1.add.a.message.to.the.bulletin.board",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.MODTAG, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.modtag.tag.1.modify.the.clan.s.tag",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.DESCRIPTION, PermissionLevel.LEADER, false)) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.description.description.1.modify.the.clan.s.description",player), clanCommand, ChatColor.WHITE));
        }

        String toggles = "";

        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle")) {
            toggles += "bb/";
        }

        if (isVerified && plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle")) {
            toggles += "tag/";
        }
        
        if (plugin.getSettingsManager().isMemberFee() && isVerified && plugin.getPermissionsManager().has(player, RankPermission.FEE_ENABLE, PermissionLevel.LEADER, false)) {
            toggles += "fee/";
        }

        if (!toggles.isEmpty()) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.toggle.command",player), clanCommand, ChatColor.WHITE, Helper.stripTrailing(toggles, "/")));
        }

        if (plugin.getPermissionsManager().has(player, RankPermission.INVITE, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.invite.player.1.invite.a.player",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, RankPermission.KICK, PermissionLevel.LEADER, false)) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.kick.player.1.kick.a.player.from.the.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.trust.untrust.player.1.set.trust.level1",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.settrust")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.trust.untrust.player.1.set.trust.level2",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.create")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.create.1.create.rank",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.assign")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.assign.player.rank.1.assign.rank.to.player",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.unassign")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.unassign.player.1.unassign.player.from.rank",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.delete")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.delete.rank.1.delete.rank",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.RANK_LIST, PermissionLevel.LEADER, false)) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.list.1.list.ranks",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.RANK_DISPLAYNAME, PermissionLevel.LEADER, false)) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.setdisplayname.rank.displayname.1.set.rank.displayname",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.permissions.available")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.permissions.1.list.available.permissions",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.permissions.list")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.permissions.rank.1.list.rank.permissions",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.permissions.add")) {        	
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.permissions.rank.add.permission.1.add.a.permission.to.rank",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.rank.permissions.remove")) {
        	chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.rank.permissions.rank.remove.permission.1.remove.a.permission.from.rank",player), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.promote")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.promote.member.1.promote.a.member.to.leader",player), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.demote")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.demote.leader.1.demote.a.leader.to.member",player), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.ff")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.clanff.allow.block.1.toggle.clan.s.friendly.fire",player), clanCommand, ChatColor.WHITE));
        }
        if (isLeader && plugin.getPermissionsManager().has(player, "simpleclans.leader.disband")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.disband.1.disband.your.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.ff")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.ff.allow.auto.1.toggle.personal.friendly.fire",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.member.resign")) {
            chatBlock.addRow(ChatColor.AQUA + "  " + MessageFormat.format(lang("0.resign.1.resign.from.the.clan",player), clanCommand, ChatColor.WHITE));
        }

        for (String item : menuItems) {
            chatBlock.addRow(ChatColor.AQUA + "  " + item);
        }

        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.verify") && plugin.getSettingsManager().isRequireVerification()) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.verify.tag.1.verify.an.unverified.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.place")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.place",player), clanCommand, ChatColor.WHITE));
        }
        if (isVerified && plugin.getPermissionsManager().has(player, RankPermission.MOSTKILLED, PermissionLevel.TRUSTED, false)) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.mostkilled",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.disband")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.disband.tag.1.disband.a.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.ban")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.ban.unban.player.1.ban.unban.a.player",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.hometp")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.hometp.clan.1.tp.home.a.clan",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.mod.globalff")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.globalff.allow.auto.1.set.global.friendly.fire",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.demote")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.demote.leader.1.demote.a.leader.to.member",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.purge")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.purge.player.1.purge.a.player",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.resetkdr")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.resetkdr.1.resets.kdr",player), clanCommand, ChatColor.WHITE));
        }
        if (plugin.getPermissionsManager().has(player, "simpleclans.admin.reload")) {
            chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.reload.1.reload.configuration",player), clanCommand, ChatColor.WHITE));
        }
        if (chatBlock.isEmpty()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }

        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getServerName() + subColor + " " + lang("clan.commands",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }

    /**
     * Execute the command
     *
     * @param sender
     */
    public void executeSender(CommandSender sender) {
        SimpleClans plugin = SimpleClans.getInstance();

        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        String clanCommand = plugin.getSettingsManager().getCommandClan();

        ChatBlock chatBlock = new ChatBlock();

        chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.purge.player.1.purge.a.player",sender), clanCommand, ChatColor.WHITE));
        chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.verify.tag.1.verify.an.unverified.clan",sender), clanCommand, ChatColor.WHITE));
        chatBlock.addRow(ChatColor.DARK_RED + "  " + MessageFormat.format(lang("0.reload.1.reload.configuration",sender), clanCommand, ChatColor.WHITE));

        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, plugin.getSettingsManager().getServerName() + subColor + " " + lang("clan.commands",sender) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(sender);
        chatBlock.sendBlock(sender, plugin.getSettingsManager().getPageSize());
        ChatBlock.sendBlank(sender);
    }

    /**
     * Adds a menu item to the /clan menu
     *
     * @param syntax
     * @param description
     */
    public void addMenuItem(String syntax, String description) {
        addMenuItem(syntax, description, ChatColor.AQUA);
    }

    /**
     * Adds a menu item to the /clan menu, specifying syntax color
     * [color] /[syntax] - [description]
     *
     * @param syntax
     * @param description
     * @param color
     */
    public void addMenuItem(String syntax, String description, ChatColor color) {
        menuItems.add(color + "/" + syntax + ChatColor.WHITE + " - " + description);
    }
}
