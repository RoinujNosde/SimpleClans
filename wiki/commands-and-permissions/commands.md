---
description: null
---

# Commands

> Required arguments are marked with `(argument)` 
>
> Optional arguments are marked with `[argument]`

## Anyone Commands

| Command | Description | Verified only |
| :--- | :--- | :--- |
| `/clan` | Opens the GUI or shows the help | No |
| `/clan help` | Shows the plugin's commands | No |
| `/clan create [tag] [name]` | Creates a new clan | No |
| `/accept` | Accepts a request | No |
| `/deny` | Denies a request | No |
| `/more` | Shows more information | No |
| `/clan leaderboard` | Shows the leaderboard | No |
| `/clan list [name|size|kdr|founded|active] [asc|desc]` | Lists all clans | No |
| `/clan rivalries` | Shows all clan rivalries | No |
| `/clan alliances` | Shows all clan alliances | No |
| `/clan lookup [player]` | Looks up your or another player's info | No |
| `/clan profile [tag]` | Shows another clan's profile | Yes\* |
| `/clan roster [tag]` | Shows another clan's roster | Yes\* |
| `/clan ff (allow|auto)` | Toggles personal friendly fire | No |
| `/clan resetkdr` | Resets your KDR | No |

\\* The target clan

## Members Commands

### General Commands

| Command | Description | Verified only |
| :--- | :--- | :--- |
| `/clan kills [player]` | Shows your or another player's kill counts | Yes |
| `/clan toggle` | Toggles personal settings | Yes |
| `/clan mostkilled` | Shows server-wide most killed counts | Yes |
| `/clan resign` | Resigns from the clan | No |
| `/clan fee check` | Checks if the fee is enabled and how much it costs | Yes |

### Chat Commands

| Command | Description | Verified only |
| :--- | :--- | :--- |
| `/. (message)` | Sends a message to your clan's chat | No |
| `/. [join|leave|mute]` | Joins/leaves/mutes your clan's chat | No |
| `/ally (message)` | Sends a message to the ally chat | No |
| `/ally [join|leave|mute]` | Joins/leaves/mutes the ally chat | No |

## Leaders Commands

| Command | Description | Verified only |
| :--- | :--- | :--- |
| `/clan description (description)` | Modifies the clan's description | Yes |
| `/clan invite (player)` | Invites a player | No |
| `/clan kick (player)` | Kicks a player from the clan | No |
| `/clan trust (player)` | Sets a member as trusted | No |
| `/clan untrust (player)` | Sets a member as untrusted | No |
| `/clan promote (member)` | Promotes a member to leader | No |
| `/clan demote (leader)` | Demotes a leader to member | No |
| `/clan setbanner` | Sets the clan's banner | Yes |
| `/clan clanff (allow|block)` | Toggles clan's friendly fire | No |
| `/clan war (start|end) (tag)` | Starts or ends a war | Yes |
| `/clan disband` | Disbands your clan | No |
| `/clan fee set (amount)` | Sets the clan's member fee | No |
| `/clan rank create` | Creates a rank | Yes |
| `/clan rank setdisplayname (rank) (displayname)` | Sets the display name of the rank \(it can contain colors and multiple words\) | Yes |
| `/clan rank assign (player) (rank)` | Assigns a user to a rank | Yes |
| `/clan rank unassign (player)` | Unassigns a user from a rank | Yes |
| `/clan rank delete (rank)` | Deletes a rank | Yes |
| `/clan rank list` | Lists the clan's ranks | Yes |
| `/clan rank permissions` | Lists the available permissions for ranks | Yes |
| `/clan rank permissions (rank)` | Lists the rank's permissions | Yes |
| `/clan rank permissions add (rank) (permission)` | Adds a permission to the rank | Yes |
| `/clan rank permissions remove (rank) (permission)` | Removes a permission from the rank | Yes |

## Mod Commands

| Command | Description |
| :--- | :--- |
| `/clan place (player) (new clan)` | Places a player in a clan |
| `/clan home set (tag)` | Sets a clan's home |
| `/clan home tp (tag)` | Teleports to a clan's home |
| `/clan ban (player)` | Bans a player from clan commands |
| `/clan unban (player)` | Unbans a player from clan commands |
| `/clan globalff (allow|auto)` | Toggles the global friendly-fire status |
| `/clan verify (tag)` | Verifies a clan |
| `/clan disband (tag)` | Disbands a clan |

## Admin Commands

| Command | Description |
| :--- | :--- |
| `/clan reload` | Reloads the plugin and its configuration \(some features may need a server restart\) |
| `/clan purge` | Purges a player's data |
| `/clan resetkdr everyone` | Resets everyone's KDR |
| `/clan resetkdr (player)` | Resets a player's KDR |
| `/clan admin demote (player)` | Demotes a leader from any clan |
| `/clan admin promote (player)` | Promotes a member from any clan |



