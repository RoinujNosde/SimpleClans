---
description: null
---

# Пример использования SimpleClans API

Вы можете использовать плагин SimpleClans вот так:

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

Каждый игрок имеет объект **ClanPlayer**, который хранит в себе информацию о нём, включая информацию о его клане, и может быть использован для исполнения различных операций на игроке.

Объект **Clan** хранит в себе всю информацию о клане и и может быть использован для исполнения различных операций на клане.

**ClanManager** хранит в себе всю информацию об объектах Clan и ClanPlayer и содержит методы, которые позволяют получить их.

