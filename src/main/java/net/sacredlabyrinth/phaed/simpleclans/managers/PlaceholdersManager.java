package net.sacredlabyrinth.phaed.simpleclans.managers;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.PlaceholderAPI;

import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static me.clip.placeholderapi.PlaceholderAPIPlugin.booleanFalse;
import static me.clip.placeholderapi.PlaceholderAPIPlugin.booleanTrue;

/**
 * {@link Class} to manage and hook {@link SimpleClans} into {@link PlaceholderAPI}
 * 
 * @since 2.10.1
 * 
 * @author Peng1104
 */

public final class PlaceholdersManager extends PlaceholderExpansion {

	private static final String TOP_CLANS_IDENTIFIER = "topclans_";
	
	/**
	 * The {@link SimpleClans} {@link Plugin} instance
	 * 
	 * @since 2.10.1
	 */
	private final SimpleClans plugin;
	
	/**
	 * The {@link PlaceholdersManager} constructor
	 * 
	 * @since 2.10.1
	 */
	public PlaceholdersManager(SimpleClans plugin) {
		this.plugin = plugin;
	}
	
	/**
	 * Gets a value for the requested {@link ClanPlayer} and identifier
	 * 
	 * @param player The {@link ClanPlayer} to request the placeholders value for
	 * @param identifier String that determine what value to return
	 * 
	 * @return value for the requested {@link ClanPlayer} and params
	 * 
	 * @since 2.10.1
	 */
	@NotNull
	public String getPlaceholderValue(@Nullable ClanPlayer player, String identifier) {
		if (identifier.startsWith(TOP_CLANS_IDENTIFIER)) {
			return getTopClansPlaceholderValue(identifier);
		}

		if (player == null) return "";
		
		Clan clan = player.getClan();
		
		switch (identifier) {
			case "neutral_kills": {
				return String.valueOf(player.getNeutralKills());
			}
			case "rival_kills": {
				return String.valueOf(player.getRivalKills());
			}
			case "civilian_kills": {
				return String.valueOf(player.getCivilianKills());
			}
			case "total_kills": {
				return String.valueOf(player.getNeutralKills() + player.getRivalKills() + player.getCivilianKills());
			}
			case "weighted_kills": {
				return String.valueOf(player.getWeightedKills());
			}
			case "deaths": {
				return String.valueOf(player.getDeaths());
			}
			case "kdr": {
				return String.valueOf(player.getKDR());
			}
			case "in_clan": {
				return (clan != null) ? booleanTrue() : booleanFalse();
			}
			case "is_leader": {
				return player.isLeader() ? booleanTrue() : booleanFalse();
			}
			case "is_trusted": {
				return (!player.isLeader() && player.isTrusted()) ? booleanTrue() : booleanFalse();
			}
			case "is_member": {
				return (!player.isTrusted() && !player.isLeader() && clan != null) ? booleanTrue() : booleanFalse();
			}
			case "is_bb_enabled": {
				return player.isBbEnabled() ? booleanTrue() : booleanFalse();
			}
			case "is_usechatshortcut": {
				return player.isUseChatShortcut() ? booleanTrue() : booleanFalse();
			}
			case "is_allychat": {
				return player.isAllyChat() ? booleanTrue() : booleanFalse();
			}
			case "is_clanchat": {
				return player.isClanChat() ? booleanTrue() : booleanFalse();
			}
			case "is_globalchat": {
				return player.isGlobalChat() ? booleanTrue() : booleanFalse();
			}
			case "is_cape_enabled": {
				return player.isCapeEnabled() ? booleanTrue() : booleanFalse();
			}
			case "is_tag_enabled": {
				return player.isTagEnabled() ? booleanTrue() : booleanFalse();
			}
			case "is_friendlyfire_on": {
				return player.isFriendlyFire() ? booleanTrue() : booleanFalse();
			}
			case "is_muted": {
				return player.isMuted() ? booleanTrue() : booleanFalse();
			}
			case "is_mutedally": {
				return player.isMutedAlly() ? booleanTrue() : booleanFalse();
			}
			case "join_date": {
				return player.getJoinDateString();
			}
			case "inactive_days": {
				return String.valueOf(player.getInactiveDays());
			}
			case "lastseen": {
				return player.getLastSeenString();
			}
			case "lastseendays": {
				return player.getLastSeenDaysString();
			}
			case "tag": {
				return player.getTag();
			}
			case "tag_label": {
				return player.getTagLabel();
			}
			case "rank": {
				return player.getRankId();
			}
			case "rank_displayname": {
				return player.getRankDisplayName();
			}
			case "clanchat_player_color": {
				if (player.isLeader()) return plugin.getSettingsManager().getClanChatLeaderColor();
				if (player.isTrusted()) return plugin.getSettingsManager().getClanChatTrustedColor();
				if (clan != null) return plugin.getSettingsManager().getClanChatMemberColor();
				return "";
			}
			case "allychat_player_color": {
				if (player.isLeader()) return plugin.getSettingsManager().getAllyChatLeaderColor();
				if (player.isTrusted()) return plugin.getSettingsManager().getAllyChatTrustedColor();
				if (clan != null) return plugin.getSettingsManager().getAllyChatMemberColor();
				return "";
			}
			default:
				break;
		}
		if (clan == null) return "";
		
		switch (identifier) {
			case "clan_total_neutral": {
				return String.valueOf(clan.getTotalNeutral());
			}
			case "clan_total_civilian": {
				return String.valueOf(clan.getTotalCivilian());
			}
			case "clan_total_rival": {
				return String.valueOf(clan.getTotalRival());
			}
			case "clan_total_kills": {
				return String.valueOf(clan.getTotalRival() + clan.getTotalNeutral() + clan.getTotalCivilian());
			}
			case "clan_total_deaths": {
				return String.valueOf(clan.getTotalDeaths());
			}
			case "clan_total_kdr": {
				return String.valueOf(clan.getTotalKDR());
			}
			case "clan_average_wk": {
				return String.valueOf(clan.getAverageWK());
			}
			case "clan_leader_size": {
				return String.valueOf(clan.getLeaders().size());
			}
			case "clan_balance": {
				return String.valueOf(clan.getBalance());
			}
			case "clan_allow_withdraw": {
				return clan.isAllowWithdraw() ? booleanTrue() : booleanFalse();
			}
			case "clan_allow_deposit": {
				return clan.isAllowDeposit() ? booleanTrue() : booleanFalse();
			}
			case "clan_size": {
				return String.valueOf(clan.getSize());
			}
			case "clan_name": {
				return clan.getName();
			}
			case "clan_color_tag": {
				return clan.getColorTag();
			}
			case "clan_tag": {
				return clan.getTag();
			}
			case "clan_founded": {
				return clan.getFoundedString();
			}
			case "clan_friendly_fire": {
				return clan.isFriendlyFire() ? booleanTrue() : booleanFalse();
			}
			case "clan_is_unrivable": {
				return clan.isUnrivable() ? booleanTrue() : booleanFalse();
			}
			case "clan_is_anyonline": {
				return clan.isAnyOnline() ? booleanTrue() : booleanFalse();
			}
			case "clan_is_verified": {
				return clan.isVerified() ? booleanTrue() : booleanFalse();
			}
			case "clan_capeurl": {
				return clan.getCapeUrl();
			}
			case "clan_inactivedays": {
				return String.valueOf(clan.getInactiveDays());
			}
			case "clan_onlinemembers_count": {
				return String.valueOf(clan.getOnlineMembers().size());
			}
			case "clan_allies_count": {
				return String.valueOf(clan.getAllies().size());
			}
			case "clan_rivals_count": {
				return String.valueOf(clan.getRivals().size());
			}
			default:
				break;
		}
		return "";
	}

