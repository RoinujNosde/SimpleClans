# 📌 Commands

> Required arguments are marked with `(argument)`&#x20;
>
> Optional arguments are marked with `[argument]`

You can check any command description or requirements using: `/clan help [`command`]`

## Anyone Commands

| Command                                                     | Description                            | Verified only |
| ----------------------------------------------------------- | -------------------------------------- | ------------- |
| `/clan`                                                     | Opens the GUI or shows the help        | No            |
| `/clan help`                                                | Shows the plugin's commands            | No            |
| `/clan create [tag] [name]`                                 | Creates a new clan                     | No            |
| `/accept`                                                   | Accepts a request                      | No            |
| `/deny`                                                     | Denies a request                       | No            |
| `/more`                                                     | Shows more information                 | No            |
| `/clan leaderboard`                                         | Shows the leaderboard                  | No            |
| `/clan list [name\|size\|kdr\|founded\|active] [asc\|desc]` | Lists all clans                        | No            |
| `/clan rivalries`                                           | Shows all clan rivalries               | No            |
| `/clan alliances`                                           | Shows all clan alliances               | No            |
| `/clan lookup [player]`                                     | Looks up your or another player's info | No            |
| `/clan profile [tag]`                                       | Shows another clan's profile           | Yes\*         |
| `/clan roster [tag]`                                        | Shows another clan's roster            | Yes\*         |
| `/clan ff (allow\|auto)`                                    | Toggles personal friendly fire         | No            |
| `/clan resetkdr`                                            | Resets your KDR                        | No            |
| `/clan toggle invite`                                       | Toggles personal clan invites          | No            |

\\\* The target clan

## Members Commands

### General Commands

| Command                  | Description                                        | Verified only |
| ------------------------ | -------------------------------------------------- | ------------- |
| `/clan kills [player]`   | Shows your or another player's kill counts         | Yes           |
| `/clan toggle`           | Toggles personal settings                          | Yes           |
| `/clan mostkilled`       | Shows server-wide most killed counts               | Yes           |
| `/clan resign`           | Resigns from the clan                              | No            |
| `/clan fee check`        | Checks if the fee is enabled and how much it costs | Yes           |
| `/clan vitals`           | Shows your clan's vitals                           | Yes           |
| `/clan stats`            | Shows your clan's stats                            | Yes           |
| `/clan profile`          | Shows your clan's profile                          | Yes           |
| `/clan roster`           | Shows your clan's roster                           | Yes           |
| `/clan coords`           | Shows your clan's coords                           | Yes           |
| `/clan bb`               | Shows your clan's bulletin board                   | Yes           |
| `/clan bb add (message)` | Writes a message to your clan's bulletin board     | Yes           |

### Chat Commands

| Command                     | Description                         | Verified only |
| --------------------------- | ----------------------------------- | ------------- |
| `/. (message)`              | Sends a message to your clan's chat | No            |
| `/. [join\|leave\|mute]`    | Joins/leaves/mutes your clan's chat | No            |
| `/ally (message)`           | Sends a message to the ally chat    | No            |
| `/ally [join\|leave\|mute]` | Joins/leaves/mutes the ally chat    | No            |

### Land Commands

You can find land commands [here](../integration/land-claiming.md).

### Bank Commands

| Command                        | Description                                          | Verified only |
| ------------------------------ | ---------------------------------------------------- | ------------- |
| `/clan bank status`            | Checks your clans bank balance                       | Yes           |
| `/clan bank withdraw (amount)` | Withdraws an amount from your clans bank             | Yes           |
| `/clan bank withdraw all`      | Withdraws all from your clans bank                   | Yes           |
| `/clan bank deposit (amount)`  | Deposits an amount of your money into your clan bank | Yes           |
| `/clan bank deposit all`       | Deposits all of your money into your clan bank       | Yes           |

## Leaders Commands

