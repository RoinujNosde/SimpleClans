package net.sacredlabyrinth.phaed.simpleclans.commands.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandParameter;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.data.*;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.*;

@CommandAlias("%clan")
@Conditions("%basic_conditions")
public class GeneralCommands extends BaseCommand {

    @Dependency
    private SimpleClans plugin;
    @Dependency
    private ClanManager cm;
    @Dependency
    private SettingsManager settings;
    @Dependency
    private StorageManager storage;
    @Dependency
    private RequestManager requestManager;

    @Default
    @Description("{@@command.description.clan}")
    @CommandAlias("%clan")
    @HelpSearchTags("menu gui interface ui")
    public void main(CommandSender sender) {
        if (sender instanceof Player && settings.isEnableGUI()) {
            InventoryDrawer.open(new MainFrame((Player) sender));
        } else {
            help(sender, new CommandHelp(getCurrentCommandManager(),
                    getCurrentCommandManager().getRootCommand(getName()), getCurrentCommandIssuer()));
        }
    }

    @Subcommand("%create")
    @CommandPermission("simpleclans.leader.create")
    @Description("{@@command.description.create}")
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

    @Subcommand("%leaderboard")
    @CommandPermission("simpleclans.anyone.leaderboard")
    @Description("{@@command.description.leaderboard}")
    public void leaderboard(CommandSender sender) {
        Leaderboard l = new Leaderboard(plugin, sender);
        l.send();
    }

    @Subcommand("%lookup")
    @CommandCompletion("@players")
    @CommandPermission("simpleclans.anyone.lookup")
    @Description("{@@command.description.lookup.other}")
    public void lookup(CommandSender sender, @Name("player") OfflinePlayer player) {
        Lookup l = new Lookup(plugin, sender, player.getUniqueId());
        l.send();
    }

    @Subcommand("%lookup")
    @CommandPermission("simpleclans.member.lookup")
    @Description("{@@command.description.lookup}")
    public void lookup(Player sender) {
        Lookup l = new Lookup(plugin, sender, sender.getUniqueId());
        l.send();
    }

    @Subcommand("%kills")
    @CommandPermission("simpleclans.member.kills")
    @Conditions("verified|rank:name=KILLS")
    @CommandCompletion("@players")
    @Description("{@@command.description.kills}")
    public void kills(Player sender, @Optional @Name("player") ClanPlayerInput player) {
        String name = sender.getName();
        if (player != null) {
            name = player.getClanPlayer().getName();
        }
        Kills k = new Kills(plugin, sender, name);
        k.send();
    }

    @Subcommand("%profile")
    @CommandPermission("simpleclans.anyone.profile")
    @CommandCompletion("@clans")
    @Description("{@@command.description.profile.other}")
    public void profile(CommandSender sender, @Conditions("verified") @Name("clan") ClanInput clan) {
        ClanProfile p = new ClanProfile(plugin, sender, clan.getClan());
        p.send();
    }

    @Subcommand("%roster")
    @CommandCompletion("@clans")
    @CommandPermission("simpleclans.anyone.roster")
    @Description("{@@command.description.roster.other}")
    public void roster(CommandSender sender, @Conditions("verified") @Name("clan") ClanInput clan) {
        ClanRoster r = new ClanRoster(plugin, sender, clan.getClan());
        r.send();
    }

    @Subcommand("%ff %allow")
    @CommandPermission("simpleclans.member.ff")
    @Description("{@@command.description.ff.allow}")
    public void allowPersonalFf(Player player, ClanPlayer cp) {
        cp.setFriendlyFire(true);
        storage.updateClanPlayer(cp);
        ChatBlock.sendMessage(player, AQUA + lang("personal.friendly.fire.is.set.to.allowed", player));
    }

    @Subcommand("%ff %auto")
    @CommandPermission("simpleclans.member.ff")
    @Description("{@@command.description.ff.auto}")
    public void autoPersonalFf(Player player, ClanPlayer cp) {
        cp.setFriendlyFire(false);
        storage.updateClanPlayer(cp);
        ChatBlock.sendMessage(player, AQUA + lang("friendy.fire.is.now.managed.by.your.clan", player));
    }

