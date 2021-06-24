---
description: null
---

# Yapılandırma

[SimpleClans için ana yapılandırma dosyası burada bulunabilir.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

## Genel Ayarlar

* `enable-gui` - GUI'yi etkinleştirir.
* `disable-messages` - Bu, "Klan oluşturuldu", "Klan dağıldı" vb. gibi eklentiden gelen yayınları devre dışı bırakacaktır.
* `tameable-mobs-sharing` -  
* `teleport-blocks` -  
* `teleport-home-on-spawn` - Oyuncular yeniden doğduklarında klanlarının evine ışınlanmasını sağlar.
* `drop-items-on-clan-home` -  
* `keep-items-on-clan-home` -  
* `item-list` -  
* `show-debug-info` -  
* `mchat-integration` -  
* `enable-auto-groups` -  
* `chat-compatibility-mode` -  
* `rival-limit-percent` -  
* `use-colorcode-from-prefix-for-name` -  
* `display-chat-tags` -  
* `unrivable-clans` -  
* `show-unverified-on-list` -  
* `blacklisted-worlds` -  
* `banned-players` -  
* `disallowed-tags` -  
* `language` -  
* `language-per-player` -  
* `disallowed-tag-colors` -  
* `server-name` -  
* `new-clan-verification-required` -  
* `allow-regroup-command` -  
* `allow-reset-kdr` -  
* `rejoin-cooldown` -  
* `rejoin-cooldown-enabled` -  
* `min-to-verify` - Klanların doğrulanması için bu sayıda üyeye sahip olması gerekir \(moderatörler bunu atlayabilir\)
* `ranking-type` - Geçerli seçenekler: ORDINAL ve DENSE
  * `DENSE`: Oyuncular aynı KD'ye sahipse, aynı rütbe pozisyonuna sahip olacaklardır. Örn.: 12234
  * `ORDINAL`: Her oyuncunun farklı bir rütbe pozisyonu olacaktır. Örn.: 12345

### Örnek

```yaml
settings:
    enable-gui: true
    disable-messages: false
    tameable-mobs-sharing: false
    teleport-blocks: false
    teleport-home-on-spawn: false
    drop-items-on-clan-home: false
    keep-items-on-clan-home: false
    item-list: []
    show-debug-info: false
    mchat-integration: true
    enable-auto-groups: false
    chat-compatibility-mode: true
    rival-limit-percent: 50
    use-colorcode-from-prefix-for-name: true
    display-chat-tags: true
    unrivable-clans:
    - admin
    - staff
    - mod
    show-unverified-on-list: false
    blacklisted-worlds: []
    banned-players: []
    disallowed-tags:
    - vip
    - clan
    language: en
    language-per-player: false
    disallowed-tag-colors:
    - '4'
    server-name: '&4SimpleClans'
    new-clan-verification-required: true
    allow-regroup-command: true
    allow-reset-kdr: false
    rejoin-cooldown: 60
    rejoin-cooldown-enabled: false
    min-to-verify: 1
    ranking-type: DENSE
```

## Etiketlerin Biçimi

### Örnek

* `default-color` - 
* `max-length` - 
* `bracket` - 
  * `color` - 
  * `leader-color` - 
  * `left` - 
  * `right` - 
* `min-length` - 
* `separator` - 
  * `color` - 
  * `leader-color` - 
  * `char` - 

```yaml
tag:
    default-color: '8'
    max-length: 5
    bracket:
        color: '8'
        leader-color: '4'
        left: ''
        right: ''
    min-length: 2
    separator:
        color: '8'
        leader-color: '4'
        char: ' .'
```

## Genel Komutlar

* `more` - 
* `ally` - 
* `clan` - 
* `accept` - 
* `deny` - 
* `global` - 
* `clan_chat` - 
* `force-priority` - 

### Örnek

```yaml
commands:
    more: more
    ally: ally
    clan: clan
    accept: accept
    deny: deny
    global: global
    clan_chat: "."
    force-priority: true
```

## Haksız KD Kazanmayı Önleme

* `enable-max-kills` - 
* `max-kills-per-victim` - 
* `enable-kill-delay` - 
* `delay-between-kills` - 

### Örnek

```yaml
kdr-grinding-prevention:
    enable-max-kills: false
    max-kills-per-victim: 10
    enable-kill-delay: false
    delay-between-kills: 5
```

## Liste Komutları

* `size` - 
* `kdr` - 
* `name` - 
* `founded` - 
* `active` - 
* `asc` - 
* `desc` - 
* `default` - 

### Örnek

```yaml
list:
    size: size
    kdr: kdr
    name: name
    founded: founded
    active: active
    asc: asc
    desc: desc
    default: kdr
```

## Ekonomi

* `creation-price` - 
* `purchase-clan-create` - 
* `verification-price` - 
* `purchase-clan-verify` - 
* `invite-price` - 
* `purchase-clan-invite` - 
* `home-teleport-price` - 
* `purchase-home-teleport` - 
* `home-teleport-set-price` - 
* `purchase-home-teleport-set` - 
* `home-regroup-price` - 
* `purchase-home-regroup` - 
* `unique-tax-on-regroup` - 
* `issuer-pays-regroup` - 
* `money-per-kill` - 
* `money-per-kill-kdr-multipier` - 
* `purchase-reset-kdr` - 
* `reset-kdr-price` - 
* `purchase-member-fee-set` - 
* `member-fee-set-price` - 
* `member-fee-enabled` - 
* `max-member-fee` - 
* `upkeep` - 
* `upkeep-enabled` - 
* `multiply-upkeep-by-clan-size` - 
* `charge-upkeep-only-if-member-fee-enabled` - 

### Örnek

```yaml
economy:
    creation-price: 100.0
    purchase-clan-create: false
    verification-price: 1000.0
    purchase-clan-verify: false
    invite-price: 20.0
    purchase-clan-invite: false
    home-teleport-price: 5.0
    purchase-home-teleport: false
    home-teleport-set-price: 5.0
    purchase-home-teleport-set: false
    home-regroup-price: 5.0
    purchase-home-regroup: false
    unique-tax-on-regroup: true
    issuer-pays-regroup: true
    money-per-kill: false
    money-per-kill-kdr-multipier: 10
    purchase-reset-kdr: true
    reset-kdr-price: 10000.0
    purchase-member-fee-set: false
    member-fee-set-price: 1000.0
    member-fee-enabled: false
    max-member-fee: 200.0
    upkeep: 200.0
    upkeep-enabled: false
    multiply-upkeep-by-clan-size: false
    charge-upkeep-only-if-member-fee-enabled: true
```

## Öldürme Ağırlıkları

* `rival` - 
* `civilian` - 
* `neutral` - 
* `deny-same-ip-kills` - 

### Örnek

```yaml
kill-weights:
    rival: 2.0
    civilian: 0.0
    neutral: 1.0
    deny-same-ip-kills: false
```

## Klan Ayarları

* `homebase-teleport-wait-secs` - 
* `homebase-can-be-set-only-once` - 
* `min-size-to-set-rival` - 
* `max-length` - 
* `max-description-length` - 
* `min-description-length` - 
* `max-members` - 
* `confirmation-for-promote` - 
* `trust-members-by-default` - 
* `confirmation-for-demote` - 
* `percentage-online-to-demote` - 
* `ff-on-by-default` - 
* `min-length` - 
* `min-size-to-set-ally` - 

### Örnek

```yaml
clan:
    homebase-teleport-wait-secs: 10
    homebase-can-be-set-only-once: true
    min-size-to-set-rival: 3
    max-length: 25
    max-description-length: 120
    min-description-length: 10
    max-members: 25
    confirmation-for-promote: false
    trust-members-by-default: false
    confirmation-for-demote: false
    percentage-online-to-demote: 100.0
    ff-on-by-default: false
    min-length: 2
    min-size-to-set-ally: 3
```

## Görevler

* `collect-upkeep` - 
  * `hour` - 
  * `minute` - 
* `collect-upkeep-warning` - 
  * `hour` - 
  * `minute` - 
* `collect-fee` - 
  * `hour` - 
  * `minute` - 

### Örnek

```yaml
tasks:
    collect-upkeep:
        hour: 1
        minute: 30
    collect-upkeep-warning:
        hour: 12
        minute: 0
    collect-fee:
        hour: 1
        minute: 0
```

## Sayfa

* `untrusted-color` - 
* `clan-name-color` - 
* `subtitle-color` - 
* `headings-color` - 
* `trusted-color` - 
* `leader-color` - 
* `separator` - 
* `size` - 

### Örnek

```yaml
page:
    untrusted-color: '8'
    clan-name-color: b
    subtitle-color: '7'
    headings-color: '8'
    trusted-color: f
    leader-color: '4'
    separator: '-'
    size: 100
```

## Klan Sohbeti

* `enable` - 
* `tag-based-clan-chat` - 
* `announcement-color` - 
* `format` - 
* `rank` - 
* `leader-color:` - 
* `trusted-color` - 
* `member-color` - 

### Örnek

```yaml
clanchat:
    enable: true
    tag-based-clan-chat: false
    announcement-color: e
    format: "&b[%clan%&b] &4<%nick-color%%player%&4> %rank%: &b%message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## İstek

* `message-color` - 
* `ask-frequency-secs` - 
* `max-asks-per-request` - 

### Örnek

```yaml
request:
    message-color: b
    ask-frequency-secs: 60
    max-asks-per-request: 1440
```

## Bülten Panosu

* `color` - 
* `accent-color` - 
* `show-on-login` - 
* `size` - 
* `login-size` - 

### Örnek

```yaml
bb:
    color: e
    accent-color: '8'
    show-on-login: true
    size: 6
    login-size: 6
```

## Müttefik Sohbeti

* `enable` - 
* `format` - 
* `rank` - 
* `leader-color` - 
* `trusted-color` - 
* `member-color` - 

### Örnek

```yaml
allychat:
    enable: true
    format: "&b[Ally Chat] &4<%clan%&4> <%nick-color%%player%&4> %rank%: &b%message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Verileri Temizleme

* `inactive-player-data-days` - 
* `inactive-clan-days` - 
* `unverified-clan-days` - 

### Örnek

```yaml
purge:
    inactive-player-data-days: 30
    inactive-clan-days: 7
    unverified-clan-days: 2
```

## mySQL Ayarları

* `username` - 
* `host` - 
* `port` - 
* `enable` - 
* `password` - 
* `database` - 

### Örnek

```yaml
mysql:
    username: ''
    host: localhost
    port: 3306
    enable: false
    password: ''
    database: ''
```

## İzinler

* `auto-group-groupname` - 

### Örnek

```yaml
permissions:
  auto-group-groupname: false
  YourClanNameHere:
  - test.permission
```

## Performans

* `save-periodically` - Eklenti, verilerini hemen değil, periyodik olarak kaydeder, doğru olarak ayarlanması **ÖNERİLİR**.
* `save-interval` - Değişikliklerin veritabanına yazıldığı dakika cinsinden aralık.
* `use-threads` - Eklenti, bu doğruysa, veritabanına bağlanmak için ana iş parçacığını kullanmaz, doğru olarak ayarlanması **ÖNERİLİR**.
* `use-bungeecord` - 

### Örnek

```yaml
performance:
  save-periodically: true
  save-interval: 10
  use-threads: true
  use-bungeecord: false
```

## Sivilleri Koru

* `safe-civilians` - Siviller PvP'den güvende olur, sivillere karşı siviller için bile savaşlar devre dışı.

### Örnek

```yaml
safe-civilians: false
```

