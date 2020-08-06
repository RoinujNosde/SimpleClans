---
description:
---

# SimpleClans API Example

You can hook into SimpleClans plugin like so:

```java
private SimpleClans sc;

public void onEnable()
{
    Plugin plug = getServer().getPluginManager().getPlugin("SimpleClans");

    if (plug != null)
    {
        sc = ((SimpleClans) plug);
    }
}
```

```java
public void doClanStuff(Player player)
{
    // get a player's clan

    if (sc != null)
    {
        ClanPlayer cp = sc.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp != null)
        {
            Clan clan = cp.getClan();
        }
        else
        {
            // player is not in a clan
        }
    }

    // get a clan from a clan tag

    if (sc != null)
    {
        Clan clan = sc.getClanManager().getClan("staff");

        if (clan != null)
        {
            // clan exists
        }
    }
}
```

Every player has a **ClanPlayer** object which holds all his information, including his clan, and can be used to perform various operations on the player.

The **Clan** object holds all the information for a clan and can be used to perform various operations on the clan.

The **ClanManager** holds all the **Clans** and **ClanPlayers** and contains methods that allow you to retrieve them.