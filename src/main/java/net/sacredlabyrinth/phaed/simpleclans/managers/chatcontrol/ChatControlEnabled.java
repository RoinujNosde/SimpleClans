package net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;

public class ChatControlEnabled extends AbstractChatControl {
    private final ClanManager clanManager;

    public ChatControlEnabled(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void chatcontrol(Player player, ClanPlayer clanPlayer) {
        clanPlayer.setClanChat(true);
        clanManager.plugin.getStorageManager().updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(player, SimpleClans.lang("enabled.clan.chat", player));
    }
}