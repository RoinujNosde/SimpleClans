---
description: null
---

# Klan Bakımı

## How does it work?

If enabled, clans will have to pay an amount everyday to keep their clans. The value will be collected at `1:30am`. At `12am`, if the clan doesn't have enough money to pay it, a wan will be sent to the clan's BB. The amount can be fixed or based on the clan size.

Ex. fee based on the clan size:

```text
upkeep base    = 20.0
clan size      = 10
final upkeep   = 200.0
```

## Configuring

* `upkeep` - The base upkeep.
* `upkeep-enabled` - Enable or disable the feature.
* `multiply-upkeep-by-clan-size` - This works as explained above
* `charge-upkeep-only-if-member-fee-enabled` - Enable upkeep if [member fee](https://github.com/RoinujNosde/SimpleClans/wiki/Member-Fee) is enabled.

#### Exemple

```yaml
economy:
    upkeep: 200.0
    upkeep-enabled: false
    multiply-upkeep-by-clan-size: false
    charge-upkeep-only-if-member-fee-enabled: true
```

