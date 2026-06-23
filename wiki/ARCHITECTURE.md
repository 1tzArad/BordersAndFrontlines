# Architecture

This document describes the internal architecture of BorderAndFrontlines (BAF).

## High-Level Overview

BAF is a Paper 1.21 Minecraft plugin built as a single Maven module. It follows a layered architecture:

```
┌─────────────────────────────────────────────┐
│              Commands (Layer 1)              │
│  NationCommands → SubCommand implementations │
├─────────────────────────────────────────────┤
│             Listeners (Layer 2)              │
│  AutoClaimListener, CityCoreListener,       │
│  NationChatListener                         │
├─────────────────────────────────────────────┤
│             Managers (Layer 3)               │
│  NationManager, MapManager,                 │
│  NationCreationSessionManager,              │
│  NationRanksManager                         │
├─────────────────────────────────────────────┤
│              Models (Layer 4)                │
│  Nation, City, CityCore, Ranks,             │
│  Permissions, Ideologies, NationInvite,     │
│  AllyInvite, NationCreationSession          │
├─────────────────────────────────────────────┤
│              bafLibs (Layer 5)               │
│  BAFPlayer, BAFChunk, BAFWorld,             │
│  JsonStorage, Config, Colors                │
└─────────────────────────────────────────────┘
```

## Entry Point

**`BorderAndFrontlines.java`** — the main plugin class extending `JavaPlugin`.

Startup sequence in `onEnable()`:

1. Initialize economy provider (FrontlineEconomy via `RegisteredServiceProvider`)
2. Load configuration via `Config.init()`
3. Load all nations from disk via `NationManager.init()`
4. Register the `/nation` command
5. Hook BlueMap and initialize `MapManager`
6. Hook PlaceholderAPI via `PlaceholderApiHooker`
7. Register event listeners

Shutdown in `onDisable()`:
- Saves all nations asynchronously via `NationManager.saveAllAsync()`

## Command System

Commands use a subcommand pattern:

- **`NationCommands`** — implements `CommandExecutor` and `TabCompleter`. Maintains a `Map<String, NationCommand>` of registered subcommands.
- **`NationCommand`** (interface) — extends `SubCommand`. Adds `requiresNation()`, `publicCommand()`, and `requiredPermission()`.
- **`SubCommand`** (interface) — defines `getName()`, `getDescription()`, `getPermission()`, `isPlayerOnly()`, and `execute(BAFPlayer, String[])`.
- **`TabCompletable`** (interface) — adds `onTabComplete(BAFPlayer, String[])`.

Each subcommand is a standalone class (e.g., `NationCreateCommand`, `NationClaimCommand`). The dispatcher in `NationCommands.onCommand()` handles:
- Permission checking
- Player-only enforcement
- Nation membership checks
- Nation creation session validation

## Data Persistence

All nation data is stored as individual JSON files using `JsonStorage`:

- Storage directory: `plugins/BorderAndFrontlines/nations/`
- Each nation is serialized/deserialized by its `UUID`
- Save operations are async (`CompletableFuture`-based) to avoid blocking the server tick
- Full save-all on plugin disable

### In-Memory Caches

`NationManager` maintains several `HashMap` caches for O(1) lookups:

| Cache | Key → Value | Purpose |
|---|---|---|
| `nationChunks` | `BAFChunk → Nation` | Which nation owns a chunk |
| `nationNameMap` | `String → Nation` | Lookup nation by name |
| `playerNationMap` | `BAFPlayer → Nation` | Which nation a player belongs to |
| `nationsCache` | `Queue<Nation>` | All loaded nations |
| `cityChunks` | `BAFChunk → City` | Which city owns a chunk |
| `cityMap` | `City → Nation` | Which nation a city belongs to |
| `coreMap` | `City → CityCore` | City core entity references |
| `playerCityMap` | `BAFPlayer → City` | Which city a player is in |

## Nation Model

A `Nation` holds:

- **Identity:** UUID, name, display name, color, leader
- **Ideology:** determines available ranks and their permissions
- **Territory:** `Set<BAFChunk>` of claimed chunks
- **Members:** `Map<UUID, Ranks>` of players and their ranks
- **Alliances:** `Set<UUID>` of allied nation UUIDs
- **Cities:** `Map<String, City>` of city name → city object
- **Economy:** vault balance, tax rate, tax interval, last tax time

### Chunk Claiming Rules

Chunks must be:
1. In an allowed world (per `config.yml` `worlds` list)
2. Adjacent (neighboring) to an existing claim
3. Not already claimed by another nation
4. The claiming player must have `CAN_CLAIM` permission and sufficient balance

## City System

Each `City` is created within a nation's claimed territory:

- Has a physical **City Core** — a campfire block with `BlockDisplay` and `TextDisplay` entities
- Has a mayor and residents with city-level ranks (`CityRank`)
- Has health calculated as `level * 20`
- The core renders the city name and mayor name as a floating label

### City Core Entities

- `BlockDisplay` — glowing campfire block at the city location
- `TextDisplay` — floating text showing city name and mayor

Both are spawned as persistent Bukkit entities and cleaned up when a city is destroyed.

## Nation Creation Session

Creating a nation requires a multi-step session:

1. Leader runs `/nation create` — validates checks (cost, name length, chunk availability, player count)
2. A `NationCreationSession` is created and stored in `NationCreationSessionManager`
3. Players in the same chunk receive a GUI dialog (`NationCreationSessionInviteDialog`) using Triumph GUI
4. Players accept or leave via subcommands
5. Once confirmed, the nation is finalized with its first city and claimed chunk

## BlueMap Integration

`MapManager` hooks into BlueMap to render nation borders:

- On BlueMap enable, rebuilds all marker sets
- Creates `ShapeMarker` polygons for each claimed chunk (16x16 block squares)
- Creates `POIMarker` pins for each city
- Supports dynamic add/remove of markers when chunks are claimed/unclaimed

Marker sets are per-map and toggleable in the BlueMap web UI.

## PlaceholderAPI Integration

`PlaceholderApiHooker` registers `NationPlaceholders` which expands:

| Placeholder | Returns |
|---|---|
| `%baf_nation_name%` | Nation name or empty |
| `%baf_nation_color%` | Nation color code |
| `%baf_nation_balance%` | Nation vault balance |
| `%baf_nation_rank%` | Player's rank name |

## Permission System

Permissions are defined in the `Permissions` enum:

```java
VAULT_WITHDRAW, VAULT_DEPOSIT, CAN_CLAIM, MAKE_ALLY,
BREAK_ALLY, CAN_UNCLAIM, PLAYER_INVITE, PLAYER_KICK,
CREATE_CITY, NATION_CHAT, NATION_SETTINGS_LOW,
NATION_SETTINGS_HIGH, MANAGER_PLAYERS, VAULT_COMMANDS
```

Each `Ranks` record holds a `Set<Permissions>`. Rank comparison uses a numeric `weight` — higher weight = higher authority. Ideologies define the available ranks and their default/leader assignments.

## External Dependencies

| Dependency | Scope | Purpose |
|---|---|---|
| Paper API | provided | Server API |
| Lombok | provided | Boilerplate reduction (`@Getter`, `@UtilityClass`) |
| VaultUnlockedAPI | provided | Economy abstraction |
| FrontlineEconomy | provided | Server-specific economy implementation |
| Triumph GUI | shaded | In-game GUI dialogs |
| BlueMap API | provided | Web map integration |
| PlaceholderAPI | provided | Placeholder expansion |
