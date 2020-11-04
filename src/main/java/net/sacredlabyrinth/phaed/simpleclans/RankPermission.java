package net.sacredlabyrinth.phaed.simpleclans;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * 
 * @author RoinujNosde
 *
 */
public enum RankPermission {

	ALLY_ADD("simpleclans.leader.ally", PermissionLevel.LEADER),
	ALLY_CHAT("simpleclans.member.ally", PermissionLevel.TRUSTED),
	ALLY_REMOVE("simpleclans.leader.ally", PermissionLevel.LEADER),
	BANK_BALANCE("simpleclans.member.bank", PermissionLevel.TRUSTED),
	BANK_DEPOSIT("simpleclans.member.bank", PermissionLevel.LEADER),
	BANK_WITHDRAW("simpleclans.member.bank", PermissionLevel.LEADER),
	BB_ADD("simpleclans.member.bb-add", PermissionLevel.TRUSTED),
	BB_CLEAR("simpleclans.leader.bb-clear", PermissionLevel.LEADER),
	COORDS("simpleclans.member.coords", PermissionLevel.TRUSTED),
	FEE_ENABLE("simpleclans.leader.fee", PermissionLevel.LEADER),
	FEE_SET("simpleclans.leader.fee", PermissionLevel.LEADER),
	REGROUP_HOME("simpleclans.leader.regroup.home", PermissionLevel.LEADER),
	REGROUP_ME("simpleclans.leader.regroup.me", PermissionLevel.LEADER),
	HOME_SET("simpleclans.leader.home-set", PermissionLevel.LEADER),
	HOME_TP("simpleclans.member.home", PermissionLevel.TRUSTED),
	INVITE("simpleclans.leader.invite", PermissionLevel.LEADER),
	KICK("simpleclans.leader.kick", PermissionLevel.LEADER),
	MODTAG("simpleclans.leader.modtag", PermissionLevel.LEADER),
	RANK_DISPLAYNAME("simpleclans.leader.rank.setdisplayname", PermissionLevel.LEADER),
	RANK_LIST("simpleclans.leader.rank.list", PermissionLevel.LEADER),
	RIVAL_ADD("simpleclans.leader.rival", PermissionLevel.LEADER),
	RIVAL_REMOVE("simpleclans.leader.rival", PermissionLevel.LEADER),
	WAR_END("simpleclans.leader.war", PermissionLevel.LEADER),
	WAR_START("simpleclans.leader.war", PermissionLevel.LEADER),
	STATS("simpleclans.member.stats", PermissionLevel.TRUSTED),
	VITALS("simpleclans.member.vitals", PermissionLevel.TRUSTED),
	KILLS("simpleclans.member.kills", PermissionLevel.TRUSTED),
	DESCRIPTION("simpleclans.leader.description", PermissionLevel.LEADER),
	MOSTKILLED("simpleclans.mod.mostkilled", PermissionLevel.TRUSTED),
	FRIENDLYFIRE("simpleclans.leader.ff", PermissionLevel.LEADER),
	SETBANNER("simpleclans.leader.setbanner", PermissionLevel.LEADER);

	private final String bukkitPermission;
	private final PermissionLevel permissionLevel;

	RankPermission(String bukkitPermission, PermissionLevel permissionLevel) {
		this.bukkitPermission = bukkitPermission;
		this.permissionLevel = permissionLevel;
	}

	/**
	 *
	 * @return the Bukkit equivalent to this rank permission
	 */
	public String getBukkitPermission() {
		return bukkitPermission;
	}

	/**
	 * 
	 * @return the PermissionLevel
	 *
	 */
	public PermissionLevel getPermissionLevel() {
		return permissionLevel;
	}

	@Override
	public String toString() {
		return super.toString().replace("_", ".").toLowerCase();
	}

	/**
	 * 
	 * @param permission the permission
	 * @return true if this is a valid rank permission
	 */
	@Contract("null -> false")
	public static boolean isValid(@Nullable String permission) {
		for (RankPermission p : values()) {
			if (p.toString().equalsIgnoreCase(permission)) {
				return true;
			}
		}
		return false;
	}
}
