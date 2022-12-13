# 👆 Clan on Tablist

## Plugins required

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [TAB](https://github.com/NEZNAMY/TAB/releases) (you can use a different tablist plugin, as long as it supports PlaceholderAPI)

## Step by step

1\. Open Tab's `groups.yml` file and add `%simpleclans_clan_color_tag%` to `tabprefix`:

{% code title="/plugins/TAB/groups.yml" %}
```yaml
_DEFAULT_:
    tabprefix: "%simpleclans_clan_color_tag%%vault-prefix% "
```
{% endcode %}

2\. Restart (or reload `/tab reload`) your server and enjoy!

## Screenshot

![](../../.gitbook/assets/clans-tablist.png)
