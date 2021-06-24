---
description: null
---

# Üye Ücreti

## Nasıl Çalışır?

Eğer sunucu ve klan tarafından etkinleştirilirse, lider olmayanlardan saat 1'de \(lider tarafından belirlenen\) bir ücret alınır. Ödemek için yeterli parası olmayan bir üye otomatik olarak klandan atılır. Toplanan ücret klan bankasına eklenir.

## Yapılandırma

* `member-fee-enabled` - Özelliği etkinleştirir veya devre dışı bırakır.
* `max-member-fee` - Ücreti sınırlar, böylece liderler üyeleri kötüye kullanamaz.
* `purchase-member-fee-set` - Bir lider ücret değerini her değiştirdiğinde aşağıdaki değer kadar ücretlendirmek için bunu etkinleştirin.
* `member-fee-set-price` - Ücret değerindeki değişiklikler için ücretlendirilecek değer

#### Örnek

```yaml
economy:
    member-fee-enabled: false
    max-member-fee: 200.0
    purchase-member-fee-set: false
    member-fee-set-price: 1000.0
```

## İzinler

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.leader.fee` | Kullanıcının ücreti değiştirmesine ve değerini ayarlamasına izin verir |
| `simpleclans.member.fee-check` | Üyenin ücretin ne kadar olduğunu ve etkin olup olmadığını kontrol etmesine izin verir |

## Komutlar

| İzin | Açıklama | İzin |
| :--- | :--- | :--- |
| `/clan toggle fee` | Ücreti etkinleştirir/devre dışı bırakır | `simpleclans.leader.fee` |
| `/clan fee set [amount]` | Ücret değerini ayarlar | `simpleclans.leader.fee` |
| `/clan fee check` | Ücret durumunu kontrol eder | `simpleclans.member.fee-check` |

