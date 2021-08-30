---
description: null
---

# Configuration

The main configuration file for SimpleClans can be found [here.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

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
        <p>Formula: <code>(rivalsOfClan - 1) * rivalLimitPercent / 100</code>
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
      <td style="text-align:left">The time in minutes, when player will can join after resigning to the
        same clan</td>
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
      <td style="text-align:left"><code>war-enabled</code>
      </td>
      <td style="text-align:left">Enables the war feature on the server</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>land-sharing</code>
      </td>
      <td style="text-align:left">Enables the <a href="https://simpleclans.gitbook.io/simpleclans/other/land-claims">land sharing</a> feature
        on the server</td>
      <td style="text-align:left"><code>true</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>protection-providers</code>
      </td>
      <td style="text-align:left">The list of land claim providers</td>
      <td style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>listeners.priority</code>
      </td>
      <td style="text-align:left">
        <p>It&apos;s <b>not recommended</b> to change it.</p>
        <p>Used to set the priority of the overridden events</p>
      </td>
      <td style="text-align:left"><code>HIGHEST</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>listeners.ignored-list.place</code>
      </td>
      <td style="text-align:left">
        <p>The list of items that will be ignored by SimpleClans.
          <br />
        </p>
        <p><b>For example</b>, WorldGuard blocked player&apos; head in the region.
          If player&apos; head is not on the list, SimpleClans will make WG allow
          the placement</p>
      </td>
      <td style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>set-base-only-in-land</code>
      </td>
      <td style="text-align:left">Allows a clan player to set the clan base only on claimed land</td>
      <td
      style="text-align:left"><code>false</code>
        </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>war-normal-expiration-time</code>
      </td>
      <td style="text-align:left">The time of war expiration independently
        <br />(in minutes)</td>
      <td style="text-align:left"><code>0</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>war-disconnect-expiration-time</code>
      </td>
      <td style="text-align:left">The time of war expiration if all members from <b>one</b> clan disconnects
        (in minutes)</td>
      <td style="text-align:left"><code>0</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>edit-all-lands</code>
      </td>
      <td style="text-align:left">Allows a clan player to change the action of all the lands instead of
        the one on which it stands</td>
      <td style="text-align:left"><code>false</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>war-actions</code>
      </td>
      <td style="text-align:left">The list of permitted actions regarding clan lands during the war</td>
      <td
      style="text-align:left">See Example below</td>
    </tr>
    <tr>
      <td style="text-align:left"><code>war-start.request-enabled</code>
      </td>
      <td style="text-align:left">If true, a war will require the approval from the clan leaders</td>
      <td
      style="text-align:left"><code>true</code>
        </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>war-start.members-online-max-difference</code>
      </td>
      <td style="text-align:left">
        <p>If the difference between the online members of two clans is greater than
          the one set, the war will not start</p>
        <p>
          <br /><b>Example 1:<br /></b>Config value: <em>5</em>
        </p>
        <p>Clan #1 has 10 members online</p>
        <p>Clan #2 has 5 members online</p>
        <p>Max difference between clans = 10 - 5 = 5
          <br />The war <em>will</em> start.
          <br /><b>Example 2:</b>
          <br />Config value: 5</p>
        <p>Clan #1 has 11 members online</p>
        <p>Clan #2 has 5 members online</p>
        <p>Max difference between clans = 11 - 5 = 6</p>
        <p>The war <em>will not</em> start.</p>
      </td>
      <td style="text-align:left"><code>5</code>
      </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>land-creation.only-leaders</code>
      </td>
      <td style="text-align:left">Allows only clan leaders to create lands (not compatible with WorldGuard)</td>
      <td
      style="text-align:left"><code>false</code>
        </td>
    </tr>
    <tr>
      <td style="text-align:left"><code>land-creation.only-one-per-clan</code>
      </td>
      <td style="text-align:left">Allows to have only one land per clan (not compatible with WorldGuard)</td>
      <td
      style="text-align:left"><code>false</code>
        </td>
    </tr>
  </tbody>
</table>

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
* `invite-price` - the price to invite a player to your clan 
* `purchase-clan-invite` - players must pay to invite
* `home-teleport-price` - the price to teleport to the clan's home
* `purchase-home-teleport` - players must pay to teleport
* `home-teleport-set-price` - the price to set the clan's home
* `purchase-home-teleport-set` - players must pay to set the clan's home
* `home-regroup-price` - the price for regrouping the clan members
* `purchase-home-regroup` - players \(or the clan\) must pay to regroup
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
* `upkeep` - the daily price for maintaining a clan \(if not paid, the clan is disbanded\)
* `upkeep-enabled` - if clans must pay the upkeep
* `multiply-upkeep-by-clan-size` - if the upkeep price should be multiplied by the amount of members
* `charge-upkeep-only-if-member-fee-enabled` - if the upkeep should be charged only for clans that choose to enable the member fee
* `bank-log.enable` - If true, all economy actions will be recorded in .CSV file  \(SimpleClans/logs/bank/\)

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

Here you can set the weight of every kill type. The weight can be negative too. It's used to calculate the KDR like so: \(Kill Count \* Kill Wight\) / Death Count = KDR

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
* `confirmation-for-demote` - if other leaders \(except the one being demoted, of course\) must confirm the demotion
* `percentage-online-to-demote` - the percentage of online leaders to demote
* `ff-on-by-default` - if the clan's friendly-fire is enabled by default
* `min-length` - the minimum length of the clan's name
* `min-size-to-set-ally` - the minimum amount of members a clan needs to add allies
* `min-to-verify` - The clan must have this amount of members to get verified \(moderators can bypass this\)

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

* `enable` - enables the clan chat
* `tag-based-clan-chat` - if true, the command to talk on the clan chat is the clan tag
* `announcement-color` - color used for announcements
* `format` - the chat format
* `rank` - the member's rank format \(used on the format\)
* `leader-color` - the color for leaders \(%nick-color%\)
* `trusted-color` - the color for trusted players \(%nick-color%\)
* `member-color` - the color for non-leaders and non-trusted players \(%nick-color%\)

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

Requests are messages sent to players to decide \(accept or deny\) on something \(joining a clan, promoting someone, etc\).

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
* `accent-color` - the accent color 
* `show-on-login` - if enabled, the BB is sent to clan members when they login 
* `size` - the BB size \(when using its command\)
* `login-size` - the BB size on login 

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
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Purge Data

This feature allows you to purge data of inactive players or clans.

### Example

```yaml
purge:
    inactive-player-data-days: 30
    inactive-clan-days: 7
    unverified-clan-days: 2
```

## mySQL Settings

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

* `auto-group-groupname` - if enabled, members of clans will receive the permission "group.TAG" \(tag, of course, gets replaced\). **Be careful with this setting, if you are using LuckPerms, players could create a clan called "admin" and be automatically added to the corresponding group. If you are using it, disable the creation of such clans \(mod, admin, etc\).**

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
* `use-bungeecord` - **deprecated setting, BungeeCord is not fully supported**

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

