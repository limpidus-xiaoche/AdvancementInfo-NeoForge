# AdvancementInfo (NeoForge 1.21.1 Port)

This is a port of **[AdvancementInfo](https://github.com/wafarm/AdvancementInfo)** (by Giselbaer / gbl, maintained by wafarm) from **Fabric 1.21** to **Minecraft 1.21.1 NeoForge**.

**Original repository:** https://github.com/wafarm/AdvancementInfo — all credits for the mod go to the original author.

## Features

- Makes it easier to see which advancements you have and what's missing
- Hover an advancement to show its requirements in the info pane on the right
- Click an advancement to pin its requirements and scroll through them
- Search box to find advancements by title or description
- The info pane and margins are configurable via a config file

## Changes from the original Fabric version

- Ported from Fabric (yarn mappings) to NeoForge 1.21.1 (mojmap mappings)
- Removed the cloth-config dependency; replaced with a lightweight `config/advancementinfo.properties` config file
- Removed the ModMenu integration (not available on NeoForge)

## Config

The config file is written to `config/advancementinfo.properties` on first launch:

```properties
colorHave=0xaa00
colorHaveNot=0xaa0000
marginX=30
marginY=30
infoWidth.min=120
infoWidth.max=300
infoWidth.percent=30
```

## Building

This port keeps an offline build script (no Gradle / maven.neoforged.net required).
It uses the local NeoForge 1.21.1 runtime libraries in `.minecraft/libraries` plus a JDK 21:

```powershell
powershell -ExecutionPolicy Bypass -File build.ps1
```

The built jar appears at `build/AdvancementInfo-1.21.1-1.4.1.jar`.

## License

[MIT](LICENSE) — same as the original project.
