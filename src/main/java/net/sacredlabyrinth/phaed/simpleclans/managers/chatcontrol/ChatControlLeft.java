package net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;

public class ChatControlLeft extends AbstractChatControl  {
    private final ClanManager clanManager;

    public ChatControlLeft(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void chatcontrol(Player player, ClanPlayer clanPlayer) {
        clanPlayer.setChannel(ClanPlayer.Channel.NONE);
        clanManager.plugin.getStorageManager().updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(player, SimpleClans.lang("left.clan.chat", player));
    }
}