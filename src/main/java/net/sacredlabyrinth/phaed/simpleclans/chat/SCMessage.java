package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SCMessage {

    private final Source source;
    private final ClanPlayer.Channel channel;
    private final @NotNull ClanPlayer sender;
    private Set<ClanPlayer> receivers;
    private String content;

    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel, @NotNull ClanPlayer sender, String content, Set<ClanPlayer> receivers) {
        this.source = source;
        this.channel = channel;
        this.sender = sender;
        this.content = content;
        this.receivers = receivers;
    }

    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel, @NotNull ClanPlayer sender, String content) {
        this(source, channel, sender, content, new HashSet<>());
    }

    public ClanPlayer.Channel getChannel() {
        return channel;
    }

    public @NotNull ClanPlayer getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Source getSource() {
        return source;
    }

    public Set<ClanPlayer> getReceivers() {
        return receivers;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReceivers(Set<ClanPlayer> receivers) {
        this.receivers = receivers;
    }

    public enum Source {
        SPIGOT, DISCORD, BUNGEE
    }
}
