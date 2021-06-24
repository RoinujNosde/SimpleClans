---
description: null
---

# Klan Bakımı

## Nasıl Çalışır?

Etkinleştirilirse, klanlar klanlarını korumak için her gün bir miktar para ödemek zorunda kalacaklar. Değer, saat `01:30`'da toplanacaktır. Gece `12`'de klanın ödemeye yetecek kadar parası yoksa klanın Bülten Panosuna bir uyarı gönderilecek. Miktar sabitlenebilir veya klan boyutuna göre belirlenebilir.

Örn. klan boyutuna göre ücret:

```text
upkeep base    = 20.0
clan size      = 10
final upkeep   = 200.0
```

## Yapılandırma

* `upkeep` - Temel bakım ücreti.
* `upkeep-enabled` - Özelliği etkinleştirin veya devre dışı bırakın.
* `multiply-upkeep-by-clan-size` - Klan boyutuna göre ücret alın
* `charge-upkeep-only-if-member-fee-enabled` - [Üye ücreti](https://github.com/RoinujNosde/SimpleClans/wiki/Member-Fee) etkinse bakımı etkinleştirin.

#### Örnek

```yaml
economy:
    upkeep: 200.0
    upkeep-enabled: false
    multiply-upkeep-by-clan-size: false
    charge-upkeep-only-if-member-fee-enabled: true
```