	/**
	 *
	 * @param identifier String that determine what value to return
	 * @return value for the requested identifier and params
	 */
	private String getTopClansPlaceholderValue(String identifier) {
		//getting the position
		int position;
		try {
			position = Integer.parseInt(identifier.substring(TOP_CLANS_IDENTIFIER.length(),
					identifier.indexOf('_', TOP_CLANS_IDENTIFIER.length())));
		} catch (NumberFormatException ex) {
			return "";
		}
		List<Clan> clans = plugin.getClanManager().getClans();
		//validating position
		if (position < 1 || position > clans.size()) {
			return "";
		}

		plugin.getClanManager().sortClansByKDR(clans);

		Clan clan = clans.get(position - 1);
		if (identifier.endsWith("_color_tag")) {
			return clan.getColorTag();
		}
		if (identifier.endsWith("_kdr")) {
			return String.valueOf(clan.getTotalKDR());
		}
		if (identifier.endsWith("_total_kills")) {
			int totalKills = clan.getTotalCivilian() + clan.getTotalNeutral() + clan.getTotalRival();
			return String.valueOf(totalKills);
		}
		return "";
	}

	@Override
	public String onRequest(OfflinePlayer player, String params) {
		ClanPlayer clanPlayer = null;
		if (player != null) {
			clanPlayer = plugin.getClanManager().getAnyClanPlayer(player.getUniqueId());
		}
		return getPlaceholderValue(clanPlayer, params);
	}

	@Override
	public String getIdentifier() {
		return "simpleclans";
	}

	@Override
	public String getAuthor() {
		List<String> authors = new ArrayList<>(plugin.getDescription().getAuthors());
		authors.add("Peng1104");
		return authors.toString();
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}
}