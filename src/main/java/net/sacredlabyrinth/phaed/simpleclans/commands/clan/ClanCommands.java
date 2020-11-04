package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.*;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanInput;
import net.sacredlabyrinth.phaed.simpleclans.commands.ClanPlayerInput;
import net.sacredlabyrinth.phaed.simpleclans.conversation.ResignPrompt;
import net.sacredlabyrinth.phaed.simpleclans.events.TagChangeEvent;
import net.sacredlabyrinth.phaed.simpleclans.managers.*;
import net.sacredlabyrinth.phaed.simpleclans.utils.TagValidator;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RED;

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

    @Subcommand("%war %start")
    @CommandPermission("simpleclans.leader.war")
    @Conditions("verified|rank:name=WAR_START")
    @Description("{@@command.description.war.start}")
    @CommandCompletion("@rivals")
    public void startWar(Player player, ClanPlayer cp, Clan issuerClan, @Name("clan") ClanInput other) {
        Clan war = other.getClan();
        if (!issuerClan.isRival(war.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("you.can.only.start.war.with.rivals", player));
            return;
        }
        if (!issuerClan.isWarring(war.getTag())) {
            List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(issuerClan.getLeaders());

            if (!onlineLeaders.isEmpty()) {
                requestManager.addWarStartRequest(cp, war, issuerClan);
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
    public void endWar(Player player, ClanPlayer cp, Clan issuerClan, @Name("clan") ClanInput other) {
        Clan war = other.getClan();
        if (issuerClan.isWarring(war.getTag())) {
            requestManager.addWarEndRequest(cp, war, issuerClan);
            ChatBlock.sendMessage(player, AQUA + lang("leaders.asked.to.end.rivalry", player, war.getName()));
        } else {
            ChatBlock.sendMessage(player, RED + lang("clans.not.at.war", player));
        }
    }

    @Subcommand("%modtag")
    @CommandPermission("simpleclans.leader.modtag")
    @Conditions("verified|rank:name=MODTAG")
    @Description("{@@command.description.modtag}")
    public void modtag(Player player, Clan clan, @Single @Name("tag") String tag) {
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

    @Subcommand("%invite")
    @CommandPermission("simpleclans.leader.invite")
    @CommandCompletion("@non_members")
    @Conditions("rank:name=INVITE")
    @Description("{@@command.description.invite}")
    public void invite(Player sender, ClanPlayer cp, Clan clan,
                       @Conditions("not_banned|not_in_clan|online") @Name("player") ClanPlayerInput invited) {
        Player invitedPlayer = invited.getClanPlayer().toPlayer();
        if (invitedPlayer == null) return;
        if (!permissions.has(invitedPlayer, "simpleclans.member.can-join")) {
            ChatBlock.sendMessage(sender, RED +
                    lang("the.player.doesn.t.not.have.the.permissions.to.join.clans", sender));
            return;
        }
        if (invitedPlayer.getUniqueId().equals(sender.getUniqueId())) {
            ChatBlock.sendMessage(sender, RED + lang("you.cannot.invite.yourself", sender));
            return;
        }
        long minutesBeforeRejoin = cm.getMinutesBeforeRejoin(invited.getClanPlayer(), clan);
        if (minutesBeforeRejoin != 0) {
            ChatBlock.sendMessage(sender, RED +
                    lang("the.player.must.wait.0.before.joining.your.clan.again", sender, minutesBeforeRejoin));
            return;
        }

        if (clan.getSize() >= settings.getMaxMembers()) {
            ChatBlock.sendMessage(sender, RED + lang("the.clan.members.reached.limit", sender));
            return;
        }
        if (!cm.purchaseInvite(sender)) {
            return;
        }

        requestManager.addInviteRequest(cp, invitedPlayer.getName(), clan);
        ChatBlock.sendMessage(sender, AQUA + lang("has.been.asked.to.join",
                sender, invitedPlayer.getName(), clan.getName()));
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
    public void setFee(Player player, Clan clan, @Name("fee") double fee) {
        fee = Math.abs(fee);
        double maxFee = settings.getMaxMemberFee();
        if (fee > maxFee) {
            ChatBlock.sendMessage(player, RED
                    + lang("max.fee.allowed.is.0", player, maxFee));
            return;
        }
        if (cm.purchaseMemberFeeSet(player)) {
            clan.setMemberFee(fee);
            clan.addBb(player.getName(), AQUA + lang("bb.fee.set", fee));
            ChatBlock.sendMessage(player, AQUA + lang("fee.set", player));
            storage.updateClan(clan);
        }
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
    public void setDescription(Player player, Clan clan, @Name("description") String description) {
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

    @Subcommand("%rival %add")
    @CommandPermission("simpleclans.leader.rival")
    @Conditions("verified|rivable|minimum_to_rival|rank:name=RIVAL_ADD")
    @CommandCompletion("@clans")
    @Description("{@@command.description.rival.add}")
    public void addRival(Player player, Clan issuerClan, @Conditions("verified|different") @Name("clan") ClanInput rival) {
        Clan rivalInput = rival.getClan();
        if (settings.isUnrivable(rivalInput.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("the.clan.cannot.be.rivaled", player));
            return;
        }
        if (!issuerClan.reachedRivalLimit()) {
            if (!issuerClan.isRival(rivalInput.getTag())) {
                issuerClan.addRival(rivalInput);
                rivalInput.addBb(player.getName(), AQUA + lang("has.initiated.a.rivalry", issuerClan.getName(),
                        rivalInput.getName()), false);
                issuerClan.addBb(player.getName(), AQUA + lang("has.initiated.a.rivalry", player.getName(),
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
    public void removeRival(Player player,
                            ClanPlayer cp,
                            Clan issuerClan,
                            @Conditions("different") @Name("clan") ClanInput rival) {
        Clan rivalInput = rival.getClan();
        if (issuerClan.isRival(rivalInput.getTag())) {
            requestManager.addRivalryBreakRequest(cp, rivalInput, issuerClan);
            ChatBlock.sendMessage(player, AQUA + lang("leaders.asked.to.end.rivalry", player,
                    rivalInput.getName()));
        } else {
            ChatBlock.sendMessage(player, RED + lang("your.clans.are.not.rivals", player));
        }
    }

    @Subcommand("%ally %add")
    @CommandPermission("simpleclans.leader.ally")
    @Conditions("verified|rank:name=ALLY_ADD|minimum_to_ally")
    @CommandCompletion("@clans")
    @Description("{@@command.description.ally.add}")
    public void addAlly(Player player,
                        ClanPlayer cp,
                        Clan issuerClan,
                        @Conditions("verified|different") @Name("clan") ClanInput other) {
        Clan input = other.getClan();
        if (issuerClan.isAlly(input.getTag())) {
            ChatBlock.sendMessage(player, RED + lang("your.clans.are.already.allies", player));
            return;
        }
        int maxAlliances = settings.getClanMaxAlliances();
        if (maxAlliances != -1) {
            if (issuerClan.getAllies().size() >= maxAlliances) {
                ChatBlock.sendMessage(player, lang("your.clan.reached.max.alliances", player));
                return;
            }
            if (input.getAllies().size() >= maxAlliances) {
                ChatBlock.sendMessage(player, lang("other.clan.reached.max.alliances", player));
                return;
            }
        }

        List<ClanPlayer> onlineLeaders = Helper.stripOffLinePlayers(issuerClan.getLeaders());
        if (onlineLeaders.isEmpty()) {
            ChatBlock.sendMessage(player, RED + lang("at.least.one.leader.accept.the.alliance",
                    player));
            return;
        }

        requestManager.addAllyRequest(cp, input, issuerClan);
        ChatBlock.sendMessage(player, AQUA + lang("leaders.have.been.asked.for.an.alliance",
                player, input.getName()));
    }

    @Subcommand("%ally %remove")
    @Conditions("verified|rank:name=ALLY_REMOVE")
    @CommandPermission("simpleclans.leader.ally")
    @Description("{@@command.description.ally.remove}")
    @CommandCompletion("@allied_clans")
    public void removeAlly(Player player, Clan issuerClan, @Conditions("different|allied_clan") @Name("clan") ClanInput ally) {
        Clan allyInput = ally.getClan();
        issuerClan.removeAlly(allyInput);
        allyInput.addBb(player.getName(), AQUA + lang("has.broken.the.alliance", issuerClan.getName(),
                allyInput.getName()), false);
        issuerClan.addBb(player.getName(), AQUA + lang("has.broken.the.alliance", player.getName(),
                allyInput.getName()));
    }

    @Subcommand("%kick")
    @CommandPermission("simpleclans.leader.kick")
    @CommandCompletion("@clan_members")
    @Conditions("rank:name=KICK")
    @Description("{@@command.description.kick}")
    public void kick(@Conditions("clan_member") Player sender,
                     @Conditions("same_clan") @Name("member") ClanPlayerInput other) {
        ClanPlayer clanPlayer = other.getClanPlayer();
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

    @Subcommand("%resign")
    @CommandPermission("simpleclans.member.resign")
    @Description("{@@command.description.resign}")
    public void resign(@Conditions("clan_member") Player player) {
        new ConversationFactory(plugin)
                .withFirstPrompt(new ResignPrompt())
                .withLocalEcho(true)
                .withTimeout(10)
                .buildConversation(player).begin();
    }
}
