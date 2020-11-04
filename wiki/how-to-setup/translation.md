---
description: null
---

# Translation

## Editing the plugin's messages

### Requirements:

* A ZIP explorer program \(such as [WinRAR](https://www.win-rar.com/download.html?&L=0) or [7-Zip](https://www.7-zip.org/download.html)\)
* A Text editor like [NotePad++](https://notepad-plus-plus.org/downloads/), [Visual Studio Code](https://code.visualstudio.com/) or [Sublime Text](https://www.sublimetext.com/).

### Step by step

1. Right click the plugin jar, click on "Open as" and choose the ZIP program;
2. Copy the messages file corresponding to your language to the plugin's folder;
   1. If there isn't one for your language, copy "messages.properties" instead and rename it with your language code appended. For example, if your language is Russian: `messages_ru_RU.properties`
3. Edit the messages and save;
4. Change `language` in config.yml to your language code;
5. Reload the plugin using /clan reload.

### A little trick

If you like the current translation, but want to change only a few lines: 1. Copy the file to the plugin's folder; 2. Delete all messages except the ones you would like to edit; 3. Edit them and save.

> **Note**: Please note that your custom `messages.properties` does not automatically update when new messages are added.

## Share your translations!

SimpleClans has a project on [Crowdin](https://crowdin.com/project/simpleclans). There you can translate the messages and suggest corrections!

> **Note**: if you have an old `language.yml` file for SimpleClans that you translated to your language, [open an issue here](https://github.com/RoinujNosde/SimpleClans/issues?q=is%3Aissue+is%3Aopen+sort%3Aupdated-desc), I will convert the file and add it to the plugin.

