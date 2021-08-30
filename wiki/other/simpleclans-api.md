---
description: >-
  Эта страница поможет вам разобраться в том, как установить и использовать
  SimpleClans API в своих плагинах.
---

# SimpleClans API

## Шаг 1. Добавьте SimpleClans API в свой плагин

Сделать это можно двумя способами: через Maven или локально.  
Мы настоятельно **рекомендуем** делать это через Maven.

### Maven

Добавьте следующие строки в `pom.xml`:

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
        <!-- Вы можете узнать последнюю доступную версию в заметке ниже -->
        <scope>provided</scope>
    </dependency>
</dependencies>
```

{% hint style="info" %}
**Заметка**  
Последнюю версию можно узнать тут: [ссылка](https://github.com/RoinujNosde/SimpleClans/releases)
{% endhint %}

### Локально

В указаном примере мы будем использовать IntelliJ IDEA, но последующие действия также работают и в других IDE.

1. Откройте структуру вашего проекта \(`F4`\)
2. Выберите пункт Libraries, кликните на крестик, в появившемся окошке выбираем "New Project Library -&gt; Java" и добавляем SimpleClans.

![](../.gitbook/assets/izobrazhenie%20%284%29.png)

Возвращаемся к структуре проекта, дальше заходим в Project Settings -&gt; Modules, ставим режим компиляции "Provided".

![](../.gitbook/assets/izobrazhenie%20%285%29.png)

* [x] Поздравляю, вы поставили SimpleClans API в свой проект. 😃

## Шаг 2. Используйте SimpleClans API

#### Что вам нужно знать?

* **ClanPlayer –** это класс, представляющий из себя объект игрока. В этом классе содержится информация об игроке, его клане и др.
* **Clan –** это класс, презентующий объект клана. В нём имеется методы для получения игроков клана, тега клана, союзников, лидеров и др.
* **ClanManager –** это класс, позволяющий получить **Clan** и **ClanPlayer.**

#### Пример использования SimpleClans

Вы можете использовать плагин SimpleClans вот так:

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
        
        // Получение клана игрока
        ClanPlayer cp = MyPlugin.getSimpleClans().getClanManager().getClanPlayer(playerUuid);
            
        if (cp != null) {
            Clan clan = cp.getClan();
        } else {
            // Игрок не является участником какого-либо клана
        }
    
        // Получение клана из клан тега
        Clan clan = MyPlugin.getSimpleClans().getClanManager().getClan("staff");
    
        if (clan != null) {
            // Клан существует
        }
    }
}
```
{% endtab %}
{% endtabs %}

В случае, если вы не хотите указывать проверку на наличие плагина, вы всегда можете указать зависимость в `plugin.yml`:

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

    public void doClanStuff(Player player)
    {
        SimpleClans sc = SimpleClans.getInstance();
        
        // Получение клана игрока
        ClanPlayer cp = sc.getClanManager().getClanPlayer(player.getUniqueId());
        if (cp != null) {
            Clan clan = cp.getClan();
        } else {
            // Игрок не является участником какого-либо клана
        }
    
        // Получение клана из клан тега
        Clan clan = sc.getClanManager().getClan("staff");
        if (clan != null) {
             // Клан существует
        }
    }
}
```
{% endtab %}
{% endtabs %}

{% hint style="info" %}
**Заметка**  
_Javadoc_ в настоящее время не реализован, вы можете ускорить его выход, отметив [тут](https://github.com/RoinujNosde/SimpleClans/discussions/210).
{% endhint %}

