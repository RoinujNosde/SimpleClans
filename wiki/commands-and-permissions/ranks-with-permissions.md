# 🏅 Ranks with Permissions

## How does it work?

Leaders have the ability to create ranks and give them permissions to perform several actions inside the clan. Unlike `/clan trust`, ranks with permissions allows leaders to have a finer control of who can do what. Ex.: A rank can handle invites (`invite` permission), another takes care of moderation (`kick` permission), etc

## Suggested ranks

| Ranks          | Description                                   |                                          Permission                                         |
| -------------- | --------------------------------------------- | :-----------------------------------------------------------------------------------------: |
| **Coleader**   | Helps take care of the clan                   |                                            `all`                                            |
| **Treasurer**  | Manages the clan's bank account               |                       `bank.balance`, `bank.deposit`, `bank.withdraw`                       |
| **Ambassador** | Manages the clan's relations with other clans | `ally.chat`, `ally.add`, `ally.remove`, `rival.add`, `rival.remove`, `war.end`, `war.start` |
| **Recruit**    | A member in trial                             |                         `stats`, `kills`, `mostkilled`, `rank.list`                         |

## Ranks Commands

| Command                                             | Description                                                                  |
| --------------------------------------------------- | ---------------------------------------------------------------------------- |
| `/clan rank create`                                 | Creates a rank with this name                                                |
| `/clan rank setdisplayname [rank] [displayname]`    | Sets the display name of the rank (it can be more than one word and colored) |
| `/clan rank assign (player) (rank)`                 | Assigns a user to a rank                                                     |
| `/clan rank unassign (player)`                      | Unassigns a user from a rank                                                 |
| `/clan rank delete (rank)`                          | Deletes a rank                                                               |
| `/clan rank list`                                   | Lists the clan's ranks                                                       |
| `/clan rank permissions`                            | Lists the available permissions for ranks                                    |
| `/clan rank permissions (rank)`                     | Lists the rank's permissions                                                 |
| `/clan rank permissions add (rank) (permission)`    | Adds a permission to the rank                                                |
| `/clan rank permissions remove (rank) (permission)` | Removes a permission from the rank                                           |

## Available permissions for ranks

A player can view those permissions in-game using `/clan rank permissions`

| Rank Permission    | Description                       |
| ------------------ | --------------------------------- |
| `ally.add`         | can add an ally                   |
| `ally.remove`      | can remove an ally                |
| `ally.chat`        | can use ally chat                 |
| `bank.balance`     | can view the bank balance         |
| `bank.deposit`     | can deposit money                 |
| `bank.withdraw`    | can withdraw money                |
| `bb.add`           | can add a message to bb           |
| `bb.clear`         | can clear the bb                  |
| `coords`           | can view the clan's coords        |
| `fee.enable`       | can enable the member fee         |
| `fee.set`          | can change the fee value          |
| `home.regroup`     | can regroup the clan              |
| `home.set`         | can set the clan home             |
| `home.tp`          | can tp to the clan home           |
| `invite`           | can invite someone to the clan    |
| `kick`             | can kick someone from the clan    |
| `modtag`           | can modify the clan tag           |
| `rank.displayname` | can modify a rank's display name  |
| `rank.list`        | can list the ranks                |
| `rival.add`        | can add a rival                   |
| `rival.remove`     | can remove a rival                |
| `war.end`          | can end a war                     |
| `war.start`        | can start a war                   |
| `vitals`           | can view the clan's vitals        |
| `stats`            | can view the clan's stats         |
| `kills`            | can view his or other's kills     |
| `mostkilled`       | can view the mostkilled           |
| `description`      | can change the clan's description |

## Permissions to use the rank commands

| Permission                                      | Description                          |
| ----------------------------------------------- | ------------------------------------ |
| `simpleclans.leader.rank.assign`                | Can assign a rank to a user          |
| `simpleclans.leader.rank.unassign`              | Can unassign a player from a rank    |
| `simpleclans.leader.rank.create`                | Can create a new rank                |
| `simpleclans.leader.rank.delete`                | Can delete a new rank                |
| `simpleclans.leader.rank.list`                  | Can list all the ranks               |
| `simpleclans.leader.rank.setdisplayname`        | Can set the display name of the rank |
| `simpleclans.leader.rank.permissions.add`       | Can add permissions to a rank        |
| `simpleclans.leader.rank.permissions.available` | Can list all available permissions   |
| `simpleclans.leader.rank.permissions.list`      | Can list the rank's permissions      |
| `simpleclans.leader.rank.permissions.remove`    | Can remove permissions from a rank   |
