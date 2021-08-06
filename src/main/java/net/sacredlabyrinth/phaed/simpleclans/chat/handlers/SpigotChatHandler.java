package net.sacredlabyrinth.phaed.simpleclans.chat.handlers;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.chat.ChatHandler;
import net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage;
import net.sacredlabyrinth.phaed.simpleclans.events.ChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.DISCORD;
import static net.sacredlabyrinth.phaed.simpleclans.chat.SCMessage.Source.SPIGOT;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField;
import static net.sacredlabyrinth.phaed.simpleclans.managers.SettingsManager.ConfigField.DISCORDCHAT_ENABLE;
import static org.bukkit.Bukkit.getPluginManager;

public class SpigotChatHandler implements ChatHandler {

    @Override
    public void sendMessage(SCMessage message) {
        /*
          TODO: Make it async, change Type to Channel in 3.0
        */
        new BukkitRunnable() {
            @Override
            public void run() {
                ChatEvent event = new ChatEvent(message.getContent(), message.getSender(), message.getReceivers(),
                        ChatEvent.Type.valueOf(message.getChannel().name()));

                getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    return;
                }

                ConfigField configField = ConfigField.valueOf(String.format("%sCHAT_FORMAT",
                        message.getSource() == SPIGOT ? message.getChannel() : message.getSource()));

                String format = settingsManager.getString(configField);
                String formattedMessage = chatManager.parseChatFormat(format, message, event.getPlaceholders());

                plugin.getLogger().info(formattedMessage);

                for (ClanPlayer cp : message.getReceivers()) {
                    ChatBlock.sendMessage(cp, formattedMessage);
                }
            }
        }.runTask(plugin);
    }

    @Override
    public boolean canHandle(SCMessage.Source source) {
        return source == SPIGOT || (source == DISCORD && settingsManager.is(DISCORDCHAT_ENABLE) &&
                getPluginManager().getPlugin("DiscordSRV") != null);
    }
}
