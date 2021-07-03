package net.sacredlabyrinth.phaed.simpleclans.commands.conditions;

import co.aikar.commands.BukkitCommandIssuer;
import co.aikar.commands.ConditionContext;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.CLAN_MIN_SIZE_TO_SET_RIVAL;
import static org.bukkit.ChatColor.RED;

@SuppressWarnings("unused")
public class MinimumToRivalCondition extends AbstractCommandCondition {
    public MinimumToRivalCondition(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public void validateCondition(ConditionContext<BukkitCommandIssuer> context) throws InvalidCommandArgument {
        BukkitCommandIssuer issuer = context.getIssuer();
        Clan clan = Conditions.assertClanMember(clanManager, issuer);
        if (clan.getSize() < settingsManager.getInt(CLAN_MIN_SIZE_TO_SET_RIVAL)) {
            throw new ConditionFailedException(RED + lang("min.players.rivalries", issuer,
                    settingsManager.get(CLAN_MIN_SIZE_TO_SET_RIVAL)));
        }
    }

    @Override
    public @NotNull String getId() {
        return "minimum_to_rival";
    }
}
