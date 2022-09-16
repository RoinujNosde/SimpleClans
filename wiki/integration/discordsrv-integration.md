---
description: More space for communication
---

# 👾 DiscordSRV

## Enabling

To enable this feature all what you need to do is enable it in `config.yml`and install [DiscordSRV](https://www.spigotmc.org/resources/discordsrv.18494/):

{% tabs %}
{% tab title="config.yml" %}
```yaml
discordchat:
    enable: true
```
{% endtab %}
{% endtabs %}

{% hint style="warning" %}
**Notice**\
****Please, make sure you have the latest version of DiscordSRV (>=1.23.0)
{% endhint %}

## How does it work?

### Automatically

At the beginning of the launch of your server, SimpleClans will create categories for the number of your clans, inside the categories, there will be a separate text channel for each clan:

![](<../../.gitbook/assets/izobrazhenie (6).png>)

{% hint style="warning" %}
**Notice**\
Take **attention** that you will have a different structure:\
`clan1`, `clan2`, `clan3` and `clan4` will be placed in **one category**, until it's reached the limit, then creates a new category.
{% endhint %}

**This will allow players to communicate in channels specially created for them.**

Discord channel will be created/deleted/modified when:

* Clan player creates/deletes a clan
* Clan player is joining/resigning from his clan
* Clan player got a promotion or a demotion
* Player just linked to Discord

### Manually

Since SimpleClans 2.16.2, it's possible to disable auto clan creation and create the discord channel by command.&#x20;

To enable manually this way, you have to disable related config option:

```yaml
discordchat:
    auto-creation: false
```

Our congratulations! Now all leaders with a specified permission (`simpleclans.leader.discord.create`) can create their discord channel.

With that permission they are allowed to use a command to create their clan's discord channel:

```
/clan discord create
```

Furthermore, if you're using an economy plugin, you can specify a price for clan creation:

```yaml
economy:
    purchase-discord-create: true
    discord-creation-price: 1000
```

### General notes

{% hint style="info" %}
**Note 1** \
****Only linked players have an access to their clans in discord, but the messages from un-linked will sent too.
{% endhint %}

{% hint style="info" %}
**Note 2**\
****If you're the server owner and you can't write a message to another chats, ask yourself: \
_is SimpleClans allow to you to send the message to other clan chats?_ \
You could use `/clan mod place <necessary clan>` to join and send the message to that chat.
{% endhint %}

## Discord limits

> Only 50 channels at category as maximum

So when the category will be filled with 50 channels, SimpleClans will create a new category and place a new channel there.

> Maximum 500 channels at discord server

&#x20;That is why, if you have more than 500 clans, then SimpleClans won't catch all of them.\
&#x20;You could use `discordchat.text.clans-limit` at configuration.

## Configuration

### DiscordChat section

| Config value                               | Description                                                                                                  |
| ------------------------------------------ | ------------------------------------------------------------------------------------------------------------ |
|  `discordchat.enable`                      | Enables the DiscordSRV integration                                                                           |
|  `discordchat.discord-format`              | The message format from minecraft to discord                                                                 |
|  `discordchat.format`                      | The message format from discord to minecraft                                                                 |
|  `discordchat.spy-format`                  | <p> The message format from discord to server admins <br>(<code>simpleclans.admin.all-seeing-eye</code>)</p> |
|  `discordchat.rank`                        | The rank format                                                                                              |
| `discordchat.auto-creation`                | Turns off automatic discord channels creation. Use `clan discord create` command for manually creation       |
| `discordchat.min-linked-players-to-create` | The count of minimum linked players to be able to create the discord channel                                 |
|  `discordchat.text.category-format`        | The name of category ("SC – TextChannel")                                                                    |
|  `discordchat.text.category-ids`           | The ids of SC categories. You may add your own category here if you want to                                  |
|  `discordchat.text.whitelist`              | This will allow only specified clans be created                                                              |
|  `discordchat.text.clans-limit`            | Limit of discord channels. Maximum is 500.                                                                   |

### Economy section

| Config value                      | Description                                              |
| --------------------------------- | -------------------------------------------------------- |
| `economy.purchase-discord-create` | Allows leaders to purchase the discord channel creation. |
| `economy.discord-creation-price`  | How much channel purchasing will cost?                   |

## DiscordSRV configuration

&#x20;Some of DiscordSRV configurations won't work on SC channels.\
&#x20;There is the small __ list:

* `DiscordChatChannelDiscordToMinecraft` – Do you really need to disable it in DiscordSRV's config, when you may disable it in SC config?<img src="https://github.githubassets.com/images/icons/emoji/unicode/1f914.png" alt="thinking" data-size="line">
* `DiscordChatChannelRequireLinkedAccount` _–_ Our implementation requires only linked players.
* `DiscordChatChannelBlockBots` – We're all robots, now you know the truth<img src="https://github.githubassets.com/images/icons/emoji/unicode/1f916.png" alt="robot" data-size="line">
* `DiscordChatChannelBlockedIds` – Where is an option to block my IP?
* `DiscordChatChannelBroadcastDiscordMessagesToConsole` – You have spy chat for admins for that.
* `DiscordChatChannelTruncateLength` – Does anyone know what `truncate` is?
* `DiscordChatChannelRolesAllowedToUseColorCodesInChat` – No.

&#x20;At least, `DiscordChatChannelEmojiBehavior` will work.<img src="https://github.githubassets.com/images/icons/emoji/unicode/1f603.png" alt="smiley" data-size="line">
