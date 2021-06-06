---
description: This page contains information about sharing your lands with clan members.
---

# Land Claims Plugins

## Sharing lands

Currently, SimpleClans may work with GriefPrevention, WorldGuard \(also plugins depending on it\) and PlotSquared. It has default methods to allow and block different actions \(break, place, interact, etc.\).

Take a look at the [configuration](https://simpleclans.gitbook.io/simpleclans/how-to-setup/configuration#war-and-protection).  
Here is the full command list:

> Optional argument marked as \[argument\]

| Command | Description |
| :--- | :--- |
| `/clan land allow [action]` | Allows **the specified action** on this land to your clan members |
| `/clan land block [action]` | Blocks **the specified action** on this land to your clan members |

Actions list:

| Action | Description |
| :--- | :--- |
| container | Used to allow/block opening chests, anvils, enchant tables, etc. on your land |
| place\_block | Used to allow/block placing blocks on your land |
| break | Used to allow/block breaking blocks on your land |
| damage | Used to allow/block taking any of sort damage on your land |
| interact | Used to allow/block doors, buttons, levers, etc. on your land |
| interact\_entity | Used to allow/block interacting with entities on your land |
| all | Used to allow/block all actions |

**Example:** `/clan land allow place_block` will allow placing blocks on your land for your clan members.

{% hint style="info" %}
**Note**  
The commands will affect the land you are currently standing on or all lands, if you are not in one _\(when enabled\)_.
{% endhint %}

## Making your own provider

{% hint style="warning" %}
**Warning**  
Work in progress.
{% endhint %}

## Another ways of sharing lands

### GriefPrevention

Configuration:

* `enable-auto-groups` - 
* `auto-group-groupname`- 

```yaml
settings:
    enable-auto-groups: false
permissions:
  auto-group-groupname: true
  YourClanNameHere:
  - test.permission
```

You can replace `<clantag>` with ANY clan tag \(ally, rival, etc\)

| Command | Description |
| :--- | :--- |
| `/trust group.<clantag>` | Gives the Clan members permission to edit in your claim |
| `/accesstrust group.<clantag>` | Gives the Clan members permission to use your buttons, levers, and beds |
| `/containertrust group.<clantag>` | Gives the Clan members permission to use your buttons, levers, beds, crafting gear, containers, and animals |
| `/permissiontrust group.<clantag>` | Gives the Clan members permission to share their permission level with others |
| `/untrust group.<clantag>` | Revokes any permissions granted to a Clan in your claim |

{% hint style="info" %}
**Note**  
After the permission is given, the player must reconnect.
{% endhint %}

