package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.*;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class SetBannerCommand {
    private final SimpleClans plugin = SimpleClans.getInstance();

    public void execute(@NotNull Player player) {
        ClanPlayer cp = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());

        if (cp == null || cp.getClan() == null) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan", player));
            return;
        }

        Clan clan = cp.getClan();
        if (!clan.isVerified()) {
            ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified", player));
            return;
        }

        if (!plugin.getPermissionsManager().has(player, RankPermission.SETBANNER, true)) {
            return;
        }

        ItemStack hand = player.getItemInHand();
        if (!hand.getType().toString().contains("BANNER")) {
            ChatBlock.sendMessage(player, lang("you.must.hold.a.banner", player));
            return;
        }

        clan.setBanner(hand);
        plugin.getStorageManager().updateClan(clan);
        ChatBlock.sendMessage(player, lang("you.changed.clan.banner", player));
    }
}
