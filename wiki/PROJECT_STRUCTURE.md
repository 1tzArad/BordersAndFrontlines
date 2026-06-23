# Project Structure

## Directory Layout

```
BorderAndFrontlines/
├── pom.xml                          # Maven build configuration
├── LICENSE                          # GPLv3 license
├── README.md                        # Project overview and documentation
├── .gitignore                       # Git ignore rules
├── wiki/                            # Developer documentation
│   ├── ARCHITECTURE.md              # Internal architecture
│   ├── SETUP.md                     # Setup guide (server + dev)
│   ├── CONTRIBUTING.md              # Contribution guidelines
│   └── PROJECT_STRUCTURE.md         # This file
└── src/main/
    ├── java/xyz/ItzArad/
    │   ├── baf/                     # Plugin core
    │   │   ├── BorderAndFrontlines.java
    │   │   ├── PlaceholderApiHooker.java
    │   │   ├── BlueMapHooker.java
    │   │   ├── commands/
    │   │   ├── listeners/
    │   │   ├── managers/
    │   │   ├── models/
    │   │   ├── dialogs/
    │   │   ├── Placeholders/
    │   │   ├── context/
    │   │   ├── abstracts/
    │   │   └── common/
    │   └── bafLibs/                 # Internal library
    │       ├── Colors.java
    │       ├── Config.java
    │       ├── JsonStorage.java
    │       ├── models/
    │       └── managers/
    └── resources/
        ├── plugin.yml               # Plugin descriptor
        └── config.yml               # Default configuration
```

## Package Breakdown

### `xyz.ItzArad.baf` — Plugin Core

| File | Purpose |
|---|---|
| `BorderAndFrontlines.java` | Main plugin class. Entry point for `onEnable`/`onDisable`. Registers commands, listeners, and hooks. |
| `PlaceholderApiHooker.java` | Registers all PlaceholderAPI expansions on plugin enable. |
| `BlueMapHooker.java` | Detects and hooks into BlueMap API. |

### `xyz.ItzArad.baf.commands` — Command System

| File | Purpose |
|---|---|
| `NationCommands.java` | Main `/nation` command executor. Routes to subcommands, handles permissions and session checks. |
| `CityCommands.java` | City-related subcommands. |
| `VaultCommands.java` | Nation vault deposit/withdraw commands. |

### `xyz.ItzArad.baf.commands.nation` — Nation Subcommands

| File | Description |
|---|---|
| `NationCreateCommand.java` | Initiates nation creation session |
| `NationConfirmCommand.java` | Confirms nation creation (session leader) |
| `NationCancelCreationCommand.java` | Cancels creation session |
| `LeaveNationCreationCommand.java` | Leave a creation session (invited player) |
| `NationInfoCommand.java` | Displays nation information |
| `NationClaimCommand.java` | Claims a chunk |
| `NationInviteCommand.java` | Invites a player to the nation |
| `NationAcceptCommand.java` | Accepts an invite |
| `NationRejectCommand.java` | Rejects an invite |
| `NationLeaveCommand.java` | Leaves the nation |
| `NationDisbandCommand.java` | Disbands the nation (leader only) |
| `NationChatToggleCommand.java` | Toggles nation chat |
| `NationAllyCommand.java` | Sends alliance request |
| `AllianceBreakCommand.java` | Breaks an alliance |

### `xyz.ItzArad.baf.commands.nation.creation_session`

| File | Description |
|---|---|
| `NationConfirmCommand.java` | Confirm nation creation |
| `NationCancelCreationCommand.java` | Cancel creation session |
| `LeaveNationCreationCommand.java` | Leave creation session |

### `xyz.ItzArad.baf.listeners` — Event Listeners

| File | Purpose |
|---|---|
| `AutoClaimListener.java` | Handles auto-claiming chunks when players move |
| `CityCoreListener.java` | Handles interactions with city core blocks |
| `NationChatListener.java` | Intercepts chat messages when nation chat is toggled |

### `xyz.ItzArad.baf.managers` — Business Logic

