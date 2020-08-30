package net.sacredlabyrinth.phaed.simpleclans.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateRankNamePrompt;
import net.sacredlabyrinth.phaed.simpleclans.conversation.ResignPrompt;
import net.sacredlabyrinth.phaed.simpleclans.language.LanguageResource;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Consumer;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

// TODO Add descriptions
@CommandAlias("%clan")
@Conditions("not_blacklisted|not_banned")
public class ClanCommand extends BaseCommand {
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

    @Default
    public void main(Player player) {
        if (settings.isEnableGUI()) {
            InventoryDrawer.open(new MainFrame(player));
        } else {
            help(player, new CommandHelp(getCurrentCommandManager(),
                    getCurrentCommandManager().getRootCommand(getName()), getCurrentCommandIssuer()));
        }
    }

    @Subcommand("%invite")
    @CommandPermission("simpleclans.leader.invite")
    // todo sender condition clan member
    @CommandCompletion("@players")
    public void invite(Player player, ClanPlayer cp, Clan clan,
                       @Conditions("not_banned|not_in_clan") ClanPlayerInput invited) {
        if (!permissions.has(player, RankPermission.INVITE, true)) {
            return;
        }
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
        // todo extract
        if (settings.isRejoinCooldown()) {
            Long resign = invited.getClanPlayer().getResignTime(clan.getTag());
            if (resign != null) {
                long timePassed = Instant.ofEpochMilli(resign).until(Instant.now(), ChronoUnit.MINUTES);
                int cooldown = settings.getRejoinCooldown();
                if (timePassed < cooldown) {
                    ChatBlock.sendMessage(player, RED +
                            lang("the.player.must.wait.0.before.joining.your.clan.again", player,
                                    cooldown - timePassed));
                    return;
                }
            }
        }

        if (clan.getSize() >= settings.getMaxMembers()) {
            ChatBlock.sendMessage(player, RED + lang("the.clan.members.reached.limit", player));
            return;
        }
        if (!cm.purchaseInvite(player)) {
            return;
        }

        plugin.getRequestManager().addInviteRequest(cp, invitedPlayer.getName(), clan);
        ChatBlock.sendMessage(player, AQUA + lang("has.been.asked.to.join",
                player, invitedPlayer.getName(), clan.getName()));
    }

    @Subcommand("%fee %check")
    @Conditions("member_fee_enabled|verified")
    @CommandPermission("simpleclans.member.fee-check")
    public void checkFee(Player player, Clan clan) {
        ChatBlock.sendMessage(player, AQUA
                + lang("the.fee.is.0.and.its.current.value.is.1", player, clan.isMemberFeeEnabled() ?
                        lang("fee.enabled", player) : lang("fee.disabled", player),
                clan.getMemberFee()
        ));
    }

    @Subcommand("%fee %set")
    @CommandPermission("simpleclans.leader.fee")
    public void setFee(Player player, Clan clan, double fee) {
        if (!permissions.has(player, RankPermission.FEE_SET, true)) {
            return;
        }
        double maxFee = settings.getMaxMemberFee();
        if (fee > maxFee) {
            ChatBlock.sendMessage(player, RED
                    + MessageFormat.format(lang("max.fee.allowed.is.0", player), maxFee));
            return;
        }
        if (plugin.getClanManager().purchaseMemberFeeSet(player)) {
            clan.setMemberFee(fee);
            clan.addBb(player.getName(), AQUA + lang("bb.fee.set", fee));
            ChatBlock.sendMessage(player, AQUA + lang("fee.set", player));
            plugin.getStorageManager().updateClan(clan);
        }
    }

    @Subcommand("%create")
    @CommandPermission("simpleclans.leader.create")
    public void create(Player player) {
        ClanPlayer cp = cm.getAnyClanPlayer(player.getUniqueId());

        if (cp != null && cp.getClan() != null) {
            ChatBlock.sendMessage(player, RED + lang("you.must.first.resign", player,
                    cp.getClan().getName()));
            return;
        }

        Conversation conversation = new ConversationFactory(plugin).withFirstPrompt(new CreateClanTagPrompt())
                .withLocalEcho(true).buildConversation(player);
        conversation.begin();
    }

