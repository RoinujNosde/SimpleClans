---
description:
---

# Clan on Tablist

## Plugins needed

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [PlayerListPlus](https://www.spigotmc.org/resources/%E2%99%9B-playerlistplus-%E2%99%9B-1-8-1-14-3-tablist-editor.55878/) (you can use a different tablist plugin, as long as it supports PlaceholderAPI)

## Step by step

1. Open PlayerListPlus config and edit the formats adding `%simpleclans_clan_color_tag%`:\
For example:

```yml
slot-items:
#   This slot items will shows all players
    PLAYERS:
        format: "%simpleclans_clan_color_tag%&c.$displayname"
        type: PLAYER_LIST
        hidevanished: true
        ping: true
        skin: true
```
2. Restart (or reload) and enjoy!

## Screenshot

![](../wiki/.gitbook/assets/tablist.png)