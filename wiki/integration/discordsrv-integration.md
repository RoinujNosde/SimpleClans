---
description: More space for communication
---

# DiscordSRV

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
**Notice**  
Please, make sure you have the latest version of DiscordSRV \(&gt;=1.23.0\)
{% endhint %}

## How does it work?

At the beginning of the launch of your server, SimpleClans will create categories for the number of your clans, inside the categories, there will be a separate text channel for each clan:

![](../.gitbook/assets/izobrazhenie%20%286%29.png)

{% hint style="warning" %}
**Notice**  
Take **attention** that you will have a different structure:  
`clan1`, `clan2`, `clan3` and `clan4` will be placed in **one category**, until it's reached the limit, then creates a new category.
{% endhint %}

**This will allow players to communicate in channels specially created for them.**

Discord channel will be created/deleted/modified when:

* Clan player creates/deletes a clan
* Clan player is joining/resigning from his clan
* Clan player got a promotion or a demotion
* Player just linked to discord

{% hint style="info" %}
**Note 1**   
Only linked players have an access to their clans in discord, but the messages from un-linked will sent too.
{% endhint %}

{% hint style="info" %}
**Note 2**  
If you're the server owner and you can't write a message to another chats, ask yourself:   
_is SimpleClans allow to you to send the message to other clan chats?_   
You could use `/clan mod place <necessary clan>` to join and send the message to that chat.
{% endhint %}

## Speaking about Discord's limits...

> Only 50 channels at category as maximum

So when the category will be filled with 50 channels, SimpleClans will create a new category and place a new channel there.

> Maximum 500 channels at discord server

 That is why, if you have more than 500 clans, then SimpleClans won't catch all of them.  
 You could use `discordchat.text.clans-limit` at configuration.

## Configuration

| Config value | Description |
| :--- | :--- |
|  `discordchat.enable` | Enables the DiscordSRV integration |
|  `discordchat.discord-format` | The message format from minecraft to discord |
|  `discordchat.format` | The message format from discord to minecraft |
|  `discordchat.spy-format` |  The message format from discord to server admins  \(`simpleclans.admin.all-seeing-eye`\) |
|  `discordchat.rank` | The rank format |
|  `discordchat.text.category-format` | The name of category \("SC – TextChannel"\) |
|  `discordchat.text.category-ids` | The ids of SC categories. You may add your own category here if you want to |
|  `discordchat.text.whitelist` | This will allow only specified clans be created |
|  `discordchat.text.clans-limit` | Limit of discord channels. Maximum is 500. |

## Options that will not work

 Some of DiscordSRV configurations won't work on SC channels.  
 There is the small __list:

* `DiscordChatChannelDiscordToMinecraft` – Do you really need to disable it in DiscordSRV's config, when you may disable it in SC config?![thinking](https://github.githubassets.com/images/icons/emoji/unicode/1f914.png)
* `DiscordChatChannelRequireLinkedAccount` _–_ Our implementation requires only linked players.
* `DiscordChatChannelBlockBots` – We're all robots, now you know the truth![robot](https://github.githubassets.com/images/icons/emoji/unicode/1f916.png)
* `DiscordChatChannelBlockedIds` – Where is an option to block my IP?
* `DiscordChatChannelBroadcastDiscordMessagesToConsole` – You have spy chat for admins for that.
* `DiscordChatChannelTruncateLength` – Does anyone know what `truncate` is?
* `DiscordChatChannelRolesAllowedToUseColorCodesInChat` – No.

 At least, `DiscordChatChannelEmojiBehavior` will work.![smiley](https://github.githubassets.com/images/icons/emoji/unicode/1f603.png)

