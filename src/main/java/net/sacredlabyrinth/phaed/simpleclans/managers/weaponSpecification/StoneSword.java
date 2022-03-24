package net.sacredlabyrinth.phaed.simpleclans.managers.weaponSpecification;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StoneSword extends AbstractWeaponSpecification{
    public StoneSword() {
    }

    @NotNull
    public String weaponSpecification(String headColor, String out, Player player, int count) {
        String countString = count > 1 ? count + "" : "";
        out += ChatColor.GRAY + SimpleClans.lang("weapon.S", player) + headColor + countString;
        return out;
    }
}