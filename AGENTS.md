# AGENTS.md - Apolo Music Bot Development Guide

## Project Overview

Apolo Music Bot is a Discord music bot built with Java, Spring Boot, and Gradle. It uses JDA for Discord API integration and LavaPlayer for audio streaming.

## Build & Run Commands

```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests "ApoloMusicBotApplicationTests"

# Run a single test method
./gradlew test --tests "ApoloMusicBotApplicationTests.contextLoads"

# Package without running tests
./gradlew assemble -x test

# Clean build
./gradlew clean
```

## Environment Configuration

The bot requires a `DISCORD_BOT_TOKEN` environment variable. The project uses `.envrc` with direnv for local development:

```bash
# .envrc
export DISCORD_BOT_TOKEN=your_discord_token_here
```

For Docker/Podman: Use `docker-compose.yml` or set environment variables in your deployment.

## Code Style Guidelines

### General Conventions

- **Package naming**: `com.zullid.apolo_music_bot.*`
- **Java version**: 25 (use latest Java features when appropriate)
- **Build tool**: Gradle (use `./gradlew` wrapper, not system `gradle`)
- **Versions**: declared in the `ext` block of `build.gradle`; do not duplicate them in docs
- **Formatter**: no formatter plugin is configured in the build; match the existing 4-space style instead of reformatting

### Project Structure

```
src/
├── main/java/com/zullid/apolo_music_bot/
│   ├── ApoloMusicBotApplication.java    # Main entry point
│   ├── config/                           # Configuration classes
│   ├── services/                         # Spring services
│   ├── player/                           # Player context
│   │   └── state/                        # State pattern implementations
│   ├── listeners/
│   │   ├── commands/                     # Slash command listeners
│   │   └── events/                       # JDA event listeners
│   └── handlers/                         # Audio handlers
└── test/java/                           # Test classes
```

### Imports

- Use explicit imports (no wildcard `.*` except for static imports)
- Follow the existing per-file import order; there is no enforced grouping
- Use Lombok to reduce boilerplate

### Naming Conventions

- **Classes**: PascalCase (e.g., `AudioPlayerService`, `PlayerCommandListener`)
- **Methods/variables**: camelCase (e.g., `play()`, `addListener()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_TITLE_LENGTH`)
- **Packages**: lowercase (e.g., `com.zullid.apolo_music_bot.services`)

### Annotations

The project uses Lombok and Spring extensively:

- `@Slf4j` - for logging
- `@Getter` / `@Setter` - for accessor methods
- `@RequiredArgsConstructor` - for constructor injection
- `@Component` / `@Service` - for Spring beans
- `@PostConstruct` - for initialization logic

### Logging

- Use Lombok's `@Slf4j` for all classes
- `info`: startup, lifecycle and operator actions only; routine per-track detail goes to `debug`
- Command flow context (`command`, `guildId`, `userId`) travels in the SLF4J MDC set by `PlayerCommandListener`; callbacks on LavaPlayer threads log identifiers inline (no MDC there)
- Levels are env-overridable (`LOGGING_LEVEL_<PACKAGE>=DEBUG`); JDA stays at `WARN` by default
- Output is configured in `logback-spring.xml`: plain text on `dev`, JSON to stdout plus rolling file otherwise

### Error Handling

- Use JDA's `.queue()` for async operations with callbacks
- Reply to user with helpful error messages for command failures
- Check preconditions before executing commands (e.g., `isFromGuild()`)

### State Pattern

The player uses a State pattern:

- `PlayerState` (abstract base)
- `ReadyState`, `PlayingState`, `PausedState` (implementations)
- Each state handles relevant commands via `onPlay()`, `onPause()`, etc.

### JDA Best Practices

- Always check `event.isFromGuild()` in guild commands
- Use `setEphemeral(true)` for help/error messages that should only be visible to the user
- Use `SlashCommandInteractionEvent.reply().queue()` for async responses
- **Discord Limits**: Be mindful of the **2000-character limit** for responses. Use truncation (e.g., for track titles) when building long messages like the `/queue` list.
- **DAVE Protocol**: As of March 2026, Discord requires **DAVE (End-to-End Encryption)** for voice. This requires JDA 6.3.2+ and JDave libraries. **DO NOT** downgrade below these versions or voice connections will fail.

### Java Features

- Use Java text blocks (triple quotes) for multi-line strings
- Use `switch` statements on command names (see `PlayerCommandListener`)

### Testing

- Tests are located in `src/test/java/`
- Use `@SpringBootTest` for integration tests
- Run tests with `./gradlew test`
- Run a single test class with `./gradlew test --tests "<ClassName>"`
- Unit tests cover services, handlers, player states, and listeners
- `test` also generates the JaCoCo HTML report (`build/reports/jacoco`); keep 100% line/method coverage — the only accepted branch gap is the unreachable `queue.offer()` false side until the queue is bounded
- Document tests with class-level scope plus per-test Given/When/Then Javadoc, keeping `@author`; main sources carry full method-level Javadoc (`@param`/`@return`) with the doc comment placed before annotations

### Discord Commands

Available slash commands:

- `/play <query>` - Play a song or add to queue
- `/pause` - Pause playback
- `/resume` - Resume playback
- `/stop` - Stop and clear queue
- `/skip` - Skip current song
- `/queue` - Show current queue and playing track
- `/help` - Show help message

### Key Dependencies

- **JDA** - Discord API wrapper (supports DAVE protocol)
- **LavaPlayer** - Audio player library
- **LavaLink YouTube** - YouTube source
- **JDave** - Voice encryption (required for March 2026 voice support)
- **Spring Boot** - Application framework
- **Lombok** - Code generation

## Docs

- `docs/DOCS.md` is the complete setup guide (everything to get the bot running). Keep it low-maintenance: no command lists (the bot's `/help` covers that), no versions duplicated from `build.gradle`.
- `docs/architecture.md` covers the codebase; the diagram source is `docs/class-diagram.puml`.
