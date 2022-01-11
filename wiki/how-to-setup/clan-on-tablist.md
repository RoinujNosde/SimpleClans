---
description: null
---

# ☝ Клан в табе

## Необходимые плагины

Вы вправе использовать и другие плагины, изменяющие таб, если они поддерживают PlaceholderAPI.

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [TAB](https://www.spigotmc.org/resources/tab-1-7-x-1-16-5-free-version.57806/)

## Пошагово

1\. Откройте `groups.yml` файл в TAB и измените формат таба для всех, добавив`%simpleclans_clan_color_tag%`:

{% code title="Пример:" %}
```yaml
_DEFAULT_:
    tabprefix: "%simpleclans_clan_color_tag%%vault-prefix% "
```
{% endcode %}

2\. Перезапустите сервер (или перезагрузите конфиг: `/tab reload`) и наслаждайтесь!

## Скриншот

![](../../.gitbook/assets/clans-tablist.png)
