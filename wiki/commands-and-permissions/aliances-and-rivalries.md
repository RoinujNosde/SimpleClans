---
description: null
---

# Klan İttifakları ve Rekabetleri

Herhangi bir klan lideri, `/clan ally add`ile başka bir klanla ittifak başlatmak için istek gönderebilir. İstek ikinci klanın lideri tarafından kabul edilirse ittifak kurulur. İttifak herhangi bir klanın herhangi bir lideri tarafından `/clan ally remove` ile herhangi bir zamanda bozulabilir, hiç kimse bir ittifakın kaldırılmasını kabul etmek zorunda değildir.

Klan rekabetleri herhangi bir zamanda herhangi bir klan tarafından başlatılabilir, herhangi bir talep gerekmez, Bir klan lideri `/clan rival add` kullanarak bir tane istediğine karar verdiğinde, rekabetler otomatik olarak oluşturulur. Biri sizi kızdırdıysa ve onları rakip olarak istiyorsanız, onların iznine gerek yoktur. Öte yandan bir klan rekabetini kırmak için diğer klanın kabulüne ihtiyacınız var, Liderlerinden biri rekabetin bozulduğunu kabul ettiğinde, diğer klana istek göndermek için `/clan rival remove` komutunu kullanmalısınız.

`/clan alliances` komutu ile tüm klanların ve müttefiklerinin bir listesini görüntüleyebilirsiniz, veya `/clan rivalries` komutuyla rakipleri.

## Komutlar

| Komutlar | İzinler |
| :--- | :--- |
| `/clan ally add [tag]` | Bir ittifak başlatmak için bir istek gönderin \(kabul gereklidir\) |
| `/clan ally remove [tag]` | İttifakı kaldır \(kabul gerekli değildir\) |
| `/clan rival add [tag]` | Bir rekabet başlatmak \(kabul gerekli değildir\) |
| `/clan rival remove [tag]` | Bir rekabeti kaldırın \(kabul gereklidir\) |
| `/clan alliances` | Tüm klanları ve müttefiklerini listele |
| `/clan rivalries` | Tüm klanları ve rakiplerini listele |

## İzinler

| İzin | Açıklama |
| :--- | :--- |
| `simpleclans.member.ally` | Müttefik sohbeti kullanabilir |
| `simpleclans.leader.ally` | Klanını diğer klanlarla ittifak yapabilir |
| `simpleclans.leader.rival` | Başka bir klanla rekabet başlatabilir |
| `simpleclans.anyone.alliances` | Klana göre ittifakları görüntüleyebilir |
| `simpleclans.anyone.rivalries` | Klana göre rekabetleri görüntüleyebilir |

