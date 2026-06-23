# Setup Guide

## For Server Owners

### Requirements

- **Minecraft Server:** Paper 1.21 or later
- **Java:** 21+
- **Required Plugins:** [FrontlineEconomy](https://github.com/1tzArad/FrontlineEconomy)
- **Optional Plugins:** [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) 2.12+, [BlueMap](https://bluemap.bluecolored.de/) 2.7+

### Installation

1. Download `BorderAndFrontlines-1.2.jar` from [Releases](https://github.com/1tzArad/BorderAndFrontlines/releases).
2. Place the jar in your server's `plugins/` folder.
3. Ensure FrontlineEconomy is also in `plugins/` and is enabled before BAF.
4. Start or restart the server.

On first startup, BAF will generate `plugins/BorderAndFrontlines/config.yml`.

### Configuration

Edit `config.yml` to customize the plugin:

```yaml
# Enable/disable war mechanics
allow-war: true

# Worlds where nation mechanics are active
worlds:
  - world

# Nation name length constraints
naming:
  min: 3      # minimum characters
  max: 18     # maximum characters

# Nation settings
nation:
  creation-cost: 1000.0   # cost to create a nation
  minPlayers: 3           # minimum players in chunk to start creation
  claim-cost: 50.0        # cost to claim a chunk
  unclaim-refund: 40.0    # refund for unclaiming a chunk
  nation-chat-format: "&7[&8{rank}&7] &7{player}&8: &7{message}"

# City settings
city:
  creation-cost: 300.0
  destruction-refund: 200.0

# Port settings (planned)
port:
  creation-cost: 500.0
  destruction-refund: 400.0
```

After editing, restart the server or use a reload command to apply changes.

### PlaceholderAPI Integration

If PlaceholderAPI is installed, BAF automatically registers these placeholders:

| Placeholder | Description |
|---|---|
| `%baf_nation_name%` | The player's nation name |
| `%baf_nation_color%` | The player's nation color code |
| `%baf_nation_balance%` | The player's nation vault balance |
| `%baf_nation_rank%` | The player's rank within their nation |

Use these in scoreboards, chat formats, tab lists, or any plugin that supports PlaceholderAPI.

### BlueMap Integration

If BlueMap is installed, BAF will:

- Render nation claimed chunks as colored polygons on all BlueMap maps
- Place city markers at each city's core location
- Update markers dynamically when chunks are claimed/unclaimed

No additional configuration is needed — BAF auto-detects BlueMap on startup.

---

## For Developers

### Prerequisites

- **Java 21** (JDK)
- **Maven 3.9+**
- **Git**
- An IDE with Lombok support (IntelliJ IDEA recommended)

### Cloning and Building

```bash
git clone https://github.com/1tzArad/BorderAndFrontlines.git
cd BorderAndFrontlines
mvn clean package
```

The compiled jar will be at `target/BorderAndFrontlines-1.2.jar`.

### IDE Setup

1. Open the project as a Maven project in your IDE.
2. Ensure annotation processing is enabled (Lombok).
3. Mark `src/main/java` as Sources Root and `src/main/resources` as Resources Root.

### Dependencies

BAF depends on FrontlineEconomy which is not published to Maven Central. You have two options:

**Option A:** Install FrontlineEconomy to your local Maven repository:
```bash
# From the FrontlineEconomy project
mvn install
```

**Option B:** Add it to a private repository and configure in your `~/.m2/settings.xml`.

### Run a Dev Server

1. Build the plugin: `mvn clean package`
2. Copy the jar from `target/` to a test server's `plugins/` folder
3. Start the Paper server

### Test Commands

Once running, use these in-game:

```
/nation                    # List all subcommands
/nation create             # Start nation creation
/nation info               # View your nation
/nation claim              # Claim current chunk
/nation chat               # Toggle nation chat
```
