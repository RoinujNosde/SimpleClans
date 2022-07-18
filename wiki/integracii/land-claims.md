---
description: >-
  На этой странице содержится информация о совместном использовании вашей
  территории с участниками клана.
---

# 🏝 GriefPrevention, WorldGuard и другие

## Раздел территории

В настоящее время SimpleClans может работать с GriefPrevention, WorldGuard (а также плагинами, зависящими от него) и PlotSquared. SimpleClans имеет методы по умолчанию для разрешения и блокирования различных действий (ломать, строить, взаимодействовать и т.д.).

Полный список команд:

> Необязательные аргументы отмечены как \[аргумент]

| Команда                     | Описание                                                                   |
| --------------------------- | -------------------------------------------------------------------------- |
| `/clan land allow [action]` | Разрешает **указанное действие** на этой территории для ваших со-клановцев |
| `/clan land block [action]` | Блокирует **указанное действие** на этой территории для ваших со-клановцев |

Список действий:

| Действие         | Описание                                                                                                                   |
| ---------------- | -------------------------------------------------------------------------------------------------------------------------- |
| container        | Используется, чтобы разрешить/заблокировать открытие сундуков, наковален, зачаровательных столов и др. на вашей территории |
| place\_block     | Используется, чтобы разрешить/заблокировать установку блоков на вашей территории                                           |
| break            | Используется, чтобы разрешить/заблокировать ломание блоков на вашей территории                                             |
| damage           | Используется, чтобы разрешить/заблокировать получение любого вида урона на вашей территории                                |
| interact         | Используется, чтобы разрешить/заблокировать взаимодействие с дверьми, кнопками, рычагами, и др. на вашей территории        |
| interact\_entity | Используется, чтобы разрешить/заблокировать взаимодействие с сущностями на вашей территории                                |
| all              | Используется, чтобы разрешить/заблокировать все действия                                                                   |

**Пример:** `/clan land allow place_block` разрешит установку блоков на вашей территории для ваших со-клановцев.

{% hint style="info" %}
**Заметка**\
Команды повляют на территорию, в которой вы в текущий момент находитесь, или на все территории, если вы не стоите не на одной. _(когда edit-all-lands – true)_.
{% endhint %}

## Другие пути для раздела территории

### GriefPrevention

#### Конфигурация

* `enable-auto-groups` -&#x20;
* `auto-group-groupname`-&#x20;

#### Пример

```yaml
settings:
    enable-auto-groups: false
permissions:
  auto-group-groupname: true
  YourClanNameHere:
  - test.permission
```

Вы можете заменить `<clantag>` с любым клан тегом (будь то союзник, противник, или прочее)

| Команда                            | Описание                                                                                                         |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| `/Trust group.<clantag>`           | Даёт право участникам вашего клана изменять ваш регион                                                           |
| `/AccessTrust group.<clantag>`     | Даёт право участникам вашего клана использовать рычаги, кнопки и кровати, расположенные на вашем регионе         |
| `/ContainerTrust group.<clantag>`  | Даёт право участникам вашего клана использовать рычаги, кнопки и кровати, столы для крафтов, сундуки и животных. |
| `/PermissionTrust group.<clantag>` | Даёт право участникам вашего клана делиться их уровнем доступа с другими                                         |
| `/UnTrust group.<clantag>`         | Забрать любые права, гарантированные ваших сокланавцам, на вашем регионе                                         |

{% hint style="info" %}
**Заметка**\
После того, как вы выдали право, игроки должны перезайти.
{% endhint %}