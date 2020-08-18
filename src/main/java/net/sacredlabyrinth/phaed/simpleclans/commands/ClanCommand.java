package net.sacredlabyrinth.phaed.simpleclans.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.*;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

// TODO Add descriptions
@CommandAlias("%clan")
@Conditions("not_banned")
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

    @Subcommand("%create")
    @CommandPermission("simpleclans.leader.create")
    public void onCreate(Player player) {
        ClanPlayer cp = cm.getAnyClanPlayer(player.getUniqueId());

        if (cp != null && cp.getClan() != null) {
            ChatBlock.sendMessage(player, ChatColor.RED +
                    MessageFormat.format(lang("you.must.first.resign", player), cp.getClan().getName()));
            return;
        }

        Conversation conversation = new ConversationFactory(plugin).withFirstPrompt(new CreateClanTagPrompt())
                .withLocalEcho(true).buildConversation(player);
        conversation.begin();
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
            ChatBlock.sendMessage(sender, ChatColor.RED + lang("you.cannot.kick.yourself", sender));
            return;
        }

        Clan clan = cm.getClanByPlayerUniqueId(sender.getUniqueId());
        if (Objects.requireNonNull(clan).isLeader(clanPlayer.getUniqueId())) {
            ChatBlock.sendMessage(sender, ChatColor.RED + lang("you.cannot.kick.another.leader", sender));
            return;
        }

        clan.addBb(sender.getName(), ChatColor.AQUA + lang("has.been.kicked.by", clanPlayer.getName(),
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
        ChatBlock.sendMessage(player, ChatColor.RED + lang("cleared.bb", player));
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
        clan.addBb(player.getName(), ChatColor.AQUA + player.getName() + ": " + ChatColor.WHITE + msg);
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
                cols.add(ChatColor.AQUA + "" + distance);
                cols.add(ChatColor.WHITE + "" + coords);
                cols.add(world);
                rows.put(distance, cols);
            }
        }

        if (rows.isEmpty()) {
            ChatBlock.sendMessage(sender, ChatColor.RED + lang("you.are.the.only.member.online", sender));
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
            ChatBlock.sendMessage(sender, headColor + MessageFormat.format(lang("view.next.page", sender),
                    settings.getCommandMore()));
        }

        ChatBlock.sendBlank(sender);
    }

    @Subcommand("%alliances")
    @CommandPermission("simpleclans.anyone.alliances")
    public void listAlliances(CommandSender sender) {
        String headColor = settings.getPageHeadingsColor();
        String subColor = settings.getPageSubTitleColor();

        List<Clan> clans = cm.getClans();
        cm.sortClansByKDR(clans);

        ChatBlock chatBlock = new ChatBlock();

        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, settings.getServerName() + subColor + " " +
                lang("alliances", sender) + " " + headColor +
                Helper.generatePageSeparator(settings.getPageSep()));
        ChatBlock.sendBlank(sender);

        chatBlock.setAlignment("l", "l");
        chatBlock.addRow("  " + headColor + lang("clan", sender), lang("allies", sender));

        for (Clan clan : clans) {
            if (!clan.isVerified()) {
                continue;
            }

            chatBlock.addRow("  " + ChatColor.AQUA + clan.getName(), clan.getAllyString(ChatColor.DARK_GRAY + ", "));
        }

        // TODO extract method
        boolean more = chatBlock.sendBlock(sender, settings.getPageSize());

        if (more) {
            storage.addChatBlock(sender, chatBlock);
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, headColor + MessageFormat.format(lang("view.next.page", sender),
                    settings.getCommandMore()));
        }

        ChatBlock.sendBlank(sender);
    }

    @Subcommand("%place")
    @CommandPermission("simpleclans.mod.place")
    @CommandCompletion("@players @clans")
    @HelpSearchTags("move put")
    public void place(CommandSender sender, OnlinePlayer onlinePlayer, @Flags("other_only") Clan newClan) {
        Player player = onlinePlayer.getPlayer();
        ClanPlayer oldCp = cm.getClanPlayer(player);
        if (oldCp != null) {
            Clan oldClan = Objects.requireNonNull(oldCp.getClan());

            if (oldClan.equals(newClan)) {
                ChatBlock.sendMessage(sender, lang("player.already.in.this.clan", sender));
                return;
            }

            if (oldClan.isLeader(player) && oldClan.getLeaders().size() <= 1) {
                oldClan.clanAnnounce(player.getName(), ChatColor.AQUA + lang("clan.has.been.disbanded",
                        oldClan.getName()));
                oldClan.disband();
            } else {
                oldClan.addBb(player.getName(), ChatColor.AQUA + lang("0.has.resigned", player.getName()));
                oldClan.removePlayerFromClan(player.getUniqueId());
            }
        }

        ClanPlayer cp = Objects.requireNonNull(cm.getCreateClanPlayerUUID(player.getName()));

        newClan.addBb(ChatColor.AQUA + lang("joined.the.clan", player.getName()));
        cm.serverAnnounce(lang("has.joined", player.getName(), newClan.getName()));
        newClan.addPlayerToClan(cp);
    }

    @HelpCommand
    public void doHelp(CommandSender sender, CommandHelp help) {
        // TODO Not showing correctly on console
        sender.sendMessage("SimpleClans Help"); // TODO Get from the messages file
        help.showHelp();
    }

    // TODO Help search tags
}
