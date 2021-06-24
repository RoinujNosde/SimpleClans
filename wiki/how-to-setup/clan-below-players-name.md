---
description: null
---

# Oyuncu Adının Altında veya Üstünde Klan Adı veya Etiketi Gösterme

## Gerekli Eklentiler

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [TAB](https://www.spigotmc.org/resources/tab-1-5-x-1-15-x-free-version.57806/) \(PlaceholderAPI'yi desteklediği sürece farklı bir tab listesi eklentisi kullanabilirsiniz.\)

## Adım Adım

1. TAB'ın yapılandırmasını açın ve Gruplar bölümünü düzenleyin:

 Altına eklemek için bu: `belowname` 

Üstüne eklemek için bu: `abovename` kısmına bunu ekleyin:`%simpleclans_clan_name%` veya`%simpleclans_clan_color_tag%`

{% code title="Example:" %}
```yaml
_OTHER_:
 tabprefix: "%vault-prefix% "
 tagprefix: "%vault-prefix% "
 tabsuffix: "%afk%"
 tagsuffix: "%afk%"
 customtabname: "%essentialsnick%"
 customtagname: "%essentialsnick%"
 belowname: "&l&b[ %simpleclans_clan_color_tag% &l&b]"
```
{% endcode %}

2. Aşağıdaki `belowname` veya`classic-vanilla-belowname` seçeneğini bulun ve devre dışı bırakın.

3. `unlimited-nametag-prefix-suffix-mode` seçeneğini bulun ve etkinleştirin.

4. Yeniden başlatın \(veya yeniden yükleyin\) ve keyfini çıkarın!

## Ekran Görüntüsü

![](../.gitbook/assets/klan.png)

