# 👇 Clan Below Player's Name

## Plugins required

* [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
* [TAB](https://github.com/NEZNAMY/TAB/releases) (you can use a different tablist plugin, as long as it supports PlaceholderAPI)

## Step by step

1\. Open TAB's `config.yml`, enable **unlimited-nametag-mode**:

{% code title="/plugins/TAB/config.yml" %}
```yaml
scoreboard-teams:
    unlimited-nametag-mode:
        enabled: true
```
{% endcode %}

2\. Now let's edit `groups.yml` file. Add `belowname` option if missing.

{% code title="/plugins/TAB/groups.yml" %}
```yaml
_DEFAULT_:
  belowname: "%simpleclans_tag_label%"
  # if you want a clan tag above your name:
  # abovename: "%simpleclans_tag_label%"
```
{% endcode %}

3 (_Optional_). Change the view of clan tags in SimpleClans's `config.yml`:

{% code title="/plugins/SimpleClans/config.yml" %}
```yaml
tag:
  default-color: '8'
  max-length: 5
  bracket:
    color: '8'
    leader-color: '4'
    left: '' # It can be '[' or any other character
    right: '' # It can be ']' or any other character
  min-length: 2
  separator:
    color: '8'
    leader-color: '4'
    char: ' .' # You can remove a dot after clan tags
```
{% endcode %}

You can check more info about this section in [configuration](https://wiki.roinujnosde.me/simpleclans/how-to-setup/configuration#tags) tab.

4\. Restart your server or reload TAB – `/tab reload`. Enjoy! :)

## Screenshot

![](../.gitbook/assets/clans-below-name.png)
