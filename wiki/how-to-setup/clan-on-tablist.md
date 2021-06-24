---
description: null
---

# Tab Listesinde Klan Adlarını Kullanma

## Gerekli Eklentiler

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [PlayerListPlus](https://www.spigotmc.org/resources/%E2%99%9B-playerlistplus-%E2%99%9B-1-8-1-14-3-tablist-editor.55878/) \(PlaceholderAPI'yi desteklediği sürece farklı bir tab listesi eklentisi kullanabilirsiniz.\)

## Adım Adım

1. PlayerListPlus yapılandırmasını açın ve formatlarını düzenleyin`%simpleclans_clan_color_tag%`:

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

1. Yeniden başlatın \(veya yeniden yükleyin\) ve keyfini çıkarın!

## Ekran Görüntüsü

![](../.gitbook/assets/clans-tablist.png)

