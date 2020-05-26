package net.sacredlabyrinth.phaed.simpleclans.commands;

import net.sacredlabyrinth.phaed.simpleclans.ChatBlock;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.RankPermission;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import static net.sacredlabyrinth.phaed.simpleclans.SimpleClans.lang;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author phaed
 */
public class ToggleCommand {

    public ToggleCommand() {
    }

    /**
     * Execute the command
     *
     * @param player
     * @param arg
     */
    public void execute(Player player, String[] arg) {
        SimpleClans plugin = SimpleClans.getInstance();

        if (arg.length == 0) {
            return;
        }

        String cmd = arg[0];

        if (cmd.equalsIgnoreCase("cape")) {
            if (plugin.getPermissionsManager().has(player, "simpleclans.member.cape-toggle")) {
                ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

                if (cp != null) {
                    Clan clan = cp.getClan();

                    if (clan.isVerified()) {
                        if (cp.isCapeEnabled()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("capeoff",player));
                            cp.setCapeEnabled(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("capeon",player));
                            cp.setCapeEnabled(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("clan.is.not.verified",player));
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("not.a.member.of.any.clan",player));
                }
            } else {
                ChatBlock.sendMessage(player, ChatColor.RED + lang("insufficient.permissions",player));
            }
        }

        if (cmd.equalsIgnoreCase("bb")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.bb-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions",player);
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isBbEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("bboff",player));
                        cp.setBbEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("bbon",player));
                        cp.setBbEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("tag")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.member.tag-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions",player);
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();

                if (clan.isVerified()) {
                    if (cp.isTagEnabled()) {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("tagoff",player));
                        cp.setTagEnabled(false);
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.AQUA + lang("tagon",player));
                        cp.setTagEnabled(true);
                    }
                    plugin.getStorageManager().updateClanPlayer(cp);
                }
            }
        }

        if (cmd.equalsIgnoreCase("deposit")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.deposit-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions",player);
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);

            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isLeader(player)) {
                    if (clan.isVerified()) {
                        if (clan.isAllowDeposit()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("depositoff",player));
                            clan.setAllowDeposit(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("depositon",player));
                            clan.setAllowDeposit(true);
                        }
                    }
                } else {
                    ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
                }
            }
        }

        if (cmd.equalsIgnoreCase("fee") && plugin.getPermissionsManager().has(player, RankPermission.FEE_ENABLE,true)) {
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                	if (clan.isMemberFeeEnabled()) {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + lang("feeoff",player));
                		clan.setMemberFeeEnabled(false);
                	} else {
                		ChatBlock.sendMessage(player, ChatColor.AQUA + lang("feeon",player));
                		clan.setMemberFeeEnabled(true);
                	}
                	plugin.getStorageManager().updateClan(clan);
                }
            }
        }

        if (cmd.equalsIgnoreCase("withdraw")) {
            if (!plugin.getPermissionsManager().has(player, "simpleclans.leader.withdraw-toggle")) {
                lang(ChatColor.RED + "insufficient.permissions",player);
                return;
            }
            ClanPlayer cp = plugin.getClanManager().getClanPlayer(player);
            if (cp != null) {
                Clan clan = cp.getClan();
                if (clan.isVerified()) {
                    if (clan.isLeader(player)) {
                        if (clan.isAllowWithdraw()) {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("withdrawoff",player));
                            clan.setAllowWithdraw(false);
                        } else {
                            ChatBlock.sendMessage(player, ChatColor.AQUA + lang("withdrawon",player));
                            clan.setAllowWithdraw(true);
                        }
                    } else {
                        ChatBlock.sendMessage(player, ChatColor.RED + lang("no.leader.permissions",player));
                    }
                }
            }
        }
    }
}
