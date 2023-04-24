# 📘 Configuration

The main configuration file for SimpleClans can be found [here.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

## General Settings

| Option                               | Description                                                                                                                                                                                         | Default                       |
| ------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| `enable-gui`                         | Enables the GUI                                                                                                                                                                                     | `true`                        |
| `disable-messages`                   | Disables broadcasts from plugin ("Clan Created", "Clan Disbanded", etc.)                                                                                                                            | `false`                       |
| `tameable-mobs-sharing`              | If true, tameable mobs will be shared with your clan members. It also disables any clan damage to them                                                                                              | `false`                       |
| `teleport-blocks`                    | Fancy teleporting (placed glass block below)                                                                                                                                                        | `false`                       |
| `teleport-home-on-spawn`             | Players will be teleported to their clan's home when they respawn                                                                                                                                   | `false`                       |
| `drop-items-on-clan-home`            | Drops defined items on teleporting to clan home                                                                                                                                                     | `false`                       |
| `keep-items-on-clan-home`            | Keeps defined items on teleporting to clan home                                                                                                                                                     | `false`                       |
| `item-list`                          | List of defined items (used with `keep-item-on-clan-home` and `drop-items-on-clan-home`)                                                                                                            | `[]`                          |
| `show-debug-info`                    | Shows debug info on console                                                                                                                                                                         | `false`                       |
| `enable-auto-groups`                 | <p>Manages group of a clan player by auto.<br>(For example, a leader would be added to <code>sc_leader</code> group, trusted player to <code>sc_trusted</code>, etc.)</p>                           | `false`                       |
| `chat-compatibility-mode`            | <p>Changes method of initiate tags.<br>If you have a problem with tags, try to disable it.</p>                                                                                                      | `true`                        |
| `rival-limit-percent`                | <p>The percent of possible rivals per clan</p><p>Formula: <code>(rivalsOfClan - 1) * rivalLimitPercent / 100</code></p>                                                                             | <p><code>50</code><br></p>    |
| `use-colorcode-from-prefix-for-name` | Uses the last color code in the end of prefix                                                                                                                                                       | `true`                        |
| `display-chat-tags`                  | Shows clan tags in chat                                                                                                                                                                             | `true`                        |
| `unrivable-clans`                    | The list of clans, which can't be rivaled                                                                                                                                                           | See Example below             |
| `show-unverified-on-list`            | <p>Shows or not unverified clans on <code>/clan list</code></p><p>(Doesn't affect on GUI)</p>                                                                                                       | `false`                       |
| `blacklisted-worlds`                 | Disables SimpleClans at defined worlds                                                                                                                                                              | <p><code>[]</code><br></p>    |
| `banned-players`                     | List of banned players from using plugin                                                                                                                                                            | `[]`                          |
| `disallowed-tags`                    | List of tags, which wouldn't be used on clan creation                                                                                                                                               | See Example below             |
| `language`                           | Default language                                                                                                                                                                                    | `en`                          |
| `user-language-selector`             | Allows players to change their language                                                                                                                                                             | `true`                        |
| `disallowed-tag-colors`              | The list of tag colors, which wouldn't be used on clan creation                                                                                                                                     | See Example below             |
| `server-name`                        | The name of your server                                                                                                                                                                             | `&4SimpleClans`               |
| `new-clan-verification-required`     | Should new clans requires being verified?                                                                                                                                                           | `true`                        |
| `allow-regroup-command`              | Allows players to use regroup command                                                                                                                                                               | `true`                        |
| `allow-reset-kdr`                    | Allows players to reset their KDR                                                                                                                                                                   | `true`                        |
| `rejoin-cooldown`                    | The time in minutes, when player will can join after resigning to the same clan                                                                                                                     | `60`                          |
| `rejoin-cooldown-enabled`            | Should rejoin cooldown be enabled?                                                                                                                                                                  | `false`                       |
| `ranking-type`                       | <p><code>DENSE</code>: if players have the same KDR, they will have the same rank position. Ex.: 12234</p><p><code>ORDINAL</code>: Every player will have a different rank position. Ex.: 12345</p> | <p><code>DENSE</code><br></p> |

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
    ranking-type: DENSE
```

## War and protection

| Option                                    | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   | Default           |
| ----------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------- |
| `war-enabled`                             | Enables the war feature on the server                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         | `false`           |
| `land-sharing`                            | Enables the [land sharing](../integration/land-claiming.md) feature on the server                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | `true`            |
| `protection-providers`                    | The list of land claim providers                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              | See Example below |
| `listeners.priority`                      | <p>It's <strong>not recommended</strong> to change it.</p><p>Used to set the priority of the overridden events</p>                                                                                                                                                                                                                                                                                                                                                                                                                                            | `HIGHEST`         |
| `listeners.ignored-list.place`            | <p>The list of items that will be ignored by SimpleClans.<br></p><p><strong>For example</strong>, WorldGuard blocked player' head in the region. If player' head is not on the list, SimpleClans will make WG allow the placement</p>                                                                                                                                                                                                                                                                                                                         | See Example below |
| `set-base-only-in-land`                   | Allows a clan player to set the clan base only on claimed land                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                | `false`           |
| `war-normal-expiration-time`              | <p>The time of war expiration independently<br>(in minutes)</p>                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               | `0`               |
| `war-disconnect-expiration-time`          | The time of war expiration if all members from **one** clan disconnects (in minutes)                                                                                                                                                                                                                                                                                                                                                                                                                                                                          | `0`               |
| `edit-all-lands`                          | Allows a clan player to change the action of all the lands instead of the one on which it stands                                                                                                                                                                                                                                                                                                                                                                                                                                                              | `false`           |
| `war-actions`                             | The list of permitted actions regarding clan lands during the war                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             | See Example below |
| `war-start.request-enabled`               | If true, a war will require the approval from the clan leaders                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                | `true`            |
| `war-start.members-online-max-difference` | <p>If the difference between the online members of two clans is greater than the one set, the war will not start</p><p><br><strong>Example 1:</strong><br>Config value: <em>5</em></p><p>Clan #1 has 10 members online</p><p>Clan #2 has 5 members online</p><p>Max difference between clans = 10 - 5 = 5<br>The war <em>will</em> start.<br><strong>Example 2:</strong><br>Config value: 5</p><p>Clan #1 has 11 members online</p><p>Clan #2 has 5 members online</p><p>Max difference between clans = 11 - 5 = 6</p><p>The war <em>will not</em> start.</p> | `5`               |
| `land-creation.only-leaders`              | Allows only clan leaders to create lands (not compatible with WorldGuard)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     | `false`           |
| `land-creation.only-one-per-clan`         | Allows to have only one land per clan (not compatible with WorldGuard)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        | `false`           |

```yaml
war-and-protection:
  war-enabled: false
  land-sharing: true
  protection-providers:
  - WorldGuardProvider
  - WorldGuard6Provider
  - PlotSquared5Provider
  - PlotSquared3Provider
  - GriefPreventionProvider
  listeners:
    priority: HIGHEST
    ignored-list:
      PLACE:
      - PLAYER_HEAD
  set-base-only-in-land: false
  war-normal-expiration-time: 0
  war-disconnect-expiration-time: 0
  edit-all-lands: false
  war-actions:
    CONTAINER: true
    INTERACT: true
    BREAK: true
    PLACE: true
    DAMAGE: true
    INTERACT_ENTITY: true
  war-start:
    request-enabled: true
    members-online-max-difference: 5
  land-creation:
    only-leaders: false
    only-one-per-clan: false
```

## Tags

* `default-color` - color used when the clan leader didn't set any colors
* `max-length` - maximum length for tags
* `bracket` - brackets are the characters around the tag, ex.: `[ ]`
  * `color` - the bracket color
  * `leader-color` - this color is used when the player is a leader
  * `left` - the left character
  * `right` - the right character
* `min-length` - minimum length for tags
* `separator` - the separator is a character that separates the tag and the player name
  * `color` - the separator color
  * `leader-color` - this color is used when the player is a leader
  * `char` - the separator character

### Example

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

In this section you can edit the base commands of the plugin. Enable `force-priority` if other plugins are interfering with the commands.

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

* `enable-max-kills` - by enabling this, you can set a limit on kills per victim.
* `max-kills-per-victim` - this is the limit of kills per victim. If it's set to 10, for example, killing the same player for the 11th time won't affect the KDR
* `enable-kill-delay` - enables a delay between kills
* `delay-between-kills` - this is the delay in minutes between kills

### Example

```yaml
kdr-grinding-prevention:
    enable-max-kills: false
    max-kills-per-victim: 10
    enable-kill-delay: false
    delay-between-kills: 5
```

## Economy

* `creation-price` - the price to create a clan
* `purchase-clan-create` - players must pay to create a clan
* `verification-price` - the price to verify a clan
* `purchase-clan-verify` - players must pay to verify their clans
* `invite-price` - the price to invite a player to your clan&#x20;
* `purchase-clan-invite` - players must pay to invite
* `home-teleport-price` - the price to teleport to the clan's home
* `purchase-home-teleport` - players must pay to teleport
* `home-teleport-set-price` - the price to set the clan's home
* `purchase-home-teleport-set` - players must pay to set the clan's home
* `home-regroup-price` - the price for regrouping the clan members
* `purchase-home-regroup` - players (or the clan) must pay to regroup
* `unique-tax-on-regroup` - if false, the price is multiplied by the amount of online members of the clan
* `issuer-pays-regroup` - if enabled, the player issuing the command pays for the regroup, otherwise the clan pays it
* `money-per-kill` - enables a prize in money for the killer clan
* `money-per-kill-kdr-multipier` - this is multiplied by the attacker's KDR, the result is the money prize for the clan
* `purchase-reset-kdr` - If true, players will be able to reset their KDR
* `reset-kdr-price` - the price to reset one's KDR
* `purchase-member-fee-set` - players must pay to set the member fee
* `member-fee-set-price` - the price to set the member fee
* `member-fee-enabled` - if clans can charge a daily fee from their members
* `max-member-fee` - the maximum amount clans can set for their member fee
* `upkeep` - the daily price for maintaining a clan (if not paid, the clan is disbanded)
* `upkeep-enabled` - if clans must pay the upkeep
* `multiply-upkeep-by-clan-size` - if the upkeep price should be multiplied by the amount of members
* `charge-upkeep-only-if-member-fee-enabled` - if the upkeep should be charged only for clans that choose to enable the member fee
* `bank-log.enable` - If true, all economy actions will be recorded in .CSV file  (SimpleClans/logs/bank/)

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

Here you can set the weight of every kill type. The weight can be negative too. It's used to calculate the KDR like so: (Kill Count \* Kill Wight) / Death Count = KDR

### Example

```yaml
kill-weights:
    rival: 2.0
    civilian: 0.0
    neutral: 1.0
    ally: -1.0
    deny-same-ip-kills: false
```

## Clan Settings

* `homebase-teleport-wait-secs` - the amount of seconds players must wait before teleporting to their clan' home
* `homebase-can-be-set-only-once` - if the clan's home can be set only once
* `min-size-to-set-rival` - the minimum amount of members a clan needs to add rivals
* `max-length` - the max length of the clan's name
* `max-description-length` - the maximum length of the clan's description
* `min-description-length` - the minimum length of the clan's description
* `max-members` - the maximum amount of members a clan can have
* `confirmation-for-promote` - if other leaders must confirm the promotion of members
* `trust-members-by-default` - if members are set as trusted by default
* `confirmation-for-demote` - if other leaders (except the one being demoted, of course) must confirm the demotion
* `percentage-online-to-demote` - the percentage of online leaders to demote
* `ff-on-by-default` - if the clan's friendly-fire is enabled by default
* `min-length` - the minimum length of the clan's name
* `min-size-to-set-ally` - the minimum amount of members a clan needs to add allies
* `min-to-verify` - The clan must have this amount of members to get verified (moderators can bypass this)

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
    min-to-verify: 1
```

## Tasks

This section allows you to set the time of collection for the two types of fee. The fees are described on the economy section.

The collect-upkeep-warning is sent when the clan doesn't have enough money to pay for its upkeep.

The time is in the 24-hour clock.

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

* `untrusted-color` -&#x20;
* `clan-name-color` -&#x20;
* `subtitle-color` -&#x20;
* `headings-color` -&#x20;
* `trusted-color` -&#x20;
* `leader-color` -&#x20;
* `separator` -&#x20;
* `size` -&#x20;

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

## Discord Chat

You can checkout the configuration [here](../integration/discordsrv-integration.md#configuration).

## Clan Chat

* `enable` - enables the clan chat
* `tag-based-clan-chat` - if true, the command to talk on the clan chat is the clan tag
* `announcement-color` - color used for announcements
* `format` - the chat format
* `spy-format` - the spy chat format
* `rank` - the member's rank format (used on the format)
* `leader-color` - the color for leaders (%nick-color%)
* `trusted-color` - the color for trusted players (%nick-color%)
* `member-color` - the color for non-leaders and non-trusted players (%nick-color%)

### Example

```yaml
clanchat:
    enable: true
    tag-based-clan-chat: false
    announcement-color: e
    format: '&b[%clan%&b] &4<%nick-color%%player%&4> %rank%: &b%message%'
    spy-format: "&8[Spy] [&bC&8] <%clean-tag%&8> <%nick-color%*&8%player%>&8 %rank%&8: %message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Request

Requests are messages sent to players to decide (accept or deny) on something (joining a clan, promoting someone, etc).

* `message-color` - the message color
* `ask-frequency-secs` - the interval in seconds between each message
* `max-asks-per-request` - maximum amount of messages before the request expires

### Example

```yaml
request:
    message-color: b
    ask-frequency-secs: 60
    max-asks-per-request: 1440
```

## Bulletin Board

* `color` - the color
* `accent-color` - the accent color&#x20;
* `show-on-login` - if enabled, the BB is sent to clan members when they login&#x20;
* `size` - the BB size (when using its command)
* `login-size` - the BB size on login&#x20;

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

The Ally Chat config works just like the [Clan Chat](configuration.md#clan-chat) one.

### Example

```yaml
allychat:
    enable: true
    format: "&b[Ally Chat] &4<%clan%&4> <%nick-color%%player%&4> %rank%: &b%message%"
    spy-format: '&8[Spy] [&cA&8] <%clean-tag%&8> <%nick-color%*&8%player%>&8 %rank%&8: %message%'
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Spy Chat

Just a few words how it works.\
\
Imagine, we are in different clans. \
You're in `Fenix` and I'm in `Dragon`, but you're an OP \
(or player with `simpleclans.admin.all-seeing-eye` permission). \
\
When **I** write a message in Dragon's clan chat: `/. Good morning, dragons!` \
\
**You** as an _admin_ (OP) will receive that message as well and it would be formatted by `spy-format`. \
So, you will get something like: `[Spy] [C] RoinujNosde: Good morning, dragons!`

## Purge Data

This feature allows you to purge data of inactive players or clans.

### Example

```yaml
purge:
    inactive-player-data-days: 30
    inactive-clan-days: 7
    unverified-clan-days: 2
```

## MySQL Settings

The plugin supports SQLite and MySQL. By disabling MySQL, SQLite will be used automatically.

Set your credentials below to use MySQL on SimpleClans.

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

In this section, you can give clans specific permissions. In the example below, members of the clan "YourClanNameHere" will receive the permission "test.permission".

* `auto-group-groupname` - if enabled, members of clans will receive the permission "group.TAG" (tag, of course, gets replaced). **Be careful with this setting, if you are using LuckPerms, players could create a clan called "admin" and be automatically added to the corresponding group. If you are using it, disable the creation of such clans (mod, admin, etc).**

### Example

```yaml
permissions:
  auto-group-groupname: false
  YourClanNameHere:
  - test.permission
```

## Performance

* `save-periodically` - The plugin will save its data periodically as opposed to right away, **RECOMMENDED** to set it true.&#x20;
* `save-interval` - The interval **in minutes** in which changes are written to the database.&#x20;
* `use-threads` - The plugin will not use the main thread to connect with the database if this is true, **RECOMMENDED** to set it true.&#x20;
* `use-bungeecord` - Enable it in both spigot plugins, also make sure you have one MySQL connection.

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
