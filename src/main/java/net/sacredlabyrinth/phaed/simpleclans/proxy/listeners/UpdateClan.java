package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import org.jetbrains.annotations.Nullable;

public class UpdateClan extends Update<Clan> {

    public UpdateClan(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    protected Class<Clan> getType() {
        return Clan.class;
    }

    @Override
    protected @Nullable Clan getCurrent(Clan clan) {
        return getClanManager().getClan(clan.getTag());
    }

    @Override
    protected void insert(Clan clan) {
        getClanManager().importClan(clan);
    }

}