    @Subcommand("%resetkdr")
    @CommandPermission("simpleclans.vip.resetkdr")
    @Description("{@@command.description.resetkdr}")
    public void resetKdr(Player player, ClanPlayer cp) {
        if (!settings.isAllowResetKdr()) {
            ChatBlock.sendMessage(player, RED + lang("insufficient.permissions", player));
            return;
        }
        if (cm.purchaseResetKdr(player)) {
            cm.resetKdr(cp);
            ChatBlock.sendMessage(player, RED + lang("you.have.reseted.your.kdr", player));
        }
    }

    @CommandAlias("%accept")
    @Description("{@@command.description.accept}")
    @Conditions("can_vote")
    public void accept(Player player, ClanPlayer cp) {
        Clan clan = cp.getClan();
        if (clan != null) {
            clan.leaderAnnounce(GREEN + lang("voted.to.accept", player.getName()));
        }
        requestManager.accept(cp);
    }

    @CommandAlias("%deny")
    @Description("{@@command.description.deny}")
    @Conditions("can_vote")
    public void deny(Player player, ClanPlayer cp) {
        Clan clan = cp.getClan();
        if (clan != null) {
            clan.leaderAnnounce(RED + lang("has.voted.to.deny", player.getName()));
        }
        requestManager.deny(cp);
    }

    @CommandAlias("%more")
    @Description("{@@command.description.more}")
    public void more(Player player) {
        ChatBlock chatBlock = storage.getChatBlock(player);

        if (chatBlock == null || chatBlock.size() <= 0) {
            ChatBlock.sendMessage(player, RED + lang("nothing.more.to.see", player));
            return;
        }

        chatBlock.sendBlock(player, settings.getPageSize());

        if (chatBlock.size() > 0) {
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, settings.getPageHeadingsColor() + lang("view.next.page", player,
                    settings.getCommandMore()));
        }
        ChatBlock.sendBlank(player);
    }

    @CatchUnknown
    @Subcommand("%help")
    @Description("{@@command.description.help}")
    public void help(CommandSender sender, CommandHelp help) {
        boolean inClan = sender instanceof Player && cm.getClanByPlayerUniqueId(((Player) sender).getUniqueId()) != null;
        for (HelpEntry helpEntry : help.getHelpEntries()) {
            for (@SuppressWarnings("rawtypes") CommandParameter parameter : helpEntry.getParameters()) {
                if (parameter.getType().equals(Clan.class) && !inClan) {
                    helpEntry.setSearchScore(0);
                }
            }
        }
        help.showHelp();
    }

    @Subcommand("%mostkilled")
    @CommandPermission("simpleclans.mod.mostkilled")
    @Conditions("verified|rank:name=MOSTKILLED")
    @Description("{@@command.description.mostkilled}")
    public void mostKilled(Player player) {
        MostKilled mk = new MostKilled(plugin, player);
        mk.send();
    }

    @Subcommand("%list")
    @CommandPermission("simpleclans.anyone.list")
    @Description("{@@command.description.list}")
    @CommandCompletion("@clan_list_type @order")
    public void list(CommandSender sender, @Optional @Values("@clan_list_type") String type,
                     @Optional @Single @Values("@order") String order) {
        ClanList list = new ClanList(plugin, sender, type, order);
        list.send();
    }

    @Subcommand("%rivalries")
    @CommandPermission("simpleclans.anyone.rivalries")
    @Description("{@@command.description.rivalries}")
    public void rivalries(CommandSender sender) {
        Rivalries rivalries = new Rivalries(plugin, sender);
        rivalries.send();
    }

    @Subcommand("%alliances")
    @CommandPermission("simpleclans.anyone.alliances")
    @Description("{@@command.description.alliances}")
    public void alliances(CommandSender sender) {
        Alliances a = new Alliances(plugin, sender);
        a.send();
    }

}
