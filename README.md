# Landen's Essentials

**Warning:** This plugin was vibe-coded. Do not use for professional Minecraft servers.

A bloat-free PaperMC plugin providing teleport requests, endermen grief prevention, and other QOL features for Minecraft 1.20+.

## Features

### 1. Teleportation Request System
- **`/tpa`**: Displays available teleport commands. 
- **`/tpa <username>`**: Send a teleport request to the player with the username provided.
- **`/tpaccept`**: Accepts a pending teleport request.
- **`/tpadeny`**: Denies a pending teleport request.
- **`/tpahere <username>`**: Requests a target player to teleport to your current location.

### 2. Endermen Grief Prevention
- Disables Endermen from picking up or placing blocks across all server worlds.
- Configurable via `endermen.prevent-griefing: true` in `config.yml`.

### 3. Message of the Day System
- Displays a custom MOTD when players join and when `/motd` is executed.
- **Live File Reloading**: Monitors file modification time on disk so changes made to `config.yml` take effect immediately without requiring `/reload`.
- **Variables**:
  - `{username}` / `{player}` – Player's username
  - `{playtime}` – Player's formatted playtime (e.g. `3h 15m`)
  - `{online}` – Online player count
  - `{max_online}` – Server maximum player capacity
  - `{world}` – Player's current world name
  - Supports color formatting with standard `&` codes (`&e`, `&a`, `&b`, etc.).

---

## Commands & Permissions

| Command | Usage | Description | Permission | Default |
| :--- | :--- | :--- | :--- | :--- |
| `/tpa` | `/tpa [username]` | Print help menu or send TPA request | `landensessentials.tpa` | `true` |
| `/tpaccept` | `/tpaccept` | Accept pending teleport request | `landensessentials.tpaccept` | `true` |
| `/tpadeny` | `/tpadeny` | Deny pending teleport request | `landensessentials.tpadeny` | `true` |
| `/tpahere` | `/tpahere <username>` | Send TPAHere request | `landensessentials.tpahere` | `true` |
| `/motd` | `/motd` | Display MOTD in player's chat | `landensessentials.motd` | `true` |

*I recommend [LuckPerms](https://modrinth.com/plugin/luckperms) for controlling user permissions*

---

## Configuration (`config.yml`)

```yaml
endermen:
  prevent-griefing: true

teleport:
  request-timeout-seconds: 120
  safe-search-radius: 4

motd:
  enabled: true
  message:
    - "&e=========================================="
    - "&bWelcome to the server, &f{username}&b!"
    - "&7Playtime: &a{playtime} &7| World: &a{world}"
    - "&7Online Players: &a{online}&7/&a{max_online}"
    - "&e=========================================="
```

---

## Building the Plugin

### Using Gradle
Run the following command from the root directory:
```bash
./gradlew build
```

The compiled plugin `.jar` will be generated at:
```
build/libs/LandensEssentials-1.0.0.jar
```

### Using Eclipse
1. Right-click project -> **Gradle** -> **Refresh Gradle Project**.
2. Run the Gradle `build` or `jar` task.
3. Copy `build/libs/LandensEssentials-1.0.0.jar` to your Paper server's `plugins/` directory.
