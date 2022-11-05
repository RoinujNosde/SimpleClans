package net.sacredlabyrinth.phaed.simpleclans.proxy.listeners;

import com.google.common.io.ByteArrayDataInput;
import net.sacredlabyrinth.phaed.simpleclans.proxy.BungeeManager;
import net.sacredlabyrinth.phaed.simpleclans.utils.ObjectUtils;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;

import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.debug;

public abstract class Update<T> extends MessageListener {

    public Update(BungeeManager bungee) {
        super(bungee);
    }

    protected abstract Class<T> getType();

    protected abstract @Nullable T getCurrent(T t);

    protected abstract void insert(T t);

    @Override
    public final void accept(ByteArrayDataInput data) {
        T t = getGson().fromJson(data.readUTF(), getType());
        T current = getCurrent(t);
        if (current == null) {
            insert(t);
            debug(String.format("Inserted %s", t));
            return;
        }
        try {
            ObjectUtils.updateFields(t, current);
        } catch (IllegalAccessException e) {
            bungee.getPlugin().getLogger().log(Level.SEVERE, String.format("An error happened while updating %s", t), e);
        }
        debug(String.format("Updated %s", t));
    }
}
