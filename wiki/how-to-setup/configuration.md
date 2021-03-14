---
description: null
---

# Конфигурация

[Главный конфигурационный файл для SimpleClans можно найти здесь.](https://github.com/RoinujNosde/SimpleClans/blob/master/src/main/resources/config.yml)

## Главные настройки

* `enable-gui` - Включить GUI. 
* `disable-messages` - Отключить такие оповещения, как "Клан создан", "Клан расформирован", и другие. 
* `tameable-mobs-sharing` -  
* `teleport-blocks` -  
* `teleport-home-on-spawn` - При возрождении игроки будут телепортированы на базу клана. 
* `drop-items-on-clan-home` - При телепортации на базу клана, выкидывать предметы на землю. 
* `keep-items-on-clan-home` - При телепортации на базу клана, оставлять предметы. 
* `item-list` - Список предметов, используемый для keep- и drop- items-on-clan-home 
* `show-debug-info` -  
* `mchat-integration` -  
* `enable-auto-groups` -  
* `chat-compatibility-mode` -  
* `rival-limit-percent` -  
* `use-colorcode-from-prefix-for-name` -  
* `display-chat-tags` - Отображать ли тег клана в чате? 
* `unrivable-clans` - Кланы, которые с которыми невозможно начать вражду. 
* `show-unverified-on-list` - Показывать ли не подтвержденные кланы в списке кланов? 
* `blacklisted-worlds` -  
* `banned-players` - Игроки, которые не могут пользоваться командами плагина. 
* `disallowed-tags` - Запрещённые теги. 
* `language` - Язык по-умолчанию. 
* `user-language-selector` - Разрешить ли игрокам выбирать язык? 
* `disallowed-tag-colors` - Запрещенные цветовые кода для тега. 
* `server-name` - Название вашего сервера. 
* `new-clan-verification-required` -  
* `allow-regroup-command` - Разрешить ли команду для перегруппировки \(телепортации игроков к себе/на базу\)? 
* `allow-reset-kdr` - Разрешить сбросить КДР \(соотношение убийств к смертям\) 
* `rejoin-cooldown` -  
* `rejoin-cooldown-enabled` -  
* `min-to-verify` - Количество игроков в клане, чтобы можно было пройти подтверждение \(Модераторы могут обойти это ограничение\). 
* `ranking-type` - Допустимые параметры: ORDINAL или DENSE
  * `DENSE`: Если у игроков будет одинаковый KDR, их ранговая позиция будет одинаковой. Пример: 12234
  * `ORDINAL`: Каждый игрок будет иметь разную ранговую позицию.  Пример: 12345

#### Пример

```yaml
settings:
    enable-gui: true
    disable-messages: false
    tameable-mobs-sharing: false
    teleport-blocks: false
    teleport-home-on-spawn: false
    drop-items-on-clan-home: false
    keep-items-on-clan-home: false
    item-list: []
    show-debug-info: false
    mchat-integration: true
    enable-auto-groups: false
    chat-compatibility-mode: true
    rival-limit-percent: 50
    use-colorcode-from-prefix-for-name: true
    display-chat-tags: true
    unrivable-clans:
    - admin
    - staff
    - mod
    show-unverified-on-list: false
    blacklisted-worlds: []
    banned-players: []
    disallowed-tags:
    - vip
    - clan
    language: en
    language-per-player: false
    disallowed-tag-colors:
    - '4'
    server-name: '&4SimpleClans'
    new-clan-verification-required: true
    allow-regroup-command: true
    allow-reset-kdr: false
    rejoin-cooldown: 60
    rejoin-cooldown-enabled: false
    min-to-verify: 1
    ranking-type: DENSE
```

## Формат тегов

#### Пример

* `default-color` - Цвет по-умолчанию
* `max-length` - Максимальная длинна
* `bracket` - Скобки
  * `color` - Цвет
  * `leader-color` - Цвет лидера
  * `left` - Слева
  * `right` - Справа
* `min-length` - Минимальная длина
* `separator` - Разделитель
  * `color` - Цвет
  * `leader-color` - Цвет лидера
  * `char` - Символ

```yaml
tag:
    default-color: '8'
    max-length: 5
    bracket:
        color: '8'
        leader-color: '4'
        left: ''
        right: ''
    min-length: 2
    separator:
        color: '8'
        leader-color: '4'
        char: ' .'
```

## Главные команды

* `more` - больше
* `ally` - союзник
* `clan` - клан
* `accept` - принять
* `deny` - отклонить
* `global` - 
* `clan_chat` - чат клана
* `force-priority` - 

#### Пример

```yaml
commands:
    more: more
    ally: ally
    clan: clan
    accept: accept
    deny: deny
    global: global
    clan_chat: "."
    force-priority: true
```

## Защита от набивания KDR

* `enable-max-kills` - Включить максимальное количество убийств
* `max-kills-per-victim` - Максимальное количество убийств за жертву
* `enable-kill-delay` - Включить задержку перед убийствами
* `delay-between-kills` - Задержка перед убийствами

#### Пример

```yaml
kdr-grinding-prevention:
    enable-max-kills: false
    max-kills-per-victim: 10
    enable-kill-delay: false
    delay-between-kills: 5
```

## Список команд

* `size` - 
* `kdr` - 
* `name` - 
* `founded` - 
* `active` - 
* `asc` - 
* `desc` - 
* `default` - 

#### Пример

```yaml
list:
    size: size
    kdr: kdr
    name: name
    founded: founded
    active: active
    asc: asc
    desc: desc
    default: kdr
```

## Экономика

* `creation-price` - Цена создание
* `purchase-clan-create` - 
* `verification-price` - Цена подтверждения
* `purchase-clan-verify` - 
* `invite-price` - Цена приглашения
* `purchase-clan-invite` - 
* `home-teleport-price` - Цена телепортации на базу клана
* `purchase-home-teleport` - Цена за установку точки базы клана.
* `home-teleport-set-price` - 
* `purchase-home-teleport-set` - 
* `home-regroup-price` - 
* `purchase-home-regroup` - 
* `unique-tax-on-regroup` - 
* `issuer-pays-regroup` - 
* `money-per-kill` - Вознаграждение за убийство.
* `money-per-kill-kdr-multipier` - 
* `purchase-reset-kdr` - 
* `reset-kdr-price` - Цена сброса KDR.
* `purchase-member-fee-set` - Цена за установку комиссии на участников.
* `member-fee-set-price` - Комиссия на участников.
* `member-fee-enabled` - Включить ли комиссию на участников?
* `max-member-fee` - Максимальная комиссия участников.
* `upkeep` - Коммисия на поддержание клана.
* `upkeep-enabled` - Включить ли комиссию на поддержание клана?
* `multiply-upkeep-by-clan-size` - 
* `charge-upkeep-only-if-member-fee-enabled` - 

#### Пример

```yaml
economy:
    creation-price: 100.0
    purchase-clan-create: false
    verification-price: 1000.0
    purchase-clan-verify: false
    invite-price: 20.0
    purchase-clan-invite: false
    home-teleport-price: 5.0
    purchase-home-teleport: false
    home-teleport-set-price: 5.0
    purchase-home-teleport-set: false
    home-regroup-price: 5.0
    purchase-home-regroup: false
    unique-tax-on-regroup: true
    issuer-pays-regroup: true
    money-per-kill: false
    money-per-kill-kdr-multipier: 10
    purchase-reset-kdr: true
    reset-kdr-price: 10000.0
    purchase-member-fee-set: false
    member-fee-set-price: 1000.0
    member-fee-enabled: false
    max-member-fee: 200.0
    upkeep: 200.0
    upkeep-enabled: false
    multiply-upkeep-by-clan-size: false
    charge-upkeep-only-if-member-fee-enabled: true
```

## Вес убийств

* `rival` - соперник/противник
* `civilian` - мирный/без клана
* `neutral` - нейтрал
* `deny-same-ip-kills` - Отменять убийства с одного айпи адреса

#### Пример

```yaml
kill-weights:
    rival: 2.0
    civilian: 0.0
    neutral: 1.0
    deny-same-ip-kills: false
```

## Настройки клана

* `homebase-teleport-wait-secs` - Задержка перед телепортацией на точку базы клана
* `homebase-can-be-set-only-once` - Точка базы клана может быть установлена единожды
* `min-size-to-set-rival` - 
* `max-length` - Максимальная длина названия клана.
* `max-description-length` - Максимальная длина описания клана
* `min-description-length` - Минимальная длина описания клана.
* `max-members` - Максимальное количество участников
* `confirmation-for-promote` - 
* `trust-members-by-default` - Подтверждать участников по умолчанию?
* `confirmation-for-demote` - 
* `percentage-online-to-demote` - 
* `ff-on-by-default` - Глобальный "Огонь по своим" включен по умолчанию.
* `min-length` - Минимальная длина названия клана.
* `min-size-to-set-ally` - 

#### Пример

```yaml
clan:
    homebase-teleport-wait-secs: 10
    homebase-can-be-set-only-once: true
    min-size-to-set-rival: 3
    max-length: 25
    max-description-length: 120
    min-description-length: 10
    max-members: 25
    confirmation-for-promote: false
    trust-members-by-default: false
    confirmation-for-demote: false
    percentage-online-to-demote: 100.0
    ff-on-by-default: false
    min-length: 2
    min-size-to-set-ally: 3
```

## Запланированные задачи

* `collect-upkeep` - Сбор комиссии на поддержание клана
  * `hour` - час
  * `minute` - минуты
* `collect-upkeep-warning` - Предупреждение о сборе комисии на поддержание клана
  * `hour` - час 
  * `minute` - минуты 
* `collect-fee` - Сбор комиссии
  * `hour` - час
  * `minute` - минуты

#### Пример

```yaml
tasks:
    collect-upkeep:
        hour: 1
        minute: 30
    collect-upkeep-warning:
        hour: 12
        minute: 0
    collect-fee:
        hour: 1
        minute: 0
```

## Страницы

* `untrusted-color` - Цвет неподтвержденного клана
* `clan-name-color` - Цвет названия клана
* `subtitle-color` - Цвет подзаголовков
* `headings-color` - Цвет заголовков
* `trusted-color` - Цвет подтвержденного клана
* `leader-color` - Цвет лидера
* `separator` - Разделитель
* `size` - Размер

#### Пример

```yaml
page:
    untrusted-color: '8'
    clan-name-color: b
    subtitle-color: '7'
    headings-color: '8'
    trusted-color: f
    leader-color: '4'
    separator: '-'
    size: 100
```

## Чат клана

* `enable` - Включить
* `tag-based-clan-chat` - 
* `announcement-color` - 
* `format` - Формат отображения чата
* `rank` - Формат отображения ранга
* `leader-color:` - Цвет лидера в чате
* `trusted-color` - Цвет подтвержденного участника в чате 
* `member-color` - Цвет участника в чате

#### Пример

```yaml
clanchat:
    enable: true
    tag-based-clan-chat: false
    announcement-color: e
    format: "&b[%clan%&b] &4<%nick-color%%player%&4> %rank%: &b%message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Запросы

* `message-color` - Цвет сообщений
* `ask-frequency-secs` - 
* `max-asks-per-request` - 

#### Пример

```yaml
request:
    message-color: b
    ask-frequency-secs: 60
    max-asks-per-request: 1440
```

## Доска объявлений клана

* `color` - Цвет
* `accent-color` - 
* `show-on-login` - Показывать при заходе?
* `size` - Размер
* `login-size` - Размер при входе

#### Пример

```yaml
bb:
    color: e
    accent-color: '8'
    show-on-login: true
    size: 6
    login-size: 6
```

## Союзный чат

* `enable` - Включить
* `format` - Формат чата
* `rank` - Формат ранга
* `leader-color` - Цвет лидера
* `trusted-color` - Цвет подтвержденного участника
* `member-color` - Цвет участника

#### Пример

```yaml
allychat:
    enable: true
    format: "&b[Ally Chat] &4<%clan%&4> <%nick-color%%player%&4> %rank%: &b%message%"
    rank: "&f[%rank%&f]"
    leader-color: '4'
    trusted-color: 'f'
    member-color: '7'
```

## Очистка данных

* `inactive-player-data-days` - Через сколько дней будут очищены данные о неактивных игроках?
* `inactive-clan-days` - Через сколько дней будут очищены не активные кланы?
* `unverified-clan-days` - Через сколько дней будут очищены не активные и не подтвержденные кланы?

#### Пример

```yaml
purge:
    inactive-player-data-days: 30
    inactive-clan-days: 7
    unverified-clan-days: 2
```

## MySQL настройки

* `username` - Логин
* `host` - Хост
* `port` - Порт
* `enable` - Включить?
* `password` - Пароль
* `database` - База данных

#### Пример

```yaml
mysql:
    username: ''
    host: localhost
    port: 3306
    enable: false
    password: ''
    database: ''
```

## Разрешения

* `auto-group-groupname` - 

#### Пример

```yaml
permissions:
  auto-group-groupname: false
  YourClanNameHere:
  - test.permission
```

## Производительность

* `save-periodically` - Плагин будет периодически сохранять данные, а не сразу. **Рекомендуется** установить данный параметр на `true`. 
* `save-interval` - Интервал **в минутах**, в течение которого изменения записываются в базу данных. 
* `use-threads` - Плагин не будет использовать основной поток для подключения к БД. **Рекомендуется** установить данный параметр на `true`. 
* `use-bungeecord` - Использовать bungeecord? \(WIP\)

#### Пример

```yaml
performance:
  save-periodically: true
  save-interval: 10
  use-threads: true
  use-bungeecord: false
```

## Защита мирных игроков

* `safe-civilians` - Мирные игроки\(без клана\) будут защищены от PVP.

#### Пример

```yaml
safe-civilians: false
```

