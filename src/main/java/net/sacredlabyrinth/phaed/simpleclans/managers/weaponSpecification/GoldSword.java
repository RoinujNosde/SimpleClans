package net.sacredlabyrinth.phaed.simpleclans.managers.weaponSpecification;

import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GoldSword extends AbstractWeaponSpecification{
    public GoldSword() {
    }

    @NotNull
    public String weaponSpecification(String headColor, String out, Player player, int count) {
        if(count > 0) {
            String countString = count > 1 ? count + "" : "";
            out += ChatColor.YELLOW + SimpleClans.lang("weapon.S", player) + headColor + countString;
            return out;
        }
        else{
            return out;
        }
    }
}