    @Subcommand("%ff %allow")
    @CommandPermission("simpleclans.member.ff")
    // TODO cp clan member
    public void allowPersonalFf(Player player, ClanPlayer cp) {
        cp.setFriendlyFire(true);
        storage.updateClanPlayer(cp);
        ChatBlock.sendMessage(player, AQUA + lang("personal.friendly.fire.is.set.to.allowed", player));
    }

    @Subcommand("%ff %auto")
    @CommandPermission("simpleclans.member.ff")
    // TODO cp clan member
    public void autoPersonalFf(Player player, ClanPlayer cp) {
        cp.setFriendlyFire(false);
        storage.updateClanPlayer(cp);
        ChatBlock.sendMessage(player, AQUA + lang("friendy.fire.is.now.managed.by.your.clan", player));
    }

    @Subcommand("%clanff %allow")
    @CommandPermission("simpleclans.leader.ff")
    public void allowClanFf(Player player, Clan clan) {
        if (!permissions.has(player, RankPermission.FRIENDLYFIRE, true)) {
            return;
        }
        clan.addBb(player.getName(), AQUA + lang("clan.wide.friendly.fire.is.allowed"));
        clan.setFriendlyFire(true);
        plugin.getStorageManager().updateClan(clan);
    }

    @Subcommand("%clanff %block")
    @CommandPermission("simpleclans.leader.ff")
    public void blockClanFf(Player player, Clan clan) {
        clan.addBb(player.getName(), AQUA + lang("clan.wide.friendly.fire.blocked"));
        clan.setFriendlyFire(false);
        plugin.getStorageManager().updateClan(clan);
    }

    @Subcommand("%description")
    @CommandPermission("simpleclans.leader.description")
    @Conditions("verified")
    public void setDescription(Player player, Clan clan, String description) {
        if (!permissions.has(player, RankPermission.DESCRIPTION, true)) {
            return;
        }
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
        plugin.getStorageManager().updateClan(clan);
    }

    @Subcommand("%ban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    public void ban(CommandSender sender, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (plugin.getSettingsManager().isBanned(uuid)) {
            ChatBlock.sendMessage(sender, RED + lang("this.player.is.already.banned", sender));
            return;
        }

        plugin.getClanManager().ban(uuid);
        ChatBlock.sendMessage(sender, AQUA + lang("player.added.to.banned.list", sender));

