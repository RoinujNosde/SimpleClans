---
description: null
---

# Komutlar

> Gerekli değişkenler`(argument)` ile işaretlenmiştir.
>
> İsteğe bağlı bağımsız değişkenler `[argument]` ile işaretlenmiştir.

## Varsayılan Komutlar

| Komut | Açıklama | Sadece Doğrulananlar |  |  |  |  |  |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `/clan` | GUI'yi açar veya yardımı gösterir | Hayır |  |  |  |  |  |
| `/clan help` | Eklenti komutlarını gösterir | Hayır |  |  |  |  |  |
| `/clan create [tag] [name]` | Yeni klan oluşturur | Hayır |  |  |  |  |  |
| `/accept` | İsteği onaylar | Hayır |  |  |  |  |  |
| `/deny` | İsteği reddeder | Hayır |  |  |  |  |  |
| `/more` | Daha fazla bilgi gösterir | Hayır |  |  |  |  |  |
| `/clan leaderboard` | Skor tablosunu gösterir | Hayır |  |  |  |  |  |
| \`/clan list \[name | boyut | kdr | kurulmuş | aktif\] \[artan | azalan\]\` | Tüm klanları listeler | Hayır |
| `/clan rivalries` | Tüm klan rekabetlerini gösterir | Hayır |  |  |  |  |  |
| `/clan alliances` | Tüm klan ittifaklarını gösterir | Hayır |  |  |  |  |  |
| `/clan lookup [player]` | Sizin veya başka bir oyuncunun bilgilerini arar | Hayır |  |  |  |  |  |
| `/clan profile [tag]` | Başka bir klanın profilini gösterir | Evet\* |  |  |  |  |  |
| `/clan roster [tag]` | Başka bir klanın kadrosunu gösterir | Evet\* |  |  |  |  |  |
| `/clan ff (otomatik izin ver)` |  | Kişisel dost ateşini açar/kapatır | Hayır |  |  |  |  |
| `/clan resetkdr` | KD'nizi sıfırlar | Hayır |  |  |  |  |  |

\\* Hedef Klan

## Üye Komutları

### Genel Komutlar

| Komut | Açıklama | Sadece Doğrulananlar |
| :--- | :--- | :--- |
| `/clan kills [player]` | Sizin veya başka bir oyuncunun öldürme sayılarını gösterir | Evet |
| `/clan toggle` | Kişisel ayarları değiştirir | Evet |
| `/clan mostkilled` | Sunucu genelinde en çok öldürülen sayıları gösterir | Evet |
| `/clan resign` | Klandan ayrıl | Hayır |
| `/clan fee check` | Ücretin etkin olup olmadığını ve ne kadar olduğunu kontrol eder | Evet |
| `/clan vitals` | Klanınızın hayati özelliklerini gösterir | Evet |
| `/clan stats` | Klanınızın istatistiklerini gösterir | Evet |
| `/clan profile` | Klanınızın profilini gösterir | Evet |
| `/clan roster` | Klanınızın kadrosunu gösterir | Evet |
| `/clan coords` | Klanınızdakilerin kordinatlarını gösterir | Evet |

### Sohbet Komutları

| Komut | Açıklama | Sadece Doğrulananlar |  |  |
| :--- | :--- | :--- | :--- | :--- |
| `/. (mesaj)` | Sends a message to your clan's chat | Hayır |  |  |
| \`/. \[join, leave, mute\] | Klanınızın sohbetine katılır/çıkar/sessizleştirir | Hayır |  |  |
| `/ally (mesaj)` | Müttefik sohbetine bir mesaj gönderir | Hayır |  |  |
| \`/ally \[join, leave, mute\] | Müttefik sohbetine katılır/çıkar/sessizleştirir | Hayır |  |  |

## Lider Komutları

| Komut | Açıklama | Sadece Doğrulananlar |  |
| :--- | :--- | :--- | :--- |
| `/clan description (açıklama)` | Klanın açıklamasını değiştirir | Evet |  |
| `/clan invite (oyuncu)` | Bir oyuncu davet eder | Hayır |  |
| `/clan kick (oyuncu)` | Klandan bir oyuncuyu tekmeler | Hayır |  |
| `/clan trust (oyuncu)` | Bir üyeyi güvenilir olarak ayarlar | Hayır |  |
| `/clan untrust (oyuncu)` | Bir üyeyi güvenilmeyen olarak ayarlar | Hayır |  |
| `/clan promote (üye)` | Bir üyeyi liderliğe terfi ettirir | Hayır |  |
| `/clan demote (lider)` | Bir lideri üyeliğe indirger | Hayır |  |
| `/clan setbanner` | Klanın bayrağını ayarlar | Evet |  |
| `/clan modtag (etiket)` | Klanınızın etiketini değiştirir \(yalnızca renkler ve büyük harf\) |  |  |
| `/clan clanff (izin ver, engelle]` | Klanın dost ateşini açar/kapatır | Hayır |  |
| `/clan war (başlat, bitir) (etiket)` | Savaş başlatır veya bitirir | Evet |  |
| `/clan rival (ekle, kaldır) (etiket)` | Rakip ekler veya kaldırırEvet |  |  |
| `/clan ally (ekle, kaldır) (etiket)` | Bir müttefik ekler veya kaldırır | Evet |  |
| `/clan verify` | Klanınızı doğrular | Hayır |  |
| `/clan disband` | Klanınızı dağıtır | Hayır |  |
| `/clan fee set (miktar)` | Klanın üye ücretini belirler | Hayır |  |
| `/clan regroup me` | Klan üyelerinizi konumunuza göre yeniden gruplandırır | Evet |  |
| `/clan regroup home` | Klan üyelerinizi klanınızın evinde yeniden gruplandırır | Evet |  |
| `/clan home` | Sizi klanınızın evine ışınlanır | Evet |  |
| `/clan home clear` | Klanınızın evini temizler | Evet |  |
| `/clan home set` | Klanınızın evini ayarlar | Evet |  |
| `/clan rank create` | Bir rütbe oluşturur | Evet |  |
| `/clan rank setdisplayname (rütbe) (görünecek isim)` | Rütbenin görünen adını ayarlar \(renkler ve birden çok kelime içerebilir\) | Evet |  |
| `/clan rank assign (oyuncu) (rütbe)` | Bir kullanıcıyı bir rütbeye atar | Evet |  |
| `/clan rank unassign (oyuncu)` | Bir kullanıcının bir rütbeden atamasını kaldırır | Evet |  |
| `/clan rank delete (rütbe)` | Rütbeyi siler | Evet |  |
| `/clan rank list` | Klanın rütbelerini listeler | Evet |  |
| `/clan rank permissions` | Rütbeler için mevcut izinleri listeler | Evet |  |
| `/clan rank permissions (rütbe)` | Rütbenin izinlerini listeler | Evet |  |
| `/clan rank permissions add (rütbe) (izin)` | Rütbeye izin ekler | Evet |  |
| `/clan rank permissions remove (rütbe) (izin)` | Rütbeden bir izni kaldırır | Evet |  |

## Moderatör Komutları

| Komut | Açıklama |  |
| :--- | :--- | :--- |
| `/clan place (oyuncu) (yeni klan)` | Bir oyuncuyu bir klana yerleştirir |  |
| `/clan home set (etiket)` | Bir klanın evini ayarlar |  |
| `/clan home tp (etiket)` | Bir klanın evine ışınlanır |  |
| `/clan ban (oyuncu)` | Bir oyuncuyu klan komutlarından yasaklar |  |
| `/clan unban (oyuncu)` | Bir oyuncunun klan komutlarından banını kaldırır |  |
| `/clan globalff (otomatik izin ver)` | Küresel dost ateşi durumunu değiştirir |  |
| `/clan verify (etiket)` | Bir klanı doğrular |  |
| `/clan disband (etiket)` | Bir klanı dağıtır |  |

## Yönetici Komutları

| Komut | Açıklama |
| :--- | :--- |
| `/clan reload` | Eklentiyi ve yapılandırmasını yeniden yükler \(bazı özelliklerin güncellenmesi için sunucunun yeniden başlatılması gerekebilir\) |
| `/clan purge` | Bir oyuncunun verilerini temizler |
| `/clan resetkdr everyone` | Herkesin KD'sini sıfırlar |
| `/clan resetkdr (oyuncu)` | Bir oyuncunun KD'sini sıfırlar |
| `/clan admin demote (player)` | Herhangi bir klandan bir lideri düşürür |
| `/clan admin promote (oyuncu)` | Herhangi bir klandan bir üyeyi terfi ettirir |

