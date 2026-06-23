# Contributing to BorderAndFrontlines

Thank you for your interest in contributing to BAF.

## Getting Started

1. **Fork** the repository on GitHub.
2. **Clone** your fork:
   ```bash
   git clone https://github.com/<your-username>/BorderAndFrontlines.git
   ```
3. **Create a branch** for your change:
   ```bash
   git checkout -b feature/your-feature-name
   ```
4. **Set up the development environment** — see [Setup Guide: For Developers](SETUP.md#for-developers).

## Development Guidelines

### Code Style

- Use **Java 21** features where appropriate.
- Follow existing conventions in the codebase:
  - Package names: `xyz.ItzArad.baf.*`
  - Use Lombok annotations (`@Getter`, `@UtilityClass`) to reduce boilerplate.
  - Use Adventure API (`Component`, `Colors`) for all player messaging — no legacy `ChatColor`.
- Keep classes focused. One class = one responsibility.
- Prefer interfaces for extensibility (see `SubCommand`, `NationCommand`).

### Project Layout

When adding new features, place code in the correct package:

| Package | Purpose |
|---|---|
| `baf.commands.nation` | Subcommands for `/nation` |
| `baf.commands` | Top-level command executors |
| `baf.listeners` | Bukkit event listeners |
| `baf.managers` | Business logic and data access |
| `baf.models` | Data models and enums |
| `baf.dialogs` | GUI dialog classes (Triumph GUI) |
| `baf.Placeholders` | PlaceholderAPI expansion classes |
| `baf.common` | Shared interfaces and utilities |
| `baf.abstracts` | Abstract base classes |

### Adding a New Subcommand

1. Create a class in `xyz.ItzArad.baf.commands.nation` implementing `NationCommand`.
2. Implement all required methods:
   ```java
   public class MyNewCommand implements NationCommand {
       @Override public String getName() { return "mycommand"; }
       @Override public String getDescription() { return "Does something"; }
       @Override public String getPermission() { return "BAF.nation.mycommand"; }
       @Override public boolean isPlayerOnly() { return true; }
       @Override public boolean requiresNation() { return false; }
       @Override public boolean publicCommand() { return true; }
       @Override public Optional<Permissions> requiredPermission() { return Optional.empty(); }
       @Override public boolean execute(BAFPlayer player, String[] args) { /* ... */ }
   }
   ```
3. Register it in `NationCommands.java` constructor:
   ```java
   registerCommand(new MyNewCommand());
   ```
4. If the command supports tab completion, implement `TabCompletable`.

### Adding a Placeholder

1. Create a class in `xyz.ItzArad.baf.Placeholders.nation` extending `PlaceholderExpansion`.
2. Register it in `PlaceholderApiHooker.java`.

### Adding an Event Listener

1. Create a class in `xyz.ItzArad.baf.listeners` implementing `Listener`.
2. Register it in `BorderAndFrontlines.onEnable()` via `registerListener()`.

## Submitting Changes

### Commit Messages

Use clear, descriptive commit messages:

```
Add nation disband cooldown

- Leaders must wait 24h between disband requests
- Added cooldown field to Nation model
- Updated config.yml with configurable cooldown
```

### Pull Request Process

1. Push your branch to your fork.
2. Open a Pull Request against the `main` branch.
3. Include in your PR description:
   - What the change does
   - Why it's needed
   - How to test it
   - Screenshots if it affects the UI/commands
4. Ensure the project builds without errors: `mvn clean package`
5. Wait for review. Address feedback as needed.

### What to Avoid

- Do not add features beyond what the PR scope requires.
- Do not introduce new dependencies without discussion.
- Do not commit `target/`, `.idea/`, or any generated files.
- Do not modify `plugin.yml` version unless intentionally bumping it.
- Do not invent features or placeholder data that doesn't exist.

## Reporting Issues

Open a GitHub issue with:

- **Title:** Brief description of the problem
- **Description:** Steps to reproduce, expected behavior, actual behavior
- **Environment:** Paper version, Java version, BAF version
- **Logs:** Relevant console errors or stack traces

## License

By contributing, you agree that your contributions will be licensed under the [GNU General Public License v3.0](../LICENSE).
