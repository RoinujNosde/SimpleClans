package net.sacredlabyrinth.phaed.simpleclans.managers.weaponSpecification;

import com.cryptomorin.xseries.XMaterial;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Arrow extends AbstractWeaponSpecification{
    public Arrow() {
    }
    @NotNull
    public String weaponSpecification(String headColor, String out, Player player, int count) {
        if(count > 0) {
            out += ChatColor.WHITE + SimpleClans.lang("weapon.A", player) + headColor + count;
            return out;
        }
        else{
            return out;
        }
    }
}