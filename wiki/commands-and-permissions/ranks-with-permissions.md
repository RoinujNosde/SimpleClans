---
description: null
---

# İzinli Rütbeler

## Nasıl Çalışır?

Liderler, rütbeler oluşturma ve onlara klan içinde çeşitli eylemler gerçekleştirme izni verme yeteneğine sahiptir. `/clan trust` farklı olarak, izinlere sahip rütbeler, liderlerin kimin ne yapabileceğini daha iyi kontrol etmelerini sağlar. Ör.: Bir rütbe davetleri işleyebilir \(`davet etme` izni\), bir diğeri moderasyonla ilgilenir \(`atma` izni\), vb.

## Önerilen Rütbeler

| Rütbe | Açıklama | İzin |
| :--- | :--- | :---: |
| Yardımcı Lider | Klanın bakımına yardımcı olur | `all` |
| **Hazinedar** | Klanın banka hesabını yönetir | `bank.balance`, `bank.deposit`, `bank.withdraw` |
| Büyükelçi | Klanın diğer klanlarla ilişkilerini yönetir | `ally.chat`, `ally.add`, `ally.remove`, `rival.add`, `rival.remove`, `war.end`, `war.start` |
| **Komutan** | Klan verilerini inceler | `stats`, `kills`, `mostkilled`, `rank.list` |

## Rütbe Komutları

| Komut | Açıklama |
| :--- | :--- |
| `/clan rank create` | Bu adla bir rütbe oluşturur |
| `/clan rank setdisplayname [rütbe] [görünen ad]` | Rütbenin görünen adını belirler \(birden fazla kelime ve renkli olabilir\) |
| `/clan rank assign (oyuncu) (rütbe)` | Bir kullanıcıyı bir rütbeye atar |
| `/clan rank unassign (oyuncu)` | Bir kullanıcının bir rütbeden atamasını kaldırır |
| `/clan rank delete (rütbe)` | Rütbeyi siler |
| `/clan rank list` | Klanın rütbelerini listeler |
| `/clan rank permissions` | Rütbeler için mevcut izinleri listeler |
| `/clan rank permissions (rütbe)` | Rütbenin izinlerini listeler |
| `/clan rank permissions add (rütbe) (izin)` | Rütbeye izin ekler |
| `/clan rank permissions remove (rütbe) (izin)` | Rütbeden bir izni kaldırır |

## Available permissions for ranks

A player can view those permissions in-game using `/clan rank permissions`

| Rank Permission | Description |
| :--- | :--- |
| `ally.add` | can add an ally |
| `ally.remove` | can remove an ally |
| `ally.chat` | can use ally chat |
| `bank.balance` | can view the bank balance |
| `bank.deposit` | can deposit money |
| `bank.withdraw` | can withdraw money |
| `bb.add` | can add a message to bb |
| `bb.clear` | can clear the bb |
| `coords` | can view the clan's coords |
| `fee.enable` | can enable the member fee |
| `fee.set` | can change the fee value |
| `home.regroup` | can regroup the clan |
| `home.set` | can set the clan home |
| `home.tp` | can tp to the clan home |
| `invite` | can invite someone to the clan |
| `kick` | can kick someone from the clan |
| `modtag` | can modify the clan tag |
| `rank.displayname` | can modify a rank's display name |
| `rank.list` | can list the ranks |
| `rival.add` | can add a rival |
| `rival.remove` | can remove a rival |
| `war.end` | can end a war |
| `war.start` | can start a war |
| `vitals` | can view the clan's vitals |
| `stats` | can view the clan's stats |
| `kills` | can view his or other's kills |
| `mostkilled` | can view the mostkilled |
| `description` | can change the clan's description |

## Permissions to use the rank commands

| Permission | Description |
| :--- | :--- |
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

