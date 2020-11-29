---
description: null
---

# Клан в табе

## Необходимые плагины

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [PlayerListPlus](https://www.spigotmc.org/resources/%E2%99%9B-playerlistplus-%E2%99%9B-1-8-1-14-3-tablist-editor.55878/) \(Вы можете использовать и другие плагины на таб, если он поддерживает PlaceholderAPI\)

## Пошагово

1. Откройте конфиг PlayerListPlus и измените format, добавив`%simpleclans_clan_color_tag%`:

{% code title="Example:" %}
```yaml
slot-items:
#   This slot items will shows all players
 PLAYERS:
     format: "%simpleclans_clan_color_tag%&c.$displayname"
     type: PLAYER_LIST
     hidevanished: true
     ping: true
     skin: true
```
{% endcode %}

2. Перезапустите сервер \(или перезагрузите конфиг\) и наслаждайтесь!

## Скриншот

![](../.gitbook/assets/clans-tablist.png)

