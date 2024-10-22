package net.sacredlabyrinth.phaed.simpleclans.commands.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandParameter;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.data.*;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import net.sacredlabyrinth.phaed.simpleclans.conversation.RequestCanceller;
import net.sacredlabyrinth.phaed.simpleclans.conversation.ResetKdrPrompt;
import net.sacredlabyrinth.phaed.simpleclans.conversation.SCConversation;
import net.sacredlabyrinth.phaed.simpleclans.events.PlayerResetKdrEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanNamePrompt.NAME_KEY;
import static net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt.TAG_KEY;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
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
    @HelpSearchTags("menu gui interface ui")
    public void main(CommandSender sender) {
        if (sender instanceof Player && settings.is(ENABLE_GUI)) {
            InventoryDrawer.open(new MainFrame((Player) sender));
        } else {
            help(sender, new CommandHelp(getCurrentCommandManager(),
                    getCurrentCommandManager().getRootCommand(getName()), getCurrentCommandIssuer()));
        }
    }

    @Subcommand("%locale")
    @CommandPermission("simpleclans.anyone.locale")
    @Description("{@@command.description.locale}")
    @CommandCompletion("@locales")
    public void locale(ClanPlayer cp, @Values("@locales") @Name("locale") @Single String locale) {
        if (!settings.is(LANGUAGE_SELECTOR)) {
            ChatBlock.sendMessageKey(cp, "locale.is.prohibited");
            return;
        }

        cp.setLocale(Helper.forLanguageTag(locale.replace("_", "-")));
        plugin.getStorageManager().updateClanPlayer(cp);

        ChatBlock.sendMessageKey(cp, "locale.has.been.changed");
    }

    @Subcommand("%create")
    @CommandPermission("simpleclans.leader.create")
    @Description("{@@command.description.create}")
    public void create(Player player, @Optional @Name("tag") String tag, @Optional @Name("name") String name) {
        ClanPlayer cp = cm.getAnyClanPlayer(player.getUniqueId());

        if (cp != null && cp.getClan() != null) {
            ChatBlock.sendMessage(player, RED + lang("you.must.first.resign", player,
                    cp.getClan().getName()));
            return;
        }
        HashMap<Object, Object> initialData = new HashMap<>();
        initialData.put(TAG_KEY, tag);
        initialData.put(NAME_KEY, name);
        SCConversation conversation = new SCConversation(plugin, player, new CreateClanTagPrompt(), initialData);
        conversation.addConversationCanceller(new RequestCanceller(player, RED + lang("clan.create.request.cancelled", player)));
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
    public void lookup(CommandSender sender, @Name("player") ClanPlayerInput player) {
        Lookup l = new Lookup(plugin, sender, player.getClanPlayer().getUniqueId());
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
    @CommandCompletion("@clans:hide_own")
    @Description("{@@command.description.profile.other}")
    public void profile(CommandSender sender, @Conditions("verified") @Name("clan") ClanInput clan) {
        ClanProfile p = new ClanProfile(plugin, sender, clan.getClan());
        p.send();
    }

    @Subcommand("%roster")
    @CommandCompletion("@clans:hide_own")
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

    @Subcommand("%resetkdr %confirm")
    @CommandPermission("simpleclans.vip.resetkdr")
    @Description("{@@command.description.resetkdr}")
    public void resetKdrConfirm(Player player, ClanPlayer cp) {
        if (!settings.is(ALLOW_RESET_KDR)) {
            ChatBlock.sendMessage(player, RED + lang("disabled.command", player));
            return;
        }
        PlayerResetKdrEvent event = new PlayerResetKdrEvent(cp);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled() && cm.purchaseResetKdr(player)) {
            cm.resetKdr(cp);
            ChatBlock.sendMessage(player, RED + lang("you.have.reseted.your.kdr", player));
        }
    }

    @Subcommand("%resetkdr")
    @CommandPermission("simpleclans.vip.resetkdr")
    @Description("{@@command.description.resetkdr}")
    public void resetKdr(Player player, ClanPlayer cp) {
        if (!settings.is(ALLOW_RESET_KDR)) {
            ChatBlock.sendMessage(player, RED + lang("disabled.command", player));
        } else {
            new SCConversation(plugin, player, new ResetKdrPrompt(cm), 60).begin();
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

        chatBlock.sendBlock(player, settings.getInt(PAGE_SIZE));

        if (chatBlock.size() > 0) {
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, settings.getColored(PAGE_HEADINGS_COLOR) + lang("view.next.page", player,
                    settings.getString(COMMANDS_MORE)));
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

    @Subcommand("%list %balance")
    @CommandPermission("simpleclans.anyone.list.balance")
    @Description("{@@command.description.list.balance}")
    public void listBalance(CommandSender sender) {
        List<Clan> clans = cm.getClans();
        if (clans.isEmpty()) {
            sender.sendMessage(RED + lang("no.clans.have.been.created", sender));
            return;
        }
        clans.sort(Comparator.comparingDouble(Clan::getBalance).reversed());

        sender.sendMessage(lang("clan.list.balance.header", sender, settings.getColored(SERVER_NAME), clans.size()));
        String lineFormat = lang("clan.list.balance.line", sender);

        String leftBracket = settings.getColored(TAG_BRACKET_COLOR) + settings.getColored(TAG_BRACKET_LEFT);
        String rightBracket = settings.getColored(TAG_BRACKET_COLOR) + settings.getColored(TAG_BRACKET_RIGHT);
        for (int i = 0; i < 10 && i < clans.size(); i++) {
            Clan clan = clans.get(i);
            String name = " " + (clan.isVerified() ? settings.getColored(PAGE_CLAN_NAME_COLOR) : GRAY) + clan.getName();
            String line = MessageFormat.format(lineFormat, i + 1, leftBracket, clan.getColorTag(),
                    rightBracket, name, clan.getBalanceFormatted());
            sender.sendMessage(line);
        }
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
