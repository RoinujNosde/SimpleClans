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

## Rütbeler için mevcut izinler

Bir oyuncu, `/clan rank permissions` kullanarak oyun içinde bu izinleri görüntüleyebilir.

| Rütbe izni | Açıklama |
| :--- | :--- |
| `ally.add` | bir müttefik ekleyebilir |
| `ally.remove` | bir müttefiki kaldırabilir |
| `ally.chat` | müttefik sohbeti kullanabilir |
| `bank.balance` | banka bakiyesini görüntüleyebilir |
| `bank.deposit` | para yatırabilir |
| `bank.withdraw` | para çekebilir |
| `bb.add` | bülten panosuna mesaj ekleyebilir |
| `bb.clear` | bülten panosunu temizleyebilir |
| `coords` | klanın koordinatlarını görebilir |
| `fee.enable` | üye ücretini etkinleştirebilir |
| `fee.set` | ücret değerini değiştirebilir |
| `home.regroup` | klanı yeniden gruplayabilir |
| `home.set` | klan evini ayarlayabilir |
| `home.tp` | klan evine ışınlanabilir |
| `invite` | birini klana davet edebilir |
| `kick` | birini klandan atabilir |
| `modtag` | klan etiketini değiştirebilir |
| `rank.displayname` | bir rütbenin görünen adını değiştirebilir |
| `rank.list` | rütbeleri listeleyebilir |
| `rival.add` | rakip ekleyebilir |
| `rival.remove` | bir rakibi kaldırabilir |
| `war.end` | bir savaşı bitirebilir |
| `war.start` | bir savaş başlatabilir |
| `vitals` | klanın hayati bilgilerini görüntüleyebilir |
| `stats` | klanın istatistiklerini görüntüleyebilir |
| `kills` | kendisinin veya başkalarının öldürmelerini görebilir |
| `mostkilled` | en çok öldürülenleri görüntüleyebilir |
| `description` | klanın açıklamasını değiştirebilir |

## Rütbe komutlarını kullanma izinleri

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.leader.rank.assign` | Bir kullanıcıya bir rütbe atayabilir |
| `simpleclans.leader.rank.unassign` | Bir rütbeden bir oyuncunun atamasını kaldırabilir |
| `simpleclans.leader.rank.create` | Yeni bir rütbe oluşturabilir |
| `simpleclans.leader.rank.delete` | Bir rütbeyi silebilir |
| `simpleclans.leader.rank.list` | Tüm rütbeleri listeleyebilir |
| `simpleclans.leader.rank.setdisplayname` | Rütbenin görünen adını bir rütbeye ayarlayabilir |
| `simpleclans.leader.rank.permissions.add` | Bir rütbeye izinler ekleyebilir |
| `simpleclans.leader.rank.permissions.available` | Mevcut tüm izinleri listeleyebilir |
| `simpleclans.leader.rank.permissions.list` | Rütbenin izinlerini listeleyebilir |
| `simpleclans.leader.rank.permissions.remove` | Bir rütbeden izinleri kaldırabilir |

