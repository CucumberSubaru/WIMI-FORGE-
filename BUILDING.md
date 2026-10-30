# Where Is My Inventory?! — Forge 1.21.1

## Requirements

- Minecraft 1.21.1
- Forge 52.1.0 or newer within the 52.x series
- Java 21

Install `backpackinv-forge-1.5.0.jar` into the client's `mods` folder.
Do not install the NeoForge edition alongside it.
The backpack is a client-side visual effect; it registers no gameplay items.

## Build

Windows: `gradlew.bat build`

Linux/macOS: `bash gradlew build`

The output is `build/libs/backpackinv-forge-1.5.0.jar`.
The wrapper uses Gradle 8.11; ForgeGradle is pinned to 6.0.54.
GitHub Actions uses the same wrapper and uploads the JAR as an artifact.
The first build needs internet access to download Minecraft and Forge dependencies.

## Changes to the supplied Forge port

- Removed the `jar.finalizedBy('reobfJar')` reference: this Forge configuration does not create that task.
- Added Gradle Wrapper and updated CI to use it.
- Unified development classes/resources output so Forge discovers the mod during `runClient`.
- Restored the original client-only model approach instead of registering backpack items.
- Restricted metadata to the Minecraft version targeted and compiled by this project.
- Preserved the supplied models, texture, animation settings, and credits.

## Manual gameplay check

Local validation: compilation and JAR packaging passed; the Forge 52.1.0 development
client loaded a singleplayer world without mod/model loading errors. Full animation
behavior and multiplayer compatibility still need gameplay testing.

Open a survival world in first person. Press E, check the backpack and lid animation,
close the inventory, and check the lowering animation. Repeat with an item in each hand.
Check disconnecting/rejoining and joining a server without this cosmetic mod.
Settings live in `config/backpackinv-config.json`.
