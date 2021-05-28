---
description: null
---

# Configuration

[The main configuration file for SimpleClans can be found here.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

## General Settings

<table>
  <thead>
    <tr>
      <th style="text-align:left">Option</th>
      <th style="text-align:left">Description</th>
      <th style="text-align:left">Default</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td style="text-align:left"><code>enable-gui</code>
      </td>
      <td style="text-align:left">Enables the GUI</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>disable-messages</code>
      </td>
      <td style="text-align:left">Disables broadcasts from plugin (&quot;Clan Created&quot;, &quot;Clan
        Disbanded&quot;, etc.)</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>tameable-mobs-sharing</code>
      </td>
      <td style="text-align:left">If true, tameable mobs will be shared with your clan members. It also
        disables any clan damage to them</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>teleport-blocks</code>
      </td>
      <td style="text-align:left">Fancy teleporting (placed glass block below)</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>teleport-home-on-spawn</code>
      </td>
      <td style="text-align:left">Players will be teleported to their clan&apos;s home when they respawn</td>
      <td
      style="text-align:left"><code>false</code>
        </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>drop-items-on-clan-home</code>
      </td>
      <td style="text-align:left">Drops defined items on teleporting to clan home</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>keep-items-on-clan-home</code>
      </td>
      <td style="text-align:left">Keeps defined items on teleporting to clan home</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>item-list</code>
      </td>
      <td style="text-align:left">List of defined items (used with <code>keep-item-on-clan-home</code> and <code>drop-items-on-clan-home</code>)</td>
      <td
      style="text-align:left"><code>[]</code>
        </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>show-debug-info</code>
      </td>
      <td style="text-align:left">Shows debug info on console</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>enable-auto-groups</code>
      </td>
      <td style="text-align:left">Manages group of a clan player by auto.
        <br />(For example, a leader would be added to <code>sc_leader</code> group, trusted
        player to <code>sc_trusted</code>, etc.)</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>chat-compatibility-mode</code>
      </td>
      <td style="text-align:left">Changes method of initiate tags.
        <br />If you have a problem with tags, try to disable it.</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>rival-limit-percent</code>
      </td>
      <td style="text-align:left">
        <p>The percent of possible rivals per clan</p>
        <p>Formula: <code>(rivalsOfClan - 1) *  rivalLimitPercent / 100</code>
        </p>
      </td>
      <td style="text-align:left"><code>50</code>
        <br />
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>use-colorcode-from-prefix-for-name</code>
      </td>
      <td style="text-align:left">Uses the last color code in the end of prefix</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>display-chat-tags</code>
      </td>
      <td style="text-align:left">Shows clan tags in chat</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>unrivable-clans</code>
      </td>
      <td style="text-align:left">The list of clans, which can&apos;t be rivaled</td>
      <td style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>show-unverified-on-list</code>
      </td>
      <td style="text-align:left">
        <p>Shows or not unverified clans on <code>/clan list</code>
        </p>
        <p>(Doesn&apos;t affect on GUI)</p>
      </td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>blacklisted-worlds</code>
      </td>
      <td style="text-align:left">Disables SimpleClans at defined worlds</td>
      <td style="text-align:left"><code>[]</code>
        <br />
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>banned-players</code>
      </td>
      <td style="text-align:left">List of banned players from using plugin</td>
      <td style="text-align:left"><code>[]</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>disallowed-tags</code>
      </td>
      <td style="text-align:left">List of tags, which wouldn&apos;t be used on clan creation</td>
      <td style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>language</code>
      </td>
      <td style="text-align:left">Default language</td>
      <td style="text-align:left"><code>en</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>user-language-selector</code>
      </td>
      <td style="text-align:left">Allows players to change their language</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>disallowed-tag-colors</code>
      </td>
      <td style="text-align:left">The list of tag colors, which wouldn&apos;t be used on clan creation</td>
      <td
      style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>server-name</code>
      </td>
      <td style="text-align:left">The name of your server</td>
      <td style="text-align:left"><code>&amp;4SimpleClans</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>new-clan-verification-required</code>
      </td>
      <td style="text-align:left">Should new clans requires being verified?</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>allow-regroup-command</code>
      </td>
      <td style="text-align:left">Allows players to use regroup command</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>allow-reset-kdr</code>
      </td>
      <td style="text-align:left">Allows players to reset their KDR</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>rejoin-cooldown</code>
      </td>
      <td style="text-align:left">The time in <del>years</del> minutes, when player will can join after resigning
        to the same clan</td>
      <td style="text-align:left"><code>60</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>rejoin-cooldown-enabled</code>
      </td>
      <td style="text-align:left">Should rejoin cooldown be enabled?</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>ranking-type</code>
      </td>
      <td style="text-align:left">
        <p><code>DENSE</code>: if players have the same KDR, they will have the same
          rank position. Ex.: 12234</p>
        <p><code>ORDINAL</code>: Every player will have a different rank position.
          Ex.: 12345</p>
      </td>
      <td style="text-align:left"><code>DENSE</code>
        <br />
      </td>
    </tr>
  </tbody>
</table>

#### Example

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
    ranking-type: DENSE
```

## Tags Format

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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
* `min-to-verify` - The clan must have this amount of members to get verified \(moderators can bypass this\)

#### Example

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
    min-to-verify: 1
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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

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

#### Example

```yaml
performance:
  save-periodically: true
  save-interval: 10
  use-threads: true
  use-bungeecord: false
```

## Safe Civilians

* `safe-civilians` - Civilians are safe from PvP, even civilian vs civilian combat is disabled

#### Example

```yaml
safe-civilians: false
```

