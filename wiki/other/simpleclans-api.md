---
description: >-
  This page will help you understand how to install and use the SimpleClans API
  in your plugins.
---

# ♟ API Examples

## Step 1. Add the SimpleClans API to your plugin

There are two ways to do this: via Maven or locally. \
We strongly **recommend** doing this via Maven.

### Maven

Add the following lines to `pom.xml`:

```markup
<repositories>
    <repository>
        <id>codemc-repo</id>
        <url>https://repo.codemc.org/repository/maven-public</url>
    </repository>
</repositories>
```

```markup
<dependencies>
    <dependency>
        <groupId>net.sacredlabyrinth.phaed.simpleclans</groupId>
        <artifactId>SimpleClans</artifactId>
        <version>2.15.2</version> 
        <!-- You can find out the latest available version in the note below -->
        <scope>provided</scope>
    </dependency>
</dependencies>
```

{% hint style="info" %}
**Note**\
****The latest version can be found here: [link](https://github.com/RoinujNosde/SimpleClans/releases)
{% endhint %}

### Locally

In this example, we will use the IntelliJ IDEA, but the following actions also work in other IDEs.

1. Open the structure of your project (`F4`)
2. Select Libraries, click on the cross, select "New Project Library -> Java" in the window that appears and add SimpleClans.

![](<../../.gitbook/assets/izobrazhenie (4) (1).png>)

Return to the project structure, then go to Project Settings - > Modules, set the compilation mode to "Provided".

![](<../../.gitbook/assets/izobrazhenie (5) (1).png>)

* [x] Congratulations, you have added the SimpleClans API to your project. 😃

## Step 2. Use the SimpleClans API

#### What do you need to know?

* **ClanPlayer** is a class that represents a player object. This class contains information about the player, his clan, etc.
* **Clan** is a class that presents a clan object. It has methods for getting clan players, clan tag, allies, leaders, etc.
* **ClanManager** is a class that allows you to retrieve **Clan** and **ClanPlayer**.

#### Example of using SimpleClans

You can hook into SimpleClans plugin like so:

{% tabs %}
{% tab title="MyPlugin.class" %}
```java
public class MyPlugin extends JavaPlugin {
    private static SimpleClans sc;
     
    @Override   
    public void onEnable() {
      Plugin plug = getServer().getPluginManager().getPlugin("SimpleClans");
      
      if (plug != null) {
          sc = (SimpleClans) plug;
      }
    }
    
    public static getSimpleClans() {
        return sc;
    }
}
```
{% endtab %}

{% tab title="Example.class" %}
```java
public class Example {

    public void doClanStuff(Player player) {
        UUID playerUuid = player.getUniqueId();
        
        // Get a player's clan
        ClanPlayer cp = MyPlugin.getSimpleClans().getClanManager().getClanPlayer(playerUuid);
            
        if (cp != null) {
            Clan clan = cp.getClan();
        } else {
            // Player is not in a clan
        }
    
        // Get a clan from a clan tag
        Clan clan = MyPlugin.getSimpleClans().getClanManager().getClan("staff");
    
        if (clan != null) {
            // Clan exists
        }
    }
}
```
{% endtab %}
{% endtabs %}

If you don't want to specify a check for the presence of SimpleClans, you can always specify a dependency in `plugin.yml`:

{% tabs %}
{% tab title="plugin.yml" %}
```yaml
depend:
    - SimpleClans
```
{% endtab %}

{% tab title="Example.class" %}
```java
public class Example {

    public void doClanStuff(Player player) {
        SimpleClans sc = SimpleClans.getInstance();
        UUID playerUuid = player.getUniqueId();
        
        // Get a player's clan
        ClanPlayer cp = sc.getClanManager().getClanPlayer(playerUuid);
            
        if (cp != null) {
            Clan clan = cp.getClan();
        } else {
            // Player is not in a clan
        }
    
        // Get a clan from a clan tag
        Clan clan = sc.getClanManager().getClan("staff");
        
        if (clan != null) {
            // Clan exists
        }
    }
}
```
{% endtab %}
{% endtabs %}

## Step 3. Continue your learning

Don't stop in your beginnings, feel free to learn our API: [javadocs](https://ci.roinujnosde.me/job/SimpleClans/Javadoc/).
