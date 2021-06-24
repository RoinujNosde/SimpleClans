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
| `simpleclans.leader.fee` | Kullanıcının ücreti değiştirmesine ve değerini ayarlamasına izin verir |
| `simpleclans.leader.ally` | Klanını diğer klanlarla ittifak yapabilir |
| `simpleclans.leader.create` | Klanlar oluşturabilir |
| `simpleclans.leader.verify` | Klanlarını doğrulayabilir |
| `simpleclans.leader.demote` | Klan liderlerini normal oyunculara indirebilir |
| `simpleclans.leader.disband` | Kendi klanını dağıtabilir |
| `simpleclans.leader.ff` | Klanının dost ateşini açabilir |
| `simpleclans.leader.home-set` | Klan evini ayarlayabilir |
| `simpleclans.leader.regroup.me` | Tüm klanı kendisine ışınlayabilir |
| `simpleclans.leader.regroup.home` | Tüm klanı klan evine ışınlayabilir |
| `simpleclans.leader.invite` | Oyuncuları klanına davet edebilir |
| `simpleclans.leader.kick` | Oyuncuları klanından atabilir |
| `simpleclans.leader.modtag` | Klanının etiketini değiştirebilir |
| `simpleclans.leader.description` | Klanlarının açıklamasını değiştirebilir |
| `simpleclans.leader.coloredtag` | Etiketlerde renk kodlarını kullanabilir |
| `simpleclans.leader.coloredrank` | Sıralama görünen adlarında renk kodlarını kullanabilir |
| `simpleclans.leader.promotable` | Klan liderine terfi ettirilebilir |
| `simpleclans.leader.promote` | Oyuncuları klan liderlerine terfi ettirebilir |
| `simpleclans.leader.rank.assign` | Bir kullanıcıya bir rütbe atayabilir |
| `simpleclans.leader.rank.unassign` | Bir rütbeden bir oyuncunun atamasını kaldırabilir |
| `simpleclans.leader.rank.create` | Yeni bir rütbe oluşturabilir |
| `simpleclans.leader.rank.delete` | Bir rütbeyi silebilir |
| `simpleclans.leader.rank.list` | Tüm rütbeleri listeleyebilir |
| `simpleclans.leader.rank.setdisplayname` | Rütbenin görünen adını ayarlayabilir |
| `simpleclans.leader.rank.permissions.add` | Bir rütbeye izinler ekleyebilir |
| `simpleclans.leader.rank.permissions.available` | Mevcut tüm izinleri listeleyebilir |
| `simpleclans.leader.rank.permissions.list` | Rütbenin izinlerini listeleyebilir |
| `simpleclans.leader.rank.permissions.remove` | Bir rütbeden izinleri kaldırabilir |
| `simpleclans.leader.rival` | Başka bir klanla rekabet başlatabilir |
| `simpleclans.leader.settrust` | Üyeler için güven seviyeleri belirleyebilir |
| `simpleclans.leader.war` | Savaşları başlatabilir |
| `simpleclans.leader.setbanner` | Klanının bayrağını ayarlayabilir |
| `simpleclans.leader.withdraw-toggle:` | Klan bankasını geri çekme arasında geçiş yapabilir |
| `simpleclans.leader.deposit-toggle:` | Klan banka mevduatını değiştirebilir |
| `simpleclans.leader.bb-clear` | Klan bülten panosunu temizleyebilir |

### Moderatör Düğümleri

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.mod.ban` | Tüm eklentiden oyuncuları yasaklayabilir |
| `simpleclans.mod.bypass` | Kısıtlamaları atlayabilir |
| `simpleclans.mod.disband` | Herhangi bir klanı dağıtabilir |
| `simpleclans.mod.globalff` | Küresel dost ateşi korumasını kapatabilir |
| `simpleclans.mod.home` | Diğer klanların evini ayarlayabilir |
| `simpleclans.mod.hometp` | Tüm klan evlerine ışınlanabilir |
| `simpleclans.mod.staffgui` | Yönetici GUI'sini açabilir |
| `simpleclans.mod.place` | Oyuncuları manuel olarak klanlara yerleştirebilir |
| `simpleclans.mod.keep-items` | Eve ışınlanırken eşyaları tutabilir |
| `simpleclans.mod.mostkilled` | Kendisinin ve diğerlerinin en çok öldürülen klanlarını görebilir |
| `simpleclans.mod.nopvpinwar` | Savaşlarda PVP'yi atlayabilir |
| `simpleclans.mod.unban` | Oyuncuların tüm eklentideki yasağını kaldırabilir |
| `simpleclans.mod.verify` | Klanları doğrulayabilir |

### Yönetici Düğümleri

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.admin.resetkdr` | Can reset a player's or everyone's KDR |
| `simpleclans.admin.purge` | Bir oyuncuyu temizleyebilir |
| `simpleclans.admin.demote` | Bir oyuncuyu tekrar üyeliğe indirebilir |
| `simpleclans.admin.promote` | Oyuncuları klan liderlerine terfi ettirebilir |
| `simpleclans.admin.all-seeing-eye` | Tüm klan sohbetlerini görebilir |
| `simpleclans.admin.reload` | Yapılandırmayı yeniden yükleyebilir |

### Diğer Düğümler

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.other.kdr-exempt` | Oyuncunun KD'si öldürme/ölme işleminden etkilenmez \(Bilinen Sorunları kontrol edin\) |
| `simpleclans.vip.resetkdr` | KD'lerini sıfırlayabilir |

