package net.sacredlabyrinth.phaed.simpleclans.managers.chatcontrol;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.entity.Player;

public class ChatControlDisabled extends AbstractChatControl {
    private final ClanManager clanManager;

    public ChatControlDisabled(ClanManager clanManager) {
        this.clanManager = clanManager;
    }

    public void chatcontrol(Player player, ClanPlayer clanPlayer) {
        clanPlayer.setClanChat(false);
        clanManager.plugin.getStorageManager().updateClanPlayer(clanPlayer);
        ChatBlock.sendMessage(player, SimpleClans.lang("disabled.clan.chat", player));
    }
}