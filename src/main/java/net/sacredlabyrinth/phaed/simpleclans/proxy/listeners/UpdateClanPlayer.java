package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ObjectUtils;

import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public class UpdateClanPlayer extends MessageListener {

    public UpdateClanPlayer(BungeeManager bungee) {
        super(bungee);
    }

    @Override
    public void accept(ByteArrayDataInput data) {
        ClanPlayer bungeeCp = getGson().fromJson(data.readUTF(), ClanPlayer.class);
        ClanPlayer cp = getClanManager().getAnyClanPlayer(bungeeCp.getUniqueId());
        if (cp == null) {
            getClanManager().importClanPlayer(bungeeCp);
            debug(String.format("Inserted cp %s", bungeeCp.getName()));
            return;
        }
        try {
            ObjectUtils.updateFields(bungeeCp, cp);
        } catch (IllegalAccessException e) {
            bungee.getPlugin().getLogger().log(Level.SEVERE, String.format("Error while updating ClanPlayer %s", cp.getUniqueId()), e);
        }
        debug(String.format("Updated cp %s", cp.getName()));
    }
}
