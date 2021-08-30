package net.sacredlabyrinth.phaed.simpleclans.commands.clan;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.StorageManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static org.bukkit.ChatColor.AQUA;

@CommandAlias("%clan")
@Subcommand("%toggle")
@Conditions("%basic_conditions")
public class ToggleCommand extends BaseCommand {

    @Dependency
    private StorageManager storage;

    @Conditions("verified")
    public class Verified extends BaseCommand {
        @Subcommand("%bb")
        @CommandPermission("simpleclans.member.bb-toggle")
        @Description("{@@command.description.toggle.bb}")
        public void bb(Player player, ClanPlayer cp) {
            toggle(player, "bbon", "bboff", cp.isBbEnabled(), cp::setBbEnabled);
        }

        @Subcommand("%tag")
        @CommandPermission("simpleclans.member.tag-toggle")
        @Description("{@@command.description.toggle.tag}")
        public void tag(Player player, ClanPlayer cp) {
            toggle(player, "tagon", "tagoff", cp.isTagEnabled(), cp::setTagEnabled);
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
        @Conditions("rank:name=FEE_ENABLE|change_fee")
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
    }

    @Subcommand("%invite")
    @CommandPermission("simpleclans.anyone.invite-toggle")
    @Description("{@@command.description.toggle.invite}")
    public void invite(Player player, ClanPlayer cp) {
        toggle(player, "inviteon", "inviteoff", cp.isInviteEnabled(), cp::setInviteEnabled);
    }

    private void toggle(CommandSender sender, String onMessageKey, String offMessageKey, boolean status,
                        Consumer<Boolean> consumer) {
        String messageOn = AQUA + lang(onMessageKey, sender);
        String messageOff = AQUA + lang(offMessageKey, sender);

        ChatBlock.sendMessage(sender, status ? messageOff : messageOn);
        consumer.accept(!status);
    }
}
