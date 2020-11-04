package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.MessageKeys;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class Conditions {
    private Conditions() {
    }

    @NotNull
    public static Clan assertClanMember(@NotNull ClanManager clanManager,
                                        @NotNull BukkitCommandIssuer issuer) {
        Conditions.assertPlayer(issuer);
        Clan clan = clanManager.getClanByPlayerUniqueId(issuer.getUniqueId());
        if (clan == null) {
            throw new ConditionFailedException(lang("not.a.member.of.any.clan", issuer));
        }
        return clan;
    }

    @NotNull
    public static Player assertPlayer(@NotNull BukkitCommandIssuer issuer) {
        Player player = issuer.getPlayer();
        if (player == null) {
            throw new ConditionFailedException(MessageKeys.NOT_ALLOWED_ON_CONSOLE);
        }
        return player;
    }

}
