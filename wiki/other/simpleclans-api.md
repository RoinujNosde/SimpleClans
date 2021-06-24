---
description: null
---

# SimpleClans API Örnek

SimpleClans eklentisine şu şekilde bağlanabilirsiniz:

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
    // bir oyuncunun klanını al

    if (sc != null)
    {
        ClanPlayer cp = sc.getClanManager().getClanPlayer(player.getUniqueId());

        if (cp != null)
        {
            Clan clan = cp.getClan();
        }
        else
        {
            // oyuncu bir klanda değil
        }
    }

    // klan etiketinden klan al

    if (sc != null)
    {
        Clan clan = sc.getClanManager().getClan("staff");

        if (clan != null)
        {
            // klan var
        }
    }
}
```

Her oyuncunun, klanı da dahil olmak üzere tüm bilgilerini tutan ve oyuncu üzerinde çeşitli işlemler gerçekleştirmek için kullanılabilen bir **ClanPlayer** nesnesi vardır.

**Klan** nesnesi, bir klana ait tüm bilgileri tutar ve klanda çeşitli işlemleri gerçekleştirmek için kullanılabilir.

**ClanManager**, tüm **Klanları** ve **ClanPlayer'ları** tutar ve bunları almanıza izin veren yöntemler içerir.

