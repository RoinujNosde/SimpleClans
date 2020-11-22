package net.sacredlabyrinth.phaed.simpleclans.managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.sacredlabyrinth.phaed.simpleclans.Clan;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import net.sacredlabyrinth.phaed.simpleclans.utils.VanishUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
	
	/**
	 * The {@link Pattern} that identifies the top {@link Clan}s placeholder
	 * 
	 * @since 1.12.3
	 */
	private static final Pattern TOP_CLANS_PATTERN = Pattern.compile("^topclans_\\d+_clan_");
	
	/**
	 * The {@link List} containing all the placeholders of the {@link SimpleClans} {@link Plugin}
	 * 
	 * @since 1.12.3
	 */
	private static final List<String> PLACEHOLDERS_LIST = new ArrayList<>();

	static {
		PLACEHOLDERS_LIST.add("%simpleclans_neutral_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_rival_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_civilian_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_total_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_weighted_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_deaths%");
		PLACEHOLDERS_LIST.add("%simpleclans_kdr%");
		PLACEHOLDERS_LIST.add("%simpleclans_in_clan%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_leader%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_trusted%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_member%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_bb_enabled%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_usechatshortcut%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_allychat%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_clanchat%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_globalchat%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_cape_enabled%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_tag_enabled%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_friendlyfire_on%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_muted%");
		PLACEHOLDERS_LIST.add("%simpleclans_is_mutedally%");
		PLACEHOLDERS_LIST.add("%simpleclans_join_date%");
		PLACEHOLDERS_LIST.add("%simpleclans_inactive_days%");
		PLACEHOLDERS_LIST.add("%simpleclans_lastseen%");
		PLACEHOLDERS_LIST.add("%simpleclans_lastseendays%");
		PLACEHOLDERS_LIST.add("%simpleclans_tag%");
		PLACEHOLDERS_LIST.add("%simpleclans_tag_label%");
		PLACEHOLDERS_LIST.add("%simpleclans_rank%");
		PLACEHOLDERS_LIST.add("%simpleclans_rank_displayname%");
		PLACEHOLDERS_LIST.add("%simpleclans_clanchat_player_color%");
		PLACEHOLDERS_LIST.add("%simpleclans_allychat_player_color%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_neutral%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_civilian%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_rival%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_deaths%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_total_kdr%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_average_wk%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_leader_size%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_balance%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_allow_withdraw%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_allow_deposit%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_size%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_name%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_color_tag%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_tag%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_founded%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_friendly_fire%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_is_unrivable%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_is_anyonline%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_is_verified%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_capeurl%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_inactivedays%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_onlinemembers_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_allies_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_rivals_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_clan_topclans_position%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_neutral%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_civilian%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_rival%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_kills%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_deaths%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_total_kdr%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_average_wk%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_leader_size%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_balance%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_allow_withdraw%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_allow_deposit%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_size%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_name%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_color_tag%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_tag%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_founded%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_friendly_fire%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_is_unrivable%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_is_anyonline%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_is_verified%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_capeurl%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_inactivedays%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_onlinemembers_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_allies_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_rivals_count%");
		PLACEHOLDERS_LIST.add("%simpleclans_topclans_<Number>_clan_topclans_position%");
	}
	
	/**
	 * The {@link SimpleClans} {@link Plugin} instance
	 * 
	 * @since 2.10.1
	 */
	private final SimpleClans simpleClans;
	
	/**
	 * The {@link PlaceholdersManager} constructor
	 * 
	 * @param simpleClans The {@link SimpleClans} {@link Plugin}
	 * 
	 * @since 2.10.1
	 */
	public PlaceholdersManager(@NotNull SimpleClans simpleClans) {
		this.simpleClans = simpleClans;
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
	public String getPlaceholderValue(@Nullable ClanPlayer player, @NotNull String identifier) {
		Clan clan = null;
		
		if (TOP_CLANS_PATTERN.matcher(identifier).find()) {
			// getting the position
			int position = Integer.parseInt(identifier.substring(9, identifier.indexOf('_', 9)));
			
			List<Clan> clans = simpleClans.getClanManager().getClans();
			
			// validating position
			if (position < 1 || position > clans.size()) {
				return "";
			}
			simpleClans.getClanManager().sortClansByKDR(clans);
			
			clan = clans.get(position - 1);
			identifier = identifier.substring(identifier.indexOf('_', 9) + 1);
		}
		if (clan == null) {
			if (player == null) {
				return "";
			}
			clan = player.getClan();
			
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
					return String.valueOf(player.getNeutralKills() + player.getRivalKills()
							+ player.getCivilianKills());
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
					return (!player.isLeader() && player.isTrusted()) ? booleanTrue()
							: booleanFalse();
				}
				case "is_member": {
					return (!player.isTrusted() && !player.isLeader() && clan != null)
							? booleanTrue()
							: booleanFalse();
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
					if (player.isLeader()) {
						return simpleClans.getSettingsManager().getClanChatLeaderColor();
					}
					if (player.isTrusted()) {
						return simpleClans.getSettingsManager().getClanChatTrustedColor();
					}
					if (clan != null) {
						return simpleClans.getSettingsManager().getClanChatMemberColor();
					}
					return "";
				}
				case "allychat_player_color": {
					if (player.isLeader()) {
						return simpleClans.getSettingsManager().getAllyChatLeaderColor();
					}
					if (player.isTrusted()) {
						return simpleClans.getSettingsManager().getAllyChatTrustedColor();
					}
					if (clan != null) {
						return simpleClans.getSettingsManager().getAllyChatMemberColor();
					}
					return "";
				}
				default:
					break;
			}
			if (clan == null) {
				return "";
			}
		}
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
				return String.valueOf(
						clan.getTotalRival() + clan.getTotalNeutral() + clan.getTotalCivilian());
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
				return String.valueOf(VanishUtils.getNonVanished(player == null ? null : player.toPlayer(),
						clan).size());
			}
			case "clan_allies_count": {
				return String.valueOf(clan.getAllies().size());
			}
			case "clan_rivals_count": {
				return String.valueOf(clan.getRivals().size());
			}
			case "clan_topclans_position": {
				List<Clan> clans = simpleClans.getClanManager().getClans();
				simpleClans.getClanManager().sortClansByKDR(clans);
				
				return String.valueOf(clans.indexOf(clan) + 1);
			}
			default:
				break;
		}
		return "";
	}
	
	@Override
	public String getIdentifier() {
		return getName().toLowerCase();
	}
	
	@Override
	public @NotNull String getAuthor() {
		List<String> authors = new ArrayList<>(simpleClans.getDescription().getAuthors());
		authors.add("Peng1104");
		return authors.toString();
	}
	
	@Override
	public String getVersion() {
		return simpleClans.getDescription().getVersion();
	}
	
	@Override
	public String onRequest(@Nullable OfflinePlayer player, @NotNull String params) {
		ClanPlayer clanPlayer = null;
		if (player != null) {
			clanPlayer = simpleClans.getClanManager().getAnyClanPlayer(player.getUniqueId());
		}
		return getPlaceholderValue(clanPlayer, params);
	}
	
	@Override
	public String getName() {
		return simpleClans.getName();
	}
	
	@Override
	public @NotNull List<String> getPlaceholders() {
		return PLACEHOLDERS_LIST;
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