        Player pl = plugin.getServer().getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, AQUA + lang("you.banned", sender));
        }
    }

    @Subcommand("%unban")
    @CommandPermission("simpleclans.mod.ban")
    @CommandCompletion("@players")
    public void unban(CommandSender sender, OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        if (!plugin.getSettingsManager().isBanned(uuid)) {
            ChatBlock.sendMessage(sender, RED + lang("this.player.is.not.banned", sender));
            return;
        }

        Player pl = Bukkit.getPlayer(uuid);
        if (pl != null) {
            ChatBlock.sendMessage(pl, AQUA + lang("you.have.been.unbanned.from.clan.commands", sender));
        }

        plugin.getSettingsManager().removeBanned(uuid);
        ChatBlock.sendMessage(sender, AQUA + lang("player.removed.from.the.banned.list", sender));

    }

    @Subcommand("%ally %add")
    @CommandPermission("simpleclans.leader.ally")
    @Conditions("verified")
    // TODO See if other commands can have completions
    public void addAlly(Player player, ClanPlayer cp, Clan clan, ClanInput other) {
        Clan input = other.getClan();
        if (!permissions.has(player, RankPermission.ALLY_ADD, true)) {
            return;
        }
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
        int maxAlliances = plugin.getSettingsManager().getClanMaxAlliances();
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

        plugin.getRequestManager().addAllyRequest(cp, input, clan);
        ChatBlock.sendMessage(player, AQUA + lang("leaders.have.been.asked.for.an.alliance",
                player, input.getName()));
    }

    @Subcommand("%ally %remove")
    @Conditions("verified")
    @CommandPermission("simpleclans.leader.ally")
    // TODO Refactor ally add and remove to a inner class
    public void removeAlly(Player player, Clan clan, ClanInput ally) {
        Clan allyInput = ally.getClan();
        if (!permissions.has(player, RankPermission.ALLY_REMOVE, true)) {
            return;
        }
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
    public void kick(@Conditions("clan_member") Player sender,
                     @Conditions("same_clan") @Flags("other") ClanPlayer clanPlayer) {
        if (!permissions.has(sender, RankPermission.KICK, true)) {
            return;
        }
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
    public void displayBb(Player sender) {
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(sender.getUniqueId()));
        clan.displayBb(sender);
    }

    @Subcommand("%bb %clear")
    @CommandPermission("simpleclans.leader.bb-clear")
    // TODO Mod clear?
    @Conditions("verified")
    public void clearBb(Player player) {
        if (!permissions.has(player, RankPermission.BB_CLEAR, true)) {
            return;
        }
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.clearBb();
        ChatBlock.sendMessage(player, RED + lang("cleared.bb", player));
    }

    @Subcommand("%bb")
    @CommandPermission("simpleclans.member.bb-add")
    @Conditions("verified")
    // TODO Conditions ranks
    public void postMessageOnBb(Player player, String msg) {
        if (!plugin.getPermissionsManager().has(player, RankPermission.BB_ADD, true)) {
            return;
        }
        Clan clan = Objects.requireNonNull(cm.getClanByPlayerUniqueId(player.getUniqueId()));
        clan.addBb(player.getName(), AQUA + player.getName() + ": " + WHITE + msg);
        plugin.getStorageManager().updateClan(clan);
    }

    @Subcommand("%coords")
    @CommandPermission("simpleclans.member.coords")
    @Conditions("verified")
    @HelpSearchTags("local location")
    public void coords(Player sender) {
        if (!permissions.has(sender, RankPermission.COORDS, true)) {
            return;
        }

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

    @Subcommand("%place")
    @CommandPermission("simpleclans.mod.place")
    @CommandCompletion("@players @clans")
    @HelpSearchTags("move put")
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

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help) {
        // TODO Not showing correctly on console
        // TODO Header footer
        sender.sendMessage("SimpleClans Help"); // TODO Get from the messages file
        help.showHelp();
    }

    // TODO Help search tags

    @Subcommand("%trust")
    @CommandPermission("simpleclans.leader.settrust")
    @Conditions("leader")
    // TODO trusted clan member condition
    @CommandCompletion("@clan_members")
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
        plugin.getStorageManager().updateClanPlayer(trustedInput);
    }

    @Subcommand("%untrust")
    @CommandPermission("simpleclans.leader.settrust")
    @Conditions("leader")
    @CommandCompletion("@clan_members")
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
        plugin.getStorageManager().updateClanPlayer(trustedInput);
    }

    @Subcommand("%resign")
    @CommandPermission("simpleclans.member.resign")
    // TODO clan member condition
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
        public void bb(Player player, ClanPlayer cp) {
            toggle(player, "bbon", "bboff", cp.isBbEnabled(), cp::setBbEnabled);

            storage.updateClanPlayer(cp);
        }

        @Subcommand("%tag")
        @CommandPermission("simpleclans.member.tag-toggle")
        public void tag(Player player, ClanPlayer cp) {
            toggle(player, "tagon", "tagoff", cp.isTagEnabled(), cp::setTagEnabled);

            storage.updateClanPlayer(cp);
        }

        @Subcommand("%deposit")
        @CommandPermission("simpleclans.leader.deposit-toggle")
        @Conditions("leader")
        public void deposit(Player player, Clan clan) {
            toggle(player, "depositon", "depositoff", clan.isAllowDeposit(),
                    clan::setAllowDeposit);

            storage.updateClan(clan);
        }

        @Subcommand("%fee")
        @CommandPermission("simpleclans.leader.fee")
        public void fee(Player player, Clan clan) {
            if (!permissions.has(player, RankPermission.FEE_ENABLE, true)) {
                return;
            }
            toggle(player, "feeon", "feeoff", clan.isMemberFeeEnabled(),
                    clan::setMemberFeeEnabled);

            plugin.getStorageManager().updateClan(clan);
        }

        @Subcommand("%withdraw")
        @CommandPermission("simpleclans.leader.withdraw-toggle")
        @Conditions("leader")
        public void withdraw(Player player, Clan clan) {
            toggle(player, "withdrawon", "withdrawoff", clan.isAllowWithdraw(),
                    clan::setAllowWithdraw);

            plugin.getStorageManager().updateClan(clan);
        }

        private void toggle(CommandSender sender, String onMessageKey, String offMessageKey, boolean status,
                            Consumer<Boolean> consumer) {
            String messageOn = AQUA + lang(onMessageKey, sender);
            String messageOff = AQUA + lang(offMessageKey, sender);

            ChatBlock.sendMessage(sender, status ? messageOff : messageOn);
            consumer.accept(!status);
        }
    }

    @Subcommand("%alliances")
    @CommandPermission("simpleclans.anyone.alliances")
    public class AlliancesCommand extends BaseCommand {
        // TODO move to ClanCommand
        private final String headColor = settings.getPageHeadingsColor();
        private final String subColor = settings.getPageSubTitleColor();
        private ChatBlock chatBlock;

        @Default
        public void listAlliances(CommandSender sender) {
            chatBlock = new ChatBlock();
            List<Clan> clans = cm.getClans();
            cm.sortClansByKDR(clans);
            sendHeader(sender);

            for (Clan clan : clans) {
                if (!clan.isVerified()) {
                    continue;
                }

                chatBlock.addRow("  " + AQUA + clan.getName(), clan.getAllyString(DARK_GRAY + ", "));
            }

            // TODO extract method
            boolean more = chatBlock.sendBlock(sender, settings.getPageSize());

            if (more) {
                storage.addChatBlock(sender, chatBlock);
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, headColor + lang("view.next.page", sender,
                        settings.getCommandMore()));
            }

            ChatBlock.sendBlank(sender);
        }

        private void sendHeader(CommandSender sender) {
            ChatBlock.sendBlank(sender);
            ChatBlock.saySingle(sender, settings.getServerName() + subColor + " " +
                    lang("alliances", sender) + " " + headColor +
                    Helper.generatePageSeparator(settings.getPageSep()));
            ChatBlock.sendBlank(sender);

            chatBlock.setAlignment("l", "l");
            chatBlock.addRow("  " + headColor + lang("clan", sender), lang("allies", sender));
        }
    }

    @Subcommand("%rank")
    @Conditions("verified|leader")
    public class RankCommand extends BaseCommand {

        @Subcommand("%assign")
        // TODO cp clan member
        @CommandPermission("simpleclans.leader.rank.assign")
        @CommandCompletion("@clan_members @ranks")
        public void assign(Player player, Clan clan, ClanPlayerInput member, String rank) {
            ClanPlayer memberInput = member.getClanPlayer();
            if (!clan.hasRank(rank)) {
                ChatBlock.sendMessage(player, RED + lang("rank.0.does.not.exist", player));
                return;
            }
            if (memberInput.getRankId().equals(rank)) {
                ChatBlock.sendMessage(player, lang("player.already.has.that.rank", player));
                return;
            }

            memberInput.setRank(rank);
            storage.updateClanPlayer(memberInput);
            ChatBlock.sendMessage(player, AQUA + lang("player.rank.changed", player));
        }

        @Subcommand("%unassign")
        @CommandPermission("simpleclans.leader.rank.unassign")
        @CommandCompletion("@clan_members")
        // todo cp clan member
        public void unassign(Player player, ClanPlayerInput cp) {
            ClanPlayer cpInput = cp.getClanPlayer();
            cpInput.setRank(null);
            plugin.getStorageManager().updateClanPlayer(cpInput);
            ChatBlock.sendMessage(player, AQUA + lang("player.unassigned.from.rank", player));
        }

        @Subcommand("%create")
        @CommandPermission("simpleclans.leader.rank.create")
        public void create(Player player, Clan clan) {
            Conversation conversation = new ConversationFactory(plugin).withFirstPrompt(new CreateRankNamePrompt())
                    .withLocalEcho(true).buildConversation(player);
            conversation.getContext().setSessionData("clan", clan);
            conversation.begin();
        }

        @Subcommand("%delete")
        @CommandPermission("simpleclans.leader.rank.delete")
        @CommandCompletion("@ranks")
        public void delete(Player player, Clan clan, Rank rank) {
            clan.deleteRank(rank.getName());
            plugin.getStorageManager().updateClan(clan, true);
            ChatBlock.sendMessage(player, AQUA + lang("rank.0.deleted", player, rank.getDisplayName()));
        }

        @Subcommand("%list")
        @CommandPermission("simpleclans.leader.rank.list")
        public void list(Player player, Clan clan) {
            List<Rank> ranks = clan.getRanks();

            if (ranks.isEmpty()) {
                ChatBlock.sendMessage(player, RED + lang("no.ranks", player));
                return;
            }

            ranks.sort(Comparator.reverseOrder());
            ChatBlock.sendMessage(player, AQUA + lang("clans.ranks", player));
            int count = 1;
            for (Rank rank : ranks) {
                ChatBlock.sendMessage(player, AQUA + lang("ranks.list.item", player, count,
                        Helper.parseColors(rank.getDisplayName()) + AQUA, rank.getName()));
                count++;
            }
        }

        @Subcommand("%setdisplayname")
        @CommandPermission("simpleclans.leader.rank.setdisplayname")
        @CommandCompletion("@ranks")
        public void setDisplayName(Player player, Clan clan, Rank rank, String displayName) {
            if (displayName.contains("&") && !permissions.has(player, "simpleclans.leader.coloredrank")) {
                ChatBlock.sendMessage(player, RED + lang("you.cannot.set.colored.ranks", player));
                return;
            }
            rank.setDisplayName(displayName);
            plugin.getStorageManager().updateClan(clan, true);
            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("rank.displayname.updated", player));
        }

        @Subcommand("%permissions")
        public class PermissionsCommand extends BaseCommand {
            private final String validPermissionsToMessage = Helper.toMessage(Helper.fromPermissionArray(), ",");

            @Default
            @CommandPermission("simpleclans.leader.rank.permissions.available")
            public void availablePermissions(Player player) {
                ChatBlock.sendMessage(player, AQUA + lang("available.rank.permissions", player));
                ChatBlock.sendMessage(player, AQUA + validPermissionsToMessage);
            }

            @Default
            @CommandPermission("simpleclans.leader.rank.permissions.list")
            @CommandCompletion("@ranks")
            public void list(Player player, Rank rank) {
                Set<String> permissions = rank.getPermissions();
                if (permissions.isEmpty()) {
                    ChatBlock.sendMessage(player, RED + lang("rank.no.permissions", player));
                    return;
                }
                ChatBlock.sendMessage(player, AQUA + lang("rank.0.permissions", player, rank.getDisplayName()));
                ChatBlock.sendMessage(player, AQUA + Helper.toMessage(permissions.toArray(new String[0]), ","));
            }

            @Subcommand("%add")
            @CommandPermission("simpleclans.leader.rank.permissions.add")
            // TODO Check if permission is valid
            @CommandCompletion("@ranks @rank_permissions")
            public void add(Player player, Clan clan, Rank rank, String permission) {
                Set<String> permissions = rank.getPermissions();
//                if (!RankPermission.isValid(permission)) {
//                    ChatBlock.sendMessage(player, ChatColor.RED +
//                            MessageFormat.format(lang("invalid.permission",player),
//                                    permission, validPermissionsToMessage));
//                    return;
//                }
                permissions.add(permission);
                ChatBlock.sendMessage(player, AQUA + lang("permission.0.added.to.rank.1", player, permission,
                        rank));
                plugin.getStorageManager().updateClan(clan, true);
            }

            @Subcommand("%remove")
            @CommandCompletion("@ranks @rank_permissions")
            @CommandPermission("simpleclans.leader.rank.permissions.remove")
            // TODO Check if permission is valid
            public void remove(Player player, Clan clan, Rank rank, String permission) {
                Set<String> permissions = rank.getPermissions();
                permissions.remove(permission);
                ChatBlock.sendMessage(player, AQUA + lang("permission.0.removed.from.rank.1", player,
                        permission, rank));
                plugin.getStorageManager().updateClan(clan, true);
            }

            // TODO Check which parameters can use @Values
        }
    }
}
