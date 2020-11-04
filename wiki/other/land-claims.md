---
description: null
---

# Land Claims Plugins

## Configuration

* `enable-auto-groups` - 
* `auto-group-groupname`- 

### Exemple

```yaml
settings:
    enable-auto-groups: false
permissions:
  auto-group-groupname: true
  YourClanNameHere:
  - test.permission
```

## GriefPrevention

You can replace `<clantag>` with ANY clan tag \(ally, rival, etc\)

| Command | Description |
| :--- | :--- |
| `/Trust group.<clantag>` | Gives the Clan members permission to edit in your claim |
| `/AccessTrust group.<clantag>` | Gives the Clan members permission to use your buttons, levers, and beds |
| `/ContainerTrust group.<clantag>` | Gives the Clan members permission to use your buttons, levers, beds, crafting gear, containers, and animals |
| `/PermissionTrust group.<clantag>` | Gives the Clan members permission to share their permission level with others |
| `/UnTrust group.<clantag>` | Revokes any permissions granted to a Clan in your claim |

## Note

After the permission is given, the player must reconnect.

