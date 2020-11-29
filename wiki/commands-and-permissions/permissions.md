---
description: null
---

# Разрешения

## SuperPerms Nodes \(Some auto added\)

These super permission nodes are a quick way to setup SimpleClans, some are automatically given \(See Auto Added\). If you wish to setup individual permissions to groups you will need to deny these auto added super permission nodes and setup permissions one by one after.

| Permission | Description |
| :--- | :--- |
| `simpleclans.anyone.*` \(Auto added\) | Permissions for anyone |
| `simpleclans.member.*` \(Auto added\) | Permissions for those who can be clan members |
| `simpleclans.leader.*` \(Auto added\) | Permissions for those who can be clan leaders |
| `simpleclans.mod.*` \(Auto added to OPS\) | Permissions for moderators |
| `simpleclans.admin.*` \(Auto added to OPS\) | Permissions for admins |

## Individual Nodes

\(You don't need to add these as they are all included if you've already added the node groups \(above\). They are only included here as reference in case you want to toggle a couple of them off individually. Then you can just drop those in, set to false, along with the node groups.\)

### Anyone Nodes

| Permission | Description |
| :--- | :--- |
| `simpleclans.anyone.alliances` | Can view alliances by clan |
| `simpleclans.anyone.leaderboard` | Can view the leaderboard |
| `simpleclans.anyone.list` | Can list simpleclans |
| `simpleclans.anyone.lookup` | Can lookup a player's info |
| `simpleclans.anyone.profile` | Can view a clan's profile |
| `simpleclans.anyone.rivalries` | Can view rivalries by clan |
| `simpleclans.anyone.roster` | Can view a clan's member list |

### Member Nodes

| Permission | Description |
| :--- | :--- |
| `simpleclans.member.abstain` | Can abstain |
| `simpleclans.member.accept` | Can accept |
| `simpleclans.member.ally` | Can use ally chat |
| `simpleclans.member.chat` | Can use clan chat |
| `simpleclans.member.bank` | Can use the clan bank |
| `simpleclans.member.bb-add` | Can add to his clan's bulletin board |
| `simpleclans.member.bb-toggle` | Can toggle the bulletin board on/off |
| `simpleclans.member.bb` | Can view his clan's bulletin board |
| `simpleclans.member.can-join` | Can join clans |
| `simpleclans.member.coords` | Can view his clan's coords |
| `simpleclans.member.deny` | Can deny |
| `simpleclans.member.ff` | Can toggle his own friendly fire |
| `simpleclans.member.home` | Can tp to home base |
| `simpleclans.member.kills` | Can view his and other's kills |
| `simpleclans.member.lookup` | Can view his own player info |
| `simpleclans.member.profile` | Can view his own clan's profile |
| `simpleclans.member.resign` | Can resign from his clan |
| `simpleclans.member.roster` | Can view his own clan's member list |
| `simpleclans.member.stats` | Can view his clan stats |
| `simpleclans.member.vitals` | Can view his clan's vitals |
| `simpleclans.member.toggle.bb` | Can toggle bb on/off |
| `simpleclans.member.tag-toggle` | Can hide/show their clan tag |
| `simpleclans.member.fee-check` | Allows the member to check how much is the fee and if it's enabled |
| `simpleclans.member.bypass-fee` | Can bypass the member fee |

### Leader Nodes

| Permission | Description |
| :--- | :--- |
| `simpleclans.leader.fee` | allows the user to toggle the fee and set its value |
| `simpleclans.leader.ally` | Can ally his clan with other clans |
| `simpleclans.leader.create` | Can create clans |
| `simpleclans.leader.verify` | Can verify their clan |
| `simpleclans.leader.demote` | Can demote clan leaders to normal players |
| `simpleclans.leader.disband` | Can disband his own clan |
| `simpleclans.leader.ff` | Can toggle his clan's friendly fire |
| `simpleclans.leader.home-set` | Can set home base |
| `simpleclans.leader.regroup.me` | Can teleport the entire clan to themself |
| `simpleclans.leader.regroup.home` | Can teleport the entire clan to homebase |
| `simpleclans.leader.invite` | Can invite players into his clan |
| `simpleclans.leader.kick` | Can kick players form his clan |
| `simpleclans.leader.modtag` | Can modify his clan's tag |
| `simpleclans.leader.description` | Can modify their clan's description |
| `simpleclans.leader.coloredtag` | Can use color codes in tags |
| `simpleclans.leader.coloredrank` | Can use color codes in rank display names |
| `simpleclans.leader.promotable` | Can be promoted to clan leader |
| `simpleclans.leader.promote` | Can promote players to clan leaders |
| `simpleclans.leader.rank.assign` | Can assign a rank to a user |
| `simpleclans.leader.rank.unassign` | Can unassign a player from a rank |
| `simpleclans.leader.rank.create` | Can create a new rank |
| `simpleclans.leader.rank.delete` | Can delete a new rank |
| `simpleclans.leader.rank.list` | Can list all the ranks |
| `simpleclans.leader.rank.setdisplayname` | Can set the display name of the rank |
| `simpleclans.leader.rank.permissions.add` | Can add permissions to a rank |
| `simpleclans.leader.rank.permissions.available` | Can list all available permissions |
| `simpleclans.leader.rank.permissions.list` | Can list the rank's permissions |
| `simpleclans.leader.rank.permissions.remove` | Can remove permissions from a rank |
| `simpleclans.leader.rival` | Can start a rivalry with another clan |
| `simpleclans.leader.settrust` | Can set trust levels for members |
| `simpleclans.leader.war` | Can start wars |
| `simpleclans.leader.setbanner` | Can set his clan's banner |
| `simpleclans.leader.withdraw-toggle:` | Can toggle clan bank withdraw |
| `simpleclans.leader.deposit-toggle:` | Can toggle clan bank deposit |
| `simpleclans.leader.bb-clear` | Clan clear their clan's bb |

### Mod Nodes

| Permission | Description |
| :--- | :--- |
| `simpleclans.mod.ban` | Can ban players from the entire plugin |
| `simpleclans.mod.bypass` | Can bypass restrictions |
| `simpleclans.mod.disband` | Can disband any clan |
| `simpleclans.mod.globalff` | Can turn off global friendly fire protection |
| `simpleclans.mod.home` | Can set other clan's home |
| `simpleclans.mod.hometp` | Can teleport to all clans homes |
| `simpleclans.mod.staffgui` | Can open the staff GUI |
| `simpleclans.mod.place` | Can manually place players in clans |
| `simpleclans.mod.keep-items` | Can keep items when teleporting home |
| `simpleclans.mod.mostkilled` | Can view his and other's clans mostkilled |
| `simpleclans.mod.nopvpinwar` | Can bypass PvP in wars |
| `simpleclans.mod.unban` | Can unban players from the entire plugin |
| `simpleclans.mod.verify` | Can verify clans |

### Admin Nodes

| Permission | Description |
| :--- | :--- |
| `simpleclans.admin.resetkdr` | Can reset a player's or everyone's KDR |
| `simpleclans.admin.purge` | Can purge a player |
| `simpleclans.admin.demote` | Can demote a player back to member |
| `simpleclans.admin.promote` | Can promote players to clan leaders |
| `simpleclans.admin.all-seeing-eye` | Can see all clan chats |
| `simpleclans.admin.reload` | Can reload configuration |

### Other Nodes

| Permission | Node |
| :--- | :--- |
| `simpleclans.other.kdr-exempt` | The player's KDR is not affected on killing/dying \(check Known Issues\) |
| `simpleclans.vip.resetkdr` | Can reset their KDR |

