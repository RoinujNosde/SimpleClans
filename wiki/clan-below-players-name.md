---
description:
---

# Clan Below Player's Name

## Plugins needed

- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
- [TAB](https://www.spigotmc.org/resources/tab-1-5-x-1-15-x-free-version.57806/) (you can use a different tablist plugin, as long as it supports PlaceholderAPI)

## Step by step

1. Open TAB's config and edit the Groups section adding `%simpleclans_clan_color_tag%` to `belowname` :\
For example:
```yml
  _OTHER_:
    tabprefix: "%vault-prefix% "
    tagprefix: "%vault-prefix% "
    tabsuffix: "%afk%"
    tagsuffix: "%afk%"
    customtabname: "%essentialsnick%"
    customtagname: "%essentialsnick%"
    belowname: "&l&b[ %simpleclans_clan_color_tag% &l&b]"
```
2. Find the option `belowname` or `classic-vanilla-belowname` and disable it.
3. Find the option `unlimited-nametag-prefix-suffix-mode` and enable it.
4. Restart (or reload) and enjoy!

## Screenshot
![Player's Name Tag](https://i.imgur.com/IBC8kLC.png)