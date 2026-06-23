<p align="center">
  <img src="https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk&logoColor=white" alt="Java 21">
  <img src="https://img.shields.io/badge/Paper-1.21-FF4500?logo=data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+PHBhdGggZmlsbD0id2hpdGUiIGQ9Ik0xMiAyTDMgN2wxIDEyIDEwIDYgMTAtNiAxMi0xMi0xLTMtMTAgNi0xMC0yem0wIDE0LjVMMTIgMTJsLTQuNCAzLjVMMTEgMjIgMTIgMTkuNSAxMyAyMmwyLjQtNi41TDEyIDE2LjV6Ii8+PC9zdmc+" alt="Paper">
  <img src="https://img.shields.io/badge/Version-1.2-blue" alt="Version">
  <img src="https://img.shields.io/github/license/1tzArad/BorderAndFrontlines?color=gpl" alt="License">
</p>

<h1 align="center">BorderAndFrontlines</h1>

<p align="center">
  A Minecraft server plugin that introduces a nation system with territory control, alliances, cities, and an economy layer built on top of Paper 1.21.
</p>

<p align="center">
  <a href="https://github.com/1tzArad/BorderAndFrontlines/releases">Releases</a>
  ·
  <a href="wiki/SETUP.md">Setup Guide</a>
  ·
  <a href="wiki/ARCHITECTURE.md">Architecture</a>
  ·
  <a href="wiki/CONTRIBUTING.md">Contributing</a>
</p>

---

## Features

- **Nation Creation** — Players can found nations by inviting nearby players in a creation session, choosing an ideology, and claiming their first chunk.
- **Territory Control** — Claim and unclaim chunks on a chunk-based system. Neighboring chunks must be adjacent to existing claims.
- **City System** — Create cities within claimed territory, each with a physical City Core block, a mayor, residents, and health/level mechanics.
- **Alliances** — Form alliances between nations via invite-based requests.
- **Nation Chat** — Toggle a dedicated nation chat channel with configurable format.
- **Rank & Permission System** — Ideology-driven rank hierarchy with fine-grained permissions (claim, unclaim, vault operations, invitations, etc.).
- **Economy Integration** — Nation vaults with deposit/withdraw, creation costs, claim costs, and configurable tax rates.
- **BlueMap Integration** — Nation borders and city markers rendered on BlueMap web maps with per-nation colors.
- **PlaceholderAPI Support** — Exposes nation name, color, balance, and rank placeholders for use in scoreboards, chat, etc.

## Requirements

| Dependency | Required | Version |
|---|---|---|
| [Paper](https://papermc.io/) | Yes | 1.21+ |
| [AnbeEconomy](https://github.com/1tzArad) | Yes | 1.0 |
| [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) | Soft-depend | 2.12+ |
| [BlueMap](https://bluemap.bluecolored.de/) | Soft-depend | 2.7+ |
| Java | Yes | 21+ |

## Installation

1. Download the latest `.jar` from [Releases](https://github.com/1tzArad/BorderAndFrontlines/releases).
2. Place the `.jar` into your server's `plugins/` folder.
3. Ensure **AnbeEconomy** is installed and enabled.
4. Restart the server. A `config.yml` will be generated on first startup.
5. Edit `plugins/BorderAndFrontlines/config.yml` to your preferences and reload.

## Configuration

The generated `config.yml` contains:

```yaml
allow-war: true

worlds:
  - world

naming:
  min: 3
  max: 18

nation:
  creation-cost: 1000.0
  minPlayers: 3
  claim-cost: 50.0
  unclaim-refund: 40.0
  nation-chat-format: "&7[&8{rank}&7] &7{player}&8: &7{message}"

city:
  creation-cost: 300.0
  destruction-refund: 200.0

port:
  creation-cost: 500.0
  destruction-refund: 400.0
```

## Commands

| Command | Description | Permission |
|---|---|---|
| `/nation` | Main command — lists all subcommands | `BAF.nation` |
| `/nation create` | Start a nation creation session | `BAF.nation.create` |
| `/nation confirm` | Confirm nation creation (session leader) | — |
| `/nation cancel` | Cancel nation creation session | — |
| `/nation info` | View nation information | — |
| `/nation claim` | Claim the chunk you are standing in | — |
| `/nation invite <player>` | Invite a player to your nation | — |
| `/nation accept <code>` | Accept a nation or alliance invite | — |
| `/nation reject <code>` | Reject an invite | — |
| `/nation leave` | Leave your current nation | — |
| `/nation disband` | Disband your nation (leader only) | — |
| `/nation chat` | Toggle nation chat | — |
| `/nation ally <nation>` | Send an alliance request | — |
| `/nation break <nation>` | Break an alliance | — |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `BAF.nation` | Access to all nation commands | `true` |
| `BAF.nation.create` | Access to create a nation | — |
| `BAF.nation.settings` | Access to nation settings panel | — |

## PlaceholderAPI Placeholders

| Placeholder | Description |
|---|---|
| `%baf_nation_name%` | Player's nation name |
| `%baf_nation_color%` | Player's nation color |
| `%baf_nation_balance%` | Player's nation vault balance |
| `%baf_nation_rank%` | Player's rank within their nation |

## Building from Source

**Prerequisites:** Java 21+, Maven 3.9+

```bash
git clone https://github.com/1tzArad/BorderAndFrontlines.git
cd BorderAndFrontlines
mvn clean package
```

The compiled `.jar` will be in `target/`.

> **Note:** AnbeEconomy must be available in your local Maven repository or a configured remote repository for the build to succeed.

## Project Structure

```
BorderAndFrontlines/
├── src/main/java/xyz/ItzArad/
│   ├── baf/                          # Plugin core
│   │   ├── BorderAndFrontlines.java  # Main plugin class
│   │   ├── commands/                 # Command handlers
│   │   ├── listeners/                # Event listeners
│   │   ├── managers/                 # Business logic managers
│   │   ├── models/                   # Data models (Nation, City, Ranks, etc.)
│   │   ├── dialogs/                  # GUI dialogs (Triumph GUI)
│   │   ├── Placeholders/             # PlaceholderAPI expansions
│   │   ├── context/                  # UI context objects
│   │   ├── abstracts/                # Abstract base classes
│   │   └── common/                   # Shared interfaces and utilities
│   └── bafLibs/                      # Internal library (chunk/player utils)
├── src/main/resources/
│   ├── plugin.yml                    # Plugin descriptor
│   └── config.yml                    # Default configuration
├── pom.xml                           # Maven build configuration
└── LICENSE                           # GPLv3
```

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE).

Any redistributed modified version must also be open-source under GPLv3. Closed-source redistribution is not permitted.

## Contributing

Contributions are welcome. See the [Contributing Guide](wiki/CONTRIBUTING.md) for details on setting up a development environment and submitting changes.
