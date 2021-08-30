---
description: >-
  Economics on your server is probably one of the most important things to do.
  To understand how much money your clans operate, you can rely this function.
---

# Logging server economy

## Configuration

By default, this feature is enabled, so you may want to turn it off. You can do it here:

{% tabs %}
{% tab title="config.yml" %}
```yaml
economy:
  bank-log:
    enable: true
```
{% endtab %}
{% endtabs %}

## Feature description

This feature will create a one per day CSV file in `/SimpleClans/logs/bank/`.  


![How does log file looks like](../.gitbook/assets/izobrazhenie%20%283%29.png)

## For developers

If you want to add another format of logging, you can see how it's [implemented](https://github.com/RoinujNosde/SimpleClans/tree/master/src/main/java/net/sacredlabyrinth/phaed/simpleclans/loggers).

