package net.sacredlabyrinth.phaed.simpleclans.commands.general;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandParameter;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.conversation.CreateClanTagPrompt;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.RequestManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import net.sacredlabyrinth.phaed.simpleclans.ui.InventoryDrawer;
import net.sacredlabyrinth.phaed.simpleclans.ui.frames.MainFrame;
import net.sacredlabyrinth.phaed.simpleclans.utils.KDRFormat;
import net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.utils.RankingNumberResolver.RankingType.ORDINAL;
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

    @Subcommand("%ff %allow")
    @CommandPermission("simpleclans.member.ff")
    // TODO cp clan member
    @Description("{@@command.description.ff.allow}")
    public void allowPersonalFf(Player player, ClanPlayer cp) {
        cp.setFriendlyFire(true);
        storage.updateClanPlayer(cp);
        ChatBlock.sendMessage(player, AQUA + lang("personal.friendly.fire.is.set.to.allowed", player));
    }

    @Subcommand("%ff %auto")
    @CommandPermission("simpleclans.member.ff")
    @Description("{@@command.description.ff.auto}")
    // TODO cp clan member
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
        player.sendMessage(getName());
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
    @Description("{@@command.description.more")
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

    @HelpCommand
    @Description("{@@command.description.help}")
    public void help(CommandSender sender, CommandHelp help) {
        // TODO /help SimpleClans shows not replaced messages
        // TODO Not showing correctly on console due to English language
        // TODO Header footer
        // TODO Change order?
        boolean inClan = sender instanceof Player && cm.getClanByPlayerUniqueId(((Player) sender).getUniqueId()) != null;
        sender.sendMessage("SimpleClans Help"); // TODO Get from the messages file
        for (HelpEntry helpEntry : help.getHelpEntries()) {
            for (@SuppressWarnings("rawtypes") CommandParameter parameter : helpEntry.getParameters()) {
                if (parameter.getType().equals(Clan.class) && !inClan) {
                    helpEntry.setSearchScore(0);
                }
            }
        }
        System.out.println("create.command");
        help.showHelp();
    }

    @Subcommand("%list")
    @CommandPermission("simpleclans.anyone.list")
    public class ListCommand extends BaseCommand {

        private final String headColor = settings.getPageHeadingsColor();
        private final String subColor = settings.getPageSubTitleColor();

        @Default
        @Description("{@@command.description.list}")
        @CommandCompletion("@clan_list_type @order")
        public void list(CommandSender sender, @Optional @Values("@clan_list_type") String type,
                         @Optional @Single @Values("@order") String order) {
            List<Clan> clans = getListableClans();
            if (clans.isEmpty()) {
                ChatBlock.sendMessage(sender, RED + lang("no.clans.have.been.created", sender));
                return;
            }
            RankingNumberResolver<Clan, ? extends Comparable<?>> ranking = getRankingResolver(clans, type, order);
            ChatBlock chatBlock = new ChatBlock();
            sendHeader(sender, clans, chatBlock);
            for (Clan clan : clans) {
                addLine(chatBlock, ranking, clan);
            }
            // TODO Extract
            boolean more = chatBlock.sendBlock(sender, settings.getPageSize());

            if (more) {
                plugin.getStorageManager().addChatBlock(sender, chatBlock);
                ChatBlock.sendBlank(sender);
                ChatBlock.sendMessage(sender, headColor + lang("view.next.page", sender, settings.getCommandMore()));
            }

            ChatBlock.sendBlank(sender);
        }

        private RankingNumberResolver<Clan, ? extends Comparable<?>> getRankingResolver(List<Clan> clans,
                                                                                        @Nullable String type,
                                                                                        @Nullable String order) {
            boolean ascending = order == null || lang("list.order.asc").equalsIgnoreCase(order);
            if (type == null) {
                type = settings.getListDefaultOrderBy();
            }
            if (type.equalsIgnoreCase(lang("list.type.size"))) {
                return new RankingNumberResolver<>(clans, Clan::getSize, order != null && ascending, ORDINAL);
            }
            if (type.equalsIgnoreCase(lang("list.type.active"))) {
                return new RankingNumberResolver<>(clans, Clan::getLastUsed, order != null && ascending , ORDINAL);
            }
            if (type.equalsIgnoreCase(lang("list.type.founded"))) {
                return new RankingNumberResolver<>(clans, Clan::getFounded, ascending , ORDINAL);
            }
            if (type.equalsIgnoreCase(lang("list.type.name"))) {
                return new RankingNumberResolver<>(clans, Clan::getName, ascending , ORDINAL);
            }
            return new RankingNumberResolver<>(clans, clan -> KDRFormat.toBigDecimal(clan.getTotalKDR()),
                        order != null && ascending , settings.getRankingType());
        }

        @NotNull
        private List<Clan> getListableClans() {
            List<Clan> clans = plugin.getClanManager().getClans();
            clans = clans.stream().filter(clan -> clan.isVerified() || settings.isShowUnverifiedOnList())
                    .collect(Collectors.toList());
            return clans;
        }

        private void sendHeader(CommandSender sender, List<Clan> clans, ChatBlock chatBlock) {
            ChatBlock.sendBlank(sender);
            ChatBlock.saySingle(sender, settings.getServerName() + subColor + " " + lang("clans.lower", sender)
                    + " " + headColor + Helper.generatePageSeparator(settings.getPageSep()));
            ChatBlock.sendBlank(sender);
            ChatBlock.sendMessage(sender, headColor + lang("total.clans", sender) + " " + subColor + clans.size());
            ChatBlock.sendBlank(sender);
            chatBlock.setAlignment("c", "l", "c", "c");
            chatBlock.setFlexibility(false, true, false, false);
            chatBlock.addRow("  " + headColor + lang("rank", sender), lang("name", sender),
                    lang("kdr", sender), lang("members", sender));
        }

        @SuppressWarnings("deprecation")
        private void addLine(ChatBlock chatBlock, RankingNumberResolver<Clan, ? extends Comparable<?>> ranking,
                             Clan clan) {
            String tag = settings.getClanChatBracketColor() + settings.getClanChatTagBracketLeft()
                    + settings.getTagDefaultColor() + clan.getColorTag() + settings.getClanChatBracketColor()
                    + settings.getClanChatTagBracketRight();
            String name = (clan.isVerified() ? settings.getPageClanNameColor() : GRAY) + clan.getName();
            String fullname = tag + " " + name;
            String size = WHITE + "" + clan.getSize();
            String kdr = clan.isVerified() ? YELLOW + "" + KDRFormat.format(clan.getTotalKDR()) : "";

            chatBlock.addRow("  " + ranking.getRankingNumber(clan), fullname, kdr, size);
        }
    }

    // TODO Refactor list commands
    @Subcommand("%alliances")
    @CommandPermission("simpleclans.anyone.alliances")
    public class AlliancesCommand extends BaseCommand {
        private final String headColor = settings.getPageHeadingsColor();
        private final String subColor = settings.getPageSubTitleColor();

        @Default
        @Description("{@@command.description.alliances}")
        public void list(CommandSender sender) {
            ChatBlock chatBlock = new ChatBlock();
            List<Clan> clans = cm.getClans();
            cm.sortClansByKDR(clans);
            sendHeader(sender, chatBlock);

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

        private void sendHeader(CommandSender sender, ChatBlock chatBlock) {
            ChatBlock.sendBlank(sender);
            ChatBlock.saySingle(sender, settings.getServerName() + subColor + " " +
                    lang("alliances", sender) + " " + headColor +
                    Helper.generatePageSeparator(settings.getPageSep()));
            ChatBlock.sendBlank(sender);

            chatBlock.setAlignment("l", "l");
            chatBlock.addRow("  " + headColor + lang("clan", sender), lang("allies", sender));
        }
    }
}
