package net.sacredlabyrinth.phaed.simpleclans.commands.data;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.*;
import static org.bukkit.ChatColor.*;

public class Vitals extends Sendable {

    private final Clan clan;

    public Vitals(@NotNull SimpleClans plugin, @NotNull CommandSender sender, @NotNull Clan clan) {
        super(plugin, sender);
        this.clan = clan;
    }

    @Override
    public void send() {
        configureAndSendHeader();

        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
        members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));

        addRows(members);

        chatBlock.addRow(" -- Allies -- ", "", "", "", "", "");

        addRows(clan.getAllAllyMembers());

        sendBlock();
    }

    private void configureAndSendHeader() {
        ChatBlock.sendBlank(sender);
        ChatBlock.saySingle(sender, sm.getColored(PAGE_CLAN_NAME_COLOR) + clan.getName() + subColor + " " +
                lang("vitals", sender) + " " + headColor + Helper.generatePageSeparator(sm.getString(PAGE_SEPARATOR)));
        ChatBlock.sendBlank(sender);
        ChatBlock.sendMessage(sender, headColor + lang("weapons", sender) + ": " +
                lang("0.s.sword.1.2.b.bow.3.4.a.arrow", sender, WHITE, DARK_GRAY, WHITE, DARK_GRAY, WHITE));
        ChatBlock.sendMessage(sender, headColor + lang("materials", sender) + ": " + AQUA +
                lang("diamond", sender) + DARK_GRAY + ", " + YELLOW + lang("gold", sender) + DARK_GRAY + ", "
                + GRAY + lang("stone", sender) + DARK_GRAY + ", " + WHITE + lang("iron", sender) + DARK_GRAY +
                ", " + GOLD + lang("wood", sender));

        ChatBlock.sendBlank(sender);

        chatBlock.setFlexibility(true, false, false, false, false, false);
        chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

        chatBlock.addRow("  " + headColor + lang("name", sender), lang("health", sender),
                lang("hunger", sender), lang("food", sender), lang("armor", sender),
                lang("weapons", sender));
    }

    private void addRows(Collection<ClanPlayer> players){
        for (ClanPlayer cpm : players) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? sm.getColored(PAGE_LEADER_COLOR) : (cpm.isTrusted() ? sm.getColored(PAGE_TRUSTED_COLOR)
                        : sm.getColored(PAGE_UNTRUSTED_COLOR))) + cpm.getName();
                String health = cm.getHealthString(p.getHealth());
                String hunger = cm.getHungerString(p.getFoodLevel());
                String armor = cm.getArmorString(p.getInventory());
                String weapons = cm.getWeaponString(p.getInventory());
                String food = cm.getFoodString(p.getInventory());

                chatBlock.addRow("  " + name, RED + health, hunger, WHITE + food, armor, weapons);
            }
        }
    }
}
