---
description: null
---

# Placeholders

## Available placeholders

| Placeholder | Description |
| :--- | :--- |
| %simpleclans\_neutral\_kills% |  |
| %simpleclans\_rival\_kills% |  |
| %simpleclans\_civilian\_kills% |  |
| %simpleclans\_total\_kills% |  |
| %simpleclans\_weighted\_kills% |  |
| %simpleclans\_deaths% |  |
| %simpleclans\_kdr% |  |
| %simpleclans\_in\_clan% |  |
| %simpleclans\_is\_leader% |  |
| %simpleclans\_is\_trusted% |  |
| %simpleclans\_is\_member% |  |
| %simpleclans\_is\_bb\_enabled% |  |
| %simpleclans\_is\_usechatshortcut% |  |
| %simpleclans\_is\_allychat% |  |
| %simpleclans\_is\_clanchat% |  |
| %simpleclans\_is\_globalchat% |  |
| %simpleclans\_is\_cape\_enabled% |  |
| %simpleclans\_is\_tag\_enabled% |  |
| %simpleclans\_is\_friendlyfire\_on% |  |
| %simpleclans\_is\_muted% |  |
| %simpleclans\_is\_mutedally% |  |
| %simpleclans\_join\_date% | Month, Day, Year, Hour |
| %simpleclans\_inactive\_days% |  |
| %simpleclans\_lastseen% |  |
| %simpleclans\_lastseendays% |  |
| %simpleclans\_tag% | lowerscored Clan tag without color |
| %simpleclans\_tag\_label% | Clan tag with correct mayus, color and at the end &c |
| %simpleclans\_rank% |  |
| %simpleclans\_rank\_displayname% |  |
| %simpleclans\_clanchat\_player\_color% |  |
| %simpleclans\_allychat\_player\_color% |  |
| %simpleclans\_clan\_total\_neutral% |  |
| %simpleclans\_clan\_total\_civilian% |  |
| %simpleclans\_clan\_total\_rival% |  |
| %simpleclans\_clan\_total\_kills% |  |
| %simpleclans\_clan\_total\_deaths% |  |
| %simpleclans\_clan\_total\_kdr% |  |
| %simpleclans\_clan\_average\_wk% |  |
| %simpleclans\_clan\_leader\_size% |  |
| %simpleclans\_clan\_balance% |  |
| %simpleclans\_clan\_allow\_withdraw% |  |
| %simpleclans\_clan\_allow\_deposit% |  |
| %simpleclans\_clan\_size% |  |
| %simpleclans\_clan\_name% |  |
| %simpleclans\_clan\_color\_tag% |  |
| %simpleclans\_clan\_tag% |  |
| %simpleclans\_clan\_founded% |  |
| %simpleclans\_clan\_friendly\_fire% |  |
| %simpleclans\_clan\_is\_unrivable% |  |
| %simpleclans\_clan\_is\_anyonline% |  |
| %simpleclans\_clan\_is\_verified% |  |
| %simpleclans\_clan\_capeurl% |  |
| %simpleclans\_clan\_inactivedays% |  |
| %simpleclans\_clan\_onlinemembers\_count% |  |
| %simpleclans\_clan\_allies\_count% |  |
| %simpleclans\_clan\_rivals\_count% |  |

#### Top Clans Placeholders

The top clans is a group of placeholders that helps you create leaderboards.  
You can put `_topclans_#_` in any existing clan placeholder to get the value for the clan in a specific rank position.  
Ex.:  
`%simpleclans_topclans_1_clan_name%` - this will return the name of the Clan in the first position.

#### Top Players Placeholders

This one is another group of placeholders to create leaderboards. As the name suggests, it's about players and not clans, but they work similarly.  
Adding `_topplayers_#_` to existing player placeholders will get the value for the Player in the specified rank.  
Ex.:  
`%simpleclans_topplayers_2_tag_label%` - this will return the tag label for the Player in the second position.

#### Relational placeholders

Relational placeholders return values based on the relation between 2 players. One example is the tab, there is the player viewing it and the players listed there  
Currently the only relational placeholder available on SimpleClans is: `%rel_simpleclans_color%`   
This returns a color based on rivalries/alliances.  
Allies: aqua  
Rivals: red  
Same clan: green

The colors above can be changed in PlaceholderAPI's config.

#### HolographicDisplays

If you intend to use these placeholders with HolographicDisplays, remember to [download ](https://www.spigotmc.org/resources/18461/)the extension.

