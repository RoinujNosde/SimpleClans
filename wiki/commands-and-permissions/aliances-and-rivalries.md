---
description: null
---

# Clan Alliances and Rivalries

Any clan leader can send an request to start an alliance with any other clan with `/clan ally add`. If the request is accepted by a leader of the second clan, the alliance is formed. The alliance can be broken by any leader of either clan at any time with `/clan ally remove`, no one needs to accept the removal of an alliance.

Clan rivalries can be started by any clan at any time, no request is needed, rivalries are automatically formed once a clan leader decides he wants one by using `/clan rival add`. If someone has pissed you off and you want them as rivals, their permission is not needed. To break a clan rivalry on the other hand, you need the acceptance of the other clan, you must use `/clan rival remove` to send the other clan a request, once one of their leaders accept the rivalry is broken.

You can view a list of all clans and their allies with the `/clan alliances` command, or their rivals with the `/clan rivalries` command.

## Commands

| Commands                   | Description                                                     |
|:---------------------------|:----------------------------------------------------------------|
| `/clan ally add [tag]`     | Send an request to start an alliance \(acceptance is required\) |
| `/clan ally remove [tag]`  | Remove alliance \(no acceptance is required\)                   |
| `/clan rival add [tag]`    | Starting a rivalry \(no acceptance is required\)                |
| `/clan rival remove [tag]` | Remove a rivalry \(acceptance is required\)                     |
| `/clan alliances`          | List all clans and their allies                                 |
| `/clan rivalries`          | List all clans and their rivals                                 |

## Permissions

| Permission                     | Description                              |
|:-------------------------------|:-----------------------------------------|
| `simpleclans.member.ally`      | Can use ally chat                        |
| `simpleclans.leader.ally`      | Can ally his clan with other simpleclans |
| `simpleclans.leader.rival`     | Can start a rivalry with another clan    |
| `simpleclans.anyone.alliances` | Can view alliances by clan               |
| `simpleclans.anyone.rivalries` | Can view rivalries by clan               |

