package net.sacredlabyrinth.phaed.simpleclans.chat;

import net.sacredlabyrinth.phaed.simpleclans.utils.ChatUtils;
import org.bukkit.ChatColor;

import java.util.Objects;

public class SpyChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {

    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return false;
    }

    @Override
    public String formatMessage(SCMessage message) {
        String formattedMessage = ChatUtils.stripColors(message.getContent());

        if (formattedMessage.contains(ChatUtils.stripColors(Objects.requireNonNull(message.getSender().getClan()).getColorTag()))) {
            return ChatColor.DARK_GRAY + formattedMessage;
        } else {
            return ChatColor.DARK_GRAY + "[" + message.getSender().getTag() + "] " + formattedMessage;
        }
    }

}
