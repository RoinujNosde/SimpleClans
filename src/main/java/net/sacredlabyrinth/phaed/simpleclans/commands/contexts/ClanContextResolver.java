package net.sacredlabyrinth.phaed.simpleclans.commands.contexts;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.InvalidCommandArgument;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class ClanContextResolver extends AbstractIssuerOnlyContextResolver<Clan> {
    public ClanContextResolver(@NotNull SimpleClans plugin) {
        super(plugin);
    }

    @Override
    public Clan getContext(BukkitCommandExecutionContext c) throws InvalidCommandArgument {
        return Contexts.assertClanMember(clanManager, c.getIssuer());
    }

    @Override
    public Class<Clan> getType() {
        return Clan.class;
    }
}
