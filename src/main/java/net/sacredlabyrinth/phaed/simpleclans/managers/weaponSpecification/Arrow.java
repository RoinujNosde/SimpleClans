package net.sacredlabyrinth.phaed.simpleclans.managers.weaponSpecification;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Arrow extends AbstractWeaponSpecification{
    public Arrow() {
    }

    @NotNull
    public String weaponSpecification(String headColor, String out, Player player, int count) {
        out += ChatColor.WHITE + SimpleClans.lang("weapon.A", player) + headColor + count;
        return out;
    }
}