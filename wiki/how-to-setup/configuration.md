---
description: null
---

# Configuration

[The main configuration file for SimpleClans can be found here.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

## General Settings

* `enable-gui` - Enables the GUI. 
* `disable-messages` - This will disable broadcasts from the plugin such as "Clan created", "Clan disbanded", etc. 
* `tameable-mobs-sharing` -  
* `teleport-blocks` -  
* `teleport-home-on-spawn` - Players will be teleported to their clan's home when they respawn. 
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
* `min-to-verify` - The clan must have this amount of members to get verified \(moderators can bypass this\) 
* `ranking-type` - Valid options: ORDINAL and DENSE
  * `DENSE`: if players have the same KDR, they will have the same rank position. Ex.: 12234
  * `ORDINAL`: Every player will have a different rank position. Ex.: 12345

### Example

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

## Tags Format

### Example

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

## General Commands

* `more` - 
* `ally` - 
* `clan` - 
* `accept` - 
* `deny` - 
* `global` - 
* `clan_chat` - 
* `force-priority` - 

### Example

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

## KDR Grinding Prevention

* `enable-max-kills` - 
* `max-kills-per-victim` - 
* `enable-kill-delay` - 
* `delay-between-kills` - 

### Example

```yaml
kdr-grinding-prevention:
    enable-max-kills: false
    max-kills-per-victim: 10
    enable-kill-delay: false
    delay-between-kills: 5
```

## List Commands

* `size` - 
* `kdr` - 
* `name` - 
* `founded` - 
* `active` - 
* `asc` - 
* `desc` - 
* `default` - 

### Example

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

## Economy

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

### Example

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

## Kill Weights

* `rival` - 
* `civilian` - 
* `neutral` - 
* `deny-same-ip-kills` - 

### Example

```yaml
kill-weights:
    rival: 2.0
    civilian: 0.0
    neutral: 1.0
    deny-same-ip-kills: false
```

## Clan Settings

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

### Example

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

## Tasks

* `collect-upkeep` - 
  * `hour` - 
  * `minute` - 
* `collect-upkeep-warning` - 
  * `hour` - 
  * `minute` - 
* `collect-fee` - 
  * `hour` - 
  * `minute` - 

### Example

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

## Page

* `untrusted-color` - 
* `clan-name-color` - 
* `subtitle-color` - 
* `headings-color` - 
* `trusted-color` - 
* `leader-color` - 
* `separator` - 
* `size` - 

### Example

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

## Clan Chat

* `enable` - 
* `tag-based-clan-chat` - 
* `announcement-color` - 
* `format` - 
* `rank` - 
* `leader-color:` - 
* `trusted-color` - 
* `member-color` - 

### Example

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

## Request

* `message-color` - 
* `ask-frequency-secs` - 
* `max-asks-per-request` - 

### Example

```yaml
request:
    message-color: b
    ask-frequency-secs: 60
    max-asks-per-request: 1440
```

## Bulletin Board

* `color` - 
* `accent-color` - 
* `show-on-login` - 
* `size` - 
* `login-size` - 

### Example

```yaml
bb:
    color: e
    accent-color: '8'
    show-on-login: true
    size: 6
    login-size: 6
```

## Ally Chat

* `enable` - 
* `format` - 
* `rank` - 
* `leader-color` - 
* `trusted-color` - 
* `member-color` - 

### Example

```yaml
allychat:
    enable: true
    format: "&b[Ally Chat] &4<%clan%&4> <%nick-color%%player%&4> %rank%: &b%message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Purge Data

* `inactive-player-data-days` - 
* `inactive-clan-days` - 
* `unverified-clan-days` - 

### Example

```yaml
purge:
    inactive-player-data-days: 30
    inactive-clan-days: 7
    unverified-clan-days: 2
```

## mySQL Settings

* `username` - 
* `host` - 
* `port` - 
* `enable` - 
* `password` - 
* `database` - 

### Example

```yaml
mysql:
    username: ''
    host: localhost
    port: 3306
    enable: false
    password: ''
    database: ''
```

## Permissions

* `auto-group-groupname` - 

### Example

```yaml
permissions:
  auto-group-groupname: false
  YourClanNameHere:
  - test.permission
```

## Performance

* `save-periodically` - The plugin will save its data periodically as opposed to right away, **RECOMMENDED** to set it true. 
* `save-interval` - The interval **in minutes** in which changes are written to the database. 
* `use-threads` - The plugin will not use the main thread to connect with the database if this is true, **RECOMMENDED** to set it true. 
* `use-bungeecord` - 

### Example

```yaml
performance:
  save-periodically: true
  save-interval: 10
  use-threads: true
  use-bungeecord: false
```

## Safe Civilians

* `safe-civilians` - Civilians are safe from PvP, even civilian vs civilian combat is disabled

### Example

```yaml
safe-civilians: false
```

