package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SCMessage {

    private final Source source;
    private final ClanPlayer.Channel channel;
    private final Player sender;
    private String content;

    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel, @NotNull Player sender, String content) {
        this.source = source;
        this.channel = channel;
        this.sender = sender;
        this.content = content;
    }

    public ClanPlayer.Channel getChannel() {
        return channel;
    }

    public Player getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Source getSource() {
        return source;
    }

    public void setContent(String content) {
        this.content = content;
    }

    enum Source {
        MINECRAFT, DISCORD
    }
}
