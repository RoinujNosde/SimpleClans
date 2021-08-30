---
description: null
---

# Разрешения

## SuperPerms права \(Некоторые добавляются автоматически\)

Эти разрешения – быстрый способ настроить SimpleClans. Некоторые добавлены автоматически \(см. приписку авто\). Если вы хотите настроить каждое разрешение по отдельности \(индивидуальные права\), то сначала нужно отрицать эти права.

| Право | Описание |
| :--- | :--- |
| `simpleclans.anyone.*` \(Авто\) | Разрешения каждого |
| `simpleclans.member.*` \(Авто\) | Разрешения участников клана |
| `simpleclans.leader.*` \(Авто\) | Разрешения лидеров клана |
| `simpleclans.mod.*` \(Авто к OP'кам\) | Разрешения модераторов |
| `simpleclans.admin.*` \(Авто к OP'кам\) | Разрешения администраторов |

## Индивидуальные права

Вам не нужно добавлять их, если они добавлены автоматически \(от SuperPerms\). Они написаны только на тот случай, если вы захотите отключить пару из них по отдельности. Тогда вам нужно отрицать SuperPerms разрешение и установить нужное из этого списка на `false`.

### Разрешения каждого

| Право | Описание |
| :--- | :--- |
| `simpleclans.anyone.alliances` | Может просматривать список союзников всех кланов |
| `simpleclans.anyone.leaderboard` | Может посмотреть таблицу лидеров |
| `simpleclans.anyone.list` | Может посмотреть список всех кланов |
| `simpleclans.anyone.lookup` | Может посмотреть информацию об игроках |
| `simpleclans.anyone.profile` | Может посматривать профили кланов |
| `simpleclans.anyone.rivalries` | Может посмотреть список противников всех кланов |
| `simpleclans.anyone.roster` | Может посмотреть список участников кланов |

### Разрешения участника

| Право | Описание |
| :--- | :--- |
| `simpleclans.member.abstain` | Может воздержаться от участия |
| `simpleclans.member.accept` | Может принять запрос |
| `simpleclans.member.ally` | Может использовать союзный чат |
| `simpleclans.member.chat` | Может использовать чат клана |
| `simpleclans.member.bank` | Может использовать банк клана |
| `simpleclans.member.bb-add` | Может добавлять сообщения на доску объявлений |
| `simpleclans.member.bb-toggle` | Может переключать отображение доски объявлений его клана |
| `simpleclans.member.bb` | Может видеть доску объявлений его клана |
| `simpleclans.member.can-join` | Может присоединяться к кланам |
| `simpleclans.member.coords` | Может посмотреть координаты его клана |
| `simpleclans.member.deny` | Может отклонить запрос |
| `simpleclans.member.ff` | Может переключать собственный "огонь по своим" |
| `simpleclans.member.home` | Может телепортироваться на точку базы клана |
| `simpleclans.member.kills` | Может посмотреть убийства свои и других |
| `simpleclans.member.lookup` | Может посмотреть информацию о себе |
| `simpleclans.member.profile` | Может посмотреть информацию о своём клане |
| `simpleclans.member.resign` | Может покинуть его клан |
| `simpleclans.member.roster` | Может посмотреть список участников его клана |
| `simpleclans.member.stats` | Может посмотреть статистику его клана |
| `simpleclans.member.vitals` | Может посмотреть боеспособность его клана |
| `simpleclans.member.toggle.bb` | Can toggle bb on/off \(?\) |
| `simpleclans.member.tag-toggle` | Может скрыть или показать клан тег |
| `simpleclans.member.fee-check` | Позволяет проверить включена ли комиссия и узнать её размер |
| `simpleclans.member.bypass-fee` | Может обойти комиссию участника |

### Разрешения лидеров

| Право | Описание |
| :--- | :--- |
| `simpleclans.leader.fee` | Разрешает переключать комиссию клана и изменять её размер |
| `simpleclans.leader.ally` | Может иметь союз с другим кланом |
| `simpleclans.leader.create` | Может создавать кланы |
| `simpleclans.leader.verify` | Может подать заявку на подтверждение собственного клана |
| `simpleclans.leader.demote` | Может понизить лидера до участника |
| `simpleclans.leader.disband` | Может расформировать собственный клан |
| `simpleclans.leader.ff` | Может переключать "огонь по своим" у собственного клана |
| `simpleclans.leader.home-set` | Может установить точку базы клана |
| `simpleclans.leader.regroup.me` | Может перегруппировать \(телепортировать \) весь клан к себе |
| `simpleclans.leader.regroup.home` | Может перегруппировать \(телепортировать \) весь клан на точку базы клана |
| `simpleclans.leader.invite` | Может приглашать игроков в собственный клан |
| `simpleclans.leader.kick` | Может выгнать \(кикнуть\) игроков с собственного клана |
| `simpleclans.leader.modtag` | Может изменять тег собственного клана |
| `simpleclans.leader.description` | Может изменять описание собственного клана |
| `simpleclans.leader.coloredtag` | Может использовать цветовые коды в тегах |
| `simpleclans.leader.coloredrank` | Может использовать цветовые коды в отобр. имени ранга |
| `simpleclans.leader.promotable` | Может быть повышен до лидера клана |
| `simpleclans.leader.promote` | Может повысить участника до лидера клана |
| `simpleclans.leader.rank.assign` | Может назначить участнику ранг |
| `simpleclans.leader.rank.unassign` | Может забрать ранг у участника  |
| `simpleclans.leader.rank.create` | Может создать новый ранг |
| `simpleclans.leader.rank.delete` | Может удалить существующий ранг |
| `simpleclans.leader.rank.list` | Может посмотреть список рангов |
| `simpleclans.leader.rank.setdisplayname` | Может устанавливать отобр. имя ранга |
| `simpleclans.leader.rank.permissions.add` | Может добавить право к рангу |
| `simpleclans.leader.rank.permissions.available` | Может посмотреть список доступных прав рангов |
| `simpleclans.leader.rank.permissions.list` | Может посмотреть список прав рангов |
| `simpleclans.leader.rank.permissions.remove` | Может удалить права у ранга |
| `simpleclans.leader.rival` | Может начать вражду с другими кланами |
| `simpleclans.leader.settrust` | Может устанавливать уровень доверия участникам |
| `simpleclans.leader.war` | Может начать войну |
| `simpleclans.leader.setbanner` | Может устанавливать баннер собственного клана |
| `simpleclans.leader.withdraw-toggle:` | Может переключать вывод клана |
| `simpleclans.leader.deposit-toggle:` | Может переключать взнос клана |
| `simpleclans.leader.bb-clear` | Может очистить доску объявлений собственного клана |

### Разрешения модераторов

| Право | Описание |
| :--- | :--- |
| `simpleclans.mod.ban` | Может заблокировать игрока от использования команд плагина |
| `simpleclans.mod.bypass` | Может обойти любые ограничения |
| `simpleclans.mod.disband` | Может расформировать любой клан |
| `simpleclans.mod.globalff` | Может выключить глобальный параметр "огонь по своим" |
| `simpleclans.mod.home` | Может ставить точку базы любому клану |
| `simpleclans.mod.hometp` | Может телепортироваться на точку базы любого клана |
| `simpleclans.mod.staffgui` | Может открыть меню администратора |
| `simpleclans.mod.place` | Может перемещать вручную игроков из одного клана в другой |
| `simpleclans.mod.keep-items` | Can keep items when teleporting home |
| `simpleclans.mod.mostkilled` | Может посмотреть список наибольших убийств у любого клана |
| `simpleclans.mod.nopvpinwar` | Может обойти PvP разрешение в войнах |
| `simpleclans.mod.unban` | Может разблокировать игрока от использования команд плагина |
| `simpleclans.mod.verify` | Может подтвердить клан |

### Разрешешения администраторов

| Право | Описание |
| :--- | :--- |
| `simpleclans.admin.resetkdr` | Может очистить KDR любого игрока |
| `simpleclans.admin.purge` | Может очистить данные об игроке |
| `simpleclans.admin.demote` | Может понизить игрока до участника |
| `simpleclans.admin.promote` | Может повышать игроков до лидера |
| `simpleclans.admin.all-seeing-eye` | Может читать чаты всех кланов |
| `simpleclans.admin.reload` | Может перезагрузить конфигурацию |
| `simpleclans.admin.permanent` | Позволяет переключить перманентный статус |
| `simpleclans.admin.bank.status` | Позволяет проверить баланс клана |
| `simpleclans.admin.bank.take` | Позволяет забрать деньги из баланса клана |
| `simpleclans.admin.bank.give` | Позволяет выдать деньги в баланс клана |
| `simpleclans.admin.bank.set` | Позволяет установить баланс клана |

### Другие разрешения

| Право | Описание |
| :--- | :--- |
| `simpleclans.other.kdr-exempt` | KDR игрока не будет изменяться из-за смерти/убийства \(см. известные проблемы\) |
| `simpleclans.vip.resetkdr` | Может очистить собственный KDR |

