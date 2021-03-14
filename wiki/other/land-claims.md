---
description: null
---

# GriefPrevention, WorldGuard и другие

## Конфигурация

* `enable-auto-groups` - 
* `auto-group-groupname`- 

### Пример

```yaml
settings:
    enable-auto-groups: false
permissions:
  auto-group-groupname: true
  YourClanNameHere:
  - test.permission
```

## GriefPrevention

Вы можете заменить `<clantag>` с любым клан тегом \(будь то союзник, противник, или прочее\)

| Команда | Описание |
| :--- | :--- |
| `/Trust group.<clantag>` | Даёт право участникам вашего клана изменять ваш регион |
| `/AccessTrust group.<clantag>` | Даёт право участникам вашего клана использовать рычаги, кнопки и кровати, расположенные на вашем регионе |
| `/ContainerTrust group.<clantag>` | Даёт право участникам вашего клана использовать рычаги, кнопки и кровати, столы для крафтов, сундуки и животных. |
| `/PermissionTrust group.<clantag>` | Даёт право участникам вашего клана делиться их уровнем доступа с другими |
| `/UnTrust group.<clantag>` | Забрать любые права, гарантированные ваших сокланавцам, на вашем регионе |

## Заметка

После того, как вы выдали право, игроки должны перезайти.

