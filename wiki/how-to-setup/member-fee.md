---
description: null
---

# Member Fee

## How does it work?

If enabled by the server and the clan, at `1am` a fee (set by the leader) is collected from the non-leaders.\
A member that doesn't have enough money to pay it will be automatically kicked from the clan. The collected fee is added to the clan bank.

## Configuration

* `member-fee-enabled` - Enables or disables the feature.
* `max-member-fee` - Limits the fee, so leaders may not abuse the members.
* `purchase-member-fee-set` - Enable this to charge the value below everytime a leader changes the fee value.
* `member-fee-set-price` - Value to charge for changes on the fee value

#### Example

```yml
economy:
    member-fee-enabled: false
    max-member-fee: 200.0
    purchase-member-fee-set: false
    member-fee-set-price: 1000.0 
```

## Permissions

|Permission|Description|
|---|---|
|`simpleclans.leader.fee`|Allows the user to toggle the fee and set its value|
|`simpleclans.member.fee-check`|allows the member to check how much is the fee and if it's enabled|

***

## Commands

|Permission|Description|Permission|
|---|---|---|
|`/clan toggle fee`|Enables/disables the fee|`simpleclans.leader.fee`|
|`/clan fee set [amount]`|Sets the fee value|`simpleclans.leader.fee`|
|`/clan fee check`|Checks the status of the fee|`simpleclans.member.fee-check`|