| Command                                             | Description                                                                  | Verified only |
| --------------------------------------------------- | ---------------------------------------------------------------------------- | ------------- |
| `/clan description (description)`                   | Modifies the clan's description                                              | Yes           |
| `/clan invite (player)`                             | Invites a player                                                             | No            |
| `/clan kick (player)`                               | Kicks a player from the clan                                                 | No            |
| `/clan trust (player)`                              | Sets a member as trusted                                                     | No            |
| `/clan untrust (player)`                            | Sets a member as untrusted                                                   | No            |
| `/clan promote (member)`                            | Promotes a member to leader                                                  | No            |
| `/clan demote (leader)`                             | Demotes a leader to member                                                   | No            |
| `/clan setbanner`                                   | Sets the clan's banner                                                       | Yes           |
| `/clan modtag (tag)`                                | Modifies your clan's tag (only colors and case)                              |               |
| `/clan clanff (allow\|block)`                       | Toggles clan's friendly fire                                                 | No            |
| `/clan war (start\|end) (tag)`                      | Starts or ends a war                                                         | Yes           |
| `/clan rival (add\|remove) (tag)`                   | Adds or removes a rival                                                      | Yes           |
| `/clan ally (add\|remove) (tag)`                    | Adds or removes an ally                                                      | Yes           |
| `/clan verify`                                      | Verifies your clan                                                           | No            |
| `/clan disband`                                     | Disbands your clan                                                           | No            |
| `/clan fee set (amount)`                            | Sets the clan's member fee                                                   | No            |
| `/clan regroup me`                                  | Regroups your clan members to your location                                  | Yes           |
| `/clan regroup home`                                | Regroups your clan members to your clan's home                               | Yes           |
| `/clan home`                                        | Teleports to your clan's home                                                | Yes           |
| `/clan home clear`                                  | Clears your clan's home                                                      | Yes           |
| `/clan home set`                                    | Sets your clan's home                                                        | Yes           |
| `/clan rank create`                                 | Creates a rank                                                               | Yes           |
| `/clan rank setdisplayname (rank) (displayname)`    | Sets the display name of the rank (it can contain colors and multiple words) | Yes           |
| `/clan rank assign (player) (rank)`                 | Assigns a user to a rank                                                     | Yes           |
| `/clan rank unassign (player)`                      | Unassigns a user from a rank                                                 | Yes           |
| `/clan rank delete (rank)`                          | Deletes a rank                                                               | Yes           |
| `/clan rank list`                                   | Lists the clan's ranks                                                       | Yes           |
| `/clan rank permissions`                            | Lists the available permissions for ranks                                    | Yes           |
| `/clan rank permissions (rank)`                     | Lists the rank's permissions                                                 | Yes           |
| `/clan rank permissions add (rank) (permission)`    | Adds a permission to the rank                                                | Yes           |
| `/clan rank permissions remove (rank) (permission)` | Removes a permission from the rank                                           | Yes           |
| `/clan discord create`                              | Creates a discord channel for your clan                                      | Yes           |
| `/clan bb clear`                                    | Clears your clan's bulletin board                                            | Yes           |

## Mod Commands

| `/clan mod modtag (clan) (tag)`       | Changes colors or case of a clan's tag  |
| ------------------------------------- | --------------------------------------- |
| `/clan mod place (player) (new clan)` | Places a player in a clan               |
| `/clan mod home set (tag)`            | Sets a clan's home                      |
| `/clan mod home tp (tag)`             | Teleports to a clan's home              |
| `/clan mod ban (player)`              | Bans a player from clan commands        |
| `/clan mod unban (player)`            | Unbans a player from clan commands      |
| `/clan mod globalff (allow\|auto)`    | Toggles the global friendly-fire status |
| `/clan mod verify (tag)`              | Verifies a clan                         |
| `/clan mod disband (tag)`             | Disbands a clan                         |
| `/clan mod locale (player) (locale)`  | Sets a player's locale                  |
| `/clan mod rename (clan) (tag)`       | Renames a clan                          |

## Admin Commands

| Command                                  | Description                                                                        |
| ---------------------------------------- | ---------------------------------------------------------------------------------- |
| `/clan admin reload`                     | Reloads the plugin and its configuration (some features may need a server restart) |
| `/clan admin purge`                      | Purges a player's data                                                             |
| `/clan admin resetkdr everyone`          | Resets everyone's KDR                                                              |
| `/clan admin resetkdr (player)`          | Resets a player's KDR                                                              |
| `/clan admin demote (player)`            | Demotes a leader from any clan                                                     |
| `/clan admin promote (player)`           | Promotes a member from any clan                                                    |
| `/clan admin permanent (clan)`           | Toggles permanent status of any clan                                               |
| `/clan admin bank give (clan) (amount)`  | Gives an amount of money to a clan's bank                                          |
| `/clan admin bank take (clan) (amount)`  | Takes an amount of money from a clan's bank                                        |
| `/clan admin bank set (clan) (amount)`   | Sets an amount of money to a clan's bank                                           |

