---
description: null
---

# Claim\(Arazi\) Eklentileri

## Yapılandırma

* `enable-auto-groups` - 
* `auto-group-groupname`- 

### Örnek

```yaml
settings:
    enable-auto-groups: false
permissions:
  auto-group-groupname: true
  YourClanNameHere:
  - test.permission
```

## GriefPrevention

`<clantag>` öğesini herhangi bir klan etiketiyle değiştirebilirsiniz. \(müttefik, rakip vb.\)

| Komut | Açıklama |
| :--- | :--- |
| `/Trust group.<clantag>` | Klan üyelerine talebinizi düzenleme izni verir |
| `/AccessTrust group.<clantag>` | Klan üyelerine düğmelerinizi, şalterlerinizi ve yataklarınızı kullanma izni verir |
| `/ContainerTrust group.<clantag>` | Klan üyelerine düğmelerinizi, kollarınızı, yataklarınızı, işleme teçhizatınızı, kaplarınızı ve hayvanlarınızı kullanma izni verir |
| `/PermissionTrust group.<clantag>` | Klan üyelerine izin düzeylerini başkalarıyla paylaşma izni verir |
| `/UnTrust group.<clantag>` | Talebinizde bir Klana verilen tüm izinleri iptal eder |

## Not

İzin verildikten sonra, oyuncu yeniden bağlanmalıdır.

