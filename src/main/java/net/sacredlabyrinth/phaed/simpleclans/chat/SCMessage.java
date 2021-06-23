package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SCMessage {

    private final Source source;
    private final ClanPlayer.Channel channel;
    private final Player sender;
    private String message;

    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel, @NotNull Player sender, String message) {
        this.source = source;
        this.channel = channel;
        this.sender = sender;
        this.message = message;
    }

    public ClanPlayer.Channel getChannel() {
        return channel;
    }

    public Player getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public Source getSource() {
        return source;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    enum Source {
        MINECRAFT, DISCORD
    }
}
