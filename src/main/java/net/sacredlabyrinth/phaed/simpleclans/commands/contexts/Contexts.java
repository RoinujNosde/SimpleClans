package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.MessageKeys;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;

public class Contexts {

    private Contexts() {
    }

    @NotNull
    public static Clan assertClanMember(@NotNull ClanManager clanManager,
                                        @NotNull BukkitCommandIssuer issuer) {
        assertPlayer(issuer);
        Clan clan = clanManager.getClanByPlayerUniqueId(issuer.getUniqueId());
        if (clan == null) {
            throw new InvalidCommandArgument(lang("not.a.member.of.any.clan", issuer), false);
        }
        return clan;
    }

    @NotNull
    public static Player assertPlayer(@NotNull BukkitCommandIssuer issuer) {
        Player player = issuer.getPlayer();
        if (player == null) {
            throw new InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false);
        }
        return player;
    }
}
