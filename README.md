SimpleClans
==========

Full featured clan system for PVP Minecraft Servers!

[![Build Status](https://travis-ci.com/RoinujNosde/SimpleClans.svg?branch=master)](https://travis-ci.com/RoinujNosde/SimpleClans)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/71242)](https://www.spigotmc.org/resources/simpleclans.71242/)
[![Issues](https://img.shields.io/github/issues/RoinujNosde/SimpleClans.svg)](https://github.com/RoinujNosde/SimpleClans/issues)
[![Crowdin](https://badges.crowdin.net/simpleclans/localized.svg)](https://crowdin.com/project/simpleclans)
[![Discord](https://img.shields.io/discord/719557355917934613?label=discord&logo=discord)](https://discord.gg/CkNwgdE)

#### Documentation 

[Documentation!](https://simpleclans.gitbook.io/simpleclans/)
[Permissions!](https://simpleclans.gitbook.io/simpleclans/commands-and-permissions/permissions)
[Commands!](https://simpleclans.gitbook.io/simpleclans/commands-and-permissions/commands)

Spigot resource page: https://www.spigotmc.org/resources/simpleclans.71242/

#### Developers

Including SimpleClans with Maven:
```xml
<repositories>
    <repository>
        <id>bintray-roinujnosde-bukkit-plugins</id>
        <url>https://dl.bintray.com/roinujnosde/bukkit-plugins</url>
    </repository>
</repositories>
<dependencies>
    <dependency>
        <groupId>net.sacredlabyrinth.phaed.simpleclans</groupId>
        <artifactId>SimpleClans</artifactId>
        <version>2.12.2</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Using Gradle:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/roinujnosde/bukkit-plugins" 
    }
}
dependencies {
    compileOnly "net.sacredlabyrinth.phaed.simpleclans:SimpleClans:2.12.2"
}
```

Some examples on how to use the API: https://github.com/RoinujNosde/SimpleClans/wiki/SimpleClans-API-example
