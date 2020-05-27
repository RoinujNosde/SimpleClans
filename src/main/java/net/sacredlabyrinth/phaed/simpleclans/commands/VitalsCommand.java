package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

/**
 * @author phaed
 */
public class VitalsCommand {
    public VitalsCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();
        String headColor = plugin.getSettingsManager().getPageHeadingsColor();
        String subColor = plugin.getSettingsManager().getPageSubTitleColor();

        if (!plugin.getPermissionsManager().has(player, "simpleclans.member.vitals")) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            return;
        }
        ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

        if (cp == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
            return;
        }
        Clan clan = cp.getClan();

        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
            return;
        }
        
        if (!plugin.getPermissionsManager().has(player, RankPermission.VITALS, PermissionLevel.TRUSTED, true)) {
        	return;
        }

        if (arg.length != 0) {
            ChatBlock.sendMessage(player, ChatColor.RED + MessageFormat.format(lang("usage.0.vitals",player), plugin.getSettingsManager().getCommandClan()));
            return;
        }
        ChatBlock chatBlock = new ChatBlock();
        ChatBlock.sendBlank(player);
        ChatBlock.saySingle(player, plugin.getSettingsManager().getPageClanNameColor() + clan.getName() + subColor + " " + lang("vitals",player) + " " + headColor + Helper.generatePageSeparator(plugin.getSettingsManager().getPageSep()));
        ChatBlock.sendBlank(player);
        ChatBlock.sendMessage(player, headColor + lang("weapons",player) + ": " + MessageFormat.format(lang("0.s.sword.1.2.b.bow.3.4.a.arrow",player), ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE, ChatColor.DARK_GRAY, ChatColor.WHITE));
        ChatBlock.sendMessage(player, headColor + lang("materials",player) + ": " + ChatColor.AQUA + lang("diamond",player) + ChatColor.DARK_GRAY + ", " + ChatColor.YELLOW + lang("gold",player) + ChatColor.DARK_GRAY + ", " + ChatColor.GRAY + lang("stone",player) + ChatColor.DARK_GRAY + ", " + ChatColor.WHITE + lang("iron",player) + ChatColor.DARK_GRAY + ", " + ChatColor.GOLD + lang("wood",player));

        ChatBlock.sendBlank(player);

        chatBlock.setFlexibility(true, false, false, false, false, false);
        chatBlock.setAlignment("l", "l", "l", "c", "c", "c");

        chatBlock.addRow("  " + headColor + lang("name",player), lang("health",player), lang("hunger",player), lang("food",player), lang("armor",player), lang("weapons",player));

        List<ClanPlayer> members = Helper.stripOffLinePlayers(clan.getLeaders());
        members.addAll(Helper.stripOffLinePlayers(clan.getNonLeaders()));

        addRows(members, chatBlock);

        chatBlock.addRow(" -- Allies -- ", "", "", "", "", "");

        addRows(clan.getAllAllyMembers(), chatBlock);

        boolean more = chatBlock.sendBlock(player, plugin.getSettingsManager().getPageSize());

        if (more) {
            plugin.getStorageManager().addChatBlock(player, chatBlock);
            ChatBlock.sendBlank(player);
            ChatBlock.sendMessage(player, headColor + MessageFormat.format(lang("view.next.page",player), plugin.getSettingsManager().getCommandMore()));
        }

        ChatBlock.sendBlank(player);
    }

    private void addRows(Collection<ClanPlayer> players, ChatBlock chatBlock){
        SimpleClans plugin = SimpleClans.getInstance();
        for (ClanPlayer cpm : players) {
            Player p = cpm.toPlayer();

            if (p != null) {
                String name = (cpm.isLeader() ? plugin.getSettingsManager().getPageLeaderColor() : (cpm.isTrusted() ? plugin.getSettingsManager().getPageTrustedColor() : plugin.getSettingsManager().getPageUnTrustedColor())) + cpm.getName();
                String health = plugin.getClanManager().getHealthString(p.getHealth());
                String hunger = plugin.getClanManager().getHungerString(p.getFoodLevel());
                String armor = plugin.getClanManager().getArmorString(p.getInventory());
                String weapons = plugin.getClanManager().getWeaponString(p.getInventory());
                String food = plugin.getClanManager().getFoodString(p.getInventory());

                chatBlock.addRow("  " + name, ChatColor.RED + health, hunger, ChatColor.WHITE + food, armor, weapons);
            }
        }
    }
}
