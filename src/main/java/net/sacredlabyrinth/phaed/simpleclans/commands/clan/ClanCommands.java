package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.conversation.ResignPrompt;
import net.sacredlabyrinth.phaed.simpleclans.events.HomeRegroupEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerHomeSetEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.TagValidator;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.MessageFormat;
import java.util.*;
import java.util.function.Consumer;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
public class ClanCommands extends BaseCommand {
    @Dependency
    private SimpleClans plugin;
    @Dependency
    private SettingsManager settings;
    @Dependency
    private ClanManager cm;
    @Dependency
    private StorageManager storage;
    @Dependency
    private PermissionsManager permissions;
    @Dependency
    private RequestManager requestManager;

    @CommandAlias("%ally")
    @Description("{@@command.description.ally}")
    @CommandPermission("simpleclans.member.ally")
    @Conditions("clan_member|rank:name=ALLY_CHAT")
    public void allyChat(Player player, String message) {
        if (!plugin.getSettingsManager().isAllyChatEnable()) {
            return;
        }
        plugin.getClanManager().processAllyChat(player, message);
    }

    @Subcommand("%war %start")
    @CommandPermission("simpleclans.leader.war")
    @Conditions("verified|rank:name=WAR_START")
    @Description("{@@command.description.war.start}")
    @CommandCompletion("@rivals")
    public void startWar(Player player, ClanPlayer cp, Clan clan, ClanInput other) {
        Clan war = other.getClan();
        if (!clan.isRival(war.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("you.can.only.start.war.with.rivals", player));
            return;
        }
        if (!clan.isWarring(war.getTag())) {
            List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());

            if (!onlineLeaders.isEmpty()) {
                requestManager.addWarStartRequest(cp, war, clan);
                ChatBlock.sendMessage(player, AQUA + lang("leaders.have.been.asked.to.accept.the.war.request",
                        player, war.getName()));
            } else {
                ChatBlock.sendMessage(player, RED + lang("at.least.one.leader.accept.the.alliance", player));
            }
        } else {
            ChatBlock.sendMessage(player, RED + lang("clans.already.at.war", player));
        }
    }

    @Subcommand("%war %end")
    @CommandPermission("simpleclans.leader.war")
    @Conditions("verified|rank:name=WAR_END")
    @Description("{@@command.description.war.end}")
    @CommandCompletion("@warring_clans")
    public void endWar(Player player, ClanPlayer cp, Clan clan, ClanInput other) {
        Clan war = other.getClan();
        if (clan.isWarring(war.getTag())) {
            requestManager.addWarEndRequest(cp, war, clan);
            ChatBlock.sendMessage(player, AQUA + lang("leaders.asked.to.end.rivalry", player, war.getName()));
        } else {
            ChatBlock.sendMessage(player, RED + lang("clans.not.at.war", player));
        }
    }

    @Subcommand("%demote")
    @CommandCompletion("@clan_leaders")
    @CommandPermission("simpleclans.leader.demote")
    @Conditions("leader")
    @Description("{@@command.description.demote}")
    public void demote(Player player, ClanPlayer cp, Clan clan, @Conditions("same_clan") ClanPlayerInput other) {
        ClanPlayer otherCp = other.getClanPlayer();
        if (!clan.enoughLeadersOnlineToDemote(otherCp)) {
            ChatBlock.sendMessage(player, RED + lang("not.enough.leaders.online.to.vote.on.demotion", player));
            return;
        }
        if (!clan.isLeader(otherCp.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("player.is.not.a.leader.of.your.clan", player));
            return;
        }
        if (clan.getLeaders().size() > 2 && settings.isConfirmationForDemote()) {
            requestManager.addDemoteRequest(cp, otherCp.getName(), clan);
            ChatBlock.sendMessage(player, AQUA + lang("demotion.vote.has.been.requested.from.all.leaders",
                    player));
            return;
        }
        clan.addBb(player.getName(), AQUA + lang("demoted.back.to.member", otherCp.getName()));
        clan.demote(otherCp.getUniqueId());
    }



    @Subcommand("%promote")
    @CommandPermission("simpleclans.leader.promote")
    @CommandCompletion("@clan_non_leaders")
    @Conditions("leader")
    @Description("{@@command.description.promote}")
    public void promote(Player player, Clan clan, ClanPlayer cp, @Conditions("online|same_clan") ClanPlayerInput other) {
        Player otherPl = Objects.requireNonNull(other.getClanPlayer().toPlayer());
        if (!permissions.has(otherPl, "simpleclans.leader.promotable")) {
            ChatBlock.sendMessage(player, RED + lang("the.player.does.not.have.the.permissions.to.lead.a.clan",
                    player));
            return;
        }
        if (otherPl.getUniqueId().equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("you.cannot.promote.yourself", player));
            return;
        }
        if (clan.isLeader(otherPl.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("the.player.is.already.a.leader", player));
            return;
        }
        if (settings.isConfirmationForPromote() && clan.getLeaders().size() > 1) {
            requestManager.addPromoteRequest(cp, otherPl.getName(), clan);
            ChatBlock.sendMessage(player, AQUA + lang("promotion.vote.has.been.requested.from.all.leaders",
                    player));
            return;
        }

        clan.addBb(player.getName(), AQUA + lang("promoted.to.leader", otherPl.getName()));
        clan.promote(otherPl.getUniqueId());
    }

    @Subcommand("%disband")
    @CommandPermission("simpleclans.leader.disband")
    @Conditions("leader")
    @Description("{@@command.description.disband}")
    public void disband(Player player, ClanPlayer cp, Clan clan) {
        if (clan.getLeaders().size() != 1) {
            requestManager.addDisbandRequest(cp, clan);
            ChatBlock.sendMessage(player, AQUA +
                    lang("clan.disband.vote.has.been.requested.from.all.leaders", player));
            return;
        }

        clan.clanAnnounce(player.getName(), AQUA + lang("clan.has.been.disbanded", clan.getName()));
        clan.disband();
    }

    // TODO withdraw and deposit: show command to enable them

    @Subcommand("%modtag")
    @CommandPermission("simpleclans.leader.modtag")
    @Conditions("verified|rank:name=MODTAG")
    @Description("{@@command.description.modtag}")
    public void modtag(Player player, Clan clan, @Single String tag) {
        String cleantag = Helper.cleanTag(tag);

        TagValidator validator = new TagValidator(plugin, player, tag);
        if (validator.getErrorMessage() != null) {
            ChatBlock.sendMessage(player, validator.getErrorMessage());
            return;
        }

        if (!cleantag.equals(clan.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("you.can.only.modify.the.color.and.case.of.the.tag",
                    player));
            return;
        }

        clan.addBb(player.getName(), AQUA + lang("tag.changed.to.0", Helper.parseColors(tag)));
        clan.changeClanTag(tag);
        cm.updateDisplayName(player);
    }

    @Subcommand("%setbanner")
    @CommandPermission("simpleclans.leader.setbanner")
    @Conditions("verified|rank:name=SETBANNER")
    @Description("{@@command.description.setbanner}")
    public void setbanner(Player player, Clan clan) {
        @SuppressWarnings("deprecation")
        ItemStack hand = player.getItemInHand();
        if (!hand.getType().toString().contains("BANNER")) {
            ChatBlock.sendMessageKey(player, "you.must.hold.a.banner");
            return;
        }

        clan.setBanner(hand);
        storage.updateClan(clan);
        ChatBlock.sendMessageKey(player, "you.changed.clan.banner");
    }

    @Subcommand("%verify")
    @CommandPermission("simpleclans.leader.verify")
    @Conditions("leader")
    @Description("{@@command.description.verify}")
    public void verify(Player player, Clan clan) {
        if (clan.isVerified()) {
            ChatBlock.sendMessageKey(player, "your.clan.already.verified");
            return;
        }
        if (!settings.isePurchaseVerification()) {
            ChatBlock.sendMessageKey(player, "staff.member.verify.clan");
            return;
        }
        int minToVerify = settings.getMinToVerify();
        if (minToVerify > clan.getAllMembers().size()) {
            ChatBlock.sendMessage(player, lang("your.clan.must.have.members.to.verify", player, minToVerify));
            return;
        }
        if (cm.purchaseVerification(player)) {
            clan.verifyClan();
            clan.addBb(player.getName(), AQUA + lang("clan.0.has.been.verified", clan.getName()));
            ChatBlock.sendMessage(player, AQUA + lang("the.clan.has.been.verified", player));
        }
    }

    @Subcommand("%invite")
    @CommandPermission("simpleclans.leader.invite")
    // todo sender condition clan member
    @CommandCompletion("@non_members")
    @Conditions("rank:name=INVITE")
    @Description("{@@command.description.invite}")
    public void invite(Player player, ClanPlayer cp, Clan clan,
                       @Conditions("not_banned|not_in_clan") ClanPlayerInput invited) {
        Player invitedPlayer = invited.getClanPlayer().toPlayer();
        if (invitedPlayer == null) return;
        if (!permissions.has(invitedPlayer, "simpleclans.member.can-join")) {
            ChatBlock.sendMessage(player, RED +
                    lang("the.player.doesn.t.not.have.the.permissions.to.join.clans", player));
            return;
        }
        if (invitedPlayer.getUniqueId().equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("you.cannot.invite.yourself", player));
            return;
        }
        long minutesBeforeRejoin = cm.getMinutesBeforeRejoin(invited.getClanPlayer(), clan);
        if (minutesBeforeRejoin != 0) {
            ChatBlock.sendMessage(player, RED +
                    lang("the.player.must.wait.0.before.joining.your.clan.again", player, minutesBeforeRejoin));
            return;
        }

        if (clan.getSize() >= settings.getMaxMembers()) {
            ChatBlock.sendMessage(player, RED + lang("the.clan.members.reached.limit", player));
            return;
        }
        if (!cm.purchaseInvite(player)) {
            return;
        }

        requestManager.addInviteRequest(cp, invitedPlayer.getName(), clan);
        ChatBlock.sendMessage(player, AQUA + lang("has.been.asked.to.join",
                player, invitedPlayer.getName(), clan.getName()));
    }

    @Subcommand("%fee %check")
    @Conditions("member_fee_enabled|verified")
    @CommandPermission("simpleclans.member.fee-check")
    @Description("{@@command.description.fee.check}")
    public void checkFee(Player player, Clan clan) {
        ChatBlock.sendMessage(player, AQUA
                + lang("the.fee.is.0.and.its.current.value.is.1", player, clan.isMemberFeeEnabled() ?
                        lang("fee.enabled", player) : lang("fee.disabled", player),
                clan.getMemberFee()
        ));
    }

    @Subcommand("%fee %set")
    @CommandPermission("simpleclans.leader.fee")
    @Conditions("rank:name=FEE_SET")
    @Description("{@@command.description.fee.set}")
    public void setFee(Player player, Clan clan, double fee) {
        double maxFee = settings.getMaxMemberFee();
        if (fee > maxFee) {
            ChatBlock.sendMessage(player, RED
                    + MessageFormat.format(lang("max.fee.allowed.is.0", player), maxFee));
            return;
        }
        if (cm.purchaseMemberFeeSet(player)) {
            clan.setMemberFee(fee);
            clan.addBb(player.getName(), AQUA + lang("bb.fee.set", fee));
            ChatBlock.sendMessage(player, AQUA + lang("fee.set", player));
            storage.updateClan(clan);
        }
    }

    @Subcommand("%bank %status")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("verified|rank:name=BANK_BALANCE")
    @Description("{@@command.description.bank.status}")
    public void bankStatus(Player player, Clan clan) {
        player.sendMessage(AQUA + lang("clan.balance", player, clan.getBalance()));
    }

    @Subcommand("%bank %withdraw %all")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("verified|rank:name=BANK_WITHDRAW")
    @Description("{@@command.description.bank.withdraw.all}")
    public void bankWithdraw(Player player, Clan clan) {
        processWithdraw(player, clan, clan.getBalance());
    }

    @Subcommand("%bank %withdraw")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("verified|rank:name=BANK_WITHDRAW")
    @Description("{@@command.description.bank.withdraw.amount}")
    public void bankWithdraw(Player player, Clan clan, double amount) {
        processWithdraw(player, clan, amount);
    }

    private void processWithdraw(Player player, Clan clan, double amount) {
        if (!clan.isAllowWithdraw()) {
            ChatBlock.sendMessage(player, RED + lang("withdraw.not.allowed", player));
            return;
        }
        if (amount < 0) {
            amount = amount * -1;
        }
        clan.withdraw(amount, player);
    }

    @Subcommand("%bank %deposit %all")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("verified|rank:name=BANK_DEPOSIT")
    @Description("{@@command.description.bank.deposit.all}")
    public void bankDeposit(Player player, Clan clan) {
        processDeposit(player, clan, permissions.playerGetMoney(player));
    }
    // TODO Check if economy is available on bank commands

    @Subcommand("%bank %deposit")
    @CommandPermission("simpleclans.member.bank")
    @Conditions("verified|rank:name=BANK_DEPOSIT")
    @Description("{@@command.description.bank.deposit.amount}")
    public void bankDeposit(Player player, Clan clan, double amount) {
        processDeposit(player, clan, amount);
    }

    private void processDeposit(Player player, Clan clan, double amount) {
        if (!clan.isAllowDeposit()) {
            ChatBlock.sendMessage(player, RED + lang("deposit.not.allowed", player));
            return;
        }
        if (amount < 0) {
            amount = amount * -1;
        }
        clan.deposit(amount, player);
    }

    @Subcommand("%clanff %allow")
    @CommandPermission("simpleclans.leader.ff")
    @Conditions("rank:name=FRIENDLYFIRE")
    @Description("{@@command.description.clanff.allow}")
    public void allowClanFf(Player player, Clan clan) {
        clan.addBb(player.getName(), AQUA + lang("clan.wide.friendly.fire.is.allowed"));
        clan.setFriendlyFire(true);
        storage.updateClan(clan);
    }

    @Subcommand("%clanff %block")
    @CommandPermission("simpleclans.leader.ff")
    @Description("{@@command.description.clanff.block}")
    public void blockClanFf(Player player, Clan clan) {
        clan.addBb(player.getName(), AQUA + lang("clan.wide.friendly.fire.blocked"));
        clan.setFriendlyFire(false);
        storage.updateClan(clan);
    }

    @Subcommand("%description")
    @CommandPermission("simpleclans.leader.description")
    @Conditions("verified|rank:name=DESCRIPTION")
    @Description("{@@command.description.description}")
    public void setDescription(Player player, Clan clan, String description) {
        if (description.length() < settings.getClanMinDescriptionLength()) {
            ChatBlock.sendMessage(player, RED + lang("your.clan.description.must.be.longer.than",
                    player, settings.getClanMinDescriptionLength()));
            return;
        }
        if (description.length() > settings.getClanMaxDescriptionLength()) {
            ChatBlock.sendMessage(player, RED + lang("your.clan.description.cannot.be.longer.than",
                    player, settings.getClanMaxDescriptionLength()));
            return;
        }
        clan.setDescription(description);
        ChatBlock.sendMessage(player, AQUA + lang("description.changed", player));
        storage.updateClan(clan);
    }

    @Subcommand("%regroup %me")
    @CommandPermission("simpleclans.leader.regroup.me")
    @Conditions("verified|rank:name=REGROUP_ME")
    @Description("{@@command.description.regroup.me}")
    public void regroupMe(Player player, ClanPlayer cp, Clan clan) {
        if (!settings.getAllowReGroupCommand()) {
            ChatBlock.sendMessage(player, RED + lang("insufficient.permissions", player));
            return;
        }

        processTeleport(player, cp, clan, player.getLocation());
    }

    private void processTeleport(Player player, ClanPlayer cp, Clan clan, Location location) {
        HomeRegroupEvent homeRegroupEvent = new HomeRegroupEvent(clan, cp, clan.getOnlineMembers(), location);
        plugin.getServer().getPluginManager().callEvent(homeRegroupEvent);

        if (homeRegroupEvent.isCancelled() || !cm.purchaseHomeRegroup(player)) {
            return;
        }

        plugin.getTeleportManager().teleport(clan, location);
    }

    @Subcommand("%regroup %home")
    @CommandPermission("simpleclans.leader.regroup.home")
    @Conditions("verified|rank:name=REGROUP_HOME")
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

    @Subcommand("%home %tp")
    @CommandPermission("simpleclans.member.home")
    @Conditions("verified|rank:name=HOME_TP")
    @Description("{@@command.description.home.tp}")
    public void homeTp(Player player, Clan clan, ClanPlayer cp) {
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
    @Conditions("verified|rank:name=HOME_SET")
    @Description("{@@command.description.home.clear}")
    public void homeClear(Player player, Clan clan) {
        if (settings.isHomebaseSetOnce() && clan.getHomeLocation() != null &&
                !permissions.has(player, "simpleclans.mod.home")) {
            ChatBlock.sendMessage(player, RED + lang("home.base.only.once", player));
            return;
        }

        clan.setHomeLocation(null);
        ChatBlock.sendMessage(player, AQUA + lang("hombase.cleared", player));
    }

    @Subcommand("%home %set")
    @Conditions("verified|rank:name=HOME_SET")
    @CommandPermission("simpleclans.leader.home-set")
    @Description("{@@command.description.home.set}")
    public void homeSet(Player player, ClanPlayer cp, Clan clan) {
        if (settings.isHomebaseSetOnce() && clan.getHomeLocation() != null &&
                !permissions.has(player, "simpleclans.mod.home")) {
            ChatBlock.sendMessage(player, RED + lang("home.base.only.once", player));
            return;
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

    @Subcommand("%rival %add")
    @CommandPermission("simpleclans.leader.rival")
    @Conditions("verified|rank:name=RIVAL_ADD")
    @CommandCompletion("@clans")
    @Description("{@@command.description.rival.add}")
    public void addRival(Player player, Clan clan, @Conditions("verified|different") ClanInput rival) {
        if (clan.isUnrivable()) {
            ChatBlock.sendMessage(player, RED + lang("your.clan.cannot.create.rivals", player));
            return;
        }
        if (clan.getSize() < settings.getClanMinSizeToRival()) {
            ChatBlock.sendMessage(player, RED + lang("min.players.rivalries", player,
                    settings.getClanMinSizeToRival()));
            return;
        }
        Clan rivalInput = rival.getClan();
        if (settings.isUnrivable(rivalInput.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("the.clan.cannot.be.rivaled", player));
            return;
        }
        if (!clan.reachedRivalLimit()) {
            if (!clan.isRival(rivalInput.getTag())) {
                clan.addRival(rivalInput);
                rivalInput.addBb(player.getName(), AQUA + lang("has.initiated.a.rivalry", clan.getName(),
                        rivalInput.getName()), false);
                clan.addBb(player.getName(), AQUA + lang("has.initiated.a.rivalry", player.getName(),
                        rivalInput.getName()));
            } else {
                ChatBlock.sendMessage(player, RED + lang("your.clans.are.already.rivals", player));
            }
        } else {
            ChatBlock.sendMessage(player, RED + lang("rival.limit.reached", player));
        }
    }

    @Subcommand("%rival %remove")
    @CommandPermission("simpleclans.leader.rival")
    @Conditions("verified|rank:name=RIVAL_REMOVE")
    @CommandCompletion("@rivals")
    @Description("{@@command.description.rival.remove}")
    public void removeRival(Player player, ClanPlayer cp, Clan clan, @Conditions("different") ClanInput rival) {
        Clan rivalInput = rival.getClan();
        if (clan.isRival(rivalInput.getTag())) {
            requestManager.addRivalryBreakRequest(cp, rivalInput, clan);
            ChatBlock.sendMessage(player, AQUA + lang("leaders.asked.to.end.rivalry", player,
                    rivalInput.getName()));
        } else {
            ChatBlock.sendMessage(player, RED + lang("your.clans.are.not.rivals", player));
        }
    }

    @Subcommand("%ally %add")
    @CommandPermission("simpleclans.leader.ally")
    @Conditions("verified|rank:name=ALLY_ADD")
    @CommandCompletion("@clans")
    @Description("{@@command.description.ally.add}")
    // TODO See if other commands can have completions
    public void addAlly(Player player, ClanPlayer cp, Clan clan, ClanInput other) {
        Clan input = other.getClan();
        if (clan.getSize() < settings.getClanMinSizeToAlly()) {
            ChatBlock.sendMessage(player, RED +
                    lang("minimum.to.make.alliance", player, settings.getClanMinSizeToAlly()));
            return;
        }
        if (!input.isVerified()) {
            ChatBlock.sendMessage(player, RED + lang("cannot.ally.with.an.unverified.clan", player));
            return;
        }
        if (clan.isAlly(input.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("your.clans.are.already.allies", player));
            return;
        }
        int maxAlliances = settings.getClanMaxAlliances();
        if (maxAlliances != -1) {
            if (clan.getAllies().size() >= maxAlliances) {
                ChatBlock.sendMessage(player, lang("your.clan.reached.max.alliances", player));
                return;
            }
            if (input.getAllies().size() >= maxAlliances) {
                ChatBlock.sendMessage(player, lang("other.clan.reached.max.alliances", player));
                return;
            }
        }

        List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(clan.getLeaders());
        if (onlineLeaders.isEmpty()) {
            ChatBlock.sendMessage(player, RED + lang("at.least.one.leader.accept.the.alliance",
                    player));
            return;
        }

        requestManager.addAllyRequest(cp, input, clan);
        ChatBlock.sendMessage(player, AQUA + lang("leaders.have.been.asked.for.an.alliance",
                player, input.getName()));
    }

    @Subcommand("%ally %remove")
    @Conditions("verified|rank:name=ALLY_REMOVE")
    @CommandPermission("simpleclans.leader.ally")
    @Description("{@@command.description.ally.remove}")
    // TODO Refactor ally add and remove to a inner class
    public void removeAlly(Player player, Clan clan, ClanInput ally) {
        Clan allyInput = ally.getClan();
        if (!clan.isAlly(allyInput.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("your.clans.are.not.allies", player));
            return;
        }
        clan.removeAlly(allyInput);
        allyInput.addBb(player.getName(), AQUA + lang("has.broken.the.alliance", clan.getName(),
                allyInput.getName()), false);
        clan.addBb(player.getName(), AQUA + lang("has.broken.the.alliance", player.getName(),
                allyInput.getName()));
    }

    @Subcommand("%kick")
    @CommandPermission("simpleclans.leader.kick")
    @CommandCompletion("@clan_members")
    @Conditions("rank:name=KICK")
    // TODO Update some usages of ClanPlayer (or Clan) as input
    @Description("{@@command.description.kick}")
    public void kick(@Conditions("clan_member") Player sender,
                     @Conditions("same_clan") @Flags("other") ClanPlayer clanPlayer) {
        if (sender.getUniqueId().equals(clanPlayer.getUniqueId())) {
            ChatBlock.sendMessage(sender, RED + lang("you.cannot.kick.yourself", sender));
            return;
        }

        Clan clan = cm.getClanByPlayerUniqueId(sender.getUniqueId());
        if (Objects.requireNonNull(clan).isLeader(clanPlayer.getUniqueId())) {
            ChatBlock.sendMessage(sender, RED + lang("you.cannot.kick.another.leader", sender));
            return;
        }

        clan.addBb(sender.getName(), AQUA + lang("has.been.kicked.by", clanPlayer.getName(),
                sender.getName(), sender));
        clan.removePlayerFromClan(clanPlayer.getUniqueId());
    }

    @Subcommand("%bb")
    @Conditions("verified")
    @CommandPermission("simpleclans.member.bb")
    @Description("{@@command.description.bb.display}")
    public void displayBb(Player sender) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(sender.getUniqueId()));
        clan.displayBb(sender);
    }

    @Subcommand("%bb %clear")
    @CommandPermission("simpleclans.leader.bb-clear")
    // TODO Mod clear?
    @Conditions("verified|rank:name=BB_CLEAR")
    @Description("{@@command.description.bb.clear}")
    public void clearBb(Player player) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.clearBb();
        ChatBlock.sendMessage(player, RED + lang("cleared.bb", player));
    }

    @Subcommand("%bb")
    @CommandPermission("simpleclans.member.bb-add")
    @Conditions("verified|rank:name=BB_ADD")
    @Description("{@@command.description.bb.post}")
    public void postMessageOnBb(Player player, String msg) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.addBb(player.getName(), AQUA + player.getName() + ": " + WHITE + msg);
        storage.updateClan(clan);
    }

    @Subcommand("%coords")
    @CommandPermission("simpleclans.member.coords")
    @Conditions("verified|rank:name=COORDS")
    @HelpSearchTags("local location")
    @Description("{@@command.description.coords}")
    public void coords(Player sender) {
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        ClanPlayer cp = cm.getClanPlayer(sender);
        Clan clan = Objects.requireNonNull(cp.getClan());

        ChatBlock chatBlock = new ChatBlock();

        chatBlock.setFlexibility(true, false, false, false);
        chatBlock.setAlignment("l", "c", "c", "c");

        chatBlock.addRow("  " + headColor + lang("name", sender), lang("distance", sender),
                lang("coords.upper", sender), lang("world", sender));

        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getMembers());

        Map<Integer, List<String>> rows = new TreeMap<>();

        for (ClanPlayer cpm : members) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? settings.getPageLeaderColor() : (cpm.isTrusted() ?
                        settings.getPageTrustedColor() : settings.getPageUnTrustedColor())) + cpm.getName();
                Location loc = p.getLocation();
                int distance = (int) Math.ceil(loc.toVector().distance(sender.getLocation().toVector()));
                String coords = loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ();
                String world = loc.getWorld() == null ? "-" : loc.getWorld().getName();

                List<String> cols = new ArrayList<>();
                cols.add("  " + name);
                cols.add(AQUA + "" + distance);
                cols.add(WHITE + "" + coords);
                cols.add(world);
                rows.put(distance, cols);
            }
        }

        if (rows.isEmpty()) {
            ChatBlock.sendMessage(sender, RED + lang("you.are.the.only.member.online", sender));
            return;
        }

        for (List<String> col : rows.values()) {
            chatBlock.addRow(col.get(0), col.get(1), col.get(2), col.get(3));
        }

        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, settings.getPageClanNameColor() + clan.getName() + subColor + " " +
                lang("coords", sender) + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
        ChatBlock.sendBlank(sender);

        boolean more = chatBlock.sendBlock(sender, settings.getPageSize());

        if (more) {
            storage.addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, headColor + lang("view.next.page", sender,
                    settings.getCommandMore()));
        }

        ChatBlock.sendBlank(sender);
    }
        // TODO Help search tags

    @Subcommand("%trust")
    @CommandPermission("simpleclans.leader.settrust")
    @Conditions("leader")
    // TODO trusted clan member condition
    @CommandCompletion("@clan_members")
    @Description("{@@command.description.trust}")
    public void trust(Player player, Clan clan, ClanPlayerInput trusted) {
        ClanPlayer trustedInput = trusted.getClanPlayer();
        if (player.getUniqueId().equals(trustedInput.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("you.cannot.trust.yourself", player));
            return;
        }
        if (clan.isLeader(trustedInput.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("leaders.are.already.trusted", player));
            return;
        }
        if (trustedInput.isTrusted()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("this.player.is.already.trusted", player));
            return;
        }
        clan.addBb(player.getName(), AQUA + lang("has.been.given.trusted.status.by", trustedInput.getName(),
                player.getName()));
        trustedInput.setTrusted(true);
        storage.updateClanPlayer(trustedInput);
    }

    @Subcommand("%untrust")
    @CommandPermission("simpleclans.leader.settrust")
    @Conditions("leader")
    @CommandCompletion("@clan_members")
    @Description("{@@command.description.untrust}")
    // TODO cp clan member
    public void untrust(Player player, Clan clan, ClanPlayerInput trusted) {
        ClanPlayer trustedInput = trusted.getClanPlayer();
        if (trustedInput.getUniqueId().equals(player.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("you.cannot.untrust.yourself", player));
            return;
        }
        if (clan.isLeader(trustedInput.getUniqueId())) {
            ChatBlock.sendMessage(player, RED + lang("leaders.cannot.be.untrusted", player));
            return;
        }
        if (!trustedInput.isTrusted()) {
            ChatBlock.sendMessage(player, RED + lang("this.player.is.already.untrusted", player));
            return;
        }

        clan.addBb(player.getName(), AQUA + lang("has.been.given.untrusted.status.by", trustedInput.getName(),
                player.getName()));
        trustedInput.setTrusted(false);
        storage.updateClanPlayer(trustedInput);
    }

    @Subcommand("%resign")
    @CommandPermission("simpleclans.member.resign")
    // TODO clan member condition
    @Description("{@@command.description.resign}")
    public void resign(Player player) {
        new ConversationFactory(plugin)
                .withFirstPrompt(new ResignPrompt())
                .withLocalEcho(true)
                .withTimeout(10)
                .buildConversation(player).begin();
    }

    @Subcommand("%toggle")
    @Conditions("verified")
    public class ToggleCommand extends BaseCommand {

        @Subcommand("%bb")
        @CommandPermission("simpleclans.member.bb-toggle")
        @Description("{@@command.description.toggle.bb}")
        public void bb(Player player, ClanPlayer cp) {
            toggle(player, "bbon", "bboff", cp.isBbEnabled(), cp::setBbEnabled);

            storage.updateClanPlayer(cp);
        }

        @Subcommand("%tag")
        @CommandPermission("simpleclans.member.tag-toggle")
        @Description("{@@command.description.toggle.tag}")
        public void tag(Player player, ClanPlayer cp) {
            toggle(player, "tagon", "tagoff", cp.isTagEnabled(), cp::setTagEnabled);

            storage.updateClanPlayer(cp);
        }

        @Subcommand("%deposit")
        @CommandPermission("simpleclans.leader.deposit-toggle")
        @Conditions("leader")
        @Description("{@@command.description.toggle.deposit}")
        public void deposit(Player player, Clan clan) {
            toggle(player, "depositon", "depositoff", clan.isAllowDeposit(),
                    clan::setAllowDeposit);

            storage.updateClan(clan);
        }

        @Subcommand("%fee")
        @CommandPermission("simpleclans.leader.fee")
        @Conditions("rank:name=FEE_ENABLE")
        @Description("{@@command.description.toggle.fee}")
        public void fee(Player player, Clan clan) {
            toggle(player, "feeon", "feeoff", clan.isMemberFeeEnabled(),
                    clan::setMemberFeeEnabled);

            storage.updateClan(clan);
        }

        @Subcommand("%withdraw")
        @CommandPermission("simpleclans.leader.withdraw-toggle")
        @Conditions("leader")
        @Description("{@@command.description.toggle.withdraw}")
        public void withdraw(Player player, Clan clan) {
            toggle(player, "withdrawon", "withdrawoff", clan.isAllowWithdraw(),
                    clan::setAllowWithdraw);

            storage.updateClan(clan);
        }

        private void toggle(CommandSender sender, String onMessageKey, String offMessageKey, boolean status,
                            Consumer<Boolean> consumer) {
            String messageOn = AQUA + lang(onMessageKey, sender);
            String messageOff = AQUA + lang(offMessageKey, sender);

            ChatBlock.sendMessage(sender, status ? messageOff : messageOn);
            consumer.accept(!status);
        }
    }

}
