package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SCMessage implements Cloneable {

    private final Source source;
    private final ClanPlayer.Channel channel;
    private final ClanPlayer sender;
    private List<ClanPlayer> receivers;
    private String content;

    /**
     * Creates a message with initial parameters
     *
     * @param source    The source of message
     * @param channel   The channel of clan player
     * @param sender    The clan player sender
     * @param content   The content of message
     * @param receivers The clan players, who will receive the content
     */
    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel,
                     @NotNull ClanPlayer sender, String content, @NotNull List<ClanPlayer> receivers) {
        this.source = source;
        this.channel = channel;
        this.sender = sender;
        this.content = content;
        this.receivers = receivers;
    }

    /**
     * Creates a new SCMessage without receivers
     *
     * @see SCMessage#SCMessage(Source, ClanPlayer.Channel, ClanPlayer, String, List) instantiate with initial receievers
     */
    public SCMessage(@NotNull Source source, @NotNull ClanPlayer.Channel channel, @NotNull ClanPlayer sender, String content) {
        this(source, channel, sender, content, new ArrayList<>());
    }

    public ClanPlayer.Channel getChannel() {
        return channel;
    }

    public ClanPlayer getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Source getSource() {
        return source;
    }

    public List<ClanPlayer> getReceivers() {
        return receivers;
    }

    public void setContent(@NotNull String content) {
        this.content = content;
    }

    public void setReceivers(@NotNull List<ClanPlayer> receivers) {
        this.receivers = receivers;
    }

    @Override
    public SCMessage clone() {
        try {
            return (SCMessage) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error(); //never thrown
        }
    }

    /**
     * The place where the message came from.
     * Used by ChatHandlers to know which SCMessages they can handle.
     *
     * @see ChatHandler initiate the handler
     */
    public enum Source {
        SPIGOT, DISCORD, PROXY
    }
}
