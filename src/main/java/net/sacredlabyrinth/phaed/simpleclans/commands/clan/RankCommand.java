package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateRankNamePrompt;
import net.sacredlabyrinth.phaed.simpleclans.conversation.RequestCanceller;
import net.sacredlabyrinth.phaed.simpleclans.events.DeleteRankEvent;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerRankUpdateEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.PermissionsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

@CommandAlias("%clan")
@Subcommand("%rank")
@Conditions("%basic_conditions|verified|leader")
public class RankCommand extends BaseCommand {

    @Dependency
    private SimpleClans plugin;
    @Dependency
    private StorageManager storage;
    @Dependency
    private PermissionsManager permissions;

    @Subcommand("%assign")
    @CommandPermission("simpleclans.leader.rank.assign")
    @CommandCompletion("@clan_members @ranks")
    @Description("{@@command.description.rank.assign}")
    public void assign(ClanPlayer player,
                       Clan clan,
                       @Name("member") @Conditions("same_clan") ClanPlayerInput member,
                       @Name("rank") Rank rank) {
        ClanPlayer memberInput = member.getClanPlayer();

        PlayerRankUpdateEvent event = new PlayerRankUpdateEvent(player, memberInput, clan, clan.getRank(memberInput.getRankId()), rank);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        if (memberInput.getRankId().equals(rank.getName())) {
            ChatBlock.sendMessage(player, lang("player.already.has.that.rank", player));
            return;
        }

        memberInput.setRank(rank.getName());
        storage.updateClanPlayer(memberInput);
        ChatBlock.sendMessage(player, AQUA + lang("player.rank.changed", player));
    }

    @Subcommand("%unassign")
    @CommandPermission("simpleclans.leader.rank.unassign")
    @CommandCompletion("@clan_members")
    @Description("{@@command.description.rank.unassign}")
    public void unassign(ClanPlayer player, Clan clan, @Conditions("same_clan") @Name("member") ClanPlayerInput cp) {
        ClanPlayer memberInput = cp.getClanPlayer();

        PlayerRankUpdateEvent event = new PlayerRankUpdateEvent(player, memberInput, clan, clan.getRank(memberInput.getRankId()), null);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        memberInput.setRank(null);
        storage.updateClanPlayer(memberInput);
        ChatBlock.sendMessage(player, AQUA + lang("player.unassigned.from.rank", player));
    }

    @Subcommand("%create")
    @CommandPermission("simpleclans.leader.rank.create")
    @Description("{@@command.description.rank.create}")
    public void create(Player player, Clan clan) {
        Conversation conversation = new ConversationFactory(plugin).withFirstPrompt(new CreateRankNamePrompt())
                .withLocalEcho(true)
                .withConversationCanceller(new RequestCanceller(player, AQUA + lang("clan.create.request.cancelled", player)))
                .withTimeout(60).buildConversation(player);
        conversation.getContext().setSessionData("clan", clan);
        conversation.begin();
    }

    @Subcommand("%delete")
    @CommandPermission("simpleclans.leader.rank.delete")
    @CommandCompletion("@ranks")
    @Description("{@@command.description.rank.delete}")
    public void delete(Player player, Clan clan, @Name("rank") Rank rank) {
        DeleteRankEvent event = new DeleteRankEvent(player, clan, rank);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            clan.deleteRank(rank.getName());
            storage.updateClan(clan, true);
            ChatBlock.sendMessage(player, AQUA + lang("rank.0.deleted", player, rank.getDisplayName()));
        }
    }

    @Subcommand("%list")
    @CommandPermission("simpleclans.leader.rank.list")
    @Description("{@@command.description.rank.list}")
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
                    ChatUtils.parseColors(rank.getDisplayName()) + AQUA, rank.getName()));
            count++;
        }
    }

    @Subcommand("%setdisplayname")
    @CommandPermission("simpleclans.leader.rank.setdisplayname")
    @CommandCompletion("@ranks @nothing")
    @Description("{@@command.description.rank.setdisplayname}")
    public void setDisplayName(Player player, Clan clan, @Name("rank") Rank rank, @Name("displayname") String displayName) {
        if (displayName.contains("&") && !permissions.has(player, "simpleclans.leader.coloredrank")) {
            ChatBlock.sendMessage(player, RED + lang("you.cannot.set.colored.ranks", player));
            return;
        }
        rank.setDisplayName(displayName);
        storage.updateClan(clan, true);
        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("rank.displayname.updated", player));
    }

    @Subcommand("%setdefault")
    @CommandPermission("simpleclans.leader.rank.setdefault")
    @CommandCompletion("@ranks")
    @Description("{@@command.description.rank.setdefault}")
    public void setDefault(Player player, Clan clan, @Name("rank") Rank rank) {
        clan.setDefaultRank(rank.getName());
        ChatBlock.sendMessage(player, AQUA + lang("rank.setdefault", player, rank.getDisplayName()));
    }

    @Subcommand("%removedefault")
    @CommandPermission("simpleclans.leader.rank.removedefault")
    @Description("{@@command.description.rank.removedefault}")
    public void removeDefault(Player player, Clan clan) {
        clan.setDefaultRank(null);
        ChatBlock.sendMessage(player, AQUA + lang("rank.removedefault", player));
    }

    @Subcommand("%permissions")
    public class PermissionsCommand extends BaseCommand {
        private final String validPermissionsToMessage = Helper.toMessage(Helper.fromPermissionArray(), ",");

        @Default
        @CommandPermission("simpleclans.leader.rank.permissions.available")
        @Description("{@@command.description.rank.permissions.available}")
        public void availablePermissions(Player player) {
            ChatBlock.sendMessage(player, AQUA + lang("available.rank.permissions", player));
            ChatBlock.sendMessage(player, AQUA + validPermissionsToMessage);
        }

        @Default
        @CommandPermission("simpleclans.leader.rank.permissions.list")
        @CommandCompletion("@ranks")
        @Description("{@@command.description.rank.permissions.rank}")
        public void list(Player player, @Name("rank") Rank rank) {
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
        @CommandCompletion("@ranks @rank_permissions")
        @Description("{@@command.description.rank.permissions.add}")
        public void add(Player player,
                        Clan clan,
                        @Name("rank") Rank rank,
                        @Values("@rank_permissions") @Name("permission") String permission) {
            Set<String> permissions = rank.getPermissions();
            permissions.add(permission);
            ChatBlock.sendMessage(player, AQUA + lang("permission.0.added.to.rank.1", player, permission,
                    rank.getDisplayName()));
            storage.updateClan(clan, true);
        }

        @Subcommand("%remove")
        @CommandCompletion("@ranks @rank_permissions")
        @CommandPermission("simpleclans.leader.rank.permissions.remove")
        @Description("{@@command.description.rank.permissions.remove}")
        public void remove(Player player,
                           Clan clan,
                           @Name("rank") Rank rank,
                           @Values("@rank_permissions") @Name("permission") String permission) {
            Set<String> permissions = rank.getPermissions();
            permissions.remove(permission);
            ChatBlock.sendMessage(player, AQUA + lang("permission.0.removed.from.rank.1", player,
                    permission, rank.getDisplayName()));
            storage.updateClan(clan, true);
        }
    }
}
