---
description: null
---

# 👆 Clan on Tablist

## Plugins needed

You can use a different tablist plugin, as long as it supports PlaceholderAPI

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [TAB](https://github.com/NEZNAMY/TAB/releases)

## Step by step

1\. Open Tab' `groups.yml` file and edit the formats adding `%simpleclans_clan_color_tag%`:

{% code title="Example:" %}
```yaml
_DEFAULT_:
    tabprefix: "%simpleclans_clan_color_tag%%vault-prefix% "
```
{% endcode %}

2\. Restart (or reload `/tab reload`) and enjoy!

## Screenshot

![](../../.gitbook/assets/clans-tablist.png)