| File | Purpose |
|---|---|
| `NationManager.java` | Central manager — nation CRUD, player lookups, invite system, claim validation, config access |
| `NationCreationSessionManager.java` | Manages active nation creation sessions |
| `NationRanksManager.java` | Manages rank registry |
| `MapManager.java` | BlueMap marker management — renders nation borders and city markers |

### `xyz.ItzArad.baf.models` — Data Models

| File | Purpose |
|---|---|
| `Nation.java` | Core nation model — members, territory, cities, economy, alliances |
| `City.java` | City model — mayor, residents, chunk, health, level |
| `CityCore.java` | Physical city core — campfire block + display entities |
| `Ranks.java` | Rank record — name, weight, permissions |
| `Permissions.java` | Permission enum (14 permission types) |
| `Ideologies.java` | Ideology enum — defines available ranks per ideology |
| `CityRank.java` | City-level ranks (Mayor, Resident, etc.) |
| `NationInvite.java` | Nation invite data |
| `AllyInvite.java` | Alliance invite data |
| `sessions/NationCreationSession.java` | Creation session state |

### `xyz.ItzArad.baf.dialogs` — GUI Dialogs

| File | Purpose |
|---|---|
| `NationCreationSessionInviteDialog.java` | Triumph GUI dialog for creation session invites |

### `xyz.ItzArad.baf.Placeholders` — PlaceholderAPI

| File | Purpose |
|---|---|
| `NationPlaceholders.java` | Main placeholder expansion container |
| `nation/NationNamePlaceholder.java` | `%baf_nation_name%` |
| `nation/NationColorPlaceholder.java` | `%baf_nation_color%` |
| `nation/NationBalancePlaceholder.java` | `%baf_nation_balance%` |
| `nation/NationRankPlaceholder.java` | `%baf_nation_rank%` |

### `xyz.ItzArad.baf.context`

| File | Purpose |
|---|---|
| `IdeologySelectionContext.java` | Context object for ideology selection UI flow |

### `xyz.ItzArad.baf.abstracts`

| File | Purpose |
|---|---|
| `InviteAbstract.java` | Abstract base for invite types (nation invite, ally invite) |

### `xyz.ItzArad.baf.common` — Shared Utilities

| File | Purpose |
|---|---|
| `commands/SubCommand.java` | Base interface for all subcommands |
| `commands/NationCommand.java` | Extended subcommand interface for nation commands |
| `commands/NationCreationSessionCommand.java` | Extended interface for session-bound commands |
| `commands/TabCompletable.java` | Tab completion interface |
| `commands/CityCommand.java` | City command interface |
| `Invite.java` | Invite utility |
| `Placeholder.java` | Placeholder utility |
| `IdeologyRequirement.java` | Ideology requirement model |

### `xyz.ItzArad.bafLibs` — Internal Library

| File | Purpose |
|---|---|
| `Colors.java` | Adventure API color parsing and utility methods |
| `Config.java` | Configuration wrapper — reads `config.yml` |
| `JsonStorage.java` | JSON file-based persistence for nation data |
| `models/BAFPlayer.java` | Player wrapper — UUID-based, online/offline aware |
| `models/BAFChunk.java` | Chunk wrapper — world + chunk coordinates, claim checks |
| `models/BAFWorld.java` | World wrapper |
| `managers/BAFChunkManager.java` | Chunk utility methods |
| `managers/BAFWorldManager.java` | World utility methods |

## Key Design Decisions

1. **Single module** — The entire plugin is one Maven module with no submodules. Simplicity over modularity.
2. **JSON persistence** — Each nation is a single JSON file. No database required. Sufficient for small-to-medium servers.
3. **In-memory caching** — All data is loaded into `HashMap` caches on startup for fast lookups. Trade-off: higher memory usage, but avoids repeated disk reads.
4. **Subcommand pattern** — Commands are dispatched through a registry map rather than a monolithic command handler.
5. **Shaded dependencies** — Triumph GUI is shaded into the final jar to avoid requiring server owners to install it separately.
6. **Lombok** — Used throughout to reduce boilerplate (`@Getter`, `@UtilityClass`).
7. **Adventure API** — All text rendering uses Kyori Adventure for modern Minecraft text support.
