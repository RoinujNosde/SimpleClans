package net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;

public class ChatControlJoined extends AbstractChatControl {
    private final ClanManager clanManager;

    public ChatControlJoined(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void chatcontrol(Player player, ClanPlayer clanPlayer) {
        clanPlayer.setChannel(ClanPlayer.Channel.CLAN);
        clanManager.plugin.getStorageManager().updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(player, SimpleClans.lang("joined.clan.chat", player));
    }
}