# AGENTS.md - Apolo Music Bot Development Guide

This file provides guidelines and instructions for agentic coding agents working on this repository.

## Project Overview

Apolo Music Bot is a Discord music bot built with Java, Spring Boot, and Maven. It uses JDA for Discord API integration and LavaPlayer for audio streaming.

## Build & Run Commands

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=ApoloMusicBotApplicationTests

# Run a single test method
./mvnw test -Dtest=ApoloMusicBotApplicationTests#contextLoads

# Package without running tests
./mvnw package -DskipTests

# Clean build
./mvnw clean
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
- **Build tool**: Maven (use `./mvnw` wrapper, not system `mvn`)
- **Spring Boot**: Latest stable

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
- Order: standard Java → external libraries → project imports
- Use Lombok to reduce boilerplate

### Naming Conventions

- **Classes**: PascalCase (e.g., `AudioPlayerService`, `PlayerCommandListener`)
- **Methods/variables**: camelCase (e.g., `play()`, `addListener()`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_QUEUE_SIZE`)
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
- Use appropriate log levels: `log.info()`, `log.debug()`, `log.warn()`, `log.error()`
- Include contextual information in log messages

### Error Handling

- Use JDA's `.queue()` for async operations with callbacks
- Reply to user with helpful error messages for command failures
- Check preconditions before executing commands (e.g., `isFromGuild()`)

### State Pattern

The player uses a State pattern:
- `PlayerState` (interface/base)
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
- Use records where immutable data containers are needed
- Use `switch` expressions for pattern matching on command names

### Testing

- Tests are located in `src/test/java/`
- Use `@SpringBootTest` for integration tests
- Run tests with `./mvnw test`
- Run a single test class with `./mvnw test -Dtest=<ClassName>`
- Unit tests cover services, handlers, player states, and listeners

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
