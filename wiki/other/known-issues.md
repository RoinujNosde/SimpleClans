---
description: Eklentinin bilinen sorunlarının ve olası çözümlerinin listesi.
---

# Bilinen Sorunlar

#### MySQL #1366 - Yanlış dize değeri

Bu hata, MySQL'in mevcut kodlamasının desteklemediği karakterleri eklemeye çalıştığınızda meydana gelir.

Çözüm**:** MySQL'in kodlamasını şu şekilde değiştir: `utf8mb4`.\
1\. MySQL'i açın: `my.cnf`.\
2\. Bu yapılandırmaları ekleyin, MySQL'i kaydedin ve yeniden başlatın:

```
[mysql]
default-character-set=utf8mb4
[mysqld]
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci
```

#### KD sayılmıyor

Oyuncular KD'lerinin değişmediğini bildiriyor.

**Çözüm:** `simpleclans.other.kdr-exempt`iznini reddet. Bazı izin eklentilerinde, izinden önce bir `-` ekleyin. Diğerleri `false` değeri kabul eder.
