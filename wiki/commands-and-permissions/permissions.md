---
description: null
---

# İzinler

## Süper İzin Düğümleri \(Bazıları otomatik olarak eklendi\)

Bu süper izin düğümleri, SimpleClans'ı kurmanın hızlı bir yoludur, bazıları otomatik olarak verilir \(Bkz. Otomatik Eklendi\). Gruplara bireysel izinler ayarlamak istiyorsanız, otomatik olarak eklenen bu süper izin düğümlerini ve kurulum izinlerini birer birer reddetmeniz gerekir.

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.anyone.*` \(Otomatik Eklendi\) | Herkes için izinler |
| `simpleclans.member.*` \(Otomatik Eklendi\) | Klan üyesi olabilecekler için izinler |
| `simpleclans.leader.*` \(Otomatik Eklendi\) | Klan lideri olabilecekler için izinler |
| `simpleclans.mod.*` \(OP'lara Otomatik Eklendi\) | Moderatörler için izinler |
| `simpleclans.admin.*` \(OP'lara Otomatik Eklendi\) | Yöneticiler için izinler |

## Bireysel Düğümler

\(Düğüm gruplarını zaten eklediyseniz \(yukarıda\) hepsi dahil oldukları için bunları eklemenize gerek yoktur. Bunlar, yalnızca birkaçını ayrı ayrı devre dışı bırakmak istemeniz durumunda referans olarak dahil edilmiştir. Düğüm gruplarıyla birlikte bunları yanlış olarak ayarlayın.\)

### Herkes İçin Düğümler

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.anyone.alliances` | Klana göre ittifakları görüntüleyebilir |
| `simpleclans.anyone.leaderboard` | Skor tablosunu görüntüleyebilir |
| `simpleclans.anyone.list` | Klanları listeleyebilir |
| `simpleclans.anyone.lookup` | Bir oyuncunun bilgilerini arayabilir |
| `simpleclans.anyone.profile` | Bir klanın profilini görüntüleyebilir |
| `simpleclans.anyone.rivalries` | Klana göre rekabetleri görüntüleyebilir |
| `simpleclans.anyone.roster` | Bir klanın üye listesini görüntüleyebilir |

### Üye Düğümleri

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.member.abstain` | Çekimser Kalabilir |
| `simpleclans.member.accept` | Kabul edebilir |
| `simpleclans.member.ally` | Müttefik sohbeti kullanabilir |
| `simpleclans.member.chat` | Klan sohbetini kullanabilir |
| `simpleclans.member.bank` | Klan bankasını kullanabilir |
| `simpleclans.member.bb-add` | Klanının bülten panosuna ekleyebilir |
| `simpleclans.member.bb-toggle` | Bülten panosunu açıp kapatabilir |
| `simpleclans.member.bb` | Klanının bülten  panosunu görüntüleyebilir |
| `simpleclans.member.can-join` | Klanlara katılabilir |
| `simpleclans.member.coords` | Klanının koordinatlarını görebilir |
| `simpleclans.member.deny` | Reddedebilir |
| `simpleclans.member.ff` | Kendi dost ateşini değiştirebilir |
| `simpleclans.member.home` | Klan evine ışınlanabilir olabilir |
| `simpleclans.member.kills` | Kendisinin ve diğerlerinin öldürmelerini görebilir |
| `simpleclans.member.lookup` | Kendi oyuncu bilgilerini görüntüleyebilir |
| `simpleclans.member.profile` | Kendi klanının profilini görebilir |
| `simpleclans.member.resign` | Klanından ayrılabilir |
| `simpleclans.member.roster` | Kendi klanının üye listesini görüntüleyebilir |
| `simpleclans.member.stats` | Klan istatistiklerini görüntüleyebilir |
| `simpleclans.member.vitals` | Klanının hayati bilgilerini görebilir |
| `simpleclans.member.toggle.bb` | Bülten Panosunu açıp kapatabilir |
| `simpleclans.member.tag-toggle` | Klan etiketini gizleyebilir/gösterebilir |
| `simpleclans.member.fee-check` | Üyenin ücretin ne kadar olduğunu ve etkin olup olmadığını kontrol etmesini sağlar |
| `simpleclans.member.bypass-fee` | Üye ücretini atlayabilir |

### Lider Düğümleri

| İzin | Açıklama |
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

