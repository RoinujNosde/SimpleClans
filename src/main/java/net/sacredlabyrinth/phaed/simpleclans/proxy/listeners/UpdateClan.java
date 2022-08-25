package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ObjectUtils;

import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public class UpdateClan extends MessageListener {

    public UpdateClan(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        Clan bungeeClan = getGson().fromJson(data.readUTF(), Clan.class);
        Clan clan = getClanManager().getClan(bungeeClan.getTag());
        if (clan == null) {
            getClanManager().importClan(bungeeClan);
            debug(String.format("Inserted clan %s", bungeeClan.getTag()));
            return;
        }
        try {
            ObjectUtils.updateFields(bungeeClan, clan);
        } catch (IllegalAccessException e) {
            bungee.getPlugin().getLogger().log(Level.SEVERE, String.format("An error happened while update the clan %s",
                    clan.getTag()), e);
        }
        debug(String.format("Updated clan %s", clan.getTag()));
    }